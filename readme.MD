Simple static http server.





Compiled using command:



mvn clean package



Started using:



java -jar target/httpserver.jar [JSON CONFIG FILE]





for example:

java -jar target/httpserver.jar config.json


If config is omited following params are used:

port: 8081

cahced: true

charset: UTF-8

root:"./resources"
