# Programmatic Guaranteed Dimension Value API Service

The Dimension Value API Service is responsible for providing RESTful interfaces to provision and query PG targeting attribute values.
The query service is typically consumed by a UI, and the upload service is used from a CLI.

# Getting Started

## Technical stack
- CentOS 7.3
- Java 8
- Spring Boot
- MySQL
- Lombok
- JUnit5, Mockito, H2
- Maven

The server responds to several HTTP [server endpoints](docs/server_endpoints.md) 

## Building

To build the project, you will need 
[Java 8 JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
and [Maven](https://maven.apache.org/) installed.

To verify the installed Java run in console:
```bash
java -version
```
which should show something like (yours may be different but must show 1.8):
```
java version "1.8.0_241"
Java(TM) SE Runtime Environment (build 1.8.0_241-b07)
Java HotSpot(TM) 64-Bit Server VM (build 25.241-b07, mixed mode)
```

Follow next steps to create JAR which can be deployed locally. 
- Download or clone a project and checkout master
```bash
git clone https://github.rp-core.com/ContainerTag/pg-dim-val-api.git
```

- Move to project directory:
```bash
cd pg-dim-val-api
```

- Run below command to build project:
```bash
mvn clean verify package
```

## Configuration

Configuration is handled by [Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html), 
which supports properties files, YAML files, environment variables and command-line arguments for setting config values.

The server requires a MySQL database version 5.7.x (x being 23 and up) to be available with the [dva schema](sql/pg-dva-init-db.sql) created.

The source code includes minimal required configuration file `src/main/resources/application.yaml`.
These properties can be extended or modified with external configuration file.

For example, `dva-config.yaml`:
```yaml
server:
  port: 6281
```
For properties not specified or overriden in `dva-config.yaml`, application will look for default settings  in `src/main/resources/application.yaml` file.

To use external application configuration just add the following as start up arguments:
```bash
--spring.config.additional-location=/path/to/dva-config.yaml
```

Details on all available configuration tags can be found in [configuration document](docs/config-app.md)

## Running

The project build has been tested at runtime with a Java 8 runtime. 
Run your local server (you may need to point to active remote URLs and a valid database schema to avoid seeing errors in the logs) with a command similar to:
```bash
java -jar target/pg-dim-val-api.jar --spring.config.additional-location=sample/dva-config.yaml 
```

## Basic check

Go to [http://localhost:8080/status/health](http://localhost:8080/status/health) 
and verify that response status is `200`.


## Code Style

The [pom.xml](pom.xml) is configured to enforce a coding style defined in [checkstyle.xml](checkstyle.xml).

The intent here is to maintain a common style across the project and rely on the process to enforce it instead of individuals.
