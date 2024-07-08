// vars/prometheusPush.groovy

import io.prometheus.client.exporter.PushGateway
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.Counter

def call(Map params) {
    def gateway = params.gateway
    def job = params.job
    def metrics = params.metrics

    def pushGateway = new PushGateway(gateway)
    def registry = CollectorRegistry.defaultRegistry

    // Define and register Prometheus metrics
   // def totalBuilds = Counter.build()
   //     .name("total_builds")
   //     .help("Total number of builds")
   //     .register(registry)

    def successBuilds = Counter.build()
        .name("success_builds")
        .help("Total number of successful builds")
        .register(registry)

    def failedBuilds = Counter.build()
        .name("failed_builds")
        .help("Total number of failed builds")
        .register(registry)

    def buildTime = Counter.build()
        .name("build_time_seconds")
        .help("Average build time in seconds")
        .register(registry)

    def successRate = Counter.build()
        .name("success_rate")
        .help("Success rate of builds")
        .register(registry)

    // Set metrics values
    //totalBuilds.inc(metrics.total_builds ?: 0)
    successBuilds.inc(metrics.total_success_builds ?: 0)
    failedBuilds.inc(metrics.total_failed_builds ?: 0)
    buildTime.inc(metrics.average_build_time ?: 0)
    successRate.inc(metrics.success_rate ?: 0)

    // Push metrics to Pushgateway
    try {
        pushGateway.push(registry, job)
        println("Metrics successfully pushed to Prometheus Pushgateway")
    } catch (Exception e) {
        throw new RuntimeException("Failed to push metrics to Prometheus Pushgateway: ${e.message}", e)
    }
}
