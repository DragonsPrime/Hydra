package com.pinecone.hydra.storage.volume.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.source.VolumeCapacityManipulator;
import com.pinecone.hydra.storage.volume.source.VolumeMasterManipulator;
import com.pinecone.hydra.system.ko.UOIUtils;
import com.pinecone.hydra.unit.imperium.ImperialTreeNode;
import com.pinecone.hydra.unit.imperium.ImperialTree;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;

import java.time.LocalDateTime;

public abstract class ArchVolumeOperator implements VolumeOperator{
    protected VolumeManager                 volumeManager;
    protected VolumeOperatorFactory         factory;
    protected ImperialTree                  imperialTree;
    protected VolumeMasterManipulator       volumeMasterManipulator;
    protected VolumeCapacityManipulator     volumeCapacityManipulator;

    public ArchVolumeOperator( VolumeMasterManipulator masterManipulator, VolumeManager volumeManager ){
        this.imperialTree =  volumeManager.getMasterTrieTree();
        this.volumeManager = volumeManager;
        this.volumeMasterManipulator   =  masterManipulator;
        this.volumeCapacityManipulator =  masterManipulator.getVolumeCapacityManipulator();
    }

    protected ImperialTreeNode affirmPreinsertionInitialize(LogicVolume volume ){
        GUID guid = volume.getGuid();
        volume.setUpdateTime( LocalDateTime.now() );
        ImperialTreeNode imperialTreeNode = new GUIDImperialTrieNode();
        imperialTreeNode.setGuid( guid );
        imperialTreeNode.setType( UOIUtils.createLocalJavaClass( volume.getClass().getName() ) );

        return imperialTreeNode;
    }

    public VolumeOperatorFactory  getVolumeOperatorFactory(){
        return this.factory;
    }
    protected String getVolumeMetaType( TreeNode treeNode ){
        return treeNode.className().replace("Titan","");
    }
}
