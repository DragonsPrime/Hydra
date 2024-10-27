package com.pinecone.hydra.storage.volume.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.entity.local.LocalSimpleVolume;
import com.pinecone.hydra.storage.volume.source.SimpleVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.VolumeMasterManipulator;
import com.pinecone.hydra.unit.udtt.DistributedTreeNode;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleVolumeOperator extends ArchVolumeOperator  implements VolumeOperator{
    protected Map<GUID, LogicVolume>  cacheMap  =  new HashMap<>();
    protected SimpleVolumeManipulator       simpleVolumeManipulator;

    public SimpleVolumeOperator( VolumeOperatorFactory  factory ){
        this( factory.getMasterManipulator(), factory.getVolumeTree() );
        this.factory = factory;
    }

    public SimpleVolumeOperator(VolumeMasterManipulator masterManipulator, VolumeTree volumeTree) {
        super(masterManipulator, volumeTree);
        this.simpleVolumeManipulator    =  masterManipulator.getSimpleVolumeManipulator();
    }

    @Override
    public GUID insert(TreeNode treeNode) {
        LocalSimpleVolume simpleVolume = ( LocalSimpleVolume ) treeNode;
        DistributedTreeNode distributedTreeNode = this.affirmPreinsertionInitialize(simpleVolume);
        GUID guid = simpleVolume.getGuid();
        VolumeCapacity64 volumeCapacity = simpleVolume.getVolumeCapacity();
        if ( volumeCapacity.getVolumeGuid() == null ){
            volumeCapacity.setVolumeGuid( guid );
        }

        this.distributedTrieTree.insert( distributedTreeNode );
        this.simpleVolumeManipulator.insert( simpleVolume );
        this.volumeCapacityManipulator.insert( volumeCapacity );
        return guid;
    }

    @Override
    public void purge(GUID guid) {
        List<GUIDDistributedTrieNode> children = this.distributedTrieTree.getChildren(guid);
        for( GUIDDistributedTrieNode node : children ){
            TreeNode newInstance = (TreeNode)node.getType().newInstance( new Class<? >[]{this.getClass()}, this );
            VolumeOperator operator = this.factory.getOperator(this.getVolumeMetaType(newInstance));
            operator.purge( node.getGuid() );
        }
        this.removeNode( guid );
    }

    @Override
    public SimpleVolume get(GUID guid) {
        SimpleVolume simpleVolume = this.simpleVolumeManipulator.getSimpleVolume(guid);
        VolumeCapacity64 volumeCapacity = this.volumeCapacityManipulator.getVolumeCapacity(guid);
        simpleVolume.setVolumeCapacity( volumeCapacity );
        simpleVolume.setVolumeTree( this.volumeTree );
        return simpleVolume;
    }

    @Override
    public TreeNode get(GUID guid, int depth) {
        return null;
    }

    @Override
    public TreeNode getSelf(GUID guid) {
        return null;
    }

    @Override
    public void update(TreeNode treeNode) {

    }

    @Override
    public void updateName(GUID guid, String name) {

    }
    private void removeNode( GUID guid ){
        GUIDDistributedTrieNode node = this.distributedTrieTree.getNode(guid);
        this.distributedTrieTree.purge( guid );
        this.distributedTrieTree.removeCachePath( guid );
        this.simpleVolumeManipulator.remove( guid );
    }

}
