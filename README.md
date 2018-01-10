# Quick Demo
This application can be deployed in a traditional way, 
but it is recommended using docker for production deployment because of the complexity of 
dependencies of a multi-resolution tiles generator. Please refer to the example below
of deploy it using docker on a fresh installed Ubuntu 16.04.
## Use the web application only
### Requirements
1. docker-ce
2. MongoDB 3
3. RabbitMQ 3

Pull the latest tour editor software
```
docker pull downhillski/pannellum-tour-maker
```
Run the docker image. 

- `-e` specifies environment variables for MongoDB and RabbitMQ service information

- `-v` mounts the host directory (`/data`, create one if it does not exist, 
if can be anywhere in your host file system ) to the container (`/home/pannellum-tour-maker`, 
the container data location should be the same in the `application.yml`, in most cases, 
there is no need to change this setting.)
to persist all uploaded resources.
- `--name` specifies the container name
- `-p` map requests to host port 80 to container port 80.
- `-d` run the image in detached mode.
 
```
docker run -it -e "SPRING_DATA_MONGODB_URI=mongodb://172.17.0.3:27017/panorama" \
               -e "SPRING_RABBITMQ_HOST=172.17.0.4" \
               -e "SPRING_RABBITMQ_PORT=5672" \
               -e "SPRING_RABBITMQ_USERNAME=guest" \
               -e "SPRING_RABBITMQ_PASSWORD=guest" \
               -e "APPLICATION_BASEURL=http://tour-editor.eastus.cloudapp.azure.com" \
    -v /data:/home/pannellum-tour-maker \
    --name tour-editor \
    -p 80:80 \
    -d \
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
mvn clean package
```
### Running the services
```
docker-compose up -d
```
### Tear down the services
```
docker-compose down
```


# Resources
Here shows how to setup the RabbitMQ and MongoDB using docker
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