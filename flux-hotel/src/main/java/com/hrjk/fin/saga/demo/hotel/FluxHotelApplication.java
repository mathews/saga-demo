package com.hrjk.fin.saga.demo.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.hrjk.fin.saga.demo.hotel")
@EnableDiscoveryClient
@EnableTransactionManagement
// @EnableScheduling
@RefreshScope
public class FluxHotelApplication {

	public static void main(String[] args) {
		SpringApplication.run(FluxHotelApplication.class, args);
	}

}
