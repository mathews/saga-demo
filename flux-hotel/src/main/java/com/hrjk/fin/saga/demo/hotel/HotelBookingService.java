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
class HotelBookingService {
	private Map<Integer, HotelBooking> bookings = new ConcurrentHashMap<>();

	@Compensable(compensationMethod = "cancel")
	Mono<Void> order(TransactionContext context, HotelBooking booking) {
		if (booking.getAmount() > 2) {
			log.error("booking amount is greater than two, will throw IllegalArgumentException!bookingId = {}",
					booking.getId());
			throw new IllegalArgumentException("can not order the rooms large than two");
		}
		if (booking.getAmount() < 0) {
			return Mono.error(new IllegalArgumentException("The rooms order quantity must be greater than 0"));
		}
		booking.confirm();
		bookings.put(booking.getId(), booking);
		log.debug("order hotel successfully!");
		return Mono.empty();
	}

	Mono<Void> cancel(TransactionContext context, HotelBooking booking) {
		Integer id = booking.getId();
		if (bookings.containsKey(id)) {
			bookings.get(id).cancel();
			log.warn("order is canceled, bookingId = {}", id);
		}
		return Mono.empty();
	}

	Flux<HotelBooking> getAllBookings() {
		return Flux.fromIterable(bookings.values());
	}

	Mono<Void> clearAllBookings() {
		bookings.clear();
		return Mono.empty();
	}
}
