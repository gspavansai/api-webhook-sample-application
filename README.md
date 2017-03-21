api-webhook-sample-application 
==============

A sample application that showcases webhook endpoint implementation, which (1)receives the resourceId (Loan GUID) as part of the request payload, (2)OAuth authentication, (3)loan retrieval based on loan GUID received and (4)stores loan information in the flat file.

### Code Repository

https://github.com/EllieMae/api-webhook-sample-application

### Basic Usage

Authenticate user 
```java
Client client = new Client.Builder()
    .setBaseUrl(baseUrl)
    .setClientId(clientId)
    .setClientSecret(clientSecret)
    .setUsername(username)
    .setPassword(password)
    .build();
```

Retrieve loan
```java
String loanPath = baseUrl + "/encompass/v1/loanPipeline";
ResponseEntity<String> loanData = restTemplate
                    .exchange(loanPath, HttpMethod.POST, entity, String.class);
```

### Dependencies

There are two main dependencies (of course automatically managed by Maven):

Spring Boot: Provides structure to the application and reduces boilerplate.
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot</artifactId>
  <version>${spring-boot.version}</version>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-autoconfigure</artifactId>
  <version>${spring-boot.version}</version>
</dependency>
```

Spring Web: A synchronous HTTP client.
```xml    
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>${spring.version}</version>
</dependency>
```

Jackson Core: JSON parser and data processor -- translates JSON to objects.
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>${jackson-core.version}</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>${jackson-core.version}</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-annotations</artifactId>
    <version>${jackson-core.version}</version>
</dependency>
```

To run this application use the following arguments
```java
 -Dbase_url=https://domainname -Dclient_id=clientId -Dclient_secret=secret -Dusername=userName -Dpassword=password -Dpayload="loan schema to be retrieved" -Dsecret=secret
```
