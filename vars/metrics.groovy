import groovy.transform.Field

class Metrics {
    String projectName
    String appName
    String appShortname
    String appId
    String branchName
    String scmStatus
    String unitTestStatus
    String sonarStatus
    String artifactoryUpload
    int totalSuccessBuilds
    int totalFailedBuilds
    double totalSuccessRate
}

def call(String projectName, String appName, Map env) {
    def metricsCollector = new MetricsCollector()
    def metrics = metricsCollector.collectMetrics(projectName, appName, env)

    return new Metrics(
        projectName: projectName,
        appName: appName,
        appShortname: env.APP_SHORTNAME ?: 'Unknown',
        appId: env.APP_ID ?: 'Unknown',
        branchName: env.BRANCH_NAME,
        scmStatus: metrics['SCM Checkout'] ?: '',
        unitTestStatus: metrics['Unit Test Coverage'] ?: '',
        sonarStatus: metrics['Sonar Scanning'] ?: '',
        artifactoryUpload: metrics['Artifactory Upload'] ?: '',
        totalSuccessBuilds: metrics['total_success_builds'] ?: 0,
        totalFailedBuilds: metrics['total_failed_builds'] ?: 0,
        totalSuccessRate: metrics['total_success_rate'] ?: 0.0
    )
}
