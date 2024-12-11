package com.pinecone.hydra.umct;

import com.pinecone.framework.unit.trie.TrieMap;
import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.express.Package;

import java.io.IOException;

public interface MessageDeliver extends Deliver {

    MessageExpress  getExpress();

    void toDispatch( Package that ) throws IOException, ServiceException;

    String  getServiceKeyword();

    TrieMap<String, MessageHandler> getRoutingTable();

    void registerHandler( String addr, MessageHandler controller );
}
