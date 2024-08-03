package com.sunpe.mygrpc.base.vo;

public class GrpcServerConfig {

    private Class<?> serviceDiscoveryClass;
    private String registry;
    private int port;
    private String group;

    public Class<?> getServiceDiscoveryClass() {
        return serviceDiscoveryClass;
    }

    public void setServiceDiscoveryClass(Class<?> serviceDiscoveryClass) {
        this.serviceDiscoveryClass = serviceDiscoveryClass;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registry) {
        this.registry = registry;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public static class Builder {
        private Class<?> serviceDiscoveryClass;
        private String registry;
        private int port;
        private String group;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder withDiscoveryServiceClass(Class<?> serviceDiscoveryClass) {
            this.serviceDiscoveryClass = serviceDiscoveryClass;
            return this;
        }

        public Builder withRegistry(String registry) {
            this.registry = registry;
            return this;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withGroup(String group) {
            this.group = group;
            return this;
        }

        public GrpcServerConfig build() {
            GrpcServerConfig config = new GrpcServerConfig();
            config.setServiceDiscoveryClass(serviceDiscoveryClass);
            config.setRegistry(this.registry);
            config.setPort(this.port);
            config.setGroup(this.group);
            return config;
        }
    }
}
