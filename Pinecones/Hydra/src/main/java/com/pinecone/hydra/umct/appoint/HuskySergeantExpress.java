package com.pinecone.hydra.umct.appoint;

import com.pinecone.hydra.umct.MessageJunction;
import com.pinecone.hydra.umct.SergeantExpress;
import com.pinecone.hydra.umct.UMCConnection;
import com.pinecone.hydra.umct.WolfMCExpress;

public class HuskySergeantExpress extends WolfMCExpress implements SergeantExpress {
    public HuskySergeantExpress( String name, MessageJunction messagram ) {
        super( name, messagram );
    }

    public HuskySergeantExpress( MessageJunction messagram ) {
        this( null, messagram );
    }

    @Override
    protected void onSuccessfulMsgReceived( UMCConnection connection, Object[] args ) throws Exception {
        super.onSuccessfulMsgReceived( connection, args );
    }
}
