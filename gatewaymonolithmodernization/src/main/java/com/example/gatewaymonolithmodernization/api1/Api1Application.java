package com.example.gatewaymonolithmodernization.api1;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Api1Application {

	@GetMapping("/api1")
	String api1(){
		return "Hello From API 1 standalone";
	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(Api1Application.class).properties("spring.profiles.active=api1").run(args);
	}


}
