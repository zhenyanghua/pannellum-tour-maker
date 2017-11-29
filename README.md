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

# Big To Do
1. provide north bearing field for Scene Object.
2. provide UI to supply bearing from client.
3. design a UI to calibrate bearing for each scene from client.
4. Add Security
5. docker compose it.
6. provide a way to save files from application directory to externally mounted storage, such as Google Cloud Storage and AWS S3 Storage