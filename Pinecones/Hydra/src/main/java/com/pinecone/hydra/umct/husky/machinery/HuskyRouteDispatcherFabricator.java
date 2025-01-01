package com.pinecone.hydra.umct.husky.machinery;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umct.UMCTExpress;

public class HuskyRouteDispatcherFabricator implements Pinenut {
    public static void afterConstructed( HuskyRouteDispatcher dispatcher, UMCTExpress express ) {
        dispatcher.applyExpress( dispatcher.getInterfacialCompiler(), express );
    }
}
