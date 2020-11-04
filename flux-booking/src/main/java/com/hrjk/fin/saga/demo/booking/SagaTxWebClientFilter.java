package com.hrjk.fin.saga.demo.booking;

import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import reactor.core.publisher.Mono;

public class SagaTxWebClientFilter {

    public static Mono<ClientResponse> sagaTxFilter(ClientRequest request, ExchangeFunction next) {
        ClientRequest filtered = ClientRequest.from(request).header("foo", "bar").build();
        return next.exchange(filtered);
    }

}
