package com.pinecone.hydra.storage.volume.entity.local.spanned;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ArchLogicVolume;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.entity.local.LocalSpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.TitanSimpleChannelReceiverEntity64;
import com.pinecone.hydra.storage.volume.entity.local.spanned.export.TitanSpannedChannelExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.spanned.receive.TitanSpannedChannelReceiveEntity64;
import com.pinecone.hydra.storage.volume.source.SpannedVolumeManipulator;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

public class TitanLocalSpannedVolume extends ArchLogicVolume implements LocalSpannedVolume {
    private SpannedVolumeManipulator spannedVolumeManipulator;

    public TitanLocalSpannedVolume(VolumeTree volumeTree, SpannedVolumeManipulator spannedVolumeManipulator) {
        super(volumeTree);
        this.spannedVolumeManipulator = spannedVolumeManipulator;
    }
    public TitanLocalSpannedVolume( VolumeTree volumeTree ){
        super( volumeTree );
    }

    public TitanLocalSpannedVolume(){
    }
    public void setSpannedVolumeManipulator( SpannedVolumeManipulator spannedVolumeManipulator ){
        this.spannedVolumeManipulator = spannedVolumeManipulator;
    }
    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }


    @Override
    public void extendLogicalVolume(GUID physicalGuid) {

    }

    @Override
    public List<GUID> listPhysicalVolume() {
        return null;
    }


    @Override
    public void setVolumeTree(VolumeTree volumeTree) {
        this.volumeTree = volumeTree;
    }

    @Override
    public MiddleStorageObject channelReceive(ReceiveStorageObject receiveStorageObject, String destDirPath, FileChannel channel) throws IOException, SQLException {
        TitanSpannedChannelReceiveEntity64 titanSpannedChannelReceiveEntity64 = new TitanSpannedChannelReceiveEntity64( this.volumeTree,receiveStorageObject,destDirPath,channel,this );
        MiddleStorageObject middleStorageObject = titanSpannedChannelReceiveEntity64.receive();
        middleStorageObject.setBottomGuid( this.guid );
        return middleStorageObject;
    }

    @Override
    public MiddleStorageObject channelReceive(ReceiveStorageObject receiveStorageObject, String destDirPath, FileChannel channel, Number offset, Number endSize) throws IOException, SQLException {
        TitanSpannedChannelReceiveEntity64 titanSpannedChannelReceiveEntity64 = new TitanSpannedChannelReceiveEntity64( this.volumeTree,receiveStorageObject,destDirPath,channel,this );
        MiddleStorageObject middleStorageObject = titanSpannedChannelReceiveEntity64.receive( offset, endSize );
        middleStorageObject.setBottomGuid( this.guid );
        return middleStorageObject;
    }

    @Override
    public MiddleStorageObject channelExport(ExportStorageObject exportStorageObject, FileChannel channel) throws IOException, SQLException {
        TitanSpannedChannelExportEntity64 titanSpannedChannelExportEntity64 = new TitanSpannedChannelExportEntity64( this.volumeTree, exportStorageObject,channel, this );
        return titanSpannedChannelExportEntity64.export();
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public boolean existStorageObject(GUID storageObject) throws SQLException {
        List<LogicVolume> volumes = this.getChildren();
        for( LogicVolume volume : volumes ){
            if ( volume.existStorageObject( storageObject ) ){
                return true;
            }
        }
        return false;
    }

    private boolean checkCapacity(long fileSize, VolumeCapacity64 capacity){
        long remainingCapacity = capacity.getDefinitionCapacity() - capacity.getUsedSize();
        return remainingCapacity > fileSize;
    }

    private long getFreeSpace( LogicVolume volume ){
        return volume.getVolumeCapacity().getDefinitionCapacity() - volume.getVolumeCapacity().getUsedSize();
    }
}
