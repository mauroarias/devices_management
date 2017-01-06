# Platform-HW
This is a simple example of Spring and other interesting frameworks/libs. It is based on spring microservice, 
implementing two simple APIs: device & users. Access to Mysql DB using JDBC connector & Hikari Datasource, 
Exception Handling, Beans, Lombok, Spotify Docker client, immutable objects, Junit, Mock classes, config and more.

`Please note that some component of this project should be compiled in another library to avoid dependency problems.
But to simplify all these components are part of the a simple and unique project.` 

## Requirements
* java 8
* Maven 3

## What next

* add Oauth2.
* add Integration test using docker.
* add hibernate
* add support to Cassandra, MongoDB and Elasticsearc DBs.
* add support to kafka.
* add logs/traces.
* create new SDK projects.

## TESTS

### Unit test
This project includes unit tests of the some business class, using Junit and mockito frameworks.
please see ../src/test/java/org/etcsoft/devicemanagement to find more details

### Mysql integration test
This project includes Mysql Integration test to test all functionaries of the project. 
It uses Spotify dockerClient, Junit, Hikari framework and some other stubs,
please see ../src/test/java/org/etcsoft/devicemanagement/it/repository to find more details.

Note that this test needs Docker (at least version 1.9.1, the only version tested), the DOCKER_HOST environment variable 
defined and the containers `mysql/mysql-server`.

The Mysql IT flow is:
* Starts mysql containers mysql
* Load a simple schema in the mysql container.
* run test on all Mysql business classes.
* Clean up everything.

## COMPILING AND RUNNING TESTS

### To compile and run unit tests:

```
mvn clean package
```

## API

### Device

* POST http://localhost:8080/deviceManagement/devices: add a new device.
* PUT http://localhost:8080/deviceManagement/devices/{deviceId}: update an existing device.
* GET http://localhost:8080/deviceManagement/devices/{deviceId}: get an existing device.
* DELETE http://localhost:8080/deviceManagement/devices/{deviceId}: delete an existing device.
* PUT http://localhost:8080/deviceManagement/devices/{deviceId}/user/{username}: link an existing device with an
existing user.
* DELETE http://localhost:8080/deviceManagement/devices/{deviceId}/user/{username}: unlink an existing device from an
existing user.

### user

* POST http://localhost:8080/deviceManagement/user: add a new user.
* PUT http://localhost:8080/deviceManagement/user/{deviceId}: update an existing user.
* PUT http://localhost:8080/deviceManagement/user/{deviceId}/password: update the password of an existing user.
* GET http://localhost:8080/deviceManagement/user/{deviceId}: get an existing user.
* DELETE http://localhost:8080/deviceManagement/user/{deviceId}: delete an existing user.
* PUT http://localhost:8080/deviceManagement/user/{username}/user/{deviceId}: link an existing user with an
existing device.
* DELETE http://localhost:8080/deviceManagement/user/{username}/user/{deviceId}: unlink an existing user from an
existing device.
