pipeline {
    agent any
    stages {
        stage("Build"){
            steps {
                sh "./gradlew clean build"
            }
        }
        stage("Docker Image"){
            steps {
                docker.build("book-service:${env.BUILD_ID}")
            }
        }
    }
}