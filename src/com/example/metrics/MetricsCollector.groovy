// src/com/example/metrics/MetricsCollector.groovy

package com.example.metrics

import jenkins.model.Jenkins

class MetricsCollector {
    def collectMetrics(String appName) {
        def project = Jenkins.instance.getItemByFullName(appName)
        def metrics = [:]

        // Total Number of Builds per Project
        metrics['total_builds'] = project.getBuilds().size()

        // Total Builds Success
        metrics['total_success_builds'] = project.getBuilds().findAll { it.result == 'SUCCESS' }.size()

        // Total Failed Builds
        metrics['total_failed_builds'] = project.getBuilds().findAll { it.result != 'SUCCESS' }.size()

        // Average Build time
        def buildTimes = project.getBuilds().collect { it.getDuration() }
        metrics['average_build_time'] = buildTimes.sum() / buildTimes.size()

        // % Success Rate of builds
        metrics['success_rate'] = (metrics['total_success_builds'] / metrics['total_builds'].toDouble()) * 100

        return metrics
    }
}
