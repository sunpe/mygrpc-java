package com.sunpe.mygrpc.base.client;

import com.sunpe.mygrpc.base.discovery.ResolverProvider;
import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import io.grpc.NameResolverRegistry;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class GrpcClient extends ManagedChannel {

    private final ManagedChannel channel;
    private final String authority;

    GrpcClient(String target) {
        URI targetUri = URI.create(target);
        String scheme = targetUri.getScheme();
        this.authority = targetUri.getAuthority();
        NameResolverRegistry.getDefaultRegistry().register(new ResolverProvider(scheme));
        this.channel = ManagedChannelBuilder
                .forTarget(target)
                .defaultLoadBalancingPolicy("round_robin")
                .compressorRegistry(CompressorRegistry.getDefaultInstance())
                .decompressorRegistry(DecompressorRegistry.getDefaultInstance())
                .usePlaintext()
                .build();
    }

    @Override
    public ManagedChannel shutdown() {
        return this.channel.shutdown();
    }

    @Override
    public boolean isShutdown() {
        return this.channel.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.channel.isTerminated();
    }

    @Override
    public ManagedChannel shutdownNow() {
        return this.channel.shutdown();
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        return this.channel.awaitTermination(l, timeUnit);
    }

    @Override
    public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions) {
        return this.channel.newCall(methodDescriptor, callOptions);
    }

    @Override
    public String authority() {
        return authority;
    }
}
