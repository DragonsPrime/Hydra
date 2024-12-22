package com.pinecone.hydra.deploy.entity.iface;

import com.pinecone.hydra.unit.imperium.entity.TreeNode;

public interface Deploy extends TreeNode {
    String getStatus();
    void setStatus(String status);
}
