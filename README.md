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
4. provide UI to import custom map image, e.g floor plan
5. design a UI for user to place location on the map if GPS data is missing from the Scene.
6. Build a graph of all connected scene on the floor plan
7. provide a backend service and UI to upload zipped tour to an existing tour.
8. provide a backend service and UI to remove scenes and their related multi-res images from the server.
9. Add Security
10. docker compose it.
11. provide a way to save files from application directory to externally mounted storage, such as Google Cloud Storage and AWS S3 Storage