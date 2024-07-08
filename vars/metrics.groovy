// vars/metrics.groovy

def call(projectName, appName) {
    def metricsCollector = new com.example.metrics.MetricsCollector()
    def metrics = metricsCollector.collectMetrics(projectName, appName)

    return metrics
}
