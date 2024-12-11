package com.pinecone.hydra.umct.appoint;

import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.umct.MessageDeliver;
import com.pinecone.hydra.umct.MessageExpress;
import com.pinecone.hydra.umct.UMCTExpress;

public interface AppointServer extends AppointNode {

    String DefaultEntityName = "__DEFAULT__";

    AppointServer apply( UMCTExpress handler );

    UMCTExpress getUMCTExpress();

    MessageExpress register          ( Deliver deliver ) ;

    MessageExpress fired             ( Deliver deliver ) ;

    MessageDeliver getDeliver        ( String name );

    MessageDeliver getDefaultDeliver ();

    void registerInstance( String deliverName, Object instance, Class<?> iface ) ;

    void registerInstance( Object instance, Class<?> iface ) ;

    void registerController( String deliverName, Object instance, Class<?> controllerType ) ;

    void registerController( Object instance, Class<?> controllerType ) ;

    default void registerController( Object instance ) {
        this.registerController( instance, instance.getClass() );
    }
}
