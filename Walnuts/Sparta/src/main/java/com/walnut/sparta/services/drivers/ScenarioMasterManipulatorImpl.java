package com.walnut.sparta.services.drivers;

import com.pinecone.hydra.scenario.source.NamespaceNodeManipulator;
import com.pinecone.hydra.scenario.source.NamespaceNodeMetaManipulator;
import com.pinecone.hydra.scenario.source.ScenarioCommonDataManipulator;
import com.pinecone.hydra.scenario.source.ScenarioMasterManipulator;

import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
public class ScenarioMasterManipulatorImpl implements ScenarioMasterManipulator {
    //@Resource
    NamespaceNodeManipulator        namespaceNodeManipulator;
    //@Resource
    NamespaceNodeMetaManipulator    namespaceNodeMetaManipulator;
    //@Resource
    ScenarioCommonDataManipulator   scenarioCommonDataManipulator;
    //@Resource
    KOISkeletonMasterManipulator    koiSkeletonMasterManipulator;
    //@Override
    public NamespaceNodeManipulator getNamespaceNodeManipulator() {
        return this.namespaceNodeManipulator;
    }

    @Override
    public NamespaceNodeMetaManipulator getNSNodeMetaManipulator() {
        return this.namespaceNodeMetaManipulator;
    }

    @Override
    public ScenarioCommonDataManipulator getScenarioCommonDataManipulator() {
        return this.scenarioCommonDataManipulator;
    }

    @Override
    public KOISkeletonMasterManipulator getSkeletonMasterManipulator() {
        return this.koiSkeletonMasterManipulator;
    }
}
