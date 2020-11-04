/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hrjk.fin.saga.demo.hotel;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.servicecomb.pack.omega.context.OmegaContext;
import org.apache.servicecomb.pack.omega.context.TransactionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class HotelBookingController {

	private static Logger log = LoggerFactory.getLogger(HotelBookingController.class);

	@Autowired
	private HotelBookingService service;

	private final AtomicInteger id = new AtomicInteger(0);

	@CrossOrigin
	@GetMapping("/bookings")
	Flux<HotelBooking> getAll() {
		return service.getAllBookings();
	}

	@PostMapping("/order/{name}/{rooms}")
	Mono<?> order(
			@RequestHeader(value = OmegaContext.GLOBAL_TX_ID_KEY, required = false, defaultValue = "") String globalTxId,
//			@RequestHeader(value = OmegaContext.LOCAL_TX_ID_KEY, required = false, defaultValue = "") String localTxId,
			@PathVariable String name, @PathVariable Integer rooms) {

		TransactionContext context = null;
		if (!globalTxId.equals("")) {
			context = new TransactionContext(globalTxId, null);
			log.info("HotelBooking with globalTxId {} and localTxId {}", globalTxId, null);
		}else{
			log.warn("HotelBooking without SAGA globalTxId!");
		}
		HotelBooking booking = new HotelBooking();
		booking.setId(id.incrementAndGet());
		booking.setName(name);
		booking.setAmount(rooms);
		return service.order(context, booking).onErrorResume(e -> Mono.error(e)).thenReturn(Mono.just(booking));
	}

	@DeleteMapping("/bookings")
	Mono<Void> clear() {
		service.clearAllBookings();
		id.set(0);
		return Mono.empty();
	}
}
