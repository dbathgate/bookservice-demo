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
                script {

                    env.PREVIOUS_VERSION_NUMBER = sh (
                        script: 'kubectl get deployments -l app=bookservice -o json | jq ".items[].spec.template.metadata.labels.version" | sed "s/\\"//g"',
                        returnStdout: true
                    ).trim()

                    env.PREVIOUS_VERSION_NAME = sh (
                        script: "kubectl get deployments -l app=bookservice,version=${env.PREVIOUS_VERSION_NUMBER} -o name",
                        returnStdout: true
                    ).trim()

                }

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
                    def finishDeployment = false

                    while(!finishDeployment) {

                        userInput = input(
                            id: 'Proceed', message: 'Proceed Deployment?', parameters: [
                            [$class: 'TextParameterDefinition', defaultValue: "0", description: '', name: 'Increase Traffic?'],
                            [$class: 'BooleanParameterDefinition', defaultValue: false, description: '', name: 'Proceed Deployment?'],
                            [$class: 'BooleanParameterDefinition', defaultValue: false, description: '', name: 'Rollback Deployment?']
                        ])

                        finishDeployment = userInput["Proceed Deployment?"] || userInput["Rollback Deployment?"]
                        def rollbackDeployment = userInput["Rollback Deployment?"]
                        def greenPercentage = userInput["Increase Traffic?"].toInteger()
                        def bluePercentage = 100 - greenPercentage;

                        if (!finishDeployment) {
                            sh "cp bookservice-canary_template.yml bookservice-canary.yml"
                            sh "sed -i \"s/%%BLUE_VERSION%%/${env.PREVIOUS_VERSION_NUMBER}/g\" bookservice-canary.yml"
                            sh "sed -i \"s/%%GREEN_VERSION%%/${env.BUILD_ID}/g\" bookservice-canary.yml"
                            sh "sed -i \"s/%%BLUE_PERCENT%%/${bluePercentage}/g\" bookservice-canary.yml"
                            sh "sed -i \"s/%%GREEN_PERCENT%%/${greenPercentage}/g\" bookservice-canary.yml"
                            sh "kubectl apply -f bookservice-canary.yml"
                        }
                        env.PROCEED_DEPLOYMENT = userInput["Proceed Deployment?"]
                        env.ROLLBACK_DEPLOYMENT = userInput["Rollback Deployment?"]
                    }
                }
            }
        
        }
        stage('Proceed Deployment') {
            when{ 
                expression {
                    env.VERSION_COUNT.toInteger() > 0 && env.PROCEED_DEPLOYMENT == "true"
                }
            }
            steps {
                sh "sed -i \"s/%%BUILD_NUMBER%%/${env.BUILD_ID}/g\" bookservice-istio-route_template.yml"
                sh "kubectl apply -f bookservice-istio-route_template.yml"

                sh "kubectl delete ${env.PREVIOUS_VERSION_NAME}"
            }
        }

        stage('Rollback Deployment') {
            when{ 
                expression {
                    env.VERSION_COUNT.toInteger() > 0 && env.ROLLBACK_DEPLOYMENT == "true"
                }
            }
            steps {
                sh "sed -i \"s/%%BUILD_NUMBER%%/${env.PREVIOUS_VERSION_NUMBER}/g\" bookservice-istio-route_template.yml"
                sh "kubectl apply -f bookservice-istio-route_template.yml"

                sh "kubectl delete deployment bookservice-${env.BUILD_ID}"
            }
        }
    }
    post { 
        always { 
            cleanWs()
        }
    }
}