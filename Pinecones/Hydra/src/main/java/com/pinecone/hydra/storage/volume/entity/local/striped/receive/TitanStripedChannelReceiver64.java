package com.pinecone.hydra.storage.volume.entity.local.striped.receive;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.rdb.MappedExecutor;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripReceiverJob;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.system.Hydrarum;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

public class TitanStripedChannelReceiver64 implements StripedChannelReceiver64{
    private FileChannel                 fileChannel;
    private VolumeManager               volumeManager;
    private StorageReceiveIORequest storageReceiveIORequest;
    private StripedVolume               stripedVolume;
    private ReceiveEntity               entity;
    private OnVolumeFileSystem          kenVolumeFileSystem;

    public TitanStripedChannelReceiver64( StripedChannelReceiverEntity entity ){
        this.entity = entity;
        this.fileChannel   = entity.getChannel();
        this.volumeManager = entity.getVolumeManager();
        this.storageReceiveIORequest = entity.getReceiveStorageObject();
        this.stripedVolume = entity.getStripedVolume();
        this.kenVolumeFileSystem = new KenVolumeFileSystem( this.volumeManager );
    }

    @Override
    public StorageIOResponse channelReceive() throws IOException, SQLException {
        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        MasterVolumeGram masterVolumeGram = new MasterVolumeGram( this.stripedVolume.getGuid().toString(), hydrarum );
        hydrarum.getTaskManager().add( masterVolumeGram );
        List<LogicVolume> volumes = this.stripedVolume.getChildren();

        MappedExecutor sqLiteExecutor = this.getExecutor();

        int index = 0;
        for( LogicVolume volume : volumes ){
            TitanStripReceiverJob receiverJob = new TitanStripReceiverJob( this.entity, this.fileChannel, volumes.size(), index, volume, sqLiteExecutor );
            LocalStripedTaskThread taskThread = new LocalStripedTaskThread(  this.stripedVolume.getName() + index, masterVolumeGram, receiverJob );
            masterVolumeGram.getTaskManager().add( taskThread );
            taskThread.start();

            index ++;
        }
        return null;
    }

    @Override
    public StorageIOResponse channelReceive(Number offset, Number endSize) throws IOException {
        return null;
    }

    private MappedExecutor getExecutor() throws SQLException {
        GUID physicsVolumeGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume(this.stripedVolume.getGuid());
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsVolumeGuid);
        String url = physicalVolume.getMountPoint().getMountPoint()+ "/" +this.stripedVolume.getGuid()+".db";
        return new SQLiteExecutor( new SQLiteHost(url) );
    }
}
