package com.example.metrics

import groovy.sql.Sql
import jenkins.model.Jenkins
import java.util.Date
import java.util.Map

@Grab(group='org.postgresql', module='postgresql', version='42.7.2')

class MetricsCollector {

    // Method to record metrics for each stage
    def recordMetrics(String stageName, String status, Map env) {
        def project = Jenkins.instance.getItemByFullName(env.JOB_NAME)
        def totalBuilds = project.getBuilds().size()
        def totalSuccessBuilds = project.getBuilds().findAll { it.result.toString() == 'SUCCESS' }.size()
        def totalFailedBuilds = totalBuilds - totalSuccessBuilds
        def totalSuccessRate = totalBuilds > 0 ? (totalSuccessBuilds / totalBuilds) * 100.0 : 0.0

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

        insertMetricsIntoDatabase(metrics, env)
    }

    // Method to insert metrics into PostgreSQL database
    private def insertMetricsIntoDatabase(Map metrics, Map env) {
        def sql = Sql.newInstance(env.DB_URL, env.DB_USER, env.DB_PASS, 'org.postgresql.Driver')

        try {
            sql.execute("""
                INSERT INTO AppFitMetrics (application_name, application_shortname, application_id, branch_name, scm_status, unit_test_status, sonar_status, artifactory_upload, total_success_builds, total_failed_builds, total_success_rate, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                [
                    metrics['app_name'],
                    metrics['app_shortname'],
                    metrics['app_id'],
                    metrics['branch_name'],
                    metrics['stage_name'] == 'SCM Checkout' ? metrics['status'] : '',
                    metrics['stage_name'] == 'Unit Test Coverage' ? metrics['status'] : '',
                    metrics['stage_name'] == 'Sonar Scanning' ? metrics['status'] : '',
                    metrics['stage_name'] == 'Artifactory Upload' ? metrics['status'] : '',
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
