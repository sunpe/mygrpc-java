package com.sunpe.mygrpc.base.vo;

public class ServiceInstance {
    private final String serviceID;
    private final String serviceName;
    private final String group;
    private final String address;
    private final int port;
    private final int weight;

    public ServiceInstance(String serviceID, String serviceName, String group, String address, int port, int weight) {
        this.serviceID = serviceID;
        this.serviceName = serviceName;
        this.group = group;
        this.address = address;
        this.port = port;
        this.weight = weight;
    }

    public String getServiceID() {
        return serviceID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getGroup() {
        return group;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getWeight() {
        return weight;
    }
}
