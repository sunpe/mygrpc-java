package com.sunpe.mygrpc.example.spring.server;

import com.sunpe.mygrpc.example.helloworld.GreeterServiceGrpc;
import com.sunpe.mygrpc.example.helloworld.HelloRequest;
import com.sunpe.mygrpc.example.helloworld.HelloResponse;
import com.sunpe.mygrpc.spring.server.GrpcService;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
@GrpcService
public class GreeterImpl extends GreeterServiceGrpc.GreeterServiceImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
        HelloResponse reply = HelloResponse.newBuilder().setMessage("Hello " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
