package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.volume.VolumeConfig;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class ArchLogicVolume extends ArchVolume implements LogicVolume{

    protected List<LogicVolume>            children;

    protected VolumeCapacity64             volumeCapacity;


    public ArchLogicVolume(VolumeManager volumeManager) {
        super(volumeManager);
        this.kenVolumeFileSystem = new KenVolumeFileSystem( this.volumeManager );
    }

    public ArchLogicVolume(){}



    @Override
    public List<LogicVolume> queryChildren() {
        if ( this.children == null || this.children.isEmpty() ){
            ArrayList<LogicVolume> logicVolumes = new ArrayList<>();
            List<TreeNode> nodes = this.volumeManager.getChildren( this.guid );
            for( TreeNode node : nodes ){
                LogicVolume volume = this.volumeManager.get(node.getGuid());
                logicVolumes.add( volume );
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

    @Override
    public SQLiteExecutor getSQLiteExecutor() throws SQLException {
        VolumeConfig config = this.volumeManager.getConfig();
        GUID physicsVolumeGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume(this.getGuid());
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsVolumeGuid);
        String url = physicalVolume.getMountPoint().getMountPoint()+ config.getPathSeparator() +this.getGuid()+config.getSqliteFileExtension();
        return (SQLiteExecutor) this.volumeManager.getKenusPool().allot(url);
    }
}
