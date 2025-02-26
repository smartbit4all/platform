pipeline {
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    agent any
    tools {
        jdk 'Java SE Development Kit 21'
    }
    environment {
        maven = 'Maven 3.9.6'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Trivy Dependecy Scan') {
            steps {
                script {
                  sh label: 'Trivy Dependecy Scan', script: "$TRIVY_DEPENDENCY_SCAN --scanDir $WORKSPACE --pipelineBlock No"
                }
            }
        }
        stage('Build & Unit test') {
            steps {
                withMaven(maven: "${maven}") {
                    sh "mvn --no-transfer-progress clean install"
                }
            }
        }
        stage('SonarQube analysis') {
            steps {
                withMaven(maven: "${maven}") {
                    withSonarQubeEnv('SonarQube') {
                        sh "mvn -Dsonar.branch.name=${env.BRANCH_NAME} --no-transfer-progress sonar:sonar"
                    }
                }
            }
        }
        stage("Quality Gate") {
            steps {
                // Sonar analízis aszinkron módon fut, ezért meg kell várni az eredményt
                // A sonarban a projecten beállított profile alapján képzi a metrikákat és a beállított quality gate-nek kell megfelelnie
                // TODO temporally ignore sonar scan result
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    timeout(time: 10, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: false
                    }
                }
            }
        }
        // TODO egyelőre minden menjen fel automatikusan a nexus-ba a feature/jdk21 branchről. Ezen majd finomítani kell
        stage('Deploy to Nexus') {
            when { branch 'feature/jdk21' }
            steps {
                script {
                    withMaven (maven: "${maven}") {
                        sh "mvn clean deploy -DskipTests"
                    }
                }
            }
        }
    }

    post {
        failure {
            mail to: "csaba2.csegedi@kh.hu", subject: "[JENKINS] ${env.JOB_NAME} #${env.BUILD_NUMBER} failed", body: "Build failed (see ${BUILD_URL})"
        }
        always {
            archiveArtifacts artifacts: "security/trivy/dependencies/*.csv", allowEmptyArchive: true
        }
		cleanup {
			cleanWs()
		}
    }
}