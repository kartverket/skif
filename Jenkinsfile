pipeline {
    agent {
        node {
            label 'SKIF'
        }
    }
    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
        GRADLE_USER_HOME = "${env.WORKSPACE}/.gradle"
        SKIF_VERSION = "${env.BRANCH_NAME}-build${BUILD_NUMBER}"
        GRADLE_ARGS = "-Pversion=$SKIF_VERSION -Pdb_hostname=nnridb097 -Pdb_service=MA04TST.statkart.no -Pdb_username=JENKINS_SKIF_${env.EXECUTOR_NUMBER} -Pusername=JENKINS_SKIF_${env.EXECUTOR_NUMBER} -Ppassword=JENKINS_SKIF_${env.EXECUTOR_NUMBER} -PWEBLOGIC_HOME=${env.'WEBLOGIC_HOME_12.1.3.0'} -PWEBLOGIC_VERSION=12.1.3"
        TEMPCRED = credentials('NEXUS_RELEASE_CREDENTIAL')
        REPO_UPLOAD_RELEASES = 'https://nexus.statkart.no/repository/releases/'
        REPO_UPLOAD_RELEASES_USERNAME = "${env.TEMPCRED_USR}"
        REPO_UPLOAD_RELEASES_PASSWORD = "${env.TEMPCRED_PSW}"
    }
    tools {
        jdk 'Java 8 Latest'
    }
    stages {
        stage('Build') { 
            steps {
                sh "gradlew clean assemble ${GRADLE_ARGS}"
            }
        }
        stage('Test') { 
            steps {
                sh "gradlew dbInit check ${GRADLE_ARGS}"
            }
        }
        stage('Deploy') { 
            steps {
                sh "gradlew uploadArchives ${GRADLE_ARGS}"
            }
        }
    }
    post {
        always {
            step([$class: 'Publisher', reportFilenamePattern: '**/build/reports/tests/testng-results.xml'])
        }
        success {
            script {
                jiraIssueSelector(issueSelector: [$class: 'DefaultIssueSelector'])
                .each {
                    id -> jiraComment(issueKey: id,
                        body: "Successfully integrated in [${env.BRANCH_NAME} build #${env.BUILD_NUMBER}|${currentBuild.absoluteUrl}]"
                    )
                }
            }
        }
    }
}
