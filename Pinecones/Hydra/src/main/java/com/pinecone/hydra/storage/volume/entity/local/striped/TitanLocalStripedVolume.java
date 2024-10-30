package com.pinecone.hydra.storage.volume.entity.local.striped;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ArchLogicVolume;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.LocalStripedVolume;
import com.pinecone.hydra.storage.volume.source.StripedVolumeManipulator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

public class TitanLocalStripedVolume extends ArchLogicVolume implements LocalStripedVolume {
    private StripedVolumeManipulator stripedVolumeManipulator;

    public TitanLocalStripedVolume(VolumeTree volumeTree, StripedVolumeManipulator stripedVolumeManipulator) {
        super(volumeTree);
        this.stripedVolumeManipulator = stripedVolumeManipulator;
    }

    public TitanLocalStripedVolume( VolumeTree volumeTree ){
        super( volumeTree );
    }

    public TitanLocalStripedVolume(){
    }


    @Override
    public void extendLogicalVolume(GUID physicalGuid) {

    }

    @Override
    public List<GUID> listPhysicalVolume() {
        return null;
    }


    public void setStripedVolumeManipulator(StripedVolumeManipulator stripedVolumeManipulator ){
        this.stripedVolumeManipulator = stripedVolumeManipulator;
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public void setVolumeTree(VolumeTree volumeTree) {
        this.volumeTree = volumeTree;
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
    public String toString() {
        return this.toJSONString();
    }
}
