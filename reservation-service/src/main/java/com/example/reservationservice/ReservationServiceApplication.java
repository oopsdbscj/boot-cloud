package com.example.reservationservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Collection;
import java.util.stream.Stream;

@RestController
@Configuration
@EnableDiscoveryClient
@SpringBootApplication
public class ReservationServiceApplication {
    @Bean
    CommandLineRunner commandLineRunner(ReservationRepository rr) {
        return args -> {
            Stream.of("chen", "li", "zhang", "wang", "feng").forEach(name -> rr.save(new Reservation(name)));
            rr.findAll().forEach(reservation -> System.out.println(reservation));
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }
}

//@RefreshScope
@RestController
class ReservationRestcontroller {
    private ReservationRepository rr;

    public ReservationRestcontroller(ReservationRepository rr) {
        this.rr = rr;
    }

    @GetMapping(value = "/reservations", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Collection<Reservation> reservations() {
        return rr.findAll();
    }

}
@RefreshScope
@RestController
class MesageRestcontroller {
    @Value("${message}")
    private String name;

    @GetMapping("/message")
    String message() {
        return name;
    }
}

interface ReservationRepository extends JpaRepository<Reservation, Long> {
}

@Entity
class Reservation {
    @Id
    @GeneratedValue
    private int id;
    private String name;

    public Reservation() {
    }

    public Reservation(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}