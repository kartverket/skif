pipeline {
    agent {
        node {
            label 'SKIF'
            customWorkspace "workspace/${JOB_NAME}"
        }
    }
    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false -XX:MaxPermSize=256m'
        GRADLE_USER_HOME = "${env.WORKSPACE}/.gradle"
        SKIF_VERSION = "${env.BRANCH_NAME}-build${BUILD_NUMBER}"
        GRADLE_ARGS = "-Pversion=$SKIF_VERSION -Pdb_hostname=nnridb009 -Pdb_service=MA02TST.statkart.no -Pdb_username=J_ANNET_${env.EXECUTOR_NUMBER} -Pusername=J_ANNET_${env.EXECUTOR_NUMBER} -Ppassword=J_ANNET_${env.EXECUTOR_NUMBER} -PWEBLOGIC_HOME=${env.'WEBLOGIC_HOME_12.1.3.0'} -PWEBLOGIC_VERSION=12.1.3"
        TEMPCRED = credentials('NEXUS_RELEASE_CREDENTIAL')
        REPO_UPLOAD_RELEASES = 'https://nexus.statkart.no/repository/releases/'
        REPO_UPLOAD_RELEASES_USERNAME = "${env.TEMPCRED_USR}"
        REPO_UPLOAD_RELEASES_PASSWORD = "${env.TEMPCRED_PSW}"
    }
    tools {
        jdk 'Java 8 Latest'
        gradle 'Gradle 2.8'
    }
    stages {
        stage('Build') { 
            steps {
                bat "gradle clean assemble ${GRADLE_ARGS}"
            }
        }
        stage('Test') { 
            steps {
                bat "gradle dbInit check ${GRADLE_ARGS}"
            }
        }
        stage('Deploy') { 
            steps {
                bat "gradle uploadArchives ${GRADLE_ARGS}"
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
