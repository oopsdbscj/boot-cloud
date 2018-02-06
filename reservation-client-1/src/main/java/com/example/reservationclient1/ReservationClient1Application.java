package com.example.reservationclient1;

import com.netflix.discovery.converters.Auto;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@EnableFeignClients
@EnableHystrix
@EnableHystrixDashboard
@EnableDiscoveryClient
@EnableCircuitBreaker
@SpringBootApplication
public class ReservationClient1Application {

	public static void main(String[] args) {
		SpringApplication.run(ReservationClient1Application.class, args);
	}
}


@FeignClient("reservation-service")
interface ReservationRestClient1{

    @RequestMapping("/reservations")
    public Collection<Resesrvation> getReseravations();
}

@Component
class ReservationIntegration {
    @Autowired
    private ReservationRestClient1 reservationRestClient1;

    public Collection<String> getReservationNames() {
        return reservationRestClient1.getReseravations().stream().map(Resesrvation::getName).collect(Collectors.toList());
    }
}

@RestController
@RequestMapping("/reservations1/")
class ReservationNamesRestController {
    @Autowired
    private ReservationIntegration reservationIntegration;

    public Collection<String> oops() {

        return new ArrayList<String>(){{add("oops");
                                        add("error");
        }};
    }

    @HystrixCommand(fallbackMethod = "oops")
    @RequestMapping("names1")
    public Collection<String> reservationNames() {
        return reservationIntegration.getReservationNames();
    }

}
class Resesrvation {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
