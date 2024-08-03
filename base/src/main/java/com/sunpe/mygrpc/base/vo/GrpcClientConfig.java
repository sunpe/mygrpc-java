package com.sunpe.mygrpc.base.vo;

public class GrpcClientConfig {
    private String registry;
    private Class<?> discoveryClass;
    private String group;

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public Class<?> getDiscoveryClass() {
        return discoveryClass;
    }

    public void setDiscoveryClass(Class<?> discoveryClass) {
        this.discoveryClass = discoveryClass;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public static class Builder {
        private String registry;
        private Class<?> discoveryClass;
        private String group;

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withRegistry(String registry) {
            this.registry = registry;
            return this;
        }

        public Builder withDiscoveryClass(Class<?> discoveryClass) {
            this.discoveryClass = discoveryClass;
            return this;
        }

        public Builder withGroup(String group) {
            this.group = group;
            return this;
        }

        public GrpcClientConfig build() {
            GrpcClientConfig config = new GrpcClientConfig();
            config.registry = registry;
            config.discoveryClass = discoveryClass;
            config.group = group;
            return config;
        }
    }
}
