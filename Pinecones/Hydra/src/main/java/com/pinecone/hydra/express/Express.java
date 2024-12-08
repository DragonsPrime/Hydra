package com.pinecone.hydra.express;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.system.Hydrarum;

public interface Express extends Pinenut {
    Hydrarum getSystem();

    Deliver  recruit ( String szName );

    Express  register( Deliver deliver );

    Express  fired   ( Deliver deliver );

    boolean  hasOwnDeliver( Deliver deliver );

    boolean  hasOwnDeliver( String deliverName );

    int      size    ();
}
