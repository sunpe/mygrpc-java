package com.sunpe.mygrpc.example.spring.client;

import com.sunpe.mygrpc.example.proto.helloworld.GreeterServiceGrpc;
import com.sunpe.mygrpc.example.proto.helloworld.HelloRequest;
import com.sunpe.mygrpc.example.proto.helloworld.HelloResponse;
import com.sunpe.mygrpc.spring.client.GrpcStub;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GrpcStub("helloworld.GreeterService")
    private GreeterServiceGrpc.GreeterServiceBlockingStub blockingStub;

    @GetMapping("/hello/{name}")
    public String test(@PathVariable String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloResponse response = blockingStub.sayHello(request);
        return response.getMessage();
    }
}
