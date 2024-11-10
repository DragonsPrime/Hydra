package com.pinecone.framework.system.aop;

public interface ScopedObject extends RawTargetAccess {
    Object getTargetObject();

    void removeFromScope();
}