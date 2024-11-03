package com.pinecone.framework.system.homotype;

import com.pinecone.framework.system.prototype.Pinenut;

public interface Injector extends Pinenut {
    Object inject ( Object that ) throws Exception ;

    default Object inject ( Object that, Object instance ) throws Exception {
        return this.inject( that, that.getClass(), instance );
    }

    Object inject ( Object that, Class<?> stereotype, Object instance ) throws Exception ;
}
