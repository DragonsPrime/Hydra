package com.pinecone.hydra.service.kom.entity;

import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.service.kom.ServiceFamilyNode;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;

public interface ElementNode extends ServiceTreeNode, ServiceFamilyNode {
    default Namespace evinceNamespace() {
        return null;
    }

    default ApplicationElement evinceApplicationElement() {
        return null;
    }

    default ServiceElement evinceServiceElement() {
        return null;
    }

    GUIDDistributedTrieNode getDistributedTreeNode();

    void setDistributedTreeNode( GUIDDistributedTrieNode distributedTreeNode );

    JSONObject toJSONObject();

    @Override
    default ElementNode evinceElementNode(){
        return this;
    }
}
