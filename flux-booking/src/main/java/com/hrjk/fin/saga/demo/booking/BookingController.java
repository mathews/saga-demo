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

package com.hrjk.fin.saga.demo.booking;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

import com.esotericsoftware.minlog.Log;

import org.apache.servicecomb.pack.omega.context.OmegaContext;
import org.apache.servicecomb.pack.omega.context.annotations.SagaStart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
public class BookingController {

	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Value("${car.service.address:http://car.servicecomb.io:8881}")
	private String carServiceUrl;

	@Value("${hotel.service.address:http://hotel.servicecomb.io:8882}")
	private String hotelServiceUrl;

	// @Autowired
	// CarClient carClient;
	// @Autowired
	// HotelClient hotelClient;
	// @Autowired
	// RequestInterceptor requestInterceptor;

	private Mono<CarBooking> orderCars(String name, int cars, String globalTxId) {
		WebClient webClient = WebClient.create(carServiceUrl);
		return webClient.post().uri("/order/{name}/{cars}", name, cars)
				.header(OmegaContext.GLOBAL_TX_ID_KEY, globalTxId)
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(CarBooking.class);

	}

	private Mono<HotelBooking> orderHotels(String name, int rooms, String globalTxId) {

		WebClient webClient = WebClient.create(hotelServiceUrl);
		return webClient.post().uri("/order/{name}/{rooms}", name, rooms)
				.header(OmegaContext.GLOBAL_TX_ID_KEY, globalTxId)
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(HotelBooking.class);

	}

	@SagaStart
	@PostMapping("/booking/{name}/{rooms}/{cars}")
	public Mono<String> order(@PathVariable String name, @PathVariable Integer rooms, @PathVariable Integer cars)
			throws Throwable {

		Log.debug("going to do dirty jobs!");

		return Mono.deferWithContext(ctx -> {
			try {
				String globalTxId = ctx.get(OmegaContext.GLOBAL_TX_ID_KEY);
				String localTxId = ctx.get(OmegaContext.GLOBAL_TX_ID_KEY);
				LOG.info("BookingController got globalTxId {} and localTxId {} from Flux Context!", globalTxId,
						localTxId);

				// return carClient.orderCars(name, cars)
				// .subscriberContext(Context.of(OmegaContext.GLOBAL_TX_ID_KEY, globalTxId,
				// OmegaContext.GLOBAL_TX_ID_KEY, localTxId))
				// .and(postCarBooking()).and(hotelClient.orderHotels(name,
				// rooms)).and(postBooking())
				// .then(Mono.just(name + " booking " + rooms + " rooms and " + cars + " cars
				// OK"));

				return orderCars(name, cars, globalTxId)
						.and(postCarBooking()).and(orderHotels(name, rooms, globalTxId)).and(postBooking())
						.then(Mono.just(name + " booking " + rooms + " rooms and " + cars + " cars OK"));

			} catch (Throwable e) {
				return Mono.error(e);
			}
		});

	}

	// This method is used by the byteman to inject exception here
	private Mono<Void> postCarBooking() throws Throwable {
		return Mono.empty();

	}

	// This method is used by the byteman to inject the faults such as the timeout
	// or the crash
	private Mono<Void> postBooking() throws Throwable {
		return Mono.empty();
	}

	// This method is used by the byteman trigger shutdown the master node in the
	// Alpha server cluster
	private Mono<Void> alphaMasterShutdown() {
		String alphaRestAddress = System.getenv("alpha.rest.address");
		LOG.info("alpha.rest.address={}", alphaRestAddress);
		List<String> addresss = Arrays.asList(alphaRestAddress.split(","));

		// addresss.stream().filter(address -> {
		// // use the actuator alpha endpoint to find the alpha master node
		// try {
		// ResponseEntity<String> responseEntity = template.getForEntity(address +
		// "/actuator/alpha",
		// String.class);
		// ObjectMapper mapper = new ObjectMapper();
		// if (responseEntity.getStatusCode() == HttpStatus.OK) {
		// String json = responseEntity.getBody();
		// Map<String, String> map = mapper.readValue(json, Map.class);
		// if (map.get("nodeType").equalsIgnoreCase("MASTER")) {
		// return true;
		// }
		// }
		// } catch (Exception ex) {
		// LOG.error("", ex);
		// }
		// return false;
		// }).forEach(address -> {
		// // call shutdown endpoint to shutdown the alpha master node
		// HttpHeaders headers = new HttpHeaders();
		// headers.setContentType(MediaType.APPLICATION_JSON);
		// HttpEntity request = new HttpEntity(headers);
		// ResponseEntity<String> responseEntity = template.postForEntity(address +
		// "/actuator/shutdown", request,
		// String.class);
		// if (responseEntity.getStatusCode() == HttpStatus.OK) {
		// LOG.info("Alpah master node {} shutdown", address);
		// }
		// });
		return Mono.empty();
	}
}
