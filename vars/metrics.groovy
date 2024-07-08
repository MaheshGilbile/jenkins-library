// vars/metrics.groovy

def call(Map params) {
    def metricsMap = [:]
    metricsMap["name"] = params.name
    metricsMap["help"] = params.help
    metricsMap["appName"] = params.appName
    metricsMap["job"] = params.job
    metricsMap["instance"] = params.instance
    metricsMap["value"] = params.value.toBigDecimal()

    // Push metrics to the Pushgateway
    def pushGateway = new com.example.metrics.PushGateway(params.gateway)
    pushGateway.push(metricsMap)

    // Return metrics map for further processing if needed
    return metricsMap
}
