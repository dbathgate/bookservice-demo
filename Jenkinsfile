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

                sh "sed -i \"s/%%BUILD_NUMBER%%/${env.BUILD_ID}/g\" bookservice-install_template.yml"
                sh "istioctl kube-inject -f bookservice-install_template.yml > bookservice-istio-install.yml"
                sh "kubectl apply -f bookservice-service.yml"
                sh "kubectl apply -f bookservice-istio-install.yml"
            }
        }

        stage ('Istio Initial Setup') {
            when {
                expression {
                    VERSION_COUNT = sh (
                        script: 'kubectl get deployments -l app=bookservice | wc -l',
                        returnStdOut: true
                    )
                    return VERSION_COUNT == 0
                }
            }
            steps {
                sh "sed \"s/%%BUILD_NUMBER%%/${env.BUILD_ID}/g\" bookservice-istio-route_template.yml"
                sh "kubectl apply -f bookservice-istio-route_template.yml"
                return
            }
        }

        stage("Canary") {
            steps {

                sh "sed \"s/%%BUILD_NUMBER%%/${env.BUILD_ID}/g\" bookservice-istio-route_template.yml"
                sh "kubectl apply -f bookservice-istio-route_template.yml"

                sh "kubectl delete ${previousVersionName}"
            }
        
        }
    }
    post { 
        always { 
            cleanWs()
        }
    }
}