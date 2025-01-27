package com.pinecone.hydra.umb.wolf;

import com.pinecone.hydra.umb.broadcast.BroadcastControlNode;
import com.pinecone.hydra.umct.UMCTExpress;
import com.pinecone.hydra.umct.husky.machinery.RouteDispatcher;

public interface UlfBroadcastControlNode extends BroadcastControlNode {

    UMCTExpress createUlfExpress( String name ) ;

    RouteDispatcher createHuskyRoute() ;

    RouteDispatcher createHuskyRoute( UMCTExpress express ) ;

}
