package com.sunpe.mygrpc.spring.client;

import com.sunpe.mygrpc.base.client.GrpcClient;
import com.sunpe.mygrpc.base.client.GrpcClientFactory;
import com.sunpe.mygrpc.base.vo.GrpcClientConfig;
import io.grpc.Channel;
import io.grpc.stub.AbstractAsyncStub;
import io.grpc.stub.AbstractBlockingStub;
import io.grpc.stub.AbstractFutureStub;
import io.grpc.stub.AbstractStub;
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

public class GrpcClientProcessor implements BeanPostProcessor {

    private final GrpcClientFactory factory;
    private final ClientProperties properties;

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
            final GrpcStub annotation = AnnotationUtils.findAnnotation(field, GrpcStub.class);
            if (annotation != null) {
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, bean, createStub(field, annotation, field.getType()));
            }
        }
    }

    private Object createStub(Member target, GrpcStub annotation, Class<?> type) {
        String name = annotation.value();
        GrpcClient client = factory.create(name);

        if (Channel.class.equals(type)) {
            return type.cast(client);
        }
        if (AbstractStub.class.isAssignableFrom(type)) {
            @SuppressWarnings("unchecked")
            AbstractStub<?> stub = null;
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
