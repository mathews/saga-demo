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

package com.hrjk.fin.saga.demo.car;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.servicecomb.pack.omega.context.TransactionContext;
import org.apache.servicecomb.pack.omega.transaction.annotations.Compensable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Slf4j
@Service
class CarBookingService {
	private Map<Integer, CarBooking> bookings = new ConcurrentHashMap<>();

	@Compensable(compensationMethod = "cancel")
	Mono<Void> order(TransactionContext context, CarBooking booking) throws Exception{
		if (booking.getAmount() > 10) {
			log.error("car booking amount is greater than 10, will throw exception!	bookID = {}", booking.getId());
			throw new IllegalArgumentException("can not order the cars large than ten");
		}
		if (booking.getAmount() < 0) {
			return Mono.error(new IllegalArgumentException("The cars order quantity must be greater than 0"));
		}
		
		booking.confirm();
		bookings.put(booking.getId(), booking);
		log.debug("order car successful!");
		return Mono.empty();
	}

	Mono<Void> cancel(TransactionContext context, CarBooking booking) {
		Integer id = booking.getId();
		if (bookings.containsKey(id)) {
			bookings.get(id).cancel();
			log.warn("car booking is canceled, id={}",id);
		}
		// Just sleep a while to ensure the Compensated event is after ordering TxAbort
		// event
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// Just ignore the exception
		}
		return Mono.empty();
	}

	Flux<CarBooking> getAllBookings() {
		return Flux.fromIterable(bookings.values());
	}

	Mono<Void>  clearAllBookings() {
		bookings.clear();
		return Mono.empty();
	}
}
