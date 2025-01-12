package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.RandomAccessChanface;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.TitanStorageExportIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripBufferInJob;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripBufferOutJob;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.umct.IlleagalResponseException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class TitanStripedExport64 implements StripedExport64{
    protected VolumeManager             volumeManager;

    protected StorageExportIORequest    storageExportIORequest;

    protected Chanface                  channel;

    protected StripedVolume             stripedVolume;

    protected OnVolumeFileSystem        kenVolumeFileSystem;

    public TitanStripedExport64( StripedExportEntity64 entity ){
        this.volumeManager              = entity.getVolumeManager();
        this.storageExportIORequest     = entity.getStorageIORequest();
        this.channel                    = entity.getChannel();
        this.stripedVolume              = entity.getStripedVolume();
        this.kenVolumeFileSystem        = new KenVolumeFileSystem( this.volumeManager );
    }
    @Override
    public StorageIOResponse export(Chanface chanface) throws IOException, SQLException {
        //初始化参数
        List<LogicVolume> volumes = this.stripedVolume.queryChildren();
        int jobCount = volumes.size();

        int StripResidentCacheAllotRatio = volumeManager.getConfig().getStripResidentCacheAllotRatio();
        SQLiteExecutor sqLiteExecutor = this.stripedVolume.getSQLiteExecutor();

        Processum supProc = this.volumeManager.getSuperiorProcess();
        MasterVolumeGram masterVolumeGram = this.createMasterVolumeGram(supProc,jobCount,StripResidentCacheAllotRatio);

        // 创建文件写入线程
        createBufferOutJob( masterVolumeGram, this.storageExportIORequest.getSize().longValue());

        // 处理每个卷的线程
        createAndStartVolumeThreads(volumes, sqLiteExecutor,  masterVolumeGram );

        // 同步等待任务完成并处理异常
        this.waitForTaskCompletion(masterVolumeGram);
        //masterVolumeGram.majorJobCountDownLatchWait();

        supProc.getTaskManager().erase(masterVolumeGram);


        return null;
    }

    @Override
    public StorageIOResponse export(Chanface chanface, Number offset, Number endSize) throws IOException, SQLException {
        //初始化参数
        List<LogicVolume> volumes = this.stripedVolume.queryChildren();
        int jobCount = volumes.size();

        int StripResidentCacheAllotRatio = volumeManager.getConfig().getStripResidentCacheAllotRatio();
        SQLiteExecutor sqLiteExecutor = this.stripedVolume.getSQLiteExecutor();

        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        MasterVolumeGram masterVolumeGram = this.createMasterVolumeGram(hydrarum,jobCount,StripResidentCacheAllotRatio);

        // 创建文件写入线程
        createBufferOutJob( masterVolumeGram, this.storageExportIORequest.getSize().longValue());

        // 处理每个卷的线程
        createAndStartVolumeThreads(volumes, sqLiteExecutor,  masterVolumeGram );

        // 同步等待任务完成并处理异常
        this.waitForTaskCompletion(masterVolumeGram);
        //masterVolumeGram.majorJobCountDownLatchWait();

        hydrarum.getTaskManager().erase(masterVolumeGram);

        return null;
    }

    @Override
    public StorageIOResponse export(RandomAccessChanface randomAccessChanface) throws IOException, SQLException {
        return null;
    }

    @Override
    public StorageIOResponse export(RandomAccessChanface randomAccessChanface, Number offset, Number endSize) throws IOException, SQLException {
        return null;
    }

    private MasterVolumeGram createMasterVolumeGram(Processum supProcess, int jobCount, int StripResidentCacheAllotRatio ) {
        Number stripSize = this.volumeManager.getConfig().getDefaultStripSize();

        MasterVolumeGram masterVolumeGram = new MasterVolumeGram(this.stripedVolume.getGuid().toString(), supProcess,jobCount, StripResidentCacheAllotRatio, stripSize.intValue());
        supProcess.getTaskManager().add(masterVolumeGram);
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

            String sourceName = this.kenVolumeFileSystem.getStripMetaSourceName(sqLiteExecutor, volume.getGuid(), this.storageExportIORequest.getStorageObjectGuid());
            if ( sourceName == null ){
                continue;
            }
            int code = this.kenVolumeFileSystem.getStripMetaCode(sqLiteExecutor, volume.getGuid(), this.storageExportIORequest.getStorageObjectGuid());
            File file = new File(sourceName);
            StorageExportIORequest titanStorageExportIORequest = new TitanStorageExportIORequest();
            titanStorageExportIORequest.setStorageObjectGuid( this.storageExportIORequest.getStorageObjectGuid() );
            titanStorageExportIORequest.setSourceName(sourceName);
            titanStorageExportIORequest.setSize(file.length());


            TitanStripBufferInJob exportJob = new TitanStripBufferInJob(masterVolumeGram,this, volume, titanStorageExportIORequest,code);
            LocalStripedTaskThread taskThread = new LocalStripedTaskThread(this.stripedVolume.getName() + code, masterVolumeGram, exportJob);
            for( int i = code; i < masterVolumeGram.getCacheGroup().size(); i += masterVolumeGram.getJobCount() ){
                masterVolumeGram.getCacheGroup().get( i ).setBufferWriteThreadId( taskThread.getId() );
            }
            masterVolumeGram.getTaskManager().add(taskThread);
            taskThread.start();

        }

    }

    private void waitForTaskCompletion(MasterVolumeGram masterVolumeGram) throws ProxyProvokeHandleException {
//        try {
//            masterVolumeGram.getTaskManager().syncWaitingTerminated();
//        }
//        catch (Exception e) {
//            throw new ProxyProvokeHandleException(e);
//        }

        try{
            Object ret = masterVolumeGram.getMajorJobFuture().get();

            if ( ret instanceof Exception ) {
                throw new ProxyProvokeHandleException( (Exception) ret );
            }

            if ( !(Boolean) ret ) {
                throw new IllegalStateException( "Buffer-To-File thread has been returned `false`, which is expected `true`." );
            }
        }
        catch ( InterruptedException | ExecutionException e ) {
            throw new ProxyProvokeHandleException( e );
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
}
