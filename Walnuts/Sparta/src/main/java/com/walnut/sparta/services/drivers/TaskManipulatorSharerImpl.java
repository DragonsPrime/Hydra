package com.walnut.sparta.services.drivers;

import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;
import com.pinecone.hydra.conduct.source.TaskCommonDataManipulator;
import com.pinecone.hydra.conduct.source.TaskMasterManipulator;
import com.pinecone.hydra.conduct.source.TaskNodeManipulator;
import com.pinecone.hydra.conduct.source.TaskNodeMetaManipulator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
public class TaskManipulatorSharerImpl implements TaskMasterManipulator {
    @Resource
    private TaskCommonDataManipulator   taskCommonDataManipulator;

    @Resource
    private TaskNodeMetaManipulator     taskNodeMetaManipulator;

    @Resource
    private TaskNodeManipulator         taskNodeManipulator;

    @Override
    public TaskNodeMetaManipulator getTaskNodeMetaManipulator() {
        return this.taskNodeMetaManipulator;
    }

    @Override
    public TaskNodeManipulator getTaskNodeManipulator() {
        return this.taskNodeManipulator;
    }

    @Override
    public TaskCommonDataManipulator getTaskCommonDataManipulator() {
        return this.taskCommonDataManipulator;
    }

    @Override
    public KOISkeletonMasterManipulator getSkeletonMasterManipulator() {
        return null;
    }
}
