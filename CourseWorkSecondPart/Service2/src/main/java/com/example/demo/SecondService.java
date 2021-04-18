package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import services.JsonFile.ExecuteTask;

import java.util.Collections;

public class SecondService {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ExecuteTask.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8081"));
        app.run(args);
    }

}
