pipeline {
    agent any
    stages {
        stage('Build'){
            steps {
                sh "./gradlew clean build"
            }
        }
        stage('Docker Image'){
            steps {
                sh "docker build -t book-service:${env.BUILD_ID} ."
            }
        }
        stage('Docker Push'){
            steps {
                sh "docker tag book-service:${env.BUILD_ID} localhost:5000/book-service:${env.BUILD_ID}"
                sh "docker push localhost:5000/book-service:${env.BUILD_ID}"
            }
        }

        stage ('Istio Initial Setup') {
            when {
                expression {
                    env.VERSION_COUNT = sh (
                        script: 'kubectl get deployments -l app=bookservice -o name | wc -l',
                        returnStdout: true
                    ).trim()
                    return env.VERSION_COUNT.toInteger() == 0
                }
            }
            steps {
                sh "sed -i \"s/%%BUILD_NUMBER%%/${env.BUILD_ID}/g\" bookservice-istio-route_template.yml"
                sh "kubectl apply -f bookservice-istio-route_template.yml"
            }
        }

        stage('k8s Deploy') {
            steps {

                sh "sed -i \"s/%%BUILD_NUMBER%%/${env.BUILD_ID}/g\" bookservice-install_template.yml"
                sh "istioctl kube-inject -f bookservice-install_template.yml > bookservice-istio-install.yml"
                sh "kubectl apply -f bookservice-service.yml"
                sh "kubectl apply -f bookservice-istio-install.yml"
            }
        }

        stage('Canary') {
            when {
                expression {
                    env.VERSION_COUNT.toInteger() > 0
                }
            }
            steps {

                script {
                    env.PREVIOUS_VERSION_NUMBER = sh (
                        script: 'kubectl get deployments -l app=bookservice -o json | jq ".items[].spec.template.metadata.labels.version" | sed "s/\"//g"',
                        returnStdout: true
                    ).trim()

                    env.PREVIOUS_VERSION_NAME = sh (
                        script: 'kubectl get deployments -l app=bookservice -o name',
                        returnStdout: true
                    ).trim()
                }

                sh "sed -i \"s/%%BUILD_NUMBER%%/${env.BUILD_ID}/g\" bookservice-istio-route_template.yml"
                sh "kubectl apply -f bookservice-istio-route_template.yml"

                sh "kubectl delete ${env.PREVIOUS_VERSION_NAME}"
            }
        
        }
    }
    post { 
        always { 
            cleanWs()
        }
    }
}