package com.pinecone.hydra.umb.wolf;

import com.pinecone.hydra.umb.UMBServiceException;
import com.pinecone.hydra.umb.broadcast.BroadcastControlConsumer;
import com.pinecone.hydra.umb.broadcast.BroadcastControlNode;
import com.pinecone.hydra.umb.broadcast.UMCBroadcastConsumer;
import com.pinecone.hydra.umct.UMCTExpressHandler;
import com.pinecone.hydra.umct.husky.machinery.RouteDispatcher;

public class WolfMCBConsumer extends ArchBroadcastControlAgent implements BroadcastControlConsumer {
    protected RouteDispatcher               mRouteDispatcher;

    protected UMCBroadcastConsumer          mBroadcastConsumer;

    public WolfMCBConsumer ( BroadcastControlNode controlNode, RouteDispatcher routeDispatcher, UMCBroadcastConsumer broadcastConsumer ) {
        super( controlNode );

        this.mRouteDispatcher    = routeDispatcher;
        this.mBroadcastConsumer  = broadcastConsumer;
    }

    public WolfMCBConsumer ( BroadcastControlNode controlNode, UMCBroadcastConsumer broadcastConsumer ) {
        this( controlNode, controlNode.getRouteDispatcher(), broadcastConsumer );
    }




    @Override
    public void start() throws UMBServiceException {
        this.start( this.mRouteDispatcher.getUMCTExpress() );
    }

    @Override
    public void start( UMCTExpressHandler handler ) throws UMBServiceException {
        this.mBroadcastConsumer.start( handler );
    }

    @Override
    public void close() {
        this.mBroadcastConsumer.close();
    }





    @Override
    public void registerInstance( String deliverName, Object instance, Class<?> iface ) {
        this.mRouteDispatcher.registerInstance( deliverName, instance, iface );
    }

    @Override
    public void registerInstance( Object instance, Class<?> iface ) {
        this.mRouteDispatcher.registerInstance( instance, iface );
    }

    @Override
    public void registerController( String deliverName, Object instance, Class<?> controllerType ) {
        this.mRouteDispatcher.registerController( deliverName, instance, controllerType );
    }

    @Override
    public void registerController( Object instance, Class<?> controllerType ) {
        this.mRouteDispatcher.registerController( instance, controllerType );
    }

}
