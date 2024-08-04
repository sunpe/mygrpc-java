package com.sunpe.mygrpc.base.discovery;

import com.sunpe.mygrpc.base.vo.ServiceInstance;

import java.util.List;

public interface Discovery {
    /**
     * get service instance from Discovery implements
     *
     * @param group
     * @param serviceName
     * @return
     */
    List<ServiceInstance> getInstances(String group, String serviceName);

    /**
     * register a service instance to Discovery
     *
     * @param instance
     * @return
     * @throws Exception
     */
    boolean registerInstance(ServiceInstance instance) throws Exception;

    /**
     * unregister a service from Discovery
     *
     * @param instance
     * @return
     * @throws Exception
     */
    boolean unregisterInstance(ServiceInstance instance) throws Exception;

    /**
     * start a Discovery
     *
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * close a Discovery
     *
     * @throws Exception
     */
    void close() throws Exception;

    /**
     * whether instance updated on Discovery. this should block until receive instances update message.
     *
     * @return
     */
    boolean instancesUpdate();
}
