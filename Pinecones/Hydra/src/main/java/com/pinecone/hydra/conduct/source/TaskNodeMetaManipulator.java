package com.pinecone.hydra.conduct.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.conduct.entity.TaskNodeMeta;

public interface TaskNodeMetaManipulator extends Pinenut {
    void insert(TaskNodeMeta taskNodeMeta);

    void remove(GUID guid);

    TaskNodeMeta getTaskNodeMeta(GUID guid);

    void update(TaskNodeMeta taskNodeMeta);
}
