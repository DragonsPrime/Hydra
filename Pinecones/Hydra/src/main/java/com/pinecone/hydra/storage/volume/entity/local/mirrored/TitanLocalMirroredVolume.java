package com.pinecone.hydra.storage.volume.entity.local.mirrored;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.hydra.storage.InputChannel;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchLogicVolume;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.ExporterEntity;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.local.LocalMirroredVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
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
    public StorageIOResponse channelReceive(StorageReceiveIORequest storageReceiveIORequest, KChannel channel) throws IOException {
        return null;
    }

    @Override
    public StorageIOResponse channelReceive(StorageReceiveIORequest storageReceiveIORequest, KChannel channel, Number offset, Number endSize) throws IOException, SQLException {
        return null;
    }

    @Override
    public StorageIOResponse channelExport(StorageExportIORequest storageExportIORequest, KChannel channel) throws IOException {
        return null;
    }

    @Override
    public StorageIOResponse channelExport(StorageExportIORequest storageExportIORequest, KChannel channel, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException, SQLException {
        return null;
    }

    @Override
    public StorageIOResponse receive(ReceiveEntity entity) throws SQLException, IOException {
        return null;
    }

    @Override
    public StorageIOResponse receive(ReceiveEntity entity, Number offset, Number endSize) throws SQLException, IOException {
        return null;
    }

    @Override
    public StorageIOResponse receive(ReceiveEntity entity, CacheBlock cacheBlock, byte[] buffer) throws SQLException, IOException {
        return null;
    }

    @Override
    public StorageIOResponse export(ExporterEntity entity) throws SQLException, IOException {
        return null;
    }

    @Override
    public StorageIOResponse export(ExporterEntity entity, Number offset, Number endSize) {
        return null;
    }

    @Override
    public boolean existStorageObject(GUID storageObject) throws SQLException {
        return false;
    }

    @Override
    public void build() throws SQLException {

    }

    @Override
    public void storageExpansion(GUID volumeGuid) {

    }
}
