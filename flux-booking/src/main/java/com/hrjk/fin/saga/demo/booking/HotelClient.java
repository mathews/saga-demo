package com.hrjk.fin.saga.demo.booking;

// import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// @FeignClient("flux-hotel")
public interface HotelClient {
	@RequestMapping(method = RequestMethod.GET, value = "/bookings")
	Flux<HotelBooking> getAllCars();

	@RequestMapping(method = RequestMethod.POST, value = "/order/{name}/{rooms}", consumes = "application/json")
	Mono<HotelBooking> orderHotels(@PathVariable("name") String name, @PathVariable("rooms") Integer rooms);

}
