package com.pinecone.hydra.conduct.source;

import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;

public interface TaskMasterManipulator extends KOIMasterManipulator {
    TaskNodeMetaManipulator         getTaskNodeMetaManipulator();

    TaskNodeManipulator             getTaskNodeManipulator();

    TaskCommonDataManipulator       getTaskCommonDataManipulator();
}
