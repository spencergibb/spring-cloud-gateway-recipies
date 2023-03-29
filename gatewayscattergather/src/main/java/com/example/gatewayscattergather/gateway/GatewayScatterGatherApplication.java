package com.example.gatewayscattergather.gateway;

import java.util.List;

import reactor.core.publisher.Mono;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.webflux.ProxyExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class GatewayScatterGatherApplication {

	@GetMapping(path = "/scattergather")
	Mono<ResponseEntity<List<String>>> scatterGather(ProxyExchange<String> proxy) {
		Mono<ResponseEntity<String>> responseMono1 = proxy.uri("http://localhost:9061/").get();
		Mono<ResponseEntity<String>> responseMono2 = proxy.uri("http://localhost:9062/").get();
		return Mono.zip(responseMono1, responseMono2).map(responses -> {
			ResponseEntity<String> response1 = responses.getT1();
			ResponseEntity<String> response2 = responses.getT2();
			List<String> body = List.of(response1.getBody(), response2.getBody());
			HttpHeaders httpHeaders = HttpHeaders.writableHttpHeaders(response1.getHeaders());
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			return ResponseEntity.status(response1.getStatusCode()).headers(httpHeaders).body(body);
		});
	}

	@Bean
	RouteLocator myRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("scattergather", r -> r.path("/api").uri("forward:/scattergather"))
				.build();
	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(GatewayScatterGatherApplication.class).properties("spring.profiles.active=gateway").run(args);
	}

}
