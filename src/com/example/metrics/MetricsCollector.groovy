package com.example.metrics

import groovy.sql.Sql
import java.text.SimpleDateFormat
import java.util.Date

@Grab(group='org.postgresql', module='postgresql', version='42.7.2')

class MetricsCollector {
    void recordMetrics(String stageName, String status, Map env) {
        def project = Jenkins.instance.getItemByFullName(env.JOB_NAME)
        def builds = project.getBuilds()

        def totalBuilds = builds.size()
        def totalSuccessBuilds = builds.findAll { it.result.toString() == 'SUCCESS' }.size()
        def totalFailedBuilds = totalBuilds - totalSuccessBuilds
        def totalSuccessRate = totalBuilds > 0 ? (totalSuccessBuilds * 100.0 / totalBuilds) : 0.0

        def metrics = [
            'stage_name': stageName,
            'status': status,
            'app_name': env.APP_NAME,
            'app_shortname': env.APP_SHORTNAME ?: 'Unknown',
            'app_id': env.APP_ID ?: 'Unknown',
            'branch_name': env.BRANCH_NAME,
            'total_success_builds': totalSuccessBuilds,
            'total_failed_builds': totalFailedBuilds,
            'total_success_rate': totalSuccessRate
        ]
        
        // Insert metrics into PostgreSQL database
        insertMetricsIntoDatabase(metrics, env)
    }

    void insertMetricsIntoDatabase(Map metrics, Map env) {
        def dbUrl = env.DB_URL
        def dbUser = env.DB_USER
        def dbPass = env.DB_PASS

        def sql = Sql.newInstance(dbUrl, dbUser, dbPass, 'org.postgresql.Driver')

        try {
            sql.execute("""
                INSERT INTO AppFitMetrics (application_name, application_shortname, application_id, branch_name, scm_status, unit_test_status, sonar_status, artifactory_upload, total_success_builds, total_failed_builds, total_success_rate, created_at)
                VALUES (?, ?, ?, ?, ?, '', '', '', ?, ?, ?, ?)
                """,
                [
                    metrics['app_name'],
                    metrics['app_shortname'],
                    metrics['app_id'],
                    metrics['branch_name'],
                    metrics['stage_name'] == 'SCM Checkout' ? metrics['status'] : '',
                    metrics['total_success_builds'],
                    metrics['total_failed_builds'],
                    metrics['total_success_rate'],
                    new Date()
                ])
            println("Metrics successfully inserted into PostgreSQL database")
        } catch (Exception e) {
            println("Failed to insert metrics into PostgreSQL database: ${e.message}")
        } finally {
            sql.close()
        }
    }
}
