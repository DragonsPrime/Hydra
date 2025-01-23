package com.pinecone.hydra.storage.volume.entity.local.striped.receive;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.rdb.MappedExecutor;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.RandomAccessChanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeConfig;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripReceiveBufferInJob;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripReceiveBufferOutJob;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripReceiverJob;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.system.Hydrarum;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class TitanStripedReceive64 implements StripedReceive64{
    protected VolumeManager             volumeManager;

    protected StorageReceiveIORequest   storageReceiveIORequest;

    protected StripedVolume             stripedVolume;

    protected ReceiveEntity             entity;

    protected OnVolumeFileSystem        kenVolumeFileSystem;

    protected MappedExecutor            mappedExecutor;

    public TitanStripedReceive64( StripedReceiveEntity64 entity ){
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
    public StorageIOResponse receive(Chanface chanface) throws IOException, SQLException {
        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        MasterVolumeGram masterVolumeGram = new MasterVolumeGram( this.stripedVolume.getGuid().toString(), hydrarum );
        hydrarum.getTaskManager().add( masterVolumeGram );
        List<LogicVolume> volumes = this.stripedVolume.queryChildren();


        int index = 0;
        masterVolumeGram.setMajorJobCountDownNum( volumes.size() );
        for( LogicVolume volume : volumes ){
            TitanStripReceiverJob receiverJob = new TitanStripReceiverJob(masterVolumeGram, this.entity, chanface, volumes.size(), index, volume, mappedExecutor, 0, this.entity.getReceiveStorageObject().getSize() );
            LocalStripedTaskThread taskThread = new LocalStripedTaskThread(  this.stripedVolume.getName() + index, masterVolumeGram, receiverJob );
            masterVolumeGram.getTaskManager().add( taskThread );
            taskThread.start();

            index ++;
        }
//        this.waitForTaskCompletion( masterVolumeGram );
//        masterVolumeGram.kill();

        masterVolumeGram.majorJobCountDownLatchWait();
        return null;
    }

    @Override
    public StorageIOResponse receive(Chanface chanface,Number offset, Number endSize) throws IOException, SQLException {
        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        MasterVolumeGram masterVolumeGram = new MasterVolumeGram( this.stripedVolume.getGuid().toString(), hydrarum );
        hydrarum.getTaskManager().add( masterVolumeGram );
        List<LogicVolume> volumes = this.stripedVolume.queryChildren();


        int index = 0;
        masterVolumeGram.setMajorJobCountDownNum( volumes.size() );
        for( LogicVolume volume : volumes ){
            TitanStripReceiverJob receiverJob = new TitanStripReceiverJob(masterVolumeGram, this.entity, chanface, volumes.size(), index, volume, this.mappedExecutor, offset, offset.longValue()+endSize.longValue() );
            LocalStripedTaskThread taskThread = new LocalStripedTaskThread(  this.stripedVolume.getName() + index, masterVolumeGram, receiverJob );
            masterVolumeGram.getTaskManager().add( taskThread );
            taskThread.start();

            index ++;
        }

//        this.waitForTaskCompletion( masterVolumeGram );
//        masterVolumeGram.kill();

        masterVolumeGram.majorJobCountDownLatchWait();
        return null;
    }

    @Override
    public StorageIOResponse receive(RandomAccessChanface randomAccessChanface) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        List<LogicVolume> volumes = this.stripedVolume.queryChildren();
        MasterVolumeGram masterVolumeGram = new MasterVolumeGram( this.stripedVolume.getGuid().toString(), hydrarum, volumes.size(), 1, this.volumeManager.getConfig().getDefaultStripSize().intValue() );
        hydrarum.getTaskManager().add( masterVolumeGram );
        MappedExecutor executor = this.getExecutor();

        TitanStripReceiveBufferOutJob bufferOutJob = new TitanStripReceiveBufferOutJob( masterVolumeGram, this.volumeManager, randomAccessChanface, this.storageReceiveIORequest, executor );
        LocalStripedTaskThread taskThread = new LocalStripedTaskThread( "bufferOut",masterVolumeGram, bufferOutJob );
        masterVolumeGram.getTaskManager().add( taskThread );
        masterVolumeGram.applyBufferOutThreadId( taskThread.getId() );
        taskThread.start();

        int index = 0;
        masterVolumeGram.setMajorJobCountDownNum( volumes.size() );
        for( LogicVolume volume : volumes ){
            TitanStripReceiveBufferInJob bufferInJob = new TitanStripReceiveBufferInJob( masterVolumeGram, index,randomAccessChanface,volume );
            LocalStripedTaskThread bufferInThread = new LocalStripedTaskThread(volume.getName(), masterVolumeGram, bufferInJob);
            masterVolumeGram.getTaskManager().add( bufferInThread );
            CacheBlock cacheBlock = masterVolumeGram.getCacheGroup().get(index);
            cacheBlock.setBufferWriteThreadId( bufferInThread.getId() );
            bufferInThread.start();
            index++;
        }

//        this.waitForTaskCompletion( masterVolumeGram );

        masterVolumeGram.majorJobCountDownLatchWait();
        return null;
    }

    @Override
    public StorageIOResponse receive(RandomAccessChanface randomAccessChanface, Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return null;
    }

    private MappedExecutor getExecutor() throws SQLException {
        VolumeConfig config = this.volumeManager.getConfig();
        GUID physicsVolumeGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume(this.stripedVolume.getGuid());
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsVolumeGuid);
        String url = physicalVolume.getMountPoint().getMountPoint()+ config.getPathSeparator() +this.stripedVolume.getGuid()+ config.getSqliteFileExtension();
        return this.volumeManager.getKenusPool().allot(url);
    }

    private void waitForTaskCompletion(MasterVolumeGram masterVolumeGram) throws ProxyProvokeHandleException {
        Semaphore semaphore = new Semaphore(0);
        //semaphore.a
        CountDownLatch latch = new CountDownLatch(10);
        latch.countDown();

        try{
            latch.await();
        }
        catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
            throw new ProxyProvokeHandleException( e );
        }
//        try {
//            masterVolumeGram.getTaskManager().syncWaitingTerminated();
//        }
//        catch (Exception e) {
//            throw new ProxyProvokeHandleException(e);
//        }
    }
}
