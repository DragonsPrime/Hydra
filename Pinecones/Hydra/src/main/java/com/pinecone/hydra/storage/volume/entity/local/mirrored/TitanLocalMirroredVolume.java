package com.pinecone.hydra.storage.volume.entity.local.mirrored;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchLogicVolume;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.LocalMirroredVolume;
import com.pinecone.hydra.storage.volume.source.MirroredVolumeManipulator;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

public class TitanLocalMirroredVolume extends ArchLogicVolume implements LocalMirroredVolume {
    private MirroredVolumeManipulator mirroredVolumeManipulator;

    public void setMirroredVolumeManipulator( MirroredVolumeManipulator mirroredVolumeManipulator ){
        this.mirroredVolumeManipulator = mirroredVolumeManipulator;
    }

    public TitanLocalMirroredVolume(VolumeManager volumeManager, MirroredVolumeManipulator mirroredVolumeManipulator) {
        super(volumeManager);
        this.mirroredVolumeManipulator = mirroredVolumeManipulator;
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }


    @Override
    public void extendLogicalVolume(GUID physicalGuid) {

    }

    @Override
    public List<GUID> listPhysicalVolume() {
        return null;
    }


    @Override
    public void setVolumeTree(VolumeManager volumeManager) {
        this.volumeManager = volumeManager;
    }

    @Override
    public MiddleStorageObject channelReceive(ReceiveStorageObject receiveStorageObject, String destDirPath, FileChannel channel) throws IOException {
        return null;
    }

    @Override
    public MiddleStorageObject channelReceive(ReceiveStorageObject receiveStorageObject, String destDirPath, FileChannel channel, Number offset, Number endSize) throws IOException, SQLException {
        return null;
    }

    @Override
    public MiddleStorageObject channelExport(ExportStorageObject exportStorageObject, FileChannel channel) throws IOException {
        return null;
    }

    @Override
    public boolean existStorageObject(GUID storageObject) throws SQLException {
        return false;
    }
}
