package com.example.turbinestreamserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.turbine.stream.EnableTurbineStream;
import org.springframework.context.annotation.Configuration;

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
