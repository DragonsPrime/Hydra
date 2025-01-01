package com.pinecone.hydra.umct;

import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.express.Package;

import java.io.IOException;
import java.util.Map;

public interface MessageDeliver extends Deliver {

    MessageExpress  getExpress();

    void toDispatch( Package that ) throws IOException, ServiceException;

    String  getServiceKeyword();

    Map<String, MessageHandler> getRoutingTable();

    void registerHandler( String addr, MessageHandler controller );
}
