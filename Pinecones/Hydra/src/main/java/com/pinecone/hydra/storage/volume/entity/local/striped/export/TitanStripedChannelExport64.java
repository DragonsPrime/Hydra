package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
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

        SQLiteExecutor sqLiteExecutor = this.stripedVolume.getSQLiteExecutor();
        AtomicInteger currentCacheBlockNumber = new AtomicInteger(0);
        ArrayList<Object> lockGroup = new ArrayList<>();
        List<CacheBlock> cacheGroup = this.initializeCacheGroup(jobNum, volumeManager.getConfig().getSuperResolutionRatio(), stripSize);


        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        MasterVolumeGram masterVolumeGram = this.createMasterVolumeGram(hydrarum);

        //创建文件写入线程
        Semaphore bufferToFileLock = createBufferToFileJob( lockGroup, currentCacheBlockNumber, jobNum, masterVolumeGram, cacheGroup);

        // 处理每个卷的线程
        createAndStartVolumeThreads(volumes, sqLiteExecutor, lockGroup, currentCacheBlockNumber, jobNum, masterVolumeGram, bufferToFileLock);

        // 同步等待任务完成并处理异常
        waitForTaskCompletion(masterVolumeGram);

        return null;
    }

    private List<byte[]> initializeBuffers(int jobNum, Number stripSize) {
        return this.initializationBuffer(jobNum, jobNum * stripSize.intValue());
    }

    private ArrayList<TerminalStateRecord> initializeTerminalStateRecords(int jobNum) {
        return this.initializationTerminalStateRecordGroup(jobNum);
    }

    private List< CacheBlock > initializeCacheGroup( int jobNum, int superResolutionRatio, Number stripSize ){
        ArrayList<CacheBlock> cacheGroup = new ArrayList<>();
        for( int i = 0; i < jobNum * superResolutionRatio; i++ ){
            StripCacheBlock stripCacheBlock = new StripCacheBlock( stripSize, i );
            cacheGroup.add( stripCacheBlock );
        }
        return  cacheGroup;
    }

    private MasterVolumeGram createMasterVolumeGram(Hydrarum hydrarum) {
        MasterVolumeGram masterVolumeGram = new MasterVolumeGram(this.stripedVolume.getGuid().toString(), hydrarum);
        hydrarum.getTaskManager().add(masterVolumeGram);
        return masterVolumeGram;
    }

    private Semaphore createBufferToFileJob( ArrayList<Object> lockGroup, AtomicInteger currentCacheBlockNumber,  int jobNum,  MasterVolumeGram masterVolumeGram, List< CacheBlock > cacheBlockGroup) {
        Semaphore bufferToFileLock = new Semaphore(0);
        lockGroup.add(bufferToFileLock);

        TitanStripLockEntity bufferToFileLockEntity = new TitanStripLockEntity(bufferToFileLock, lockGroup, currentCacheBlockNumber, bufferToFileLock);
        LocalStripExportFlyweightEntity exportFlyweight = new LocalStripExportFlyweightEntity( bufferToFileLockEntity);
        exportFlyweight.setJobNum(jobNum);

        TitanStripChannelBufferToFileJob bufferToFileJob = new TitanStripChannelBufferToFileJob(this.volumeManager, this.channel, exportFlyweight, cacheBlockGroup );
        LocalStripedTaskThread bufferToFileThread = new LocalStripedTaskThread("bufferToFile", masterVolumeGram, bufferToFileJob);
        masterVolumeGram.getTaskManager().add(bufferToFileThread);
        bufferToFileThread.start();

        return bufferToFileLock;
    }

    private void createAndStartVolumeThreads(List<LogicVolume> volumes, SQLiteExecutor sqLiteExecutor, ArrayList<Object> lockGroup, AtomicInteger currentCacheBlockNumber,  int jobNum, MasterVolumeGram masterVolumeGram, Semaphore bufferToFileLock) throws SQLException {
        int index = 0;

        for (LogicVolume volume : volumes) {
            Object lockObject = new Semaphore(0);
            lockGroup.add(lockObject);

            String sourceName = this.kenVolumeFileSystem.getKVFSFileStripSourceName(sqLiteExecutor, volume.getGuid(), this.exportStorageObject.getStorageObjectGuid());
            File file = new File(sourceName);
            this.exportStorageObject.setSourceName(sourceName);
            this.exportStorageObject.setSize(file.length());

            TitanStripLockEntity lockEntity = new TitanStripLockEntity(lockObject, lockGroup, currentCacheBlockNumber, bufferToFileLock);
            StripExportFlyweightEntity flyweightEntity = new LocalStripExportFlyweightEntity( jobNum, index, lockEntity);

            TitanStripChannelBufferWriteJob exportJob = new TitanStripChannelBufferWriteJob(this, flyweightEntity, volume);
            LocalStripedTaskThread taskThread = new LocalStripedTaskThread(this.stripedVolume.getName() + index, masterVolumeGram, exportJob);
            masterVolumeGram.getTaskManager().add(taskThread);
            taskThread.start();

            ++index;
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

    private List< byte[]> initializationBuffer( int bufferNum, int bufferSize ){
        ArrayList<byte[]> buffers = new ArrayList<>();
        for( int i = 0; i < bufferNum; i++ ){
            byte[] buffer = new byte[ bufferSize ];
            buffers.add( buffer );
        }
        return buffers;
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
