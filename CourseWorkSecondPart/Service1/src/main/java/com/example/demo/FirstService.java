package com.example.demo;

import org.springframework.boot.SpringApplication;
import services.Timetable.GenerateParameters;

import java.util.Collections;

public class FirstService {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GenerateParameters.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8080"));
        app.run(args);
    }

}