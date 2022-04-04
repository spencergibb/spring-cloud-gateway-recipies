package com.example.gatewaymonolithmodernization;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class MonolithApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(MonolithApplication.class).properties("spring.profiles.active=monolith").run(args);
	}

	@Controller
	static class WebController {
		@GetMapping("/")
		String home(Model model) {
			model.addAttribute("hello", "Hello Monolith UI");
			return "index";
		}

	}

	@RestController
	static class Api1Controller {
		@GetMapping("/api1")
		String api1(){
			return "Hello From API 1";
		}

	}

	@RestController
	static class Api2Controller {
		@GetMapping("/api2")
		String api2(){
			return "Hello From API 2";
		}

	}

	@RestController
	static class Api3Controller {
		@GetMapping("/api3")
		String api1(){
			return "Hello From API 3";
		}

	}

}
