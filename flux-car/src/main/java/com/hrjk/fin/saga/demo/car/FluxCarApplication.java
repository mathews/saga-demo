package com.hrjk.fin.saga.demo.car;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.hrjk.fin.saga.demo.car")
@EnableDiscoveryClient
@EnableTransactionManagement
// @EnableScheduling
@RefreshScope
public class FluxCarApplication {

	public static void main(String[] args) {
		SpringApplication.run(FluxCarApplication.class, args);
	}

}
