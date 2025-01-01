package com.walnut.sparta.services.drivers;

import com.pinecone.hydra.unit.imperium.source.TireOwnerManipulator;
import com.pinecone.hydra.unit.imperium.source.TriePathCacheManipulator;
import com.pinecone.hydra.unit.imperium.source.TrieTreeManipulator;
import com.pinecone.hydra.unit.imperium.source.TreeMasterManipulator;
import com.pinecone.hydra.scenario.ibatis.ScenarioNodeOwnerMapper;
import com.pinecone.hydra.scenario.ibatis.ScenarioNodePathCacheMapper;
import com.pinecone.hydra.scenario.ibatis.ScenarioTreeMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
public class ScenarioTreeManipulatorSharerImpl implements TreeMasterManipulator {

    @Resource
    ScenarioTreeMapper              scenarioTreeMapper;
    @Resource
    ScenarioNodeOwnerMapper         scenarioNodeOwnerMapper;
    @Resource
    ScenarioNodePathCacheMapper scenarioNodePathMapper;
    @Override
    public TireOwnerManipulator getTireOwnerManipulator() {
        return this.scenarioNodeOwnerMapper;
    }

    @Override
    public TrieTreeManipulator getTrieTreeManipulator() {
        return this.scenarioTreeMapper;
    }

    @Override
    public TriePathCacheManipulator getTriePathCacheManipulator() {
        return this.scenarioNodePathMapper;
    }
}
