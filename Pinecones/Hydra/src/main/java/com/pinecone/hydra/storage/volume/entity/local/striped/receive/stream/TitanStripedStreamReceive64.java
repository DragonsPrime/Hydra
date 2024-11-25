package com.pinecone.hydra.storage.volume.entity.local.striped.receive.stream;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.rdb.MappedExecutor;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.system.Hydrarum;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class TitanStripedStreamReceive64 implements StripedStreamReceive64{
    protected InputStream  stream;

    protected VolumeManager volumeManager;

    protected StorageReceiveIORequest storageReceiveIORequest;

    protected StripedVolume  stripedVolume;

    protected ReceiveEntity  entity;

    protected OnVolumeFileSystem kenVolumeFileSystem;

    protected SQLiteHost   mSqLiteHost;

    public TitanStripedStreamReceive64( StripedStreamReceiveEntity entity ){
        this.entity = entity;
        this.stream = entity.getStream();
        this.volumeManager = entity.getVolumeManager();
        this.storageReceiveIORequest = entity.getReceiveStorageObject();
        this.kenVolumeFileSystem = new KenVolumeFileSystem( this.volumeManager );
        this.stripedVolume = entity.getStripedVolume();
    }

    @Override
    public StorageIOResponse streamReceive() throws IOException, SQLException {
        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        List<LogicVolume> volumes = this.stripedVolume.getChildren();
        MasterVolumeGram masterVolumeGram = new MasterVolumeGram( this.stripedVolume.getGuid().toString(), hydrarum, volumes.size(), 1, this.volumeManager.getConfig().getDefaultStripSize().intValue() );
        hydrarum.getTaskManager().add( masterVolumeGram );

        MappedExecutor sqLiteExecutor = this.getExecutor();

        int index = 0;
        for( LogicVolume volume : volumes ){

        }
        this.mSqLiteHost.close();
        this.waitForTaskCompletion( masterVolumeGram );
        return null;
    }

    @Override
    public StorageIOResponse streamReceive(Number offset, Number endSize) throws IOException, SQLException {
        return null;
    }

    private MappedExecutor getExecutor() throws SQLException {
        GUID physicsVolumeGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume(this.stripedVolume.getGuid());
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsVolumeGuid);
        String url = physicalVolume.getMountPoint().getMountPoint()+ "/" +this.stripedVolume.getGuid()+".db";
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
