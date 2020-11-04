package com.hrjk.fin.saga.demo.booking;

// import org.apache.servicecomb.pack.omega.transport.feign.FeignAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
// import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.hrjk.fin.saga.demo.booking")
@EnableDiscoveryClient
@EnableTransactionManagement
// @EnableScheduling
// @EnableFeignClients
@RefreshScope
public class FluxBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(FluxBookingApplication.class, args);
	}

}
