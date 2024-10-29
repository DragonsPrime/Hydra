package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

import java.util.ArrayList;
import java.util.List;

public abstract class ArchLogicVolume extends ArchVolume implements LogicVolume{

    protected List<LogicVolume>            children;
    protected VolumeCapacity64             volumeCapacity;

    public ArchLogicVolume(VolumeTree volumeTree) {
        super(volumeTree);
    }

    public ArchLogicVolume(){}



    @Override
    public List<LogicVolume> getChildren() {
        if ( this.children == null || this.children.isEmpty() ){
            ArrayList<LogicVolume> logicVolumes = new ArrayList<>();
            List<TreeNode> nodes = this.volumeTree.getChildren( this.guid );
            for( TreeNode node : nodes ){
                logicVolumes.add( (LogicVolume) node);
            }
            this.children = logicVolumes;
        }
        return this.children;
    }

    @Override
    public void setChildren(List<LogicVolume> children) {
        this.children = children;
    }

    @Override
    public VolumeCapacity64 getVolumeCapacity() {
        return this.volumeCapacity;
    }

    @Override
    public void setVolumeCapacity(VolumeCapacity64 volumeCapacity) {
        this.volumeCapacity = volumeCapacity;
    }


}
