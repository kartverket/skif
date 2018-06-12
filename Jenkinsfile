pipeline {
    agent {
        node {
            label 'SKIF'
            customWorkspace "${JOB_NAME}"
        }
    }
    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false -XX:MaxPermSize=256m'
        GRADLE_USER_HOME = "${env.WORKSPACE}/.gradle"
        SKIF_VERSION = "2.7-build${BUILD_NUMBER}"
        GRADLE_ARGS = "-Pversion=$SKIF_VERSION -Pdb_hostname=nnridb009 -Pdb_service=MA02TST.statkart.no -Pdb_username=J_ANNET_${env.EXECUTOR_NUMBER} -Pusername=J_ANNET_${env.EXECUTOR_NUMBER} -Ppassword=J_ANNET_${env.EXECUTOR_NUMBER} -PWEBLOGIC_HOME=${env.'WEBLOGIC_HOME_12.1.3.0'} -PWEBLOGIC_VERSION=12.1.3"
		TEMPCRED = credentials('MAVEN_DEPLOY_RELEASES')
		REPO_UPLOAD_RELEASES = 'https://nexus.statkart.no/repository/releases/'
		REPO_UPLOAD_RELEASES_USERNAME = "${env.TEMPCRED_USR}"
		REPO_UPLOAD_RELEASES_PASSWORD = "${env.TEMPCRED_PSW}"
    }
    tools {
        jdk 'Java 7 Latest'
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
                echo "gradle uploadArchives ${GRADLE_ARGS}"
            }
        }
    }
}
