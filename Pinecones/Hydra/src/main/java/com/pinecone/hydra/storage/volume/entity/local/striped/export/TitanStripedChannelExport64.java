package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.TitanExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.striped.BufferToFileMate;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripExportFlyweightEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripCacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripExportFlyweightEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripTerminalStateRecord;
import com.pinecone.hydra.storage.volume.entity.local.striped.TerminalStateRecord;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanLocalStripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripChannelBufferToFileJob;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripChannelBufferWriteJob;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripLockEntity;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.system.Hydrarum;
import org.springframework.cglib.core.Local;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TitanStripedChannelExport64 implements StripedChannelExport64{
    private VolumeManager           volumeManager;
    private ExportStorageObject     exportStorageObject;
    private FileChannel             channel;
    private StripedVolume           stripedVolume;
    private OnVolumeFileSystem      kenVolumeFileSystem;

    public TitanStripedChannelExport64(StripedChannelExportEntity entity){
        this.volumeManager = entity.getVolumeManager();
        this.exportStorageObject = entity.getExportStorageObject();
        this.channel = entity.getChannel();
        this.stripedVolume = entity.getStripedVolume();
        this.kenVolumeFileSystem = new KenVolumeFileSystem( this.volumeManager );
    }

    @Override
    public MiddleStorageObject export() throws IOException, SQLException {
        //初始化参数
        List<LogicVolume> volumes = this.stripedVolume.getChildren();
        int jobNum = volumes.size();
        Number stripSize = this.volumeManager.getConfig().getDefaultStripSize();

        int superResolutionRatio = volumeManager.getConfig().getSuperResolutionRatio();
        SQLiteExecutor sqLiteExecutor = this.stripedVolume.getSQLiteExecutor();
        //已废弃，后续优化
        AtomicInteger currentCacheBlockNumber = new AtomicInteger(0);
//        ArrayList<Object> lockGroup = new ArrayList<>();
//        List<CacheBlock> cacheGroup = this.initializeCacheGroup(jobNum, superResolutionRatio, stripSize );
//        byte[] buffer = this.initializationBuffer(jobNum, stripSize.intValue(), superResolutionRatio);

        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        MasterVolumeGram masterVolumeGram = this.createMasterVolumeGram(hydrarum,jobNum,superResolutionRatio);

        //创建文件写入线程
        BufferToFileMate bufferToFileMate = createBufferToFileJob(  currentCacheBlockNumber, masterVolumeGram, this.exportStorageObject.getSize().longValue());

        // 处理每个卷的线程
        createAndStartVolumeThreads(volumes, sqLiteExecutor, currentCacheBlockNumber,  masterVolumeGram, bufferToFileMate.getBufferToFileLock(), bufferToFileMate.getBufferToFileThreadId() );

        // 同步等待任务完成并处理异常
        waitForTaskCompletion(masterVolumeGram);

        return null;
    }


    private List< CacheBlock > initializeCacheGroup( int jobNum, int superResolutionRatio, Number stripSize ){
        ArrayList<CacheBlock> cacheGroup = new ArrayList<>();
        Number currentPosition = 0;
        for( int i = 0; i < jobNum * superResolutionRatio; i++ ){
            StripCacheBlock stripCacheBlock = new StripCacheBlock( i, currentPosition, currentPosition.intValue() + stripSize.intValue() );
            cacheGroup.add( stripCacheBlock );
            currentPosition = currentPosition.intValue() + stripSize.intValue();
        }
        return  cacheGroup;
    }

    private MasterVolumeGram createMasterVolumeGram(Hydrarum hydrarum, int jobNum, int superResolutionRatio ) {
        Number stripSize = this.volumeManager.getConfig().getDefaultStripSize();
        ArrayList<Object> lockGroup = new ArrayList<>();
        List<CacheBlock> cacheGroup = this.initializeCacheGroup(jobNum, superResolutionRatio, stripSize );
        byte[] buffer = this.initializationBuffer(jobNum, stripSize.intValue(), superResolutionRatio);

        MasterVolumeGram masterVolumeGram = new MasterVolumeGram(this.stripedVolume.getGuid().toString(), hydrarum,jobNum, lockGroup, cacheGroup, buffer);
        hydrarum.getTaskManager().add(masterVolumeGram);
        return masterVolumeGram;
    }

    private BufferToFileMate createBufferToFileJob( AtomicInteger currentCacheBlockNumber,  MasterVolumeGram masterVolumeGram, long totalSIze) {
        BufferToFileMate bufferToFileMate = new BufferToFileMate();
        Semaphore bufferToFileLock = new Semaphore(0);
        masterVolumeGram.getLockGroup().add(bufferToFileLock);

        TitanStripLockEntity bufferToFileLockEntity = new TitanStripLockEntity(bufferToFileLock, masterVolumeGram.getLockGroup(), currentCacheBlockNumber, bufferToFileLock);
        LocalStripExportFlyweightEntity exportFlyweight = new LocalStripExportFlyweightEntity( bufferToFileLockEntity);
        exportFlyweight.setJobNum(masterVolumeGram.getJobNum());
        exportFlyweight.setBuffer(masterVolumeGram.getBuffer() );
        exportFlyweight.setCacheBlockGroup(masterVolumeGram.getCacheGroup());

        TitanStripChannelBufferToFileJob bufferToFileJob = new TitanStripChannelBufferToFileJob(this.volumeManager, this.channel, exportFlyweight,totalSIze );
        LocalStripedTaskThread bufferToFileThread = new LocalStripedTaskThread("bufferToFile", masterVolumeGram, bufferToFileJob);
        masterVolumeGram.getTaskManager().add(bufferToFileThread);
        bufferToFileThread.start();

        bufferToFileMate.setBufferToFileLock( bufferToFileLock );
        bufferToFileMate.setBufferToFileThreadId( bufferToFileThread.getId() );
        return bufferToFileMate;
    }

    private void createAndStartVolumeThreads(List<LogicVolume> volumes, SQLiteExecutor sqLiteExecutor,  AtomicInteger currentCacheBlockNumber,  MasterVolumeGram masterVolumeGram, Semaphore bufferToFileLock,  int bufferToFileThreadId) throws SQLException {

        for (LogicVolume volume : volumes) {
            Object lockObject = new Semaphore(0);
            masterVolumeGram.getLockGroup().add(lockObject);

            String sourceName = this.kenVolumeFileSystem.getKVFSFileStripSourceName(sqLiteExecutor, volume.getGuid(), this.exportStorageObject.getStorageObjectGuid());
            int code = this.kenVolumeFileSystem.getKVFSFileStripCode(sqLiteExecutor, volume.getGuid(), this.exportStorageObject.getStorageObjectGuid());
            File file = new File(sourceName);
            ExportStorageObject titanExportStorageObject = new TitanExportStorageObject();
            titanExportStorageObject.setStorageObjectGuid( this.exportStorageObject.getStorageObjectGuid() );
            titanExportStorageObject.setSourceName(sourceName);
            titanExportStorageObject.setSize(file.length());

            //Debug.warnSyn( sourceName, index );

            TitanStripLockEntity lockEntity = new TitanStripLockEntity(lockObject, masterVolumeGram.getLockGroup(), currentCacheBlockNumber, bufferToFileLock);
            StripExportFlyweightEntity flyweightEntity = new LocalStripExportFlyweightEntity( masterVolumeGram.getJobNum(), code, lockEntity);
            flyweightEntity.setBuffer(masterVolumeGram.getBuffer() );
            flyweightEntity.setBufferToFileThreadId( bufferToFileThreadId );

            flyweightEntity.setCacheBlockGroup( masterVolumeGram.getCacheGroup() );

            TitanStripChannelBufferWriteJob exportJob = new TitanStripChannelBufferWriteJob(this, flyweightEntity, volume, titanExportStorageObject);
            LocalStripedTaskThread taskThread = new LocalStripedTaskThread(this.stripedVolume.getName() + code, masterVolumeGram, exportJob);
            for( int i = code; i < masterVolumeGram.getCacheGroup().size(); i += masterVolumeGram.getJobNum() ){
                masterVolumeGram.getCacheGroup().get( i ).setBufferWriteThreadId( taskThread.getId() );
            }
            masterVolumeGram.getTaskManager().add(taskThread);
            taskThread.start();

        }

    }

    private void waitForTaskCompletion(MasterVolumeGram masterVolumeGram) throws ProxyProvokeHandleException {
        try {
            masterVolumeGram.getTaskManager().syncWaitingTerminated();
        } catch (Exception e) {
            throw new ProxyProvokeHandleException(e);
        }
    }

    @Override
    public VolumeManager getVolumeManager() {
        return this.volumeManager;
    }

    @Override
    public ExportStorageObject getExportStorageObject() {
        return this.exportStorageObject;
    }

    @Override
    public FileChannel getFileChannel() {
        return this.channel;
    }

    @Override
    public StripedVolume getStripedVolume() {
        return this.stripedVolume;
    }

    private byte[] initializationBuffer(int jobNum, int bufferSize, int superResolutionRatio ){
        return new byte[jobNum * bufferSize * superResolutionRatio];
    }

    private ArrayList< TerminalStateRecord> initializationTerminalStateRecordGroup( int jobNum ){
        ArrayList<TerminalStateRecord> terminalStateRecords = new ArrayList<>();
        for( int i=0; i < jobNum; i++ ){
            StripTerminalStateRecord stateRecord = new StripTerminalStateRecord();
            terminalStateRecords.add( stateRecord );
        }
        return terminalStateRecords;
    }
}
