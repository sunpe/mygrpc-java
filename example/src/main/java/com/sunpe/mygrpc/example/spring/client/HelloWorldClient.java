package com.sunpe.mygrpc.example.spring.client;

import com.sunpe.mygrpc.spring.client.EnableGrpcClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@SpringBootApplication
@RestController
@EnableGrpcClient
public class HelloWorldClient {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(HelloWorldClient.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "9002"));
        app.run(args);
    }
}
