package com.example.demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@EnableZuulProxy
@EnableCircuitBreaker
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationClientApplication {
    /*@Bean
    @LoadBalanced   //add this anotation to fix the problem that "service id(spring.application.name) can not be recognized"
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    CommandLineRunner rt(RestTemplate restTemplate) {
        return args -> {
            ParameterizedTypeReference<List<Reservation>> ptr
                    = new ParameterizedTypeReference<List<Reservation>>() {
            };

            List<Reservation> reservations = restTemplate.exchange(
                    "http://reservation-service/reservations",
                    HttpMethod.GET, null, ptr).getBody();

            reservations.forEach(x->{System.out.println(x.getName());});
        };
    }

    @Bean
    CommandLineRunner dc(DiscoveryClient dc) {
        return args ->
                dc.getInstances("reservation-service")
                        .forEach(si -> System.out.println(
                                si.getHost() + ':' + si.getPort()));
    }


    @Bean
    CommandLineRunner feign(ReservationRestClient client) {
        return args ->
                client.getReservations().forEach(System.out::println);
    }*/

    public static void main(String[] args) {
        SpringApplication.run(ReservationClientApplication.class, args);
    }
}

@FeignClient("reservation-service")
interface ReservationRestClient {
    @RequestMapping(value = "/reservations", method = RequestMethod.GET)
    Collection<Reservation> getReservations();
}

@Component
class ReservationIntegration {

    @Autowired
    private ReservationRestClient reservationRestClient;

    Collection<String> getReservationsNames() {
        return reservationRestClient.getReservations().stream().map(Reservation::getName).collect(Collectors.toList());
    }
}

@RestController
@RequestMapping("/reservations")
class ReservationApiGatewayRestController {
    //    @Autowired
//    private RestTemplate restTemplate;

    /*@Autowired
    private RestTemp RestTemp;*/
    @Autowired
    private ReservationIntegration reservationIntegration;

    private Collection<String> oops() {
        return new ArrayList<String>(){
            {
                add("oops...");
                add("something wrong...");
            }};
    }

    @HystrixCommand(fallbackMethod = "oops")
    @RequestMapping(method = RequestMethod.GET, value = "/names")
    public Collection<String> getResrvationNames() {
        /*ParameterizedTypeReference<List<Reservation>> ptr
                = new ParameterizedTypeReference<List<Reservation>>() {
        };
        List<Reservation> reservations = RestTemp.restTemplate.exchange(
//                "http://localhost:8777/reservations",
                "http://reservation-service/reservations",
                HttpMethod.GET, null, ptr).getBody();

        return reservations.stream().map(Reservation::getName).collect(Collectors.toList());*/

        //=======================上面是老的方式===============================
        return reservationIntegration.getReservationsNames();

    }
}

class Reservation {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

//@Component  //使用hystrix circuit breaker替代fallbackprovider
class MyFallbackProvider implements FallbackProvider {

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public ClientHttpResponse fallbackResponse(final Throwable cause) {
        if (cause instanceof HystrixTimeoutException) {
            return response(HttpStatus.GATEWAY_TIMEOUT);
        } else {
            return fallbackResponse();
        }
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return response(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ClientHttpResponse response(final HttpStatus status) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return status;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return status.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return status.getReasonPhrase();
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("sevice-down".getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}