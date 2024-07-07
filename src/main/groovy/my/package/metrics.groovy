package my.package

import io.prometheus.client.exporter.PushGateway
import io.prometheus.client.Gauge

class Metrics {
    static void collect(Map args) {
        String job = args.job
        String instance = args.instance
        String name = args.name
        String help = args.help
        double value = args.value

        Gauge gauge = Gauge.build()
                .name(name)
                .help(help)
                .labelNames('job', 'instance')
                .register()

        gauge.labels(job, instance).set(value)

        PushGateway pg = new PushGateway('pushgateway:9091')
        pg.pushAdd(gauge, job)
    }
}
