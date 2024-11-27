package com.sparta;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.file.ibatis.hydranium.FileMappingDriver;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.TitanFileChannelKChannel;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.UniformObjectFileSystem;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.MountPoint;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.TitanStorageExportExportIORequest;
import com.pinecone.hydra.storage.TitanStorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.VolumeAllotment;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.entity.local.LocalPhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalSimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalSpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalStripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.spanned.export.stream.TitanSpannedStreamExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.spanned.receive.stream.TitanSpannedStreamReceiveEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.receive.stream.TitanStripedStreamReceiveEntity64;
import com.pinecone.hydra.storage.volume.kvfs.KenVolumeFileSystem;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.volume.ibatis.hydranium.VolumeMappingDriver;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;
import com.pinecone.ulf.util.id.GUIDs;
import com.pinecone.ulf.util.id.GuidAllocator;
import com.sauron.radium.Radium;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;


class Alice extends Radium {
    public Alice( String[] args, CascadeSystem parent ) {
        this( args, null, parent );
    }

    public Alice( String[] args, String szName, CascadeSystem parent ){
        super( args, szName, parent );
    }
    @Override
    public void vitalize () throws Exception {
        KOIMappingDriver koiMappingDriver = new VolumeMappingDriver(
                this, (IbatisClient)this.getMiddlewareManager().getRDBManager().getRDBClientByName( "MySQLKingHydranium" ), this.getDispenserCenter()
        );
        KOIMappingDriver koiFileMappingDriver = new FileMappingDriver(
                this, (IbatisClient)this.getMiddlewareManager().getRDBManager().getRDBClientByName( "MySQLKingHydranium" ), this.getDispenserCenter()
        );

        KOMFileSystem fileSystem = new UniformObjectFileSystem( koiFileMappingDriver );

        UniformVolumeManager volumeTree = new UniformVolumeManager( koiMappingDriver );
        VolumeAllotment volumeAllotment = volumeTree.getVolumeAllotment();


        //this.testSimpleThread();
        //this.testDirectReceive( volumeTree );
        //this.testDirectExport( volumeTree );
        //Debug.trace( volumeTree.queryGUIDByPath( "逻辑卷三/逻辑卷一" ) );
        //volumeTree.get( GUIDs.GUID72( "05e44c4-00022b-0006-20" ) ).build();
        //this.testStripedInsert( volumeTree );
        //this.testSpannedInsert( volumeTree );
        //this.testStripedReceive( volumeTree );
        //this.testStripedExport( volumeTree );
        //this.testHash( volumeTree );
        //this.testSpannedReceive( volumeTree );
        this.testSpannedExport( volumeTree );
    }





    private void testStripedInsert( UniformVolumeManager volumeManager ) throws SQLException {
        VolumeAllotment volumeAllotment = volumeManager.getVolumeAllotment();
        VolumeCapacity64 volumeCapacity1 = volumeAllotment.newVolumeCapacity();
        volumeCapacity1.setDefinitionCapacity( 100*1024*1024 );
        VolumeCapacity64 volumeCapacity2 = volumeAllotment.newVolumeCapacity();
        volumeCapacity2.setDefinitionCapacity( 200*1024*1024 );

        LocalPhysicalVolume physicalVolume1 = volumeAllotment.newLocalPhysicalVolume();
        physicalVolume1.setType("PhysicalVolume");
        physicalVolume1.setVolumeCapacity( volumeCapacity1 );
        physicalVolume1.setName( "C" );
        MountPoint mountPoint1 = volumeAllotment.newMountPoint();
        mountPoint1.setMountPoint("D:/文件系统/簇1");
        physicalVolume1.setMountPoint( mountPoint1 );

        LocalPhysicalVolume physicalVolume2 = volumeAllotment.newLocalPhysicalVolume();
        physicalVolume2.setType("PhysicalVolume");
        physicalVolume2.setVolumeCapacity( volumeCapacity2 );
        physicalVolume2.setName( "D" );
        MountPoint mountPoint2 = volumeAllotment.newMountPoint();
        mountPoint2.setMountPoint( "D:/文件系统/簇2" );
        physicalVolume2.setMountPoint( mountPoint2 );

        VolumeCapacity64 logicVolumeCapacity1 = volumeAllotment.newVolumeCapacity();
        logicVolumeCapacity1.setDefinitionCapacity( 100*1024*1024 );
        VolumeCapacity64 logicVolumeCapacity2 = volumeAllotment.newVolumeCapacity();
        logicVolumeCapacity2.setDefinitionCapacity( 200*1024*1024 );
        VolumeCapacity64 logicVolumeCapacity3 = volumeAllotment.newVolumeCapacity();
        logicVolumeCapacity3.setDefinitionCapacity( 300*1024*1024 );

        LocalSimpleVolume simpleVolume1 = volumeAllotment.newLocalSimpleVolume();
        simpleVolume1.setName( "简单卷一" );
        simpleVolume1.setType( "SimpleVolume" );
        simpleVolume1.setVolumeCapacity( logicVolumeCapacity1 );

        LocalSimpleVolume simpleVolume2 = volumeAllotment.newLocalSimpleVolume();
        simpleVolume2.setName( "简单卷二" );
        simpleVolume2.setVolumeCapacity( logicVolumeCapacity2 );
        simpleVolume2.setType( "SimpleVolume" );

        LocalStripedVolume stripedVolume = volumeAllotment.newLocalStripedVolume();
        stripedVolume.setName( "条带卷" );
        stripedVolume.setVolumeCapacity( logicVolumeCapacity3 );
        stripedVolume.setType( "StripedVolume" );

        volumeManager.insertPhysicalVolume( physicalVolume1 );
        volumeManager.insertPhysicalVolume( physicalVolume2 );
        simpleVolume1.build();
        simpleVolume2.build();
        stripedVolume.build();

        simpleVolume1.extendLogicalVolume( physicalVolume1.getGuid() );
        simpleVolume2.extendLogicalVolume( physicalVolume2.getGuid() );
        stripedVolume.storageExpansion( simpleVolume1.getGuid() );
        stripedVolume.storageExpansion( simpleVolume2.getGuid() );
    }

    private void testSpannedInsert( UniformVolumeManager volumeManager ) throws SQLException {
        VolumeAllotment volumeAllotment = volumeManager.getVolumeAllotment();
        VolumeCapacity64 volumeCapacity1 = volumeAllotment.newVolumeCapacity();
        volumeCapacity1.setDefinitionCapacity( 300*1024*1024 );
        VolumeCapacity64 volumeCapacity2 = volumeAllotment.newVolumeCapacity();
        volumeCapacity2.setDefinitionCapacity( 400*1024*1024 );

        LocalPhysicalVolume physicalVolume1 = volumeAllotment.newLocalPhysicalVolume();
        physicalVolume1.setType("PhysicalVolume");
        physicalVolume1.setVolumeCapacity( volumeCapacity1 );
        physicalVolume1.setName( "E" );
        MountPoint mountPoint1 = volumeAllotment.newMountPoint();
        mountPoint1.setMountPoint("D:/文件系统/簇4");
        physicalVolume1.setMountPoint( mountPoint1 );

        LocalPhysicalVolume physicalVolume2 = volumeAllotment.newLocalPhysicalVolume();
        physicalVolume2.setType("PhysicalVolume");
        physicalVolume2.setVolumeCapacity( volumeCapacity2 );
        physicalVolume2.setName( "F" );
        MountPoint mountPoint2 = volumeAllotment.newMountPoint();
        mountPoint2.setMountPoint( "D:/文件系统/簇5" );
        physicalVolume2.setMountPoint( mountPoint2 );

        VolumeCapacity64 logicVolumeCapacity1 = volumeAllotment.newVolumeCapacity();
        logicVolumeCapacity1.setDefinitionCapacity( 300*1024*1024 );
        VolumeCapacity64 logicVolumeCapacity2 = volumeAllotment.newVolumeCapacity();
        logicVolumeCapacity2.setDefinitionCapacity( 400*1024*1024 );
        VolumeCapacity64 logicVolumeCapacity3 = volumeAllotment.newVolumeCapacity();
        logicVolumeCapacity3.setDefinitionCapacity( 700*1024*1024 );

        LocalSimpleVolume simpleVolume1 = volumeAllotment.newLocalSimpleVolume();
        simpleVolume1.setName( "简单卷四" );
        simpleVolume1.setType( "SimpleVolume" );
        simpleVolume1.setVolumeCapacity( logicVolumeCapacity1 );

        LocalSimpleVolume simpleVolume2 = volumeAllotment.newLocalSimpleVolume();
        simpleVolume2.setName( "简单卷五" );
        simpleVolume2.setVolumeCapacity( logicVolumeCapacity2 );
        simpleVolume2.setType( "SimpleVolume" );

        LocalSpannedVolume spannedVolume = volumeAllotment.newLocalSpannedVolume();
        spannedVolume.setName( "跨区卷" );
        spannedVolume.setVolumeCapacity( logicVolumeCapacity3 );
        spannedVolume.setType( "spannedVolume" );

        volumeManager.insertPhysicalVolume( physicalVolume1 );
        volumeManager.insertPhysicalVolume( physicalVolume2 );
        simpleVolume1.build();
        simpleVolume2.build();

        simpleVolume1.extendLogicalVolume( physicalVolume1.getGuid() );
        simpleVolume2.extendLogicalVolume( physicalVolume2.getGuid() );
        spannedVolume.storageExpansion( simpleVolume1.getGuid() );
        spannedVolume.storageExpansion( simpleVolume2.getGuid() );
        spannedVolume.build();
    }

    void testStripedReceive( UniformVolumeManager volumeManager ) throws IOException, SQLException {
        GuidAllocator guidAllocator = volumeManager.getGuidAllocator();
        LogicVolume volume = volumeManager.get(volumeManager.queryGUIDByPath("条带卷"));
        TitanStorageReceiveIORequest titanReceiveStorageObject = new TitanStorageReceiveIORequest();
        File file = new File("D:\\井盖视频块\\4月13日 (2).mp4");
        titanReceiveStorageObject.setName( "我的视频" );
        titanReceiveStorageObject.setSize( file.length() );
        titanReceiveStorageObject.setStorageObjectGuid( guidAllocator.nextGUID72() );

//        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
//        TitanKChannel titanKChannel = new TitanKChannel( channel );
        try (FileInputStream stream = new FileInputStream(file)){
            TitanStripedStreamReceiveEntity64 receiveEntity = new TitanStripedStreamReceiveEntity64( volumeManager, titanReceiveStorageObject, stream, (StripedVolume) volume );
            volume.receive( receiveEntity );
        }

        //StorageIOResponse storageIOResponse = volume.channelReceive(titanReceiveStorageObject, titanKChannel);
    }

    void testSpannedReceive( UniformVolumeManager volumeManager ) throws IOException, SQLException {
        GuidAllocator guidAllocator = volumeManager.getGuidAllocator();
        LogicVolume volume = volumeManager.get(volumeManager.queryGUIDByPath("跨区卷"));
        TitanStorageReceiveIORequest titanReceiveStorageObject = new TitanStorageReceiveIORequest();
        File file = new File("D:/井盖视频块/4月13日 (2).mp4");
        titanReceiveStorageObject.setName( "视频" );
        titanReceiveStorageObject.setSize( file.length() );
        titanReceiveStorageObject.setStorageObjectGuid( guidAllocator.nextGUID72() );

//        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
//        TitanKChannel titanKChannel = new TitanKChannel( channel );
        try (FileInputStream stream = new FileInputStream( file )){
            TitanSpannedStreamReceiveEntity64 receiveEntity = new TitanSpannedStreamReceiveEntity64( volumeManager, titanReceiveStorageObject, stream, (SpannedVolume) volume);
            StorageIOResponse storageIOResponse = volume.receive( receiveEntity );
       }

    }

    void testStripedExport( UniformVolumeManager volumeManager ) throws Exception {
        File file = new File("D:\\文件系统\\大文件\\我的视频.mp4");
        File originalFile = new File( "D:/井盖视频块/4月13日 (2).mp4" );
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        TitanFileChannelKChannel titanFileChannelKChannel = new TitanFileChannelKChannel( channel );
        LogicVolume volume = volumeManager.get(volumeManager.queryGUIDByPath("条带卷"));
        TitanStorageExportExportIORequest titanExportStorageObject = new TitanStorageExportExportIORequest();
        titanExportStorageObject.setSize( originalFile.length() );
        titanExportStorageObject.setStorageObjectGuid( GUIDs.GUID72("0955566-00008d-0001-ec") );
        //titanExportStorageObject.setSourceName("D:/文件系统/簇1/文件夹/视频_0662cf6-0000cd-0001-10.storage");
        volume.channelExport( titanExportStorageObject, titanFileChannelKChannel);
    }

    void testSpannedExport( UniformVolumeManager volumeManager ) throws IOException {
        File file = new File("D:\\文件系统\\大文件\\我的视频.mp4");
        File originalFile = new File( "D:/井盖视频块/4月13日 (2).mp4" );
//        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
//        TitanKChannel titanKChannel = new TitanKChannel( channel );
        LogicVolume volume = volumeManager.get(volumeManager.queryGUIDByPath("跨区卷"));
        TitanStorageExportExportIORequest titanExportStorageObject = new TitanStorageExportExportIORequest();
        titanExportStorageObject.setSize( originalFile.length() );
        titanExportStorageObject.setStorageObjectGuid( GUIDs.GUID72("095cec0-0003b9-0001-c8") );
        try (FileOutputStream stream = new FileOutputStream( file )){
            TitanSpannedStreamExportEntity64 exportEntity = new TitanSpannedStreamExportEntity64( volumeManager, titanExportStorageObject, stream, (SpannedVolume) volume);
            volume.export( exportEntity );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //titanExportStorageObject.setSourceName("D:/文件系统/簇1/文件夹/视频_0662cf6-0000cd-0001-10.storage");

    }

    void testHash( UniformVolumeManager volumeManager ){
        KenVolumeFileSystem kenVolumeFileSystem = new KenVolumeFileSystem(volumeManager);
//        for( int i = 0; i < 1000000; i++ ){
//            GUID72 guid72 = GUIDs.Dummy72();
//            int hash = kenVolumeFileSystem.KVFSHash(guid72, 2);
//            if( hash != 0 && hash != 1 ){
//                Debug.trace( guid72 );
//            }
//        }
        Debug.trace( kenVolumeFileSystem.KVFSHash( GUIDs.GUID72( "0860ff4-0003ac-0000-cc" ), 2 ) );
    }

}
public class TestVolume {
    public static void main( String[] args ) throws Exception {
        Pinecone.init( (Object...cfg )->{
            Alice Alice = (Alice) Pinecone.sys().getTaskManager().add( new Alice( args, Pinecone.sys() ) );
            Alice.vitalize();
            return 0;
        }, (Object[]) args );
    }
}


