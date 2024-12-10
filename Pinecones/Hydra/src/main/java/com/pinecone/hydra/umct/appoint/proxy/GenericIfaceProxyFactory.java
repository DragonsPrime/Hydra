package com.pinecone.hydra.umct.appoint.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.umct.appoint.AppointClient;
import com.pinecone.hydra.umct.protocol.compiler.ClassDigest;
import com.pinecone.hydra.umct.protocol.compiler.DynamicMethodPrototype;
import com.pinecone.hydra.umct.stereotype.IfaceUtils;

public class GenericIfaceProxyFactory implements IfaceProxyFactory {
    protected final ConcurrentHashMap<Class<?>, Enhancer> mEnhancerCache = new ConcurrentHashMap<>();

    protected AppointClient mClient;

    public GenericIfaceProxyFactory( AppointClient client ) {
        this.mClient = client;
    }

    @Override
    public <T> T createProxy( AppointClient client, ClassDigest classDigest, Class<T> iface ) {
//        if (!iface.isInterface()) {
//            throw new IllegalArgumentException("The provided class must be an interface.");
//        }

        Enhancer enhancer = this.mEnhancerCache.computeIfAbsent(iface, clazz -> {
            Enhancer e = new Enhancer();
            e.setSuperclass(UMCTHub.class);
            if( iface != null ) {
                e.setInterfaces( new Class[]{iface} );
            }

            e.setCallback(new MethodInterceptor() {
                private DynamicMethodPrototype methodPrototype;

                @Override
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                    if ( this.methodPrototype == null ) {
                        String methodName = IfaceUtils.getIfaceMethodName( method );
                        this.methodPrototype = (DynamicMethodPrototype) client.queryMethodDigest(
                                classDigest.getClassName() + Namespace.DEFAULT_SEPARATOR + methodName
                        );
                    }
                    return client.invokeInform( this.methodPrototype, args );
                }
            });
            return e;
        });

        return iface.cast( enhancer.create() );
    }

    @Override
    public <T> T createProxy( AppointClient client, Class<T> iface ) {
        ClassDigest classDigest = client.queryClassDigest( iface.getName() );

        return this.createProxy( client, classDigest, iface );
    }

    @Override
    public <T> T createProxy( Class<T> iface ) {
        return this.createProxy( this.mClient, iface );
    }

}
