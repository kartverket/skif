#!groovy

String schemaNumber() {
    NODE_NAME.split('-')[2].toInteger() % SKIF_19C_TOTAL_SCHEMAS.toInteger()
}

def normalizedBranchName = script.BRANCH_NAME.replaceAll('/', '-')

pipeline {
    agent {
        node {
            label 'SKIF'
        }
    }
    environment {
        GRADLE_ARGS = "-Pbase_version=${normalizedBranchName} -Psub_version=-build${env.BUILD_NUMBER} -Pdb_jdbc_url=$SKIF_19C_JDBC_URL -Pdb_username=JENKINS_SKIF_${schemaNumber()}"

        //for publisering til sentralt maven repo bines opp via jenkins credential (secret text)
        MAVEN_PUBLISH = credentials('MAVEN_DEPLOY_RELEASE_CANDIDATE')

        TZ = 'Europe/Oslo' //SKIF-760: workaround for historikk-tester
    }
    tools {
        jdk 'Java 17 Latest'
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
