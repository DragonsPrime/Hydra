package com.sauron.radium.messagron;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.umct.ArchMessagram;
import com.pinecone.hydra.umct.UMCConnection;

public class HeistMessage extends Messageletson {
    public HeistMessage(UMCConnection msgPackage, ArchMessagram servtron ) {
        super( msgPackage, servtron );
    }

    @Override
    public void dispatch() {
        Debug.trace( this.$_MSG() );
    }

    @Override
    public void terminate(){

    }
}
