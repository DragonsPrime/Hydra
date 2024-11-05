package com.pinecone.hydra.storage.volume.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.entity.local.LocalSpannedVolume;
import com.pinecone.hydra.storage.volume.source.SpannedVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.VolumeMasterManipulator;
import com.pinecone.hydra.unit.udtt.DistributedTreeNode;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpannedVolumeOperator extends ArchVolumeOperator  implements VolumeOperator{
    protected Map<GUID, LogicVolume> cacheMap  =  new HashMap<>();
    protected SpannedVolumeManipulator     SpannedVolumeManipulator;

    public SpannedVolumeOperator( VolumeOperatorFactory  factory ){
        this( factory.getMasterManipulator(), factory.getVolumeManager() );
        this.factory = factory;
    }

    public SpannedVolumeOperator(VolumeMasterManipulator masterManipulator, VolumeManager volumeManager) {
        super(masterManipulator, volumeManager);
        this.SpannedVolumeManipulator = masterManipulator.getSpannedVolumeManipulator();
    }

    @Override
    public GUID insert(TreeNode treeNode) {
        LocalSpannedVolume simpleVolume = ( LocalSpannedVolume ) treeNode;
        DistributedTreeNode distributedTreeNode = this.affirmPreinsertionInitialize(simpleVolume);
        GUID guid = simpleVolume.getGuid();
        VolumeCapacity64 volumeCapacity = simpleVolume.getVolumeCapacity();
        if ( volumeCapacity.getVolumeGuid() == null ){
            volumeCapacity.setVolumeGuid( guid );
        }

        this.distributedTrieTree.insert( distributedTreeNode );
        this.SpannedVolumeManipulator.insert( simpleVolume );
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
    public TreeNode get(GUID guid) {
        SpannedVolume spannedVolume = this.SpannedVolumeManipulator.getSpannedVolume(guid);
        VolumeCapacity64 volumeCapacity = this.volumeCapacityManipulator.getVolumeCapacity(guid);
        spannedVolume.setVolumeCapacity( volumeCapacity );
        spannedVolume.setVolumeTree( this.volumeManager);
        spannedVolume.setKenVolumeFileSystem();
        return spannedVolume;
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
        this.SpannedVolumeManipulator.remove( guid );
    }
}
