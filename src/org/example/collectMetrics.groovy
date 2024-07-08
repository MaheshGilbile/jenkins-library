package org.example

import io.prometheus.client.CollectorRegistry
import io.prometheus.client.Gauge
import io.prometheus.client.exporter.PushGateway

class CollectMetrics {
    String jobName
    String instanceName
    CollectorRegistry registry
    PushGateway pushGateway

    CollectMetrics(String jobName, String instanceName) {
        this.jobName = jobName
        this.instanceName = instanceName
        this.registry = new CollectorRegistry()
        this.pushGateway = new PushGateway("http://prometheus-pushgateway:9091") // Replace with your Pushgateway URL
    }

    void collectBranchMetrics(String branchName) {
        // Implement SCM checkout metrics collection
        Gauge.build()
            .name("scm_checkout_status")
            .help("SCM Checkout Status")
            .labelNames("job", "instance")
            .register(registry)
            .set(1)
            .labels(jobName, instanceName)
    }

    void buildStageMetrics(boolean buildStatus, long buildDuration) {
        Gauge.build()
            .name("build_status")
            .help("Build Status")
            .labelNames("job", "instance")
            .register(registry)
            .set(buildStatus ? 1 : 0)
            .labels(jobName, instanceName)

        Gauge.build()
            .name("build_duration_seconds")
            .help("Build Duration in Seconds")
            .labelNames("job", "instance")
            .register(registry)
            .set(buildDuration / 1000) // Convert milliseconds to seconds
            .labels(jobName, instanceName)
    }

    void unitTestCoverageMetrics(int coveragePercentage) {
        Gauge.build()
            .name("unit_test_coverage_percentage")
            .help("Unit Test Coverage Percentage")
            .labelNames("job", "instance")
            .register(registry)
            .set(coveragePercentage)
            .labels(jobName, instanceName)
    }

    void sonarAnalysisMetrics(boolean sonarStatus) {
        Gauge.build()
            .name("sonar_analysis_status")
            .help("Sonar Analysis Status")
            .labelNames("job", "instance")
            .register(registry)
            .set(sonarStatus ? 1 : 0)
            .labels(jobName, instanceName)
    }

    void artifactoryUploadMetrics(boolean uploadStatus) {
        Gauge.build()
            .name("artifactory_upload_status")
            .help("Artifactory Upload Status")
            .labelNames("job", "instance")
            .register(registry)
            .set(uploadStatus ? 1 : 0)
            .labels(jobName, instanceName)
    }

    void pushMetricsToPrometheus() {
        try {
            pushGateway.pushAdd(registry, jobName)
        } catch (Exception e) {
            println("Error pushing metrics to Prometheus: ${e.message}")
        }
    }
}

return new CollectMetrics(jobName, instanceName)
