package com.pinecone.hydra.umct;

import com.pinecone.hydra.express.Package;
import com.pinecone.hydra.umct.util.JSONHeaderEvaluator;

import java.io.IOException;

public class JSONLetMsgDeliver extends ArchMsgDeliver {

    public JSONLetMsgDeliver( String name, MessageExpress express ) {
        super( name, express, new JSONHeaderEvaluator() );
    }

    public JSONLetMsgDeliver( MessageExpress express ) {
        this( "Messagelet", express );
    }

    @Override
    public String getServiceKeyword() {
        return ArchMessagram.DefaultServiceKey;
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
        this.getMessagram().contriveByScheme( szMessagelet, (UMCConnection) that ).dispatch();
    }

}
