package com.pinecone.hydra.storage.volume.entity.local.striped.export;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.LocalStripedTaskThread;
import com.pinecone.hydra.storage.volume.entity.local.striped.TerminalStateRecord;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanLocalStripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripChannelBufferToFileJob;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripChannelBufferWriteJob;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanStripLockEntity;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.storage.volume.kvfs.OnVolumeFileSystem;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.system.Hydrarum;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
        List<LogicVolume> volumes = this.stripedVolume.getChildren();
        Number stripSize = this.volumeManager.getConfig().getDefaultStripSize();
        int jobNum = volumes.size();
        SQLiteExecutor sqLiteExecutor = this.stripedVolume.getSQLiteExecutor();

        List<byte[]> buffers = this.initializationBuffer(jobNum, jobNum * stripSize.intValue());

        AtomicInteger currentBufferCode = new AtomicInteger(0);
        AtomicInteger counter = new AtomicInteger(0);
        ArrayList<Object> lockGroup = new ArrayList<>();
        ArrayList<TerminalStateRecord> terminalStateRecordGroup = new ArrayList<>();

        Lock maoLock = new ReentrantLock();

        Hydrarum hydrarum = this.volumeManager.getHydrarum();
        MasterVolumeGram masterVolumeGram = new MasterVolumeGram( this.stripedVolume.getGuid().toString(), hydrarum );
        hydrarum.getTaskManager().add( masterVolumeGram );

        //创建写入文件的线程
        Number bufferToFileSize = jobNum * stripSize.intValue();
        Object bufferToFileLock = new Object();
        lockGroup.add( bufferToFileLock );
        TitanStripLockEntity bufferToFileLockEntity = new TitanStripLockEntity( bufferToFileLock, lockGroup, currentBufferCode, maoLock);
        TitanStripChannelBufferToFileJob bufferToFileJob = new TitanStripChannelBufferToFileJob( buffers,this.channel, currentBufferCode, bufferToFileLockEntity, bufferToFileSize );
        LocalStripedTaskThread bufferToFileThread = new LocalStripedTaskThread( "bufferToFile", masterVolumeGram,bufferToFileJob );
        masterVolumeGram.getTaskManager().add( bufferToFileThread );
        bufferToFileThread.start();

        int index = 0;
        for( LogicVolume volume : volumes ){
            Object lockObject = new Object();
            lockGroup.add( lockObject );

            String sourceName = this.kenVolumeFileSystem.getKVFSFileStripSourceName(sqLiteExecutor, volume.getGuid(), this.exportStorageObject.getStorageObjectGuid());
            File file = new File(sourceName);
            this.exportStorageObject.setSourceName( sourceName );
            this.exportStorageObject.setSize( file.length() );

            TitanStripLockEntity lockEntity = new TitanStripLockEntity( lockObject, lockGroup, currentBufferCode, maoLock );

            TitanStripChannelBufferWriteJob exportJob = new TitanStripChannelBufferWriteJob( this, buffers, jobNum, index, lockEntity, counter, volume, terminalStateRecordGroup,bufferToFileSize );
            LocalStripedTaskThread taskThread = new LocalStripedTaskThread( this.stripedVolume.getName()+index,masterVolumeGram,exportJob);
            masterVolumeGram.getTaskManager().add( taskThread );
            taskThread.start();
            ++index;
        }

        try{
            masterVolumeGram.getTaskManager().syncWaitingTerminated();
        }
        catch ( Exception e ) {
            throw new ProxyProvokeHandleException( e );
        }

        return null;
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
}
