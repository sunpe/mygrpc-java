package com.sunpe.mygrpc.example.spring.client;

import com.sunpe.mygrpc.example.helloworld.GreeterServiceGrpc;
import com.sunpe.mygrpc.example.helloworld.HelloRequest;
import com.sunpe.mygrpc.example.helloworld.HelloResponse;
import com.sunpe.mygrpc.spring.client.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GrpcClient
    private GreeterServiceGrpc.GreeterServiceBlockingStub blockingStub;

    @GetMapping("/hello/{name}")
    public String test(@PathVariable String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloResponse response = blockingStub.sayHello(request);
        return response.getMessage();
    }
}
