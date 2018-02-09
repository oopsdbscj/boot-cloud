package com.example.turbinestreamserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;
import org.springframework.cloud.netflix.turbine.stream.TurbineStreamConfiguration;
import org.springframework.cloud.netflix.turbine.stream.TurbineStreamProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rx.subjects.PublishSubject;

@Configuration
@SpringBootApplication
@EnableTurbineStream
//@EnableHystrix
//@EnableHystrixDashboard
public class TurbineStreamServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurbineStreamServerApplication.class, args);
	}
}
