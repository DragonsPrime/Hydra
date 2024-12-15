package com.pinecone.hydra.express;

import com.pinecone.framework.system.prototype.Pinenut;

public interface Deliver extends Pinenut {
    String   getName();

    Express  getExpress();

    void toDispatch( Package that ) throws Exception;
}
