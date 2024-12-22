package com.pinecone.hydra.umct.appoint;

import com.pinecone.hydra.umct.MessageJunction;
import com.pinecone.hydra.umct.DuplexExpress;
import com.pinecone.hydra.umct.UMCConnection;
import com.pinecone.hydra.umct.WolfMCExpress;

public class HuskyDuplexExpress extends WolfMCExpress implements DuplexExpress {
    public HuskyDuplexExpress(String name, MessageJunction messagram ) {
        super( name, messagram );
    }

    public HuskyDuplexExpress(MessageJunction messagram ) {
        this( null, messagram );
    }

    @Override
    protected void onSuccessfulMsgReceived( UMCConnection connection, Object[] args ) throws Exception {
        super.onSuccessfulMsgReceived( connection, args );
    }
}
