package com.example.metrics

import groovy.sql.Sql
import java.util.Date

class MetricsCollector {

    def recordMetrics(Map metricsMap, Map env) {
        def stageName = metricsMap.stageName
        def status = metricsMap.status

        // Additional metrics collection logic as needed
        def totalSuccessBuilds = getSuccessBuildCount(env.JOB_NAME)
        def totalFailedBuilds = getFailedBuildCount(env.JOB_NAME)
        def totalBuilds = getTotalBuildCount(env.JOB_NAME)
        def totalSuccessRate = calculateSuccessRate(totalSuccessBuilds, totalBuilds)

        def metrics = [
            stage_name: stageName,
            status: status,
            app_name: env.APP_NAME,
            app_shortname: env.APP_SHORTNAME ?: 'Unknown',
            app_id: env.APP_ID ?: 'Unknown',
            branch_name: env.BRANCH_NAME,
            total_success_builds: totalSuccessBuilds,
            total_failed_builds: totalFailedBuilds,
            total_success_rate: totalSuccessRate
        ]

        insertMetricsIntoDatabase(metrics, env)
    }

    private def getSuccessBuildCount(String jobName) {
        def project = Jenkins.instance.getItemByFullName(jobName)
        project.getBuilds().findAll { it.result == hudson.model.Result.SUCCESS }.size()
    }

    private def getFailedBuildCount(String jobName) {
        def project = Jenkins.instance.getItemByFullName(jobName)
        def totalBuilds = project.getBuilds().size()
        totalBuilds - getSuccessBuildCount(jobName)
    }

    private def getTotalBuildCount(String jobName) {
        Jenkins.instance.getItemByFullName(jobName).getBuilds().size()
    }

    private def calculateSuccessRate(int successBuilds, int totalBuilds) {
        totalBuilds > 0 ? (successBuilds / totalBuilds.toDouble()) * 100.0 : 0.0
    }

    private def insertMetricsIntoDatabase(Map metrics, Map env) {
        def sql = Sql.newInstance(env.DB_URL, env.DB_USER, env.DB_PASS, 'org.postgresql.Driver')

        try {
            sql.execute("""
                INSERT INTO AppFitMetrics (application_name, application_shortname, application_id, branch_name, scm_status, unit_test_status, sonar_status, artifactory_upload, total_success_builds, total_failed_builds, total_success_rate, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                [
                    metrics.app_name,
                    metrics.app_shortname,
                    metrics.app_id,
                    metrics.branch_name,
                    metrics.stage_name == 'SCM Checkout' ? metrics.status : '',
                    metrics.stage_name == 'Unit Test Coverage' ? metrics.status : '',
                    metrics.stage_name == 'Sonar Scanning' ? metrics.status : '',
                    metrics.stage_name == 'Artifactory Upload' ? metrics.status : '',
                    metrics.total_success_builds,
                    metrics.total_failed_builds,
                    metrics.total_success_rate,
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
