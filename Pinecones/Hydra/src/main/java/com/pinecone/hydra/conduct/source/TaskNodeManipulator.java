package com.pinecone.hydra.conduct.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.conduct.entity.TaskNode;

import java.util.List;

public interface TaskNodeManipulator extends Pinenut {
    void insert(TaskNode taskNode);

    void remove(GUID guid);

    TaskNode getTaskNode(GUID guid);

    void update(TaskNode taskNode);

    List<GUID> getGuidsByName(String name);
}
