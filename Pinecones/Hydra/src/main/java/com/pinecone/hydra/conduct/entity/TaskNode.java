package com.pinecone.hydra.conduct.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;

public interface TaskNode extends TreeNode {

    long getEnumId();
    void setEnumId(long id);

    GUID getGuid();

    @Override
    default TaskNode evinceTreeNode() {
        return this;
    }

    void setGuid(GUID guid);

    String getName();
    void setName(String name);

    GenericTaskCommonData getGenericTaskCommonData();
    void setGenericTaskCommonData(GenericTaskCommonData genericTaskCommonData);

    GenericTaskNodeMeta getGenericTaskNodeMeta();
    void setGenericTaskNodeMeta(GenericTaskNodeMeta genericTaskNodeMeta);
}
