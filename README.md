# N26 Code Challenge

RESTful API for statistics.The main user case for the API is to calculate real time statistics from the past 60 seconds

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them

```
Java 8
Maven
```

### Installing

Clone the project and build the project with

```
mvn clean install
```

Start the application with

```
java -jar target/n26-0.0.1-SNAPSHOT.jar

```

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

```
mvn test
```

## Built With

* [SringBoot](https://projects.spring.io/spring-boot/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [Java 8](http://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html) - Programming language
* [Junit](http://junit.org/junit4/) - Test Framework
* [Mockito](http://site.mockito.org/) - Test Framework
* [RestAssured](http://rest-assured.io/) - Integration Test Framework

## Notes

### Constant time and memory, O(1) in GET /statistics
In order to make the GET /statistics execute in constant time and memory space,
I've chosen to calculate the statistic when POST /transaction is called, only concerning in
acquiring those statistics back when GET /statistics is called. To achieve O(1), [tailmap](https://docs.oracle.com/javase/7/docs/api/java/util/NavigableMap.html#tailMap(K)) is used
to retrieve only the statistics within allowed time interval (past 60 seconds). Once in the worst case, only 60 keys would be iterated to aggregate the statistics, time and memory are constant.

### Threadsafe and Concurrency
For concurrency, the map is an [ConcurrencySkipListMap](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ConcurrentSkipListMap.html) and every method in that access this map is synchronized.

### Unused Statistics
Every time after some method accessing this map, all obsolete statistics is removed with tailmap.
Since we use tailmap, It could throw an IllegalArgumentException -- This exception is thrown if this map itself has a restricted range, and fromKey lies outside the bounds of the range.
However, the only prevention is to check at the controller if the transaction time is within this range, later we could create an Exception and improve this treatment.

### Response POST /transactions
When POST /transactions is called, if there were no statistics with the transaction time created, we create it, and return a HttpStatus 201, CREATED
If the time is not in the allowed time interval, or there were already a statistic with the transaction time created, we return a HttpStatus 204, NO_CONTENT
