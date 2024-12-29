package com.pinecone.hydra.conduct.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;
import com.pinecone.hydra.conduct.ibatis.TaskCommonDataMapper;
import com.pinecone.hydra.conduct.ibatis.TaskNodeMapper;
import com.pinecone.hydra.conduct.ibatis.TaskNodeMetaMapper;
import com.pinecone.hydra.conduct.source.TaskCommonDataManipulator;
import com.pinecone.hydra.conduct.source.TaskMasterManipulator;
import com.pinecone.hydra.conduct.source.TaskNodeManipulator;
import com.pinecone.hydra.conduct.source.TaskNodeMetaManipulator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
@Component
public class TaskMasterManipulatorImpl implements TaskMasterManipulator {
    @Resource
    @Structure(type = TaskNodeMetaMapper.class)
    TaskNodeMetaManipulator taskNodeMetaManipulator;

    @Resource
    @Structure(type = TaskNodeMapper.class)
    TaskNodeManipulator taskNodeManipulator;

    @Resource
    @Structure(type = TaskCommonDataMapper.class)
    TaskCommonDataManipulator taskCommonDataManipulator;

    @Resource
    @Structure(type = TaskMasterTreeManipulatorImpl.class)
    KOISkeletonMasterManipulator skeletonMasterManipulator;
    public TaskMasterManipulatorImpl() {

    }

    public TaskMasterManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( TaskMasterManipulatorImpl.class, Map.of(), this );
        this.skeletonMasterManipulator = new TaskMasterTreeManipulatorImpl( driver );
    }


    @Override
    public KOISkeletonMasterManipulator getSkeletonMasterManipulator() {
        return this.skeletonMasterManipulator;
    }

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
}
