package com.pinecone.hydra.umct.appoint;

import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.umct.MessageDeliver;
import com.pinecone.hydra.umct.MessageExpress;
import com.pinecone.hydra.umct.UMCTExpress;

public interface AppointServer extends AppointNode {

    String DefaultEntityName = "__DEFAULT__";

    AppointServer apply( UMCTExpress handler );

    UMCTExpress getUMCTExpress();

    MessageExpress register          ( Deliver deliver ) ;

    MessageExpress fired             ( Deliver deliver ) ;

    MessageDeliver getDeliver        ( String name );

    MessageDeliver getDefaultDeliver ();

}
