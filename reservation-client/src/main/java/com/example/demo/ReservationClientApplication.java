package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
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
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationClientApplication.class, args);
	}
}
@Component
class RestTemp{
    public RestTemplate restTemplate = new RestTemplate();
}
@RestController
@RequestMapping("/reservations")
class ReservationApiGatewayRestController{
//    @Autowired
//    private RestTemplate restTemplate;
    @Autowired
    private RestTemp RestTemp;

    @RequestMapping(method = RequestMethod.GET,value = "/names")
    public Collection<String> getResrvationNames(){
        ParameterizedTypeReference<List<Reservation>> ptr
                = new ParameterizedTypeReference<List<Reservation>>() {};
        List<Reservation> reservations = RestTemp.restTemplate.exchange(
//                "http://localhost:8777/reservations",
                "http://reservation-service/reservations",
                HttpMethod.GET, null, ptr).getBody();

        return reservations.stream().map(Reservation::getName).collect(Collectors.toList());

    }
}
class Reservation{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}