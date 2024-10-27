package com.pinecone.hydra.storage.volume.entity.local;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ArchLogicVolume;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.source.SimpleVolumeManipulator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.List;

public class TitanLocalSimpleVolume extends ArchLogicVolume implements LocalSimpleVolume {
    private SimpleVolumeManipulator simpleVolumeManipulator;


    public TitanLocalSimpleVolume(VolumeTree volumeTree, SimpleVolumeManipulator simpleVolumeManipulator) {
        super(volumeTree);
        this.simpleVolumeManipulator = simpleVolumeManipulator;
    }

    public TitanLocalSimpleVolume( VolumeTree volumeTree ){
        super( volumeTree );
    }

    public TitanLocalSimpleVolume(){
    }

    public void setSimpleVolumeManipulator( SimpleVolumeManipulator simpleVolumeManipulator ){
        this.simpleVolumeManipulator = simpleVolumeManipulator;
    }

    @Override
    public List<LogicVolume> getChildren() {
        return super.getChildren();
    }

    @Override
    public void channelExport( KOMFileSystem fileSystem, FileNode file ) throws IOException {
        List<GUID> physicalVolumes = this.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeTree.getPhysicalVolume(physicalVolumes.get(0));
        physicalVolume.channelExport( fileSystem,file );
    }

    @Override
    public void streamExport( KOMFileSystem fileSystem, FileNode file ) throws IOException {
        List<GUID> physicalVolumes = this.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeTree.getPhysicalVolume(physicalVolumes.get(0));
        physicalVolume.streamExport( fileSystem, file );
    }

    @Override
    public void channelReceive(KOMFileSystem fileSystem, FileNode file, FileChannel channel) throws IOException {
        List<GUID> physicalVolumes = this.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeTree.getPhysicalVolume(physicalVolumes.get(0));
        physicalVolume.channelReceive( fileSystem, file, channel );
    }

    @Override
    public void extendLogicalVolume(GUID physicalGuid) {
        this.simpleVolumeManipulator.extendLogicalVolume( this.guid, physicalGuid );
    }

    @Override
    public List<GUID> listPhysicalVolume() {
        return this.simpleVolumeManipulator.lsblk( this.guid );
    }

    @Override
    public void streamReceive(KOMFileSystem fileSystem, FileNode file, InputStream inputStream) throws IOException {
        List<GUID> physicalVolumes = this.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeTree.getPhysicalVolume(physicalVolumes.get(0));
        physicalVolume.streamReceive( fileSystem,file,inputStream );
    }

    @Override
    public void channelReceive(KOMFileSystem fileSystem, FileNode file, FileChannel channel, Number offset, Number endSize) throws IOException {
        List<GUID> physicalVolumes = this.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeTree.getPhysicalVolume(physicalVolumes.get(0));
        physicalVolume.channelReceive( fileSystem,file,channel,offset,endSize );
    }

    @Override
    public void channelReceive(KOMFileSystem fileSystem, FileNode file, GUID frameGuid, int threadNum, int threadId) throws IOException {

    }

    @Override
    public void setVolumeTree(VolumeTree volumeTree) {
        this.volumeTree = volumeTree;
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
