package com.pinecone.hydra.storage.volume.entity.local.simple;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchLogicVolume;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.entity.local.LocalSimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.simple.export.TitanSimpleChannelExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.TitanSimpleChannelReceiverEntity64;
import com.pinecone.hydra.storage.volume.source.SimpleVolumeManipulator;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TitanLocalSimpleVolume extends ArchLogicVolume implements LocalSimpleVolume {
    private SimpleVolumeManipulator simpleVolumeManipulator;


    public TitanLocalSimpleVolume(VolumeManager volumeManager, SimpleVolumeManipulator simpleVolumeManipulator) {
        super(volumeManager);
        this.simpleVolumeManipulator = simpleVolumeManipulator;
    }

    public TitanLocalSimpleVolume( VolumeManager volumeManager){
        super(volumeManager);
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
    public void extendLogicalVolume(GUID physicalGuid) {
        this.simpleVolumeManipulator.extendLogicalVolume( this.guid, physicalGuid );
    }

    @Override
    public List<GUID> listPhysicalVolume() {
        return this.simpleVolumeManipulator.listPhysicalVolume( this.guid );
    }


    @Override
    public MiddleStorageObject channelReceive(ReceiveStorageObject receiveStorageObject,String destDirPath, FileChannel channel) throws IOException, SQLException {
        TitanSimpleChannelReceiverEntity64 titanSimpleChannelReceiverEntity64 = new TitanSimpleChannelReceiverEntity64( this.volumeManager, receiveStorageObject, destDirPath, channel, this );
        MiddleStorageObject middleStorageObject = titanSimpleChannelReceiverEntity64.receive();
        middleStorageObject.setBottomGuid( this.guid );
        this.saveMate( middleStorageObject , receiveStorageObject.getName());
        return middleStorageObject;
    }

    @Override
    public MiddleStorageObject channelReceive(ReceiveStorageObject receiveStorageObject, String destDirPath, FileChannel channel, Number offset, Number endSize) throws IOException, SQLException {
        TitanSimpleChannelReceiverEntity64 titanSimpleChannelReceiverEntity64 = new TitanSimpleChannelReceiverEntity64( this.volumeManager, receiveStorageObject, destDirPath, channel, this );
        MiddleStorageObject middleStorageObject = titanSimpleChannelReceiverEntity64.receive(offset,endSize);
        middleStorageObject.setBottomGuid( this.guid );
        this.saveMate( middleStorageObject , receiveStorageObject.getName());
        return middleStorageObject;
    }

    @Override
    public MiddleStorageObject channelExport(ExportStorageObject exportStorageObject, FileChannel channel) throws IOException {
        TitanSimpleChannelExportEntity64 titanSimpleChannelExportEntity64 = new TitanSimpleChannelExportEntity64( this.volumeManager, exportStorageObject, channel );
        return titanSimpleChannelExportEntity64.export();
    }

    @Override
    public MiddleStorageObject channelRaid0Export(ExportStorageObject exportStorageObject, FileChannel channel, byte[] buffer, Number offset, Number endSize, int jobCode, int jobNum, AtomicInteger counter) throws IOException, SQLException {
        TitanSimpleChannelExportEntity64 titanSimpleChannelExportEntity64 = new TitanSimpleChannelExportEntity64( this.volumeManager, exportStorageObject, channel );
        return titanSimpleChannelExportEntity64.raid0Export( buffer, offset, endSize, jobCode, jobNum, counter );
    }

    @Override
    public void setVolumeTree(VolumeManager volumeManager) {
        this.volumeManager = volumeManager;
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
    public boolean existStorageObject(GUID storageObject) throws SQLException {
        GUID physicsGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume(this.guid);
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsGuid);
        String url = physicalVolume.getMountPoint().getMountPoint()+ "/" +this.guid+".db";
        SQLiteExecutor sqLiteExecutor = new SQLiteExecutor( new SQLiteHost(url) );
        return this.kenVolumeFileSystem.existStorageObject( sqLiteExecutor, storageObject );
    }

    private void saveMate(MiddleStorageObject middleStorageObject , String storageObjectName) throws SQLException {
        GUID physicsGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume(this.guid);
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsGuid);
        String url = physicalVolume.getMountPoint().getMountPoint()+ "\\" +this.guid+".db";
        File file = new File(url);
        long totalSpace = file.getTotalSpace();
        SQLiteExecutor sqLiteExecutor = new SQLiteExecutor( new SQLiteHost(url) );
        if( !kenVolumeFileSystem.existStorageObject( sqLiteExecutor, middleStorageObject.getObjectGuid() ) ){
            this.kenVolumeFileSystem.insertKVFSTable( middleStorageObject.getObjectGuid(), storageObjectName, sqLiteExecutor );
        }
//        long newTotalSpace = file.getTotalSpace();
//        VolumeCapacity64 physicalVolumeVolumeCapacity = physicalVolume.getVolumeCapacity();
//        physicalVolumeVolumeCapacity.setUsedSize( physicalVolumeVolumeCapacity.getUsedSize() - ( totalSpace - newTotalSpace ) );
//        this.volumeCapacity.setUsedSize( this.volumeCapacity.getUsedSize() - ( totalSpace - newTotalSpace ) );
    }

    @Override
    public void build() throws SQLException {
        PhysicalVolume smallestCapacityPhysicalVolume = this.volumeManager.getSmallestCapacityPhysicalVolume();
        String url = smallestCapacityPhysicalVolume.getMountPoint().getMountPoint() + "/" + this.guid + ".db";
        SQLiteExecutor sqLiteExecutor = new SQLiteExecutor( new SQLiteHost(url) );
        this.kenVolumeFileSystem.createKVFSMetaTable( sqLiteExecutor );
        this.volumeManager.put( this );
        this.kenVolumeFileSystem.insertKVFSMateTable( smallestCapacityPhysicalVolume.getGuid(), this.getGuid() );
    }

    @Override
    public void storageExpansion(GUID volumeGuid) {
        this.extendLogicalVolume( volumeGuid );
    }
}
