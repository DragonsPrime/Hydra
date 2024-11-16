package com.pinecone.hydra.umct;

import com.pinecone.hydra.express.Package;
import java.io.IOException;

public class MessageletMsgDeliver extends ArchMsgDeliver {

    public MessageletMsgDeliver( MessageExpress express ) {
        super( "Messagelet", express );
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
        this.getMessagram().contriveByScheme( szMessagelet, (MessagePackage) that ).dispatch();
    }

}
