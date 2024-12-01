package com.pinecone.hydra.storage;

import com.pinecone.framework.system.prototype.Pinenut;

public class WriteChannelRecalled implements Pinenut {
    protected Object    recalledEntity;

    public WriteChannelRecalled( Object recalledEntity ){
        this.recalledEntity = recalledEntity;
    }

    public void recalled( Object rs ){
        this.recalledEntity = rs;
    }
}
