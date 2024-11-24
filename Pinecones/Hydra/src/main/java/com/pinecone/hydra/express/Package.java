package com.pinecone.hydra.express;

import com.pinecone.framework.system.prototype.Pinenut;

public interface Package extends Pinenut {
    Deliver getDeliver();

    String  getConsignee();

    Package entrust( Deliver deliver );
}
