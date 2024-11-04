pipeline {
    agent any

    stages {
        stage('Checkout GIT') {
            steps {
                echo 'Pulling...'
                git branch: 'Mariem', url: 'https://github.com/ikramdhib/StationSki5SAE2Devops.git', credentialsId: 'GitHub-PAT-Jenkins'
            }
        }

        stage('Build') {
            steps {
                echo 'Building with Maven...'
                sh 'mvn compile'
            }
        }

        stage('Testing ') {
            steps {
                echo 'Testing with Maven...'
                sh 'mvn test'
            }
        }


       stage('sonarQube') {
                   steps {
                     withCredentials([usernamePassword(credentialsId: 'Sonar_Credential', usernameVariable: 'SONAR_USER', passwordVariable: 'SONAR_PASS')]) {
                           sh 'mvn sonar:sonar -Dsonar.login=$SONAR_USER -Dsonar.password=$SONAR_PASS'
                       }
                   }
               }



           stage('Deploy SNAPSHOT') {
                      steps {
                              sh 'mvn deploy '
                      }
               }


       stage('Docker Build') {
                 steps {
                     echo 'Building Docker image...'
                     script {
                         sh 'docker build -t maryemsebeii/managerstationski:1.0 .'
                     }
                 }
             }

          stage('Docker Repository Creation') {
              steps {
                  withCredentials([usernamePassword(credentialsId: 'Docker-Credential', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                      script {
                          echo 'Checking if Docker repository exists...'
                          def response = sh(script: "curl -s -u $DOCKER_USERNAME:$DOCKER_PASSWORD https://hub.docker.com/v2/repositories/maryemsebei/managerstationski/", returnStdout: true)
                          if (response.contains('404')) {
                              echo 'Repository does not exist, creating now...'
                              def createResponse = sh(script: "curl -s -X POST -u $DOCKER_USERNAME:$DOCKER_PASSWORD https://hub.docker.com/v2/repositories/maryemsebei/managerstationski/", returnStdout: true)
                              echo "Repository creation response: ${createResponse}"
                          } else {
                              echo 'Repository already exists.'
                          }
                      }
                  }
              }
          }

                   stage('Docker Push') {
                       steps {
                           withCredentials([usernamePassword(credentialsId: 'Docker-Credential', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                               script {
                                   echo 'Logging in to Docker Hub...'
                                   sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USERNAME --password-stdin"
                                   echo 'Pushing Docker image...'
                                   sh 'docker push maryemsebeii/managerstationski:1.0'
                               }
                           }
                       }
                   }




    }



    post {
        always {
            echo 'Cleaning up...'
            sh 'docker container prune -f'
            sh 'docker image prune -f'
        }
    }
}
