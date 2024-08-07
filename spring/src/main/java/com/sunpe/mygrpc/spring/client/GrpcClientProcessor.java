package com.sunpe.mygrpc.spring.client;

import com.sunpe.mygrpc.base.client.GrpcClientFactory;
import com.sunpe.mygrpc.base.vo.GrpcClientConfig;
import io.grpc.Channel;
import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractFutureStub;
import io.grpc.stub.AbstractStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GrpcClientProcessor implements BeanPostProcessor {

    private final GrpcClientFactory factory;
    private final ClientProperties properties;
    private final Map<String, Object> initializedStubs = new ConcurrentHashMap<>();
    private final String serviceNameField = "SERVICE_NAME";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public GrpcClientProcessor(ClientProperties properties) {
        this.properties = properties;
        // init grpc client factory
        try {
            this.factory = createGrpcClientFactory();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create grpc client factory", e);
        }
    }

    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        while (clazz != null) {
            processFields(clazz, bean);
            clazz = clazz.getSuperclass();
        }
        return bean;
    }

    private GrpcClientFactory createGrpcClientFactory() throws Exception {
        logger.info("create grpc client factory for registry-[{}]", properties.getRegistry());
        GrpcClientConfig config = GrpcClientConfig.Builder.newBuilder()
                .withDiscoveryClass(properties.getServiceDiscoveryClass())
                .withRegistry(properties.getRegistry())
                .withGroup(properties.getGroup())
                .build();
        return new GrpcClientFactory(config);
    }

    // process field. init grpc stub if it has @GrpcStub annotation in Spring Bean.
    private void processFields(final Class<?> clazz, final Object bean) {
        for (final Field field : clazz.getDeclaredFields()) {
            final GrpcClient annotation = AnnotationUtils.findAnnotation(field, GrpcClient.class);
            if (annotation != null) {
                String serviceName = getServiceName(field);
                ReflectionUtils.makeAccessible(field);
                Object fieldValue = initializedStubs.computeIfAbsent(serviceName,
                        sn -> createStub(field, sn, field.getType()));
                ReflectionUtils.setField(field, bean, fieldValue);
            }
        }
    }

    private String getServiceName(Field field) {
        Class<?> type = field.getType().getDeclaringClass();
        Field serviceName = ReflectionUtils.findField(type, serviceNameField);
        if (serviceName == null) {
            throw new BeanInstantiationException(field.getType(), "SERVICE_NAME in Grpc service class not found");
        }
        serviceName.setAccessible(true);
        try {
            return (String) serviceName.get(null);
        } catch (IllegalAccessException e) {
            throw new BeanInstantiationException(field.getType(), "SERVICE_NAME in Grpc service class not found");
        }
    }

    @SuppressWarnings("unchecked")
    private Object createStub(Member target, String serviceName, Class<?> type) {
        logger.info("starting create grpc client stub for service-[{}]", serviceName);
        com.sunpe.mygrpc.base.client.GrpcClient client = factory.create(serviceName);

        if (Channel.class.equals(type)) {
            return type.cast(client);
        }
        if (AbstractStub.class.isAssignableFrom(type)) {
            AbstractStub<?> stub;
            try {
                stub = createStubObject((Class<? extends AbstractStub<?>>) type.asSubclass(AbstractStub.class), client);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new BeanInstantiationException(type, "Unsupported grpc stub type" + type.getName());
            }
            return type.cast(stub);
        }
        if (target != null) {
            throw new InvalidPropertyException(target.getDeclaringClass(), target.getName(),
                    "Unsupported type " + type.getName());
        }
        throw new BeanInstantiationException(type, "Unsupported grpc stub or channel type");
    }

    private AbstractStub<?> createStubObject(Class<? extends AbstractStub<?>> stubType,
                                             Channel channel) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String methodName = getStubConstructorName(stubType);
        final Class<?> enclosingClass = stubType.getEnclosingClass();
        final Method factoryMethod = enclosingClass.getMethod(methodName, Channel.class);
        return stubType.cast(factoryMethod.invoke(null, channel));
    }

    private String getStubConstructorName(Class<? extends AbstractStub<?>> stubType) {
        // initialize stub. there are 3 types of stub:
        // "newStub",  AbstractAsyncStub
        // "newBlockingStub",  AbstractBlockingStub
        //  newFutureStub AbstractFutureStub
        if (AbstractAsyncStub.class.isAssignableFrom(stubType)) {
            return "newStub";
        }
        if (AbstractBlockingStub.class.isAssignableFrom(stubType)) {
            return "newBlockingStub";
        }
        if (AbstractFutureStub.class.isAssignableFrom(stubType)) {
            return "newFutureStub";
        }
        throw new BeanInstantiationException(stubType, "Unsupported grpc stub type" + stubType.getName());
    }

}
