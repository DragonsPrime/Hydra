package com.pinecone.hydra.umct;

import com.pinecone.hydra.express.Package;
import com.pinecone.hydra.umct.decipher.JSONHeaderDecipher;

import java.io.IOException;

public class JSONLetMsgDeliver extends ArchMsgDeliver {

    public JSONLetMsgDeliver( String name, MessageExpress express ) {
        this( name, express, ArchMessagram.DefaultServiceKey );
    }

    public JSONLetMsgDeliver( String name, MessageExpress express, String szServiceKey ) {
        super( name, express, new JSONHeaderDecipher( szServiceKey ), szServiceKey );
    }

    public JSONLetMsgDeliver( MessageExpress express ) {
        this( ProtoletMsgDeliver.class.getSimpleName(), express );
    }

    @Override
    protected void prepareDispatch( Package that ) throws IOException {

    }

    @Override
    protected boolean sift( Package that ) {
        return false;
    }

    @Override
    protected void doMessagelet( String szMessagelet, Package that ) {
        if ( this.getJunction() instanceof ArchMessagram ) {
            ( (ArchMessagram)this.getJunction() ).contriveByScheme( szMessagelet, (UMCConnection) that ).dispatch();
        }
    }

}
