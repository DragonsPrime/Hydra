package com.walnut.sparta.service;

import com.pinecone.framework.util.id.GUID;

public interface ServiceTreeService {
    void addNodeToParent(GUID nodeGUID,GUID parentGUID);
    void deleteNode(GUID nodeGUID);
}
