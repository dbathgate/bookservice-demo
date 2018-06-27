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
                sh "docker build -t book-service:${env.BUILD_ID} ."
            }
        }
        stage("Docker Push"){
            steps {
                sh "docker tag book-service:${env.BUILD_ID} localhost:5000/book-service:${env.BUILD_ID}"
                sh "docker push localhost:5000/book-service:${env.BUILD_ID}"
            }
        }
        stage("k8s Deploy") {
            steps {
                sh "sed \"s/%%BUILD_NUMBER%%/${env.BUILD_ID}/g\" ./bookservice-install.1.yml"
                sh "kubectl apply -f <(istioctl kube-inject -f bookservice-install.1.yml)"
            }
        }
    }
}