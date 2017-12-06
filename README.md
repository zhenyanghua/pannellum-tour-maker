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
2. provide a UI to supply bearing from client.
7. provide a backend service and UI to upload zipped tour to an existing tour.
8. provide a backend service and UI to remove scenes and their related multi-res images from the server.
9. Add Security
10. docker compose it.
11. provide a way to save files from application directory to externally mounted storage, such as Google Cloud Storage and AWS S3 Storage