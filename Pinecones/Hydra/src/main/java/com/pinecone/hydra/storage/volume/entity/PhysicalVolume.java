package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripLockEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.TerminalStateRecord;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public interface PhysicalVolume extends Volume{
    MountPoint getMountPoint();
    void setMountPoint( MountPoint mountPoint );

    MiddleStorageObject channelReceive(VolumeManager volumeManager, ReceiveStorageObject receiveStorageObject, FileChannel channel, String destDirPath ) throws IOException, SQLException;
    MiddleStorageObject channelReceive(VolumeManager volumeManager, ReceiveStorageObject receiveStorageObject, FileChannel channel, String destDirPath, Number offset, Number endSize ) throws IOException;

    MiddleStorageObject channelExport(VolumeManager volumeManager, ExportStorageObject exportStorageObject, FileChannel channel ) throws IOException;
    MiddleStorageObject channelRaid0Export(VolumeManager volumeManager, ExportStorageObject exportStorageObject, FileChannel channel, byte[] buffer, Number offset, Number endSize, int jobCode, int jobNum, AtomicInteger counter, StripLockEntity lockEntity, ArrayList<TerminalStateRecord> terminalStateRecordGroup) throws IOException;

}
