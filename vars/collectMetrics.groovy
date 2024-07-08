// vars/collectMetrics.groovy

import my.package.Metrics

def call(Map pipelineParams) {
    def metrics = new Metrics()

    def collectBranchMetrics(branchName) {
        metrics.pushToPushgateway("branch_metrics", [branch_name: branchName])
    }

    def collectBuildMetrics(buildStatus) {
        def buildValue = buildStatus == "SUCCESS" ? 1 : 0
        metrics.pushToPushgateway("build_metrics", [build_status: buildValue])
    }

    def collectUnitTestMetrics(testCoverage, testStatus) {
        metrics.pushToPushgateway("unit_test_metrics", [unit_test_coverage: testCoverage, unit_test_status: testStatus == "SUCCESS" ? 1 : 0])
    }

    def collectSonarMetrics(sonarMetrics) {
        metrics.pushToPushgateway("sonar_metrics", sonarMetrics)
    }

    def collectArtifactoryMetrics(artifactoryStatus) {
        def artifactoryValue = artifactoryStatus == "SUCCESS" ? 1 : 0
        metrics.pushToPushgateway("artifactory_metrics", [artifactory_upload_status: artifactoryValue])
    }

    return this
}
