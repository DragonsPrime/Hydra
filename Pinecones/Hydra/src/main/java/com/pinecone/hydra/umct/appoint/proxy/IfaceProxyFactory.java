package com.pinecone.hydra.umct.appoint.proxy;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umct.appoint.AppointClient;
import com.pinecone.hydra.umct.husky.compiler.ClassDigest;

public interface IfaceProxyFactory extends Pinenut {
    <T> T createProxy( AppointClient client, ClassDigest classDigest, Class<T> iface ) ;

    <T> T createProxy( AppointClient client, Class<T> iface ) ;

    <T> T createProxy( Class<T> iface );
}
