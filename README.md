# Quick Demo
This application can be deployed in a traditional way, 
but it is recommended using docker for production deployment because of the complexity of 
dependencies of a multi-resolution tiles generator. Please refer to the example below
of deploy it using docker on a fresh installed Ubuntu 16.04.
## Use the web application only
### Requirements
1. jre8
2. docker-ce
```
docker pull downhillski/pannellum-tour-maker
docker run -it -e SPRING_APPLICATION_JSON='{\
    "spring":{\
        "data":{"mongodb":{"uri":"mongodb://localhost:27017/panorama-test"}}, \
        "rabbitmq": {"host": "localhost", "port": "5672", "username": "guest", "password": "guest"}}\
    "application": {"baseUrl": "http://52.224.165.47"}}' \
    --name tour-editor-pull \
    downhillski/pannellum-tour-maker
```

## Deploy the full solution
### Requirements
1. jdk8
1. maven
2. docker-ce
3. docker-compose

```
sudo apt-get -y install default-jdk maven
```
```
sudo apt-get update
sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
sudo apt-get update
sudo apt-get install docker-ce=17.12.0~ce-0~ubuntu
```
```
sudo curl -L https://github.com/docker/compose/releases/download/1.18.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### Build the jar
```
mvn clean package -Dmaven.test.skip=true
```
### Running the services
```
docker-compose up -d
```
### Tear down the services
```
docker-compose down
```


# Local Development Dependency
### RabbitMQ
```
docker run -d --hostname localhost --name my-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:management
```
### MongoDB
```
docker run -d --name my-mongo -p 27017:27017 mongo
```

# Big To Do
1. Add Security
2. provide a way to save files from application directory to externally mounted storage, such as Google Cloud Storage and AWS S3 Storage