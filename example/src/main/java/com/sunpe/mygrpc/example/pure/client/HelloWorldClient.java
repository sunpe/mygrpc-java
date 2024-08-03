package com.sunpe.mygrpc.example.pure.client;

import com.sunpe.mygrpc.base.client.GrpcClient;
import com.sunpe.mygrpc.base.client.GrpcClientFactory;
import com.sunpe.mygrpc.base.vo.GrpcClientConfig;
import com.sunpe.mygrpc.example.proto.helloworld.GreeterServiceGrpc;
import com.sunpe.mygrpc.example.proto.helloworld.HelloRequest;
import com.sunpe.mygrpc.example.proto.helloworld.HelloResponse;
import com.sunpe.mygrpc.zookeeper.ZkServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class HelloWorldClient {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldClient.class);

    public static void main(String[] args) throws Exception {
        GrpcClientConfig config = GrpcClientConfig.Builder.newBuilder()
                .withRegistry("zk://127.0.0.1:2181")
                .withGroup("test_group")
                .withDiscoveryClass(ZkServiceDiscovery.class)
                .build();
        GrpcClientFactory factory = new GrpcClientFactory(config);
        GrpcClient grpcClient = factory.create("helloworld.GreeterService");

        GreeterServiceGrpc.GreeterServiceBlockingStub blockingStub = GreeterServiceGrpc.newBlockingStub(grpcClient);

        HelloRequest request = HelloRequest.newBuilder().setName("world").build();
        HelloResponse response = blockingStub.sayHello(request);
        logger.info("Greeting: {}", response.getMessage());
        grpcClient.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }

}
