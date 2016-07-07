# TLS Controller

This project is targeted to build a controller to dynamically manage the memory allocate for native code execution and 
storage.

## Building

You build the project using:

```
mvn clean package
```

## Testing

The application is tested using [vertx-unit](http://vertx.io/docs/vertx-unit/java/).

## Packaging

The application is packaged as a _fat jar_, using the 
[Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/).

## Running

Once packaged, just launch the _fat jar_ as follows:

```
java -jar target/my-first-app-1.0-SNAPSHOT-fat.jar
```

Then, produce message to kafka and receive result at vertx. 

