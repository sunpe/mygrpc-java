package com.sunpe.mygrpc.spring.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mygrpc.client")
public class ClientProperties {
    /**
     * the discovery implement class
     */
    private Class<?> serviceDiscoveryClass;

    /**
     * the server registry
     */
    private String registry;

    /**
     * the server group
     */
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
