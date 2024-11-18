package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.StorageIORequest;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.TitanStorageIORequest;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripBufferOutJob;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripBufferInJob;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.system.Hydrarum;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Semaphore;

public class TitanStripedChannelExport64 implements StripedChannelExport64{
    private VolumeManager           volumeManager;
    private StorageIORequest        storageIORequest;
    private FileChannel             channel;
    private StripedVolume           stripedVolume;
    private OnVolumeFileSystem      kenVolumeFileSystem;

    public TitanStripedChannelExport64(StripedChannelExportEntity entity){
        this.volumeManager = entity.getVolumeManager();
        this.storageIORequest = entity.getStorageIORequest();
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
         createBufferOutJob( masterVolumeGram, this.storageIORequest.getSize().longValue());

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

    private void  createBufferOutJob(MasterVolumeGram masterVolumeGram, long totalSIze) {
        Semaphore BufferOutLock = new Semaphore(0);

        TitanStripBufferOutJob BufferOutJob = new TitanStripBufferOutJob(masterVolumeGram,this.volumeManager, this.channel,totalSIze, BufferOutLock );
        LocalStripedTaskThread BufferOutThread = new LocalStripedTaskThread("BufferOut", masterVolumeGram, BufferOutJob);
        masterVolumeGram.getTaskManager().add(BufferOutThread);
        BufferOutThread.start();

        masterVolumeGram.applyBufferOutBlockerLatch( BufferOutLock );
        masterVolumeGram.applyBufferOutThreadId( BufferOutThread.getId() );

    }

    private void createAndStartVolumeThreads(List<LogicVolume> volumes, SQLiteExecutor sqLiteExecutor,  MasterVolumeGram masterVolumeGram) throws SQLException {

        for ( LogicVolume volume : volumes ) {

            String sourceName = this.kenVolumeFileSystem.getKVFSFileStripSourceName(sqLiteExecutor, volume.getGuid(), this.storageIORequest.getStorageObjectGuid());
            int code = this.kenVolumeFileSystem.getKVFSFileStripCode(sqLiteExecutor, volume.getGuid(), this.storageIORequest.getStorageObjectGuid());
            File file = new File(sourceName);
            StorageIORequest titanStorageIORequest = new TitanStorageIORequest();
            titanStorageIORequest.setStorageObjectGuid( this.storageIORequest.getStorageObjectGuid() );
            titanStorageIORequest.setSourceName(sourceName);
            titanStorageIORequest.setSize(file.length());


            TitanStripBufferInJob exportJob = new TitanStripBufferInJob(masterVolumeGram,this, volume, titanStorageIORequest,code);
            LocalStripedTaskThread taskThread = new LocalStripedTaskThread(this.stripedVolume.getName() + code, masterVolumeGram, exportJob);
            for( int i = code; i < masterVolumeGram.getCacheGroup().size(); i += masterVolumeGram.getJobCount() ){
                masterVolumeGram.getCacheGroup().get( i ).setBufferWriteThreadId( taskThread.getId() );
            }
            masterVolumeGram.getTaskManager().add(taskThread);
            taskThread.start();

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
    public StorageIORequest getStorageIORequest() {
        return this.storageIORequest;
    }

    @Override
    public FileChannel getFileChannel() {
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
