package com.example.metrics

import jenkins.model.Jenkins
import groovy.sql.Sql

class MetricsCollector {
    def recordMetrics(stageName, status, env) {
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

    def insertMetricsIntoDatabase(metrics, env) {
        def sql = Sql.newInstance(env.DB_URL, env.DB_USER, env.DB_PASS, 'org.postgresql.Driver')

        try {
            sql.execute("INSERT INTO AppFitMetrics (application_name, application_shortname, application_id, branch_name, scm_status, unit_test_status, sonar_status, artifactory_upload, total_success_builds, total_failed_builds, total_success_rate, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
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
                    new Date() // created_at
                ])
            println("Metrics successfully inserted into PostgreSQL database")
        } catch (Exception e) {
            println("Failed to insert metrics into PostgreSQL database: ${e.message}")
        } finally {
            sql.close()
        }
    }
}
