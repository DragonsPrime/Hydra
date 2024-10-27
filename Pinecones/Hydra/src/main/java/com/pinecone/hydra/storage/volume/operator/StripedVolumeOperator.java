package com.pinecone.hydra.storage.volume.operator;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.entity.local.LocalStripedVolume;
import com.pinecone.hydra.storage.volume.source.StripedVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.VolumeMasterManipulator;
import com.pinecone.hydra.unit.udtt.DistributedTreeNode;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StripedVolumeOperator extends ArchVolumeOperator  implements VolumeOperator{
    protected Map<GUID, LogicVolume> cacheMap  =  new HashMap<>();
    protected StripedVolumeManipulator          stripedVolumeManipulator;

    public StripedVolumeOperator( VolumeOperatorFactory  factory ){
        this( factory.getMasterManipulator(), factory.getVolumeTree() );
        this.factory = factory;
    }

    public StripedVolumeOperator(VolumeMasterManipulator masterManipulator, VolumeTree volumeTree) {
        super(masterManipulator, volumeTree);
        this.stripedVolumeManipulator = masterManipulator.getStripedVolumeManipulator();
    }

    @Override
    public GUID insert(TreeNode treeNode) {
        LocalStripedVolume stripedVolume = ( LocalStripedVolume ) treeNode;
        DistributedTreeNode distributedTreeNode = this.affirmPreinsertionInitialize(stripedVolume);
        GUID guid = stripedVolume.getGuid();
        VolumeCapacity64 volumeCapacity = stripedVolume.getVolumeCapacity();
        if ( volumeCapacity.getVolumeGuid() == null ){
            volumeCapacity.setVolumeGuid( guid );
        }

        this.distributedTrieTree.insert( distributedTreeNode );
        this.stripedVolumeManipulator.insert( stripedVolume );
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
        StripedVolume stripedVolume = this.stripedVolumeManipulator.getStripedVolume(guid);
        VolumeCapacity64 volumeCapacity = this.volumeCapacityManipulator.getVolumeCapacity(guid);
        stripedVolume.setVolumeCapacity( volumeCapacity );
        stripedVolume.setVolumeTree( this.volumeTree );
        return stripedVolume;
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
        this.distributedTrieTree.purge( guid );
        this.distributedTrieTree.removeCachePath( guid );
        this.stripedVolumeManipulator.remove( guid );
    }
}
