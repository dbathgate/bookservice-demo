pipeline {
    agent any

    // def previousVersion = sh (
    //     script: 'kubectl get deployments -l app=bookservice -o json | jq ".items[].spec.template.metadata.labels.version" | sed "s/\"//g"',
    //     returnStdOut: true
    // ).trim()

    // def previousVersionName = sh (
    //     script: 'kubectl get deployments -l app=bookservice -o name',
    //     returnStdOut: true
    // ).trim()

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

            when {
                expression {
                    sh (
                        script: 'kubectl get deployments -l app=bookservice | wc -l',
                        returnStdOut: true
                    ).trim() == 0
                } steps {
                    sh "sed \"s/%%BUILD_NUMBER%%/${env.BUILD_ID}/g\" bookservice-istio-route_template.yml"
                    sh "kubectl apply -f bookservice-istio-route_template.yml"
                    return
                }
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