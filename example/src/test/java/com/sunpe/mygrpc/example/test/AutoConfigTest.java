package com.sunpe.mygrpc.example.test;

import com.sunpe.mygrpc.example.spring.server.HelloWorldServer;
import com.sunpe.mygrpc.spring.server.GrpcServerProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HelloWorldServer.class)
public class AutoConfigTest {

    @Autowired
    private GrpcServerProcessor processor;

    @Test
    public void test() {
        System.out.println(processor.isRunning());
    }
}
