package com.pinecone.hydra.umct.appoint.proxy;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.umct.appoint.AppointClient;
import com.pinecone.hydra.umct.protocol.compiler.ClassDigest;
import com.pinecone.hydra.umct.protocol.compiler.DynamicMethodPrototype;
import com.pinecone.hydra.umct.stereotype.IfaceUtils;

public interface IfaceProxyFactory extends Pinenut {
    <T> T createProxy(AppointClient client, ClassDigest classDigest, Class<T> iface ) ;

    <T> T createProxy( AppointClient client, Class<T> iface ) ;

    <T> T createProxy( Class<T> iface );
}
