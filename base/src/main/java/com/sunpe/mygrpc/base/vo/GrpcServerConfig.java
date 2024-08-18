package com.sunpe.mygrpc.base.vo;

public class GrpcServerConfig {

    private Class<?> serviceDiscoveryClass;
    private String registry;
    private int port;
    private String group;
    private boolean keepAlive;
    private int keepAliveTime;
    private int keepAliveTimeout;
    private int maxConnectionIdle;
    private int maxConnectionAge;
    private int maxConnectionAgeGrace;

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

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public void setKeepAliveTimeout(int keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public int getMaxConnectionIdle() {
        return maxConnectionIdle;
    }

    public void setMaxConnectionIdle(int maxConnectionIdle) {
        this.maxConnectionIdle = maxConnectionIdle;
    }

    public int getMaxConnectionAge() {
        return maxConnectionAge;
    }

    public void setMaxConnectionAge(int maxConnectionAge) {
        this.maxConnectionAge = maxConnectionAge;
    }

    public int getMaxConnectionAgeGrace() {
        return maxConnectionAgeGrace;
    }

    public void setMaxConnectionAgeGrace(int maxConnectionAgeGrace) {
        this.maxConnectionAgeGrace = maxConnectionAgeGrace;
    }

    public static class Builder {
        private Class<?> serviceDiscoveryClass;
        private String registry;
        private int port;
        private String group;
        private boolean keepAlive;
        private int keepAliveTime;
        private int keepAliveTimeout;
        private int maxConnectionIdle;
        private int maxConnectionAge;
        private int maxConnectionAgeGrace;

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

        public Builder withKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
            return this;
        }

        public Builder withKeepAliveTime(int keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        public Builder withKeepAliveTimeout(int keepAliveTimeout) {
            this.keepAliveTimeout = keepAliveTimeout;
            return this;
        }

        public Builder withMaxConnectionIdle(int maxConnectionIdle) {
            this.maxConnectionIdle = maxConnectionIdle;
            return this;
        }

        public Builder withMaxConnectionAge(int maxConnectionAge) {
            this.maxConnectionAge = maxConnectionAge;
            return this;
        }

        public Builder withMaxConnectionAgeGrace(int maxConnectionAgeGrace) {
            this.maxConnectionAgeGrace = maxConnectionAgeGrace;
            return this;
        }

        public GrpcServerConfig build() {
            GrpcServerConfig config = new GrpcServerConfig();
            config.setServiceDiscoveryClass(serviceDiscoveryClass);
            config.setRegistry(this.registry);
            config.setPort(this.port);
            config.setGroup(this.group);
            config.setKeepAlive(this.keepAlive);
            config.setKeepAliveTime(this.keepAliveTime);
            config.setKeepAliveTimeout(this.keepAliveTimeout);
            config.setMaxConnectionIdle(this.maxConnectionIdle);
            config.setMaxConnectionAge(this.maxConnectionAge);
            config.setMaxConnectionAgeGrace(this.maxConnectionAgeGrace);
            return config;
        }
    }
}
