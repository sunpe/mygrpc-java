package com.sunpe.mygrpc.example.spring.server;

import com.sunpe.mygrpc.spring.server.EnableGrpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication(scanBasePackages = {"com.sunpe.mygrpc.example.spring.server"})
@EnableGrpcServer
public class HelloWorldServer {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(HelloWorldServer.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "9001"));
        app.run(args);
    }
}