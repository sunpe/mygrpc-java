package com.sunpe.mygrpc.base.discovery;

import com.sunpe.mygrpc.base.vo.ServiceInstance;

import java.util.List;

public interface Discovery {
    List<ServiceInstance> getInstances(String group, String serviceName);

    boolean registerInstance(ServiceInstance instance) throws Exception;

    boolean unregisterInstance(ServiceInstance instance) throws Exception;

    void start() throws Exception;

    void close() throws Exception;
}
