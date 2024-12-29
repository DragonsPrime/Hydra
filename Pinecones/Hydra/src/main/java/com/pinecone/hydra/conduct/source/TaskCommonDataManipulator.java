package com.pinecone.hydra.conduct.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.conduct.entity.TaskCommonData;

public interface TaskCommonDataManipulator extends Pinenut {
    void insert(TaskCommonData taskCommonData);

    void remove(GUID guid);

    TaskCommonData getTaskCommonData(GUID guid);

    void update(TaskCommonData taskCommonData);
}
