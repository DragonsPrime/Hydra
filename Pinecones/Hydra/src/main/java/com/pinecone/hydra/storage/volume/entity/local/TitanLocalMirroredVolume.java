package com.pinecone.hydra.storage.volume.entity.local;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ArchLogicVolume;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.source.MirroredVolumeManipulator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.List;

public class TitanLocalMirroredVolume extends ArchLogicVolume implements LocalMirroredVolume {
    private MirroredVolumeManipulator mirroredVolumeManipulator;

    public void setMirroredVolumeManipulator( MirroredVolumeManipulator mirroredVolumeManipulator ){
        this.mirroredVolumeManipulator = mirroredVolumeManipulator;
    }

    public TitanLocalMirroredVolume(VolumeTree volumeTree, MirroredVolumeManipulator mirroredVolumeManipulator) {
        super(volumeTree);
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
    public void channelExport(KOMFileSystem fileSystem, FileNode file) throws IOException {

    }

    @Override
    public void streamExport(KOMFileSystem fileSystem, FileNode file) throws IOException {

    }

    @Override
    public void channelReceive(KOMFileSystem fileSystem, FileNode file, FileChannel channel, Number offset, Number endSize) {

    }

    @Override
    public void extendLogicalVolume(GUID physicalGuid) {

    }

    @Override
    public List<GUID> listPhysicalVolume() {
        return null;
    }

    @Override
    public void channelReceive(KOMFileSystem fileSystem, FileNode file, FileChannel channel) throws IOException {
        List<LogicVolume> volumes = this.getChildren();

    }

    @Override
    public void streamReceive(KOMFileSystem fileSystem, FileNode file, InputStream inputStream) {

    }

    @Override
    public void setVolumeTree(VolumeTree volumeTree) {
        this.volumeTree = volumeTree;
    }
}
