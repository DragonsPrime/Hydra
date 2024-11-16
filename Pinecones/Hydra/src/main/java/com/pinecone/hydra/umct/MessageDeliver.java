package com.pinecone.hydra.umct;

import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.express.Package;

import java.io.IOException;

public interface MessageDeliver extends Deliver {

    MessageExpress  getExpress();

    void toDispatch( Package that ) throws IOException;

    String  getServiceKeyword();

}
