package com.pinecone.hydra.umct.appoint.proxy;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.umct.appoint.DuplexAppointServer;
import com.pinecone.hydra.umct.husky.compiler.ClassDigest;
import com.pinecone.hydra.umct.husky.compiler.DynamicMethodPrototype;
import com.pinecone.hydra.umct.stereotype.IfaceUtils;

public class GenericPassiveClientIfaceProxyFactory implements PassiveClientIfaceProxyFactory {
    protected final ConcurrentHashMap<Class<?>, Enhancer> mEnhancerCache = new ConcurrentHashMap<>();

    protected DuplexAppointServer mServer;

    public GenericPassiveClientIfaceProxyFactory( DuplexAppointServer server ) {
        this.mServer = server;
    }

    @Override
    public <T> T createProxy( long clientId, DuplexAppointServer server, ClassDigest classDigest, Class<T> iface ) {
        Enhancer enhancer = this.mEnhancerCache.computeIfAbsent(iface, clazz -> {
            Enhancer e = new Enhancer();
            e.setSuperclass( UMCTHub.class );
            e.setInterfaces( new Class[]{iface} );

            e.setCallback(new MethodInterceptor() {
                private DynamicMethodPrototype methodPrototype;

                @Override
                public Object intercept( Object obj, Method method, Object[] args, MethodProxy proxy ) throws Throwable {
                    if ( this.methodPrototype == null ) {
                        String methodName = IfaceUtils.getIfaceMethodName( method );
                        this.methodPrototype = (DynamicMethodPrototype) server.queryMethodDigest(
                                classDigest.getClassName() + Namespace.DEFAULT_SEPARATOR + methodName
                        );
                    }
                    return server.invokeInform( clientId, this.methodPrototype, args );
                }
            });
            return e;
        });

        return iface.cast( enhancer.create() );
    }

    @Override
    public <T> T createProxy( long clientId, DuplexAppointServer server, Class<T> iface ) {
        ClassDigest classDigest = server.queryClassDigest( iface.getName() );

        return this.createProxy( clientId, server, classDigest, iface );
    }

    @Override
    public <T> T createProxy( long clientId, Class<T> iface ) {
        return this.createProxy( clientId, this.mServer, iface );
    }

}
