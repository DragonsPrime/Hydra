package com.pinecone.hydra.umb.wolf;

import com.pinecone.hydra.umb.broadcast.BroadcastControlConsumer;
import com.pinecone.hydra.umb.broadcast.BroadcastControlNode;
import com.pinecone.hydra.umct.husky.machinery.RouteDispatcher;

public class WolfMCBConsumer extends ArchBroadcastControlAgent implements BroadcastControlConsumer {
    protected RouteDispatcher               mRouteDispatcher;

    public WolfMCBConsumer ( BroadcastControlNode controlNode ) {
        super( controlNode );
    }
}
