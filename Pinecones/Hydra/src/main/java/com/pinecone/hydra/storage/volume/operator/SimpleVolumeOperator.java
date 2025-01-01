package com.pinecone.hydra.storage.volume.operator;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.entity.local.LocalSimpleVolume;
import com.pinecone.hydra.storage.volume.source.SimpleVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.VolumeMasterManipulator;
import com.pinecone.hydra.unit.imperium.ImperialTreeNode;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleVolumeOperator extends ArchVolumeOperator  implements VolumeOperator{
    protected Map<GUID, LogicVolume>  cacheMap  =  new HashMap<>();
    protected SimpleVolumeManipulator       simpleVolumeManipulator;

    public SimpleVolumeOperator( VolumeOperatorFactory  factory ){
        this( factory.getMasterManipulator(), factory.getVolumeManager() );
        this.factory = factory;
    }

    public SimpleVolumeOperator(VolumeMasterManipulator masterManipulator, VolumeManager volumeManager) {
        super(masterManipulator, volumeManager);
        this.simpleVolumeManipulator    =  masterManipulator.getSimpleVolumeManipulator();
    }

    @Override
    public GUID insert(TreeNode treeNode) {
        LocalSimpleVolume simpleVolume = ( LocalSimpleVolume ) treeNode;
        ImperialTreeNode imperialTreeNode = this.affirmPreinsertionInitialize(simpleVolume);
        GUID guid = simpleVolume.getGuid();
        VolumeCapacity64 volumeCapacity = simpleVolume.getVolumeCapacity();
        if ( volumeCapacity.getVolumeGuid() == null ){
            volumeCapacity.setVolumeGuid( guid );
        }

        this.imperialTree.insert(imperialTreeNode);
        this.simpleVolumeManipulator.insert( simpleVolume );
        this.volumeCapacityManipulator.insert( volumeCapacity );
        return guid;
    }

    @Override
    public void purge(GUID guid) {
        List<GUIDImperialTrieNode> children = this.imperialTree.getChildren(guid);
        for( GUIDImperialTrieNode node : children ){
            TreeNode newInstance = (TreeNode)node.getType().newInstance( new Class<? >[]{this.getClass()}, this );
            VolumeOperator operator = this.factory.getOperator(this.getVolumeMetaType(newInstance));
            operator.purge( node.getGuid() );
        }
        this.removeNode( guid );
    }

    @Override
    public SimpleVolume get(GUID guid)  {
        SimpleVolume simpleVolume = this.simpleVolumeManipulator.getSimpleVolume(guid);
        VolumeCapacity64 volumeCapacity = this.volumeCapacityManipulator.getVolumeCapacity(guid);
        simpleVolume.setVolumeCapacity( volumeCapacity );
        simpleVolume.setVolumeTree( this.volumeManager);
        simpleVolume.setKenVolumeFileSystem();
        try {
            simpleVolume.assembleSQLiteExecutor();
        } catch (SQLException e) {
            throw new ProxyProvokeHandleException(e);
        }
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
        GUIDImperialTrieNode node = this.imperialTree.getNode(guid);
        this.imperialTree.purge( guid );
        this.imperialTree.removeCachePath( guid );
        this.simpleVolumeManipulator.remove( guid );
    }

}
