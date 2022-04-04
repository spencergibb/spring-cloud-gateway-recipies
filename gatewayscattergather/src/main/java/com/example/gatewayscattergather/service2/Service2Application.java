package com.example.gatewayscattergather.service2;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Service2Application {

    @GetMapping("/")
    String service2() {
        return "Service 2";
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Service2Application.class).properties("spring.profiles.active=service2").run(args);
    }

}
