package com.pinecone.hydra.umct.husky.machinery;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.umct.MessageDeliver;
import com.pinecone.hydra.umct.MessageExpress;
import com.pinecone.hydra.umct.UMCTExpress;
import com.pinecone.hydra.umct.husky.compiler.ClassDigest;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.husky.compiler.MethodDigest;

public interface RouteDispatcher extends Pinenut {
    void setUMCTExpress( UMCTExpress handler );

    PMCTContextMachinery getContextMachinery();

    UMCTExpress getUMCTExpress();

    MessageExpress register( Deliver deliver );

    MessageExpress  fired   ( Deliver deliver );

    MessageDeliver getDeliver( String name );

    MessageDeliver getDefaultDeliver();

    InterfacialCompiler getInterfacialCompiler();


    void registerInstance( String deliverName, Object instance, Class<?> iface ) ;

    void registerInstance( Object instance, Class<?> iface );

    void registerController( String deliverName, Object instance, Class<?> controllerType ) ;

    void registerController( Object instance, Class<?> controllerType ) ;

    default void registerController( Object instance ) {
        this.registerController( instance, instance.getClass() );
    }

    ClassDigest queryClassDigest( String name );

    MethodDigest queryMethodDigest( String name ) ;

    void addClassDigest( ClassDigest that );

    void addMethodDigest( MethodDigest that );

    ClassDigest compile( Class<? > clazz, boolean bAsIface ) ;
}
