package com.sunpe.mygrpc.example.pure.server;

import com.sunpe.mygrpc.base.server.GrpcServer;
import com.sunpe.mygrpc.base.vo.GrpcServerConfig;
import com.sunpe.mygrpc.example.helloworld.GreeterServiceGrpc;
import com.sunpe.mygrpc.example.helloworld.HelloRequest;
import com.sunpe.mygrpc.example.helloworld.HelloResponse;
import com.sunpe.mygrpc.zookeeper.ZkServiceDiscovery;
import io.grpc.stub.StreamObserver;

public class HelloWorldServer {

    public static void main(String[] args) throws Exception {
        GrpcServerConfig config = GrpcServerConfig.Builder.newBuilder()
                .withDiscoveryServiceClass(ZkServiceDiscovery.class)
                .withRegistry("zk://127.0.0.1:2181")
                .withPort(8000)
                .withGroup("test_group")
                .build();
        GrpcServer server = new GrpcServer(config);
        server.bindService(new GreeterImpl());
        server.start();
    }

    static class GreeterImpl extends GreeterServiceGrpc.GreeterServiceImplBase {
        @Override
        public void sayHello(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
            HelloResponse reply = HelloResponse.newBuilder().setMessage("Hello " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
