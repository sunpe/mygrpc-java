package com.sunpe.mygrpc.base.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscoveryRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryRegistry.class);
    private static final Map<String, Class<?>> discoveries = new ConcurrentHashMap<>();

    public static void register(String registry, Class<?> clazz) throws Exception {
        URI uri = URI.create(registry);
        try {
            clazz.getConstructor(URI.class);
        } catch (NoSuchMethodException e) {
            logger.error("register Discovery error, discovery class [{}] does not have constructor(URI.class)", clazz);
            throw new Exception("discovery class does not have constructor(URI.class)");
        }
        discoveries.put(uri.getScheme(), clazz);
    }

    public static Discovery getDiscovery(URI uri) throws Exception {
        String scheme = uri.getScheme();
        Class<?> clazz = discoveries.get(scheme);
        if (clazz == null) {
            throw new Exception(String.format("discovery class for schema [%s] not found", scheme));
        }
        Constructor<?> constructor;
        try {
            constructor = clazz.getConstructor(URI.class);
        } catch (NoSuchMethodException e) {
            logger.error("get Discovery error, discovery class [{}] does not have constructor(URI.class)", clazz);
            throw new Exception("discovery class does not have constructor(URI.class)");
        }
        return (Discovery) constructor.newInstance(uri);
    }

    public static Discovery getDiscovery(String target) throws Exception {
        URI uri = URI.create(target);
        return getDiscovery(uri);
    }
}
