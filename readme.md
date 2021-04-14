# spring gateway server cannot get response from upsteam server when gateway server protected witeh csrf and request content type is application/x-www-form-urlencoded

there are two simple server.
## the 1st is gateway server.


it's only import gateway and security.
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```
and it's security config like this. they are very simple. so i write them in the same class
```java
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange().anyExchange().permitAll();
        http.csrf().csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse());
        return http.build();
    }
    /**
     * add csrf cookie response on every response
     * @return
     */
    @Bean
    public WebFilter addCsrfTokenFilter() {
        return (exchange, next) -> Mono.just(exchange)
                .flatMap(ex -> ex.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName()))
                .doOnNext(ex -> {
                })
                .then(next.filter(exchange));
    }
}
```
the gateway routes
```yml
spring:
  cloud:
    gateway:
      routes:
        - id: test
          uri: http://localhost:8888
          predicates:
            - Path=/test/**
          filters:
            - StripPrefix=1
```
# the 2nd is an upstream server
it is a simple web server
```java
@SpringBootApplication
@RestController
public class UpstreamApplication {


    @PostMapping("/test")
    public String postData(Dto dto) {
        return "post success";
    }

    public static void main(String[] args) {
        SpringApplication.run(UpstreamApplication.class, args);
    }

}
```
the dto class has 2 properties a and b
```java
public class Dto {
    private String a;
    private String b;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }
}
```
and i config the server start on port 8888
```yml
server:
  port: 8888
```
and then ,i use post man to test them.
the 1st time ,i have no csrf value.
```
curl --location --request POST 'http://localhost:8080/test/test' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'a=1' \
--data-urlencode 'b=2'
```
the server response the invalidate csrf token exception.
and then i get csrf token value from cookie values.
and then i send request with csrf header. 
```bash
curl --location --request POST 'http://localhost:8080/test/test' \
--header 'X-XSRF-TOKEN: f8db31f3-8be6-4103-8e15-5ef4594a08f0' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Cookie: XSRF-TOKEN=f8db31f3-8be6-4103-8e15-5ef4594a08f0' \
--data-urlencode 'a=1' \
--data-urlencode 'b=2'
```
the client is wait for response more than 3 minutes and not completed.

i test the other content type ,they are completed normal. 

if the upstream server's function params is empty or the client request params is empty. it can be completed.



