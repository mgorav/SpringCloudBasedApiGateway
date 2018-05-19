# Spring Cloud API Gateway/Edge Service Example

Service Mesh & API Gateway plays a very important part in compositional microservices architecture. In good old days ESB/Application server were used to provide abilities to implement horizontal concerns like circuit breaker, proxying, security, caching (ETAG), client side load balancing etc. Horizontal concerns should be implemented in Service Mesh/API Gateway rather than in each microservice.  Developers should develop business logic in microservice without bothering about horizontal concerns. Checkout Spring Cloud Gateway framework based sample edge service which demonstrates implementation of following concepts:
1. Proxying - using url rewriting, pattern matching & replacing
2. Circuit breaker - HystrixSlowCommand breaks the circuit if the service is slow
3. Security - spring security


## Pre-requisite

1. Setup [jpa-eclipselink](https://github.com/mgorav/jpa-eclipselink) and install jar in local repository:
```
mvn clean install 
```

2. Setup [mocker](https://github.com/mgorav/Mocker) and install jar in local repository:
```
mvn clean install 
```

3. Download & run [SpringCloudApiGateway](https://github.com/mgorav/SpringCloudBasedApiGateway)

```
git clone git@github.com:mgorav/SpringCloudBasedApiGateway.git .

mvn clean install

mvn spring-boot run
```

## Setup Data for gateway

**NOTE** Following command uses [httpie](https://httpie.org) for calling HTTP APIs

1. Call to mocker running on 8090 port for recording following GET request

```
http :8090/mocker/data/2.5/weather?q='delhi,ind&appid=b6907d289e10d714a6e88b30761fae22'
```

2. Call API Gateway running on 9090 port to retrieve above recorded request

```
http :9090/getmockerscenario/?url='http%3A%2F%2Fsamples.openweathermap.org%2Fdata%2F2.5%2Fweather%3Fq%3Ddelhi%2Cind%26appid%3Db6907d289e10d714a6e88b30761fae22'
```

#### Expected output

``` xml
HTTP/1.1 200 OK
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Type: application/json
Date: Sat, 12 May 2018 14:11:59 GMT
Expires: 0
Pragma: no-cache
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1 ; mode=block
transfer-encoding: chunked

{
    "httpHeaders": "{host=[localhost:8090], accept=[text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8], upgrade-insecure-requests=[1], cookie=[JSESSIONID.00f59fcc=node05p9x2wen0bg8ghilh3tuflhc2.node0; _ga=GA1.1.1917776611.1522441586; iconSize=32x32; jenkins-timestamper-offset=-7200000], user-agent=[Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.1 Safari/603.1.30], accept-language=[en-us], accept-encoding=[gzip, deflate], connection=[keep-alive]}",
    "httpMethod": "GET",
    "id": 1,
    "links": [
        {
            "href": "http:/localhost:8090/mocker/view/scenario/servicerequestresponse1",
            "rel": "self"
        }
    ],
    "protcol": "http",
    "requestHashKey": "D41D8CD98F00B204E9800998ECF8427E",
    "response": "{\"coord\":{\"lon\":77.22,\"lat\":28.65},\"weather\":[{\"id\":721,\"main\":\"Haze\",\"description\":\"haze\",\"icon\":\"50d\"}],\"base\":\"stations\",\"main\":{\"temp\":38,\"pressure\":1002,\"humidity\":25,\"temp_min\":38,\"temp_max\":38},\"visibility\":4000,\"wind\":{\"speed\":12.9,\"deg\":180,\"gust\":18},\"clouds\":{\"all\":75},\"dt\":1526122800,\"sys\":{\"type\":1,\"id\":7809,\"message\":0.0091,\"country\":\"IN\",\"sunrise\":1526083322,\"sunset\":1526131995},\"id\":1273294,\"name\":\"Delhi\",\"cod\":200}",
    "status": "200",
    "url": "http://samples.openweathermap.org/data/2.5/weather?q=delhi,ind&appid=b6907d289e10d714a6e88b30761fae22"
}
```

3. Call API Gateway to demonstrate circuit breaker. In order to do so for demo propose change following property in 
application.yml. This means if a call to mocker takes more than 1 milliseconds, http://localhost:8090/hystrixfallback
will be called. 

``` yaml
hystrix:
  command:
    HystrixSlowCommand:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 1
```

**NOTE** default timeoutInMilliseconds is 5000

Expected output
```
HTTP/1.1 200 OK
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Length: 36
Content-Type: text/plain;charset=UTF-8
Expires: 0
Pragma: no-cache
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1 ; mode=block

This is a gateway fallback situation
```
