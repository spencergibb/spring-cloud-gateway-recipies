package com.example.gatewayredisratelimiting;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import reactor.core.publisher.Mono;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.config.Customizer.withDefaults;

//  watch -n .1 http :9091/hello -h --auth user:password
@SpringBootApplication
public class GatewayredisratelimitingApplication {
	static GenericContainer redis = new GenericContainer<>("redis:5.0.14").withExposedPorts(6379).waitingFor(Wait.forListeningPort());
	static {
		redis.start();
	}

	public static void main(String[] args) {
		 new SpringApplicationBuilder(GatewayredisratelimitingApplication.class)
				 .properties("spring.redis.host=" + redis.getContainerIpAddress())
				 .properties("spring.redis.port=" + redis.getFirstMappedPort())
				 .run(args);
	}

	@Bean
	RouteLocator myRouteLocator(RouteLocatorBuilder builder, RedisRateLimiter rateLimiter) {
		rateLimiter.getConfig().put("rate_limited", new RedisRateLimiter.Config().setReplenishRate(1).setBurstCapacity(1));
		return builder.routes()
				.route("rate_limited", r -> r.path("/hello")
						.filters(f -> f.prefixPath("/myservice")
								.requestRateLimiter().and())
						.uri("http://localhost:9091"))
				.build();
	}

	@Bean
	@Primary
	KeyResolver myKeyResolver() {
		return exchange -> exchange.getPrincipal().flatMap(principal -> Mono.justOrEmpty(principal.getName()));
	}

	@Bean
	SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
		// @formatter:off
		http
				.authorizeExchange((authorize) -> authorize
						.anyExchange().authenticated()
				)
				.httpBasic(withDefaults());
		// @formatter:on
		return http.build();
	}

	@Bean
	MapReactiveUserDetailsService userDetailsService() {
		// @formatter:off
		UserDetails user = User.withDefaultPasswordEncoder()
				.username("user")
				.password("password")
				.roles("USER")
				.build();
		// @formatter:on
		return new MapReactiveUserDetailsService(user);
	}

	@Controller
	@RestController
	static class MyService {
		@GetMapping("/myservice/hello")
		String hello() {
			return "Hello from a service " + System.currentTimeMillis();
		}
	}
}
