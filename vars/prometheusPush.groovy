// vars/prometheusPush.groovy

import io.prometheus.client.exporter.PushGateway
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.Gauge
import io.prometheus.client.Counter
import io.prometheus.client.Summary



def call(Map params) {
    def gateway = params.gateway
    def job = params.job
    def metrics = params.metrics
    def appName = params.app_name

    def pushGateway = new PushGateway(gateway)
    def registry = new CollectorRegistry()

    // Define and register Prometheus metrics
    def totalBuilds = Gauge.build()
        .name("total_builds")
        .help("Total number of builds")
        .labelNames("app_name")
        .register(registry)

    def successBuilds = Gauge.build()
        .name("success_builds")
        .help("Total number of successful builds")
        .labelNames("app_name")
        .register(registry)

    def failedBuilds = Gauge.build()
        .name("failed_builds")
        .help("Total number of failed builds")
        .labelNames("app_name")
        .register(registry)

    def buildTime = Gauge.build()
        .name("build_time_seconds")
        .help("Average build time in seconds")
        .labelNames("app_name")
        .register(registry)

    def successRate = Gauge.build()
        .name("success_rate")
        .help("Success rate of builds")
        .labelNames("app_name")
        .register(registry)
		
    def branchName = Summary.build()
        .name("branch_name")
        .help("Branch name")
        .labelNames("app_name", "branchname")
        .register(registry)

    def artifactoryUploadStatus = Summary.build()
        .name("artifactory_upload_status")
        .help("Artifactory upload status")
        .labelNames("app_name", "status")
        .register(registry)

    def sonarScanStatus = Summary.build()
        .name("sonar_scan_status")
        .help("Sonar scan status")
        .labelNames("app_name", "status")
        .register(registry)

    def unitTestStatus = Summary.build()
        .name("unit_test_status")
        .help("Unit test status")
        .labelNames("app_name", "status")
        .register(registry)
		
    // Set metrics values
    totalBuilds.labels(appName).set(metrics.total_builds ?: 0)
    successBuilds.labels(appName).set(metrics.total_success_builds ?: 0)
    failedBuilds.labels(appName).set(metrics.total_failed_builds ?: 0)
    buildTime.labels(appName).set(metrics.average_build_time ?: 0)
    successRate.labels(appName).set(metrics.success_rate ?: 0)
    branchName.labels(appName, "branch_name").observe(1)
    artifactoryUploadStatus.labels(appName, "artifactory_upload_status").observe(1)
    sonarScanStatus.labels(appName, "sonar_scan_status").observe(1)
    unitTestStatus.labels(appName, "unit_test_status").observe(1)

    // Push metrics to Pushgateway
    try {
        pushGateway.pushAdd(registry, job)
        println("Metrics successfully pushed to Prometheus Pushgateway")
    } catch (Exception e) {
        throw new RuntimeException("Failed to push metrics to Prometheus Pushgateway: ${e.message}", e)
    }
}
