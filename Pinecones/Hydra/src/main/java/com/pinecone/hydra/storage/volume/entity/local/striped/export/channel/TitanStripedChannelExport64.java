package com.pinecone.hydra.storage.volume.entity.local.striped.export.channel;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.TitanStorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripBufferOutJob;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.system.Hydrarum;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Semaphore;

public class TitanStripedChannelExport64 implements StripedChannelExport64{
    private VolumeManager           volumeManager;
    private StorageExportIORequest  storageExportIORequest;
    private Chanface channel;
    private StripedVolume           stripedVolume;
    private OnVolumeFileSystem      kenVolumeFileSystem;

    public TitanStripedChannelExport64(StripedChannelExportEntity entity){
        this.volumeManager = entity.getVolumeManager();
        this.storageExportIORequest = entity.getStorageIORequest();
        this.channel = entity.getChannel();
        this.stripedVolume = entity.getStripedVolume();
        this.kenVolumeFileSystem = new KenVolumeFileSystem( this.volumeManager );
    }

    @Override
    public StorageIOResponse export() throws IOException, SQLException {
        //初始化参数
        List<LogicVolume> volumes = this.stripedVolume.getChildren();
        int jobCount = volumes.size();

        int superResolutionRatio = volumeManager.getConfig().getSuperResolutionRatio();
        SQLiteExecutor sqLiteExecutor = this.stripedVolume.getSQLiteExecutor();

        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        MasterVolumeGram masterVolumeGram = this.createMasterVolumeGram(hydrarum,jobCount,superResolutionRatio);

        // 创建文件写入线程
         createBufferOutJob( masterVolumeGram, this.storageExportIORequest.getSize().longValue());

        // 处理每个卷的线程
        createAndStartVolumeThreads(volumes, sqLiteExecutor,  masterVolumeGram );

        // 同步等待任务完成并处理异常
        this.waitForTaskCompletion(masterVolumeGram);

        return null;
    }

    @Override
    public StorageIOResponse export(Number offset, Number endSize) throws IOException, SQLException {
        //初始化参数
        List<LogicVolume> volumes = this.stripedVolume.getChildren();
        int jobCount = volumes.size();

        int superResolutionRatio = volumeManager.getConfig().getSuperResolutionRatio();
        SQLiteExecutor sqLiteExecutor = this.stripedVolume.getSQLiteExecutor();

        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        MasterVolumeGram masterVolumeGram = this.createMasterVolumeGram(hydrarum,jobCount,superResolutionRatio);

        // 创建文件写入线程
        createBufferOutJob( masterVolumeGram, this.storageExportIORequest.getSize().longValue());

        // 处理每个卷的线程
        createAndStartVolumeThreads(volumes, sqLiteExecutor,  masterVolumeGram );

        // 同步等待任务完成并处理异常
        this.waitForTaskCompletion(masterVolumeGram);

        return null;
    }

    private MasterVolumeGram createMasterVolumeGram(Hydrarum hydrarum, int jobCount, int superResolutionRatio ) {
        Number stripSize = this.volumeManager.getConfig().getDefaultStripSize();

        MasterVolumeGram masterVolumeGram = new MasterVolumeGram(this.stripedVolume.getGuid().toString(), hydrarum,jobCount, superResolutionRatio, stripSize.intValue());
        hydrarum.getTaskManager().add(masterVolumeGram);
        return masterVolumeGram;
    }

    private void  createBufferOutJob(MasterVolumeGram masterVolumeGram, long totalSize) {
        Semaphore BufferOutLock = new Semaphore(0);

        TitanStripBufferOutJob BufferOutJob = new TitanStripBufferOutJob(masterVolumeGram,this.volumeManager, this.channel,totalSize, BufferOutLock );
        LocalStripedTaskThread BufferOutThread = new LocalStripedTaskThread("BufferOut", masterVolumeGram, BufferOutJob);
        masterVolumeGram.getTaskManager().add(BufferOutThread);
        BufferOutThread.start();

        masterVolumeGram.applyBufferOutBlockerLatch( BufferOutLock );
        masterVolumeGram.applyBufferOutThreadId( BufferOutThread.getId() );

    }

    private void createAndStartVolumeThreads(List<LogicVolume> volumes, SQLiteExecutor sqLiteExecutor,  MasterVolumeGram masterVolumeGram) throws SQLException {

        for ( LogicVolume volume : volumes ) {

            String sourceName = this.kenVolumeFileSystem.getKVFSFileStripSourceName(sqLiteExecutor, volume.getGuid(), this.storageExportIORequest.getStorageObjectGuid());
            if ( sourceName == null ){
                continue;
            }
            int code = this.kenVolumeFileSystem.getKVFSFileStripCode(sqLiteExecutor, volume.getGuid(), this.storageExportIORequest.getStorageObjectGuid());
            File file = new File(sourceName);
            StorageExportIORequest titanStorageExportIORequest = new TitanStorageExportIORequest();
            titanStorageExportIORequest.setStorageObjectGuid( this.storageExportIORequest.getStorageObjectGuid() );
            titanStorageExportIORequest.setSourceName(sourceName);
            titanStorageExportIORequest.setSize(file.length());


//            TitanStripBufferInJob exportJob = new TitanStripBufferInJob(masterVolumeGram,this, volume, titanStorageExportIORequest,code);
//            LocalStripedTaskThread taskThread = new LocalStripedTaskThread(this.stripedVolume.getName() + code, masterVolumeGram, exportJob);
//            for( int i = code; i < masterVolumeGram.getCacheGroup().size(); i += masterVolumeGram.getJobCount() ){
//                masterVolumeGram.getCacheGroup().get( i ).setBufferWriteThreadId( taskThread.getId() );
//            }
//            masterVolumeGram.getTaskManager().add(taskThread);
//            taskThread.start();

        }

    }

    private void waitForTaskCompletion(MasterVolumeGram masterVolumeGram) throws ProxyProvokeHandleException {
        try {
            masterVolumeGram.getTaskManager().syncWaitingTerminated();
        }
        catch (Exception e) {
            throw new ProxyProvokeHandleException(e);
        }
    }

    @Override
    public VolumeManager getVolumeManager() {
        return this.volumeManager;
    }

    @Override
    public StorageExportIORequest getStorageIORequest() {
        return this.storageExportIORequest;
    }

    @Override
    public Chanface getFileChannel() {
        return this.channel;
    }

    @Override
    public StripedVolume getStripedVolume() {
        return this.stripedVolume;
    }

    private byte[] initializationBuffer(int jobCount, int bufferSize, int superResolutionRatio ){
        return new byte[jobCount * bufferSize * superResolutionRatio];
    }
}
