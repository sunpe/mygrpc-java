package com.sunpe.mygrpc.base.client;

import com.sunpe.mygrpc.base.discovery.ResolverProvider;
import com.sunpe.mygrpc.base.utils.URIUtil;
import io.grpc.CallOptions;
import io.grpc.ClientCall;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import io.grpc.NameResolverRegistry;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class GrpcClient extends ManagedChannel {

    private final ManagedChannel channel;
    private final String authority;
    private final String roundRobin = "round_robin";
    private final String keepAliveKey = "keep_alive";
    private final String keepAliveTimeKey = "keep_alive_time";
    private final String keepAliveTimeoutKey = "keep_alive_timeout";

    GrpcClient(String target) {
        URI targetUri = URI.create(target);
        String scheme = targetUri.getScheme();
        NameResolverRegistry.getDefaultRegistry().register(new ResolverProvider(scheme));

        ManagedChannelBuilder<?> builder = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
                .defaultLoadBalancingPolicy(roundRobin)
                .compressorRegistry(CompressorRegistry.getDefaultInstance())
                .decompressorRegistry(DecompressorRegistry.getDefaultInstance());

        Optional<Map<String, String>> params = URIUtil.queryParams(targetUri);
        if (params.isPresent()) {
            Map<String, String> paramMap = params.get();
            setParamsToBuilder(builder, paramMap);
        }

        this.channel = builder.build();
        this.authority = targetUri.getAuthority();
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

    private void setParamsToBuilder(ManagedChannelBuilder<?> builder, Map<String, String> params) {
        boolean keepAlive = Boolean.parseBoolean(params.getOrDefault(keepAliveKey, "false"));
        int keepAliveTime = Integer.parseInt(params.getOrDefault(keepAliveTimeKey, "0"));
        int keepAliveTimeout = Integer.parseInt(params.getOrDefault(keepAliveTimeoutKey, "0"));
        if (keepAlive && keepAliveTimeout > 0) {
            builder.keepAliveTime(keepAliveTime, TimeUnit.MILLISECONDS);
        }
        if (keepAlive && keepAliveTimeout > 0) {
            builder.keepAliveTimeout(keepAliveTimeout, TimeUnit.MILLISECONDS);
        }
    }
}
