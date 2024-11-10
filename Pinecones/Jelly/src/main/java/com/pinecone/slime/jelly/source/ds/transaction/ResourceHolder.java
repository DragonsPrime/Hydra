package com.pinecone.slime.jelly.source.ds.transaction;

import com.pinecone.framework.system.prototype.Pinenut;

public interface ResourceHolder extends Pinenut {
    void reset();

    void unbound();

    boolean isVoid();
}