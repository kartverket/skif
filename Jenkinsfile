pipeline {
    agent {
        node {
            label 'SKIF'
        }
    }
    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
        GRADLE_USER_HOME = "${env.WORKSPACE}/.gradle"
        GRADLE_ARGS = "-Pbase_version=${env.BRANCH_NAME} -Psub_version=-build$BUILD_NUMBER -Pdb_hostname=nnridb161.statkart.no -Pdb_service==MA02TST.statkart.no -Pdb_username=JENKINS_SKIF_${env.EXECUTOR_NUMBER} -Pusername=JENKINS_SKIF_${env.EXECUTOR_NUMBER} -Ppassword=JENKINS_SKIF_${env.EXECUTOR_NUMBER}"

        //for publisering til sentralt maven repo bines opp via jenkins credential (secret text)
        MAVEN_PUBLISH = credentials('MAVEN_DEPLOY_RELEASE_CANDIDATE')
    }
    tools {
        jdk 'Java 8 Latest'
    }
    stages {
        stage('Build') { 
            steps {
                sh "./gradlew clean assemble ${GRADLE_ARGS}"
            }
        }
        stage('Test') { 
            steps {
                sh "./gradlew dbInit check ${GRADLE_ARGS}"
            }
        }
        stage('Deploy') { 
            steps {
                sh "./gradlew publish ${GRADLE_ARGS} --init-script gradle/scripts/mavenPublish.gradle"
            }
        }
    }
    post {
        always {
            step([$class: 'Publisher', reportFilenamePattern: '**/testng-results.xml'])
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
