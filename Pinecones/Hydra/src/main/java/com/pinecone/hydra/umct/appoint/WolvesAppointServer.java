package com.pinecone.hydra.umct.appoint;

import com.pinecone.hydra.umc.wolfmc.server.UlfServer;
import com.pinecone.hydra.umct.DuplexExpress;
import com.pinecone.hydra.umct.UMCTExpress;
import com.pinecone.hydra.umct.husky.compiler.CompilerEncoder;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.husky.machinery.HuskyRouteDispatcher;
import com.pinecone.hydra.umct.husky.machinery.RouteDispatcher;
import com.pinecone.hydra.umct.mapping.ControllerInspector;

public class WolvesAppointServer extends WolfAppointServer implements DuplexAppointServer {
    protected static Class<?> checkExpressType( Class<?> expressType ) {
        if ( !DuplexExpress.class.isAssignableFrom( expressType ) ) {
            throw new IllegalArgumentException( "`" + expressType.getSimpleName() + "` is not DuplexExpress calibre qualified." );
        }
        return expressType;
    }

    protected WolvesAppointServer( UlfServer messenger, RouteDispatcher dispatcher ){
        super( messenger, dispatcher );
    }

    public WolvesAppointServer( UlfServer messenger, InterfacialCompiler compiler, ControllerInspector controllerInspector, UMCTExpress express ){
        super( messenger, compiler, controllerInspector, express );
    }

    public WolvesAppointServer( UlfServer messenger, CompilerEncoder encoder, UMCTExpress express ){
        this( messenger, new HuskyRouteDispatcher( encoder, express, messenger.getTaskManager().getClassLoader() ) );
        this.apply( express );
    }

    public WolvesAppointServer( UlfServer messenger, UMCTExpress express ){
        this( messenger, new HuskyRouteDispatcher( express, messenger.getTaskManager().getClassLoader() ) );
        this.apply( express );
    }

    public WolvesAppointServer( UlfServer messenger, Class<?> expressType ){
        super( messenger, WolvesAppointServer.checkExpressType( expressType ) );
    }

    public WolvesAppointServer( UlfServer messenger ){
        this( messenger, HuskyDuplexExpress.class );
    }


    @Override
    public boolean supportDuplex() {
        return true;
    }

}
