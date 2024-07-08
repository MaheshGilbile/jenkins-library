// vars/metrics.groovy

def call(projectName) {
    def metricsCollector = new com.example.metrics.MetricsCollector()
    def metrics = metricsCollector.collectMetrics(projectName)

    return metrics
}



