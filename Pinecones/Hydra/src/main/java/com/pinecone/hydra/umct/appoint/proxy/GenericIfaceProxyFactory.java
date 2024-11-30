package com.pinecone.hydra.umct.appoint.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import com.pinecone.hydra.umct.stereotype.Iface;

public class GenericIfaceProxyFactory {

    // Thread-safe cache for Enhancer instances
    private final ConcurrentHashMap<Class<?>, Enhancer> enhancerCache = new ConcurrentHashMap<>();

    public <T> T createProxy(Class<T> iface, Object impl) {
//        if (!iface.isInterface()) {
//            throw new IllegalArgumentException("The provided class must be an interface.");
//        }

        Enhancer enhancer = enhancerCache.computeIfAbsent(impl.getClass(), clazz -> {
            Enhancer e = new Enhancer();
            e.setSuperclass(impl.getClass());
            if( iface != null ) {
                e.setInterfaces(new Class[]{iface});
            }

            e.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
//                // Example: Process @Iface annotations on methods
//                Iface ifaceAnnotation = method.getAnnotation(Iface.class);
//                if (ifaceAnnotation != null) {
//                    String ifaceName = ifaceAnnotation.name().isEmpty() ? method.getName() : ifaceAnnotation.name();
//                    System.out.println("Intercepted @Iface method: " + ifaceName);
//                    System.out.println("Arguments: " + java.util.Arrays.toString(args));
//                }

                // Invoke the original method on the implementation
                return proxy.invoke(impl, args) + " proxy ";
            });
            return e;
        });

        return (T)(enhancer.create());
        //return iface.cast(enhancer.create());
    }

}
