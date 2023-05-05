#!groovy

String schemaNumber() {
    NODE_NAME.split('-')[2].toInteger() % SKIF_19C_TOTAL_SCHEMAS.toInteger()
}

pipeline {
    agent {
        node {
            label 'SKIF'
        }
    }
    environment {
        GRADLE_ARGS = "-Pbase_version=${BRANCH_NAME} -Psub_version=-build$BUILD_NUMBER -Pdb_jdbc_url=$SKIF_19C_JDBC_URL -Pdb_username=JENKINS_SKIF_${schemaNumber()}"

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
