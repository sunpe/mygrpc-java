package com.sunpe.mygrpc.base.discovery;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.URI;

public class ResolverProvider extends NameResolverProvider {
    private final String scheme;

    public ResolverProvider(String scheme) {
        this.scheme = scheme;
    }

    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 5;
    }

    @Override
    public NameResolver newNameResolver(URI uri, NameResolver.Args args) {
        try {
            return new Resolver(uri, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDefaultScheme() {
        return scheme;
    }
}
