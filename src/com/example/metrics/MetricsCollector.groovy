// com/example/metrics/MetricsCollector.groovy
package com.example.metrics

import jenkins.model.Jenkins

class MetricsCollector {
    def collectMetrics(projectName, appName) {
        def project = Jenkins.instance.getItemByFullName(projectName)
        def metrics = [:]

        // Application Name
        metrics['app_name'] = appName

        // Total Number of Builds per Project
        metrics['total_builds'] = project.getBuilds().size()

        // Total Builds Success
        metrics['total_success_builds'] = project.getBuilds().findAll { it.result.toString() == 'SUCCESS' }.size()

        // Total Failed Builds
        metrics['total_failed_builds'] = project.getBuilds().findAll { it.result.toString() != 'SUCCESS' }.size()

        // Average Build time
        def buildTimes = project.getBuilds().collect { it.getDuration() }
        metrics['average_build_time'] = buildTimes.sum() / buildTimes.size()

        // %Success Rate of builds
        metrics['success_rate'] = (metrics['total_success_builds'] / metrics['total_builds']) * 100

        return metrics
    }
}
