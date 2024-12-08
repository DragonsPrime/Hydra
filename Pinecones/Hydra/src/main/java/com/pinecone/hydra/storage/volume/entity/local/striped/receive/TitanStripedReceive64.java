package com.pinecone.hydra.storage.volume.entity.local.striped.receive;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.rdb.MappedExecutor;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeConfig;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripReceiverJob;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.system.Hydrarum;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class TitanStripedReceive64 implements StripedReceive64{
    protected Chanface channel;

    protected VolumeManager             volumeManager;

    protected StorageReceiveIORequest   storageReceiveIORequest;

    protected StripedVolume             stripedVolume;

    protected ReceiveEntity             entity;

    protected OnVolumeFileSystem        kenVolumeFileSystem;

    protected SQLiteHost                mSqLiteHost;

    protected MappedExecutor            mappedExecutor;

    public TitanStripedReceive64( StripedReceiveEntity64 entity ){
        this.channel                    = entity.getKChannel();
        this.volumeManager              = entity.getVolumeManager();
        this.storageReceiveIORequest    = entity.getReceiveStorageObject();
        this.kenVolumeFileSystem        = new KenVolumeFileSystem( this.volumeManager );
        this.stripedVolume              = entity.getStripedVolume();
        this.entity                     = entity;
        try {
            this.mappedExecutor             = this.getExecutor();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public StorageIOResponse receive() throws IOException, SQLException {
        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        MasterVolumeGram masterVolumeGram = new MasterVolumeGram( this.stripedVolume.getGuid().toString(), hydrarum );
        hydrarum.getTaskManager().add( masterVolumeGram );
        List<LogicVolume> volumes = this.stripedVolume.getChildren();


        int index = 0;
        for( LogicVolume volume : volumes ){
            TitanStripReceiverJob receiverJob = new TitanStripReceiverJob( this.entity, this.channel, volumes.size(), index, volume, mappedExecutor, 0, this.entity.getReceiveStorageObject().getSize() );
            LocalStripedTaskThread taskThread = new LocalStripedTaskThread(  this.stripedVolume.getName() + index, masterVolumeGram, receiverJob );
            masterVolumeGram.getTaskManager().add( taskThread );
            taskThread.start();

            index ++;
        }
        this.mSqLiteHost.close();
        this.waitForTaskCompletion( masterVolumeGram );
        return null;
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException {
        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        MasterVolumeGram masterVolumeGram = new MasterVolumeGram( this.stripedVolume.getGuid().toString(), hydrarum );
        hydrarum.getTaskManager().add( masterVolumeGram );
        List<LogicVolume> volumes = this.stripedVolume.getChildren();


        int index = 0;
        for( LogicVolume volume : volumes ){
            TitanStripReceiverJob receiverJob = new TitanStripReceiverJob( this.entity, this.channel, volumes.size(), index, volume, this.mappedExecutor, offset, offset.longValue()+endSize.longValue() );
            LocalStripedTaskThread taskThread = new LocalStripedTaskThread(  this.stripedVolume.getName() + index, masterVolumeGram, receiverJob );
            masterVolumeGram.getTaskManager().add( taskThread );
            taskThread.start();

            index ++;
        }

        this.waitForTaskCompletion( masterVolumeGram );
        return null;
    }

    private MappedExecutor getExecutor() throws SQLException {
        VolumeConfig config = this.volumeManager.getConfig();
        GUID physicsVolumeGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume(this.stripedVolume.getGuid());
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsVolumeGuid);
        String url = physicalVolume.getMountPoint().getMountPoint()+ config.getPathSeparator() +this.stripedVolume.getGuid()+ config.getSqliteFileExtension();
        this.mSqLiteHost = new SQLiteHost(url);
        return new SQLiteExecutor( this.mSqLiteHost );
    }

    private void waitForTaskCompletion(MasterVolumeGram masterVolumeGram) throws ProxyProvokeHandleException {
        try {
            masterVolumeGram.getTaskManager().syncWaitingTerminated();
        }
        catch (Exception e) {
            throw new ProxyProvokeHandleException(e);
        }
    }
}
