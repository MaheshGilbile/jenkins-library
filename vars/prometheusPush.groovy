// vars/prometheusPush.groovy

import io.prometheus.client.exporter.PushGateway

def call(Map params) {
    def gateway = params.gateway
    def job = params.job
    def metrics = params.metrics

    def pushgatewayUrl = "http://${gateway}/metrics/job/${job}"
    def payload = metrics.collect { "${it.key} ${it.value}" }.join('\n')

    def pushGateway = new PushGateway(gateway)
    pushGateway.pushAdd({ job, group -> payload })

    echo "Metrics pushed to Prometheus Pushgateway successfully."
}
