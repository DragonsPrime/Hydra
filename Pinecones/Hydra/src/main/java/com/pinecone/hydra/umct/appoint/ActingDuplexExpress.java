package com.pinecone.hydra.umct.appoint;

import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.umct.MessageDeliver;
import com.pinecone.hydra.umct.MessageExpress;
import com.pinecone.hydra.umct.MessageJunction;

public abstract class ActingDuplexExpress extends ArchDuplexExpress {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public MessageJunction getJunction() {
        return null;
    }

    @Override
    public MessageDeliver recruit( String szName ) {
        return null;
    }

    @Override
    public MessageExpress register( Deliver deliver ) {
        return null;
    }

    @Override
    public MessageExpress fired( Deliver deliver ) {
        return null;
    }

    @Override
    public MessageDeliver getDeliver( String szName ) {
        return null;
    }

    @Override
    public boolean hasOwnDeliver( Deliver deliver ) {
        return false;
    }

    @Override
    public boolean hasOwnDeliver( String deliverName ) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }
}
