package com.hrjk.fin.saga.demo.booking;

// import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// @FeignClient("flux-car")
public interface CarClient{
	@RequestMapping(method = RequestMethod.GET, value = "/bookings")
	Flux<CarBooking> getAllCars();

	@RequestMapping(method = RequestMethod.POST, value = "/order/{name}/{cars}", consumes = "application/json")
	Mono<CarBooking> orderCars(@PathVariable("name") String name, @PathVariable("cars") Integer cars);

}
