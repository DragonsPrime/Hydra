package com.pinecone.hydra.umb.broadcast;

import com.pinecone.hydra.umb.UMBServiceException;
import com.pinecone.hydra.umct.UMCTExpressHandler;

public interface BroadcastControlConsumer extends BroadcastControlAgent {

    void start() throws UMBServiceException;

    void start( UMCTExpressHandler handler ) throws UMBServiceException;

    void close();




    void registerInstance( String deliverName, Object instance, Class<?> iface ) ;

    void registerInstance( Object instance, Class<?> iface ) ;

    void registerController( String deliverName, Object instance, Class<?> controllerType ) ;

    void registerController( Object instance, Class<?> controllerType ) ;

    default void registerController( Object instance ) {
        this.registerController( instance, instance.getClass() );
    }

}
