// vars/metrics.groovy
package vars

import io.prometheus.client.CollectorRegistry
import io.prometheus.client.Gauge
import io.prometheus.client.exporter.PushGateway

def call(Map params) {
    def gateway = new PushGateway('localhost:9091')
    def registry = new CollectorRegistry()

    Gauge metric = Gauge.build()
        .name(params.name)
        .help(params.help)
        .labelNames('job', 'instance')
        .register(registry)

    metric.labels(params.job, params.instance).set(params.value)

    gateway.pushAdd(registry, params.job)
}
