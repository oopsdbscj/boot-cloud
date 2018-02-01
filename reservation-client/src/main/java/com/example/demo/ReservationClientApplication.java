package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@EnableZuulProxy
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationClientApplication {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    CommandLineRunner dc(DiscoveryClient dc) {
        return args ->
                dc.getInstances("reservation-service")
                        .forEach(si -> System.out.println(
                                si.getHost() + ':' + si.getPort()));
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
    CommandLineRunner feign(ReservationRestClient client) {
        return args ->
                client.getReservations().forEach(System.out::println);
    }

    public static void main(String[] args) {
        SpringApplication.run(ReservationClientApplication.class, args);
    }
}

@Component
class RestTemp {
    public RestTemplate restTemplate = new RestTemplate();
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