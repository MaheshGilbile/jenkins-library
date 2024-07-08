// vars/prometheusPush.groovy

import io.prometheus.client.exporter.PushGateway
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.Gauge

def call(Map params) {
    def gateway = params.gateway
    def job = params.job
    def metrics = params.metrics

    def pushGateway = new PushGateway(gateway)
    def registry = new CollectorRegistry()

    // Define and register Prometheus metrics
    def appName = Gauge.build()
        .name("app_name")
        .help("Application Name")
        .labelNames("app")
        .register(registry)
    def totalBuilds = Gauge.build()
        .name("total_builds")
        .help("Total number of builds")
        .labelNames("app")
        .register(registry)
    def successBuilds = Gauge.build()
        .name("success_builds")
        .help("Total number of successful builds")
        .labelNames("app")
        .register(registry)
    def failedBuilds = Gauge.build()
        .name("failed_builds")
        .help("Total number of failed builds")
        .labelNames("app")
        .register(registry)
    def buildTime = Gauge.build()
        .name("build_time_seconds")
        .help("Average build time in seconds")
        .labelNames("app")
        .register(registry)
    def successRate = Gauge.build()
        .name("success_rate")
        .help("Success rate of builds")
        .labelNames("app")
        .register(registry)

    // Set metrics values
    appName.labels(metrics.app_name).set(1)
    totalBuilds.labels(metrics.app_name).set(metrics.total_builds ?: 0)
    successBuilds.labels(metrics.app_name).set(metrics.total_success_builds ?: 0)
    failedBuilds.labels(metrics.app_name).set(metrics.total_failed_builds ?: 0)
    buildTime.labels(metrics.app_name).set(metrics.average_build_time ?: 0)
    successRate.labels(metrics.app_name).set(metrics.success_rate ?: 0)

    // Push metrics to Pushgateway
    try {
        pushGateway.pushAdd(registry, job)
        println("Metrics successfully pushed to Prometheus Pushgateway")
    } catch (Exception e) {
        throw new RuntimeException("Failed to push metrics to Prometheus Pushgateway: ${e.message}", e)
    }
}
