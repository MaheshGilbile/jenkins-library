import io.prometheus.client.CollectorRegistry
import io.prometheus.client.Gauge
import io.prometheus.client.exporter.PushGateway

class CollectMetrics {
    String jobName
    String instanceName

    CollectMetrics(String jobName, String instanceName) {
        this.jobName = jobName
        this.instanceName = instanceName
    }

    void collectBranchMetrics(String branchName) {
        Gauge scmStatus = Gauge.build()
            .name("scm_status")
            .help("Status of SCM Checkout")
            .labelNames("job", "instance", "branch")
            .register()

        scmStatus.labels(jobName, instanceName, branchName).set(1)
    }

    void buildStageMetrics(boolean buildStatus, long buildDuration) {
        Gauge buildStatusGauge = Gauge.build()
            .name("build_status")
            .help("Status of Build Stage")
            .labelNames("job", "instance")
            .register()

        buildStatusGauge.labels(jobName, instanceName).set(buildStatus ? 1 : 0)

        Gauge buildDurationGauge = Gauge.build()
            .name("build_duration_seconds")
            .help("Duration of Build Stage in seconds")
            .labelNames("job", "instance")
            .register()

        buildDurationGauge.labels(jobName, instanceName).set(buildDuration / 1000.0) // Convert milliseconds to seconds
    }

    void unitTestCoverageMetrics(int coveragePercentage) {
        Gauge unitTestCoverage = Gauge.build()
            .name("unit_test_coverage")
            .help("Unit Test Coverage Percentage")
            .labelNames("job", "instance")
            .register()

        unitTestCoverage.labels(jobName, instanceName).set(coveragePercentage)
    }

    void sonarAnalysisMetrics(boolean sonarStatus) {
        Gauge sonarStatusGauge = Gauge.build()
            .name("sonar_analysis_status")
            .help("Status of SonarQube Analysis")
            .labelNames("job", "instance")
            .register()

        sonarStatusGauge.labels(jobName, instanceName).set(sonarStatus ? 1 : 0)
    }

    void artifactoryUploadMetrics(boolean uploadStatus) {
        Gauge artifactoryStatusGauge = Gauge.build()
            .name("artifactory_upload_status")
            .help("Status of Artifactory Upload")
            .labelNames("job", "instance")
            .register()

        artifactoryStatusGauge.labels(jobName, instanceName).set(uploadStatus ? 1 : 0)
    }

    void pushMetricsToPrometheus() {
        String pushGatewayAddress = "http://localhost:9091" // Replace with your PushGateway address
        PushGateway pushGateway = new PushGateway(pushGatewayAddress)

        try {
            pushGateway.push(CollectorRegistry.defaultRegistry, jobName)
            println("Metrics pushed successfully to Prometheus PushGateway")
        } catch (Exception e) {
            println("Failed to push metrics to Prometheus PushGateway: ${e.message}")
        }
    }
}

return new CollectMetrics(jobName, instanceName)
