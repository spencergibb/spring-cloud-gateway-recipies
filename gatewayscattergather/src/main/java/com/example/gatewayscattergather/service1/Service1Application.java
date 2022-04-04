package com.example.gatewayscattergather.service1;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Service1Application {

    @GetMapping("/")
    String service1() {
        return "Service 1";
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Service1Application.class).properties("spring.profiles.active=service1").run(args);
    }

}
