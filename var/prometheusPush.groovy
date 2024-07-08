// vars/prometheusPush.groovy
def call(Map params) {
    def gateway = params.gateway
    def job = params.job
    def metrics = params.metrics

    def pushgatewayUrl = "http://${gateway}/metrics/job/${job}"
    def payload = metrics.collect { "${it.key} ${it.value}" }.join('\n')

    def response = httpRequest url: pushgatewayUrl, httpMode: 'POST', contentType: 'TEXT', requestBody: payload

    if (response.status != 200) {
        error "Failed to push metrics to Prometheus Pushgateway: ${response.status} ${response.content}"
    }
}