## External Dependency
### RabbitMQ
```
docker run -d --hostname localhost --name my-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3
docker exec -it my-rabbit rabbitmq-plugins enable rabbitmq_management
```
### MongoDB
```
docker run -d --name my-mongo -p 27017:27017 mongo
```