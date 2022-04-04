package com.example.gatewaymonolithmodernization.gateway;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(GatewayApplication.class).properties("spring.profiles.active=gateway").run(args);
	}

	@Bean
	RouteLocator myRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("api1", r -> r.path("/api1/**").uri("http://localhost:9072"))
				.route("monolith", r -> r.path("/**").uri("http://localhost:9070"))
				.build();
	}

}
