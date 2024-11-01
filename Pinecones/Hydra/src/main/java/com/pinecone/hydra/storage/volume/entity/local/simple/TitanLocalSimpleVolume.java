package com.pinecone.hydra.storage.volume.entity.local.simple;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.framework.util.rdb.ResultSession;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ArchLogicVolume;
import com.pinecone.hydra.storage.volume.entity.ExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.local.LocalSimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.simple.export.TitanSimpleChannelExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.TitanSimpleChannelReceiverEntity64;
import com.pinecone.hydra.storage.volume.source.SimpleVolumeManipulator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public void extendLogicalVolume(GUID physicalGuid) {
        this.simpleVolumeManipulator.extendLogicalVolume( this.guid, physicalGuid );
    }

    @Override
    public List<GUID> listPhysicalVolume() {
        return this.simpleVolumeManipulator.lsblk( this.guid );
    }


    @Override
    public MiddleStorageObject channelReceive(ReceiveStorageObject receiveStorageObject,String destDirPath, FileChannel channel) throws IOException, SQLException {
        TitanSimpleChannelReceiverEntity64 titanSimpleChannelReceiverEntity64 = new TitanSimpleChannelReceiverEntity64( this.volumeTree, receiveStorageObject, destDirPath, channel, this );
        MiddleStorageObject middleStorageObject = titanSimpleChannelReceiverEntity64.receive();
        middleStorageObject.setBottomGuid( this.guid );
        this.saveMate( middleStorageObject );
        return middleStorageObject;
    }

    @Override
    public MiddleStorageObject channelReceive(ReceiveStorageObject receiveStorageObject, String destDirPath, FileChannel channel, Number offset, Number endSize) throws IOException, SQLException {
        TitanSimpleChannelReceiverEntity64 titanSimpleChannelReceiverEntity64 = new TitanSimpleChannelReceiverEntity64( this.volumeTree, receiveStorageObject, destDirPath, channel, this );
        MiddleStorageObject middleStorageObject = titanSimpleChannelReceiverEntity64.receive(offset,endSize);
        middleStorageObject.setBottomGuid( this.guid );
        this.saveMate( middleStorageObject );
        return middleStorageObject;
    }

    @Override
    public MiddleStorageObject channelExport(ExportStorageObject exportStorageObject, FileChannel channel) throws IOException {
        TitanSimpleChannelExportEntity64 titanSimpleChannelExportEntity64 = new TitanSimpleChannelExportEntity64( this.volumeTree, exportStorageObject, channel );
        return titanSimpleChannelExportEntity64.export();
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

    @Override
    public boolean existStorageObject(GUID storageObject) throws SQLException {
        GUID physicsGuid = this.volumeTree.getSqlitePhysicsVolume(this.guid);
        PhysicalVolume physicalVolume = this.volumeTree.getPhysicalVolume(physicsGuid);
        String url = physicalVolume.getMountPoint().getMountPoint()+ "\\" +this.guid+".db";
        SQLiteExecutor sqLiteExecutor = new SQLiteExecutor( new SQLiteHost(url) );
        ResultSession query = sqLiteExecutor.query(" SELECT COUNT(*) FROM `table` WHERE `storage_object_guid` = '" + storageObject + "' ");
        ResultSet resultSet = query.getResultSet();
        if( resultSet.next() ){
            int count = resultSet.getInt(1);
            return count != 0;
        }
        return true;
    }

    private void saveMate(MiddleStorageObject middleStorageObject ) throws SQLException {
        GUID physicsGuid = this.volumeTree.getSqlitePhysicsVolume(this.guid);
        if( physicsGuid == null ){
            PhysicalVolume smallestCapacityPhysicalVolume = this.volumeTree.getSmallestCapacityPhysicalVolume();
            this.volumeTree.insertSqliteMeta( smallestCapacityPhysicalVolume.getGuid(), this.getGuid() );
            String url = smallestCapacityPhysicalVolume.getMountPoint().getMountPoint()+ "\\" +this.guid+".db";
            SQLiteExecutor sqLiteExecutor = new SQLiteExecutor( new SQLiteHost(url) );
            sqLiteExecutor.execute( "CREATE TABLE `table`( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `storage_object_guid` VARCHAR(36) );" );
            sqLiteExecutor.execute( "INSERT INTO `table` ( `storage_object_guid` ) VALUES ( '"+ middleStorageObject.getObjectGuid()+ "' )" );
        }
        else {
            PhysicalVolume physicalVolume = this.volumeTree.getPhysicalVolume(physicsGuid);
            String url = physicalVolume.getMountPoint().getMountPoint()+ "\\" +this.guid+".db";
            SQLiteExecutor sqLiteExecutor = new SQLiteExecutor( new SQLiteHost(url) );
            sqLiteExecutor.execute( "INSERT INTO `table` ( `storage_object_guid` ) VALUES ( '"+ middleStorageObject.getObjectGuid()+ "' )" );
        }
    }
}
