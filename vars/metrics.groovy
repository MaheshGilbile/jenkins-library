// metrics.groovy

import jenkins.model.Jenkins
import jenkins.branch.MultiBranchProject
import jenkins.model.Run

class MetricsCollector {
    def collectMetrics(projectName) {
        def project = Jenkins.instance.getItemByFullName(projectName)
        def metrics = [:]

        // Total Number of Builds per Project
        metrics['total_builds'] = project.getBuilds().size()

        // Total Builds Success
        metrics['total_success_builds'] = project.getBuilds().findAll { it.result == 'SUCCESS' }.size()

        // Total Failed Builds
        metrics['total_failed_builds'] = project.getBuilds().findAll { it.result!= 'SUCCESS' }.size()

        // Average Build time
        def buildTimes = project.getBuilds().collect { it.getDuration() }
        metrics['average_build_time'] = buildTimes.sum() / buildTimes.size()

        // %Success Rate of builds
        metrics['success_rate'] = (metrics['total_success_builds'] / metrics['total_builds']) * 100

        return metrics
    }
}