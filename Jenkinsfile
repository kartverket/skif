pipeline {
    agent {
        node {
            label 'SKIF'
            customWorkspace "workspace/${JOB_NAME}"
        }
    }
    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
        GRADLE_USER_HOME = "${env.WORKSPACE}/.gradle"
        GRADLE_ARGS = "-Pbase_version=${env.BRANCH_NAME} -Psub_version=-build$BUILD_NUMBER -Pdb_hostname=nnridb097 -Pdb_service=MA04TST.statkart.no -Pdb_username=JENKINS_SKIF_${env.EXECUTOR_NUMBER} -Pusername=JENKINS_SKIF_${env.EXECUTOR_NUMBER} -Ppassword=JENKINS_SKIF_${env.EXECUTOR_NUMBER} -PWEBLOGIC_HOME=${env.'WEBLOGIC_HOME_12.1.3.0'} -PWEBLOGIC_VERSION=12.1.3"

        //for publisering til sentralt maven repo bines opp via jenkins credential (secret text)
        MAVEN_PUBLISH = credentials('MAVEN_DEPLOY_RELEASE_CANDIDATE')
    }
    tools {
        jdk 'Java 8 Latest'
    }
    stages {
        stage('Build') { 
            steps {
                bat "gradlew clean assemble ${GRADLE_ARGS}"
            }
        }
        stage('Test') { 
            steps {
                bat "gradlew dbInit check ${GRADLE_ARGS}"
            }
        }
        stage('Deploy') { 
            steps {
                bat "gradlew publish ${GRADLE_ARGS} --init-script gradle/scripts/mavenPublish.gradle"
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
