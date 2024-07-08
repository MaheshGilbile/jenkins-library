package my.package

import groovy.transform.Field

class Metrics {
    
    @Field
    static final String PUSHGATEWAY_URL = 'http://localhost:9091/metrics/job' // Adjust URL as per your Pushgateway setup
    
    static void pushToPushgateway(String job, Map<String, Object> metrics) {
        def timestamp = System.currentTimeMillis() / 1000
        def payload = metrics.collect { name, value ->
            "${name.replaceAll('[^a-zA-Z0-9_:]', '_')} ${value} ${timestamp}\n"
        }.join()
        
        def url = "${PUSHGATEWAY_URL}/${job}/build"
        
        try {
            sh "echo -e '${payload}' | curl -X POST --data-binary @- ${url}"
        } catch (Exception e) {
            echo "Failed to push metrics to Prometheus Pushgateway: ${e.message}"
        }
    }
}
