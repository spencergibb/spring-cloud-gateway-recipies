package com.example.gatewaymonolithmodernization.gateway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import reactor.core.publisher.Mono;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(GatewayApplication.class).properties("spring.profiles.active=gateway").run(args);
	}

	// http :9071/ Host:tenant3.myhost.com #404
	// http :9071/ Host:tenant2.myhost.com #200
	// http :9071/ Host:tenant1.myhost.com #200
	@Bean
	RouteLocator myRouteLocator(RouteLocatorBuilder builder, VerifyTenantGatewayFilterFactory factory) {
		return builder.routes()
				.route("api1", r -> r.path("/api1/**").uri("http://localhost:9072"))
				.route("monolith", r -> r.path("/**").and().host("{tenant}.myhost.com")
						.filters(f -> f.filter(factory.apply(new VerifyTenantGatewayFilterFactory.Config().tenants("tenant1", "tenant2"))))
						.uri("http://localhost:9070"))
				.build();
	}


	@Bean
	VerifyTenantGatewayFilterFactory verifyTenantGatewayFilterFactory() {
		return new VerifyTenantGatewayFilterFactory();
	}

	static class VerifyTenantGatewayFilterFactory extends AbstractGatewayFilterFactory<VerifyTenantGatewayFilterFactory.Config> {

		@Override
		public GatewayFilter apply(Config config) {
			return (exchange, chain) -> {
				var vars = ServerWebExchangeUtils.getUriTemplateVariables(exchange);
				String tenant = vars.get("tenant");
				if (config.getTenants().contains(tenant)) {
					return chain.filter(exchange);
				}
				return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND));
			};
		}

		static class Config {
			List<String> tenants = new ArrayList<>();

			public List<String> getTenants() {
				return tenants;
			}

			public Config tenants(String... tenants) {
				this.tenants.addAll(Arrays.asList(tenants));
				return this;
			}
		}
	}

}
