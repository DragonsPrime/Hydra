package com.sparta;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.file.ibatis.hydranium.FileMappingDriver;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.UniformObjectFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.FileTreeNode;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.MountPoint;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.TitanExportStorageObject;
import com.pinecone.hydra.storage.volume.entity.TitanReceiveStorageObject;
import com.pinecone.hydra.storage.volume.entity.VolumeAllotment;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.entity.local.LocalPhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalSimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.physical.export.TitanDirectChannelExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.TitanDirectChannelReceiveEntity64;
import com.pinecone.hydra.storage.volume.runtime.ArchStripedTaskThread;
import com.pinecone.hydra.storage.volume.runtime.MasterVolumeGram;
import com.pinecone.hydra.storage.volume.runtime.VolumeJob;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.volume.ibatis.hydranium.VolumeMappingDriver;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;
import com.pinecone.ulf.util.id.GUIDs;
import com.sauron.radium.Radium;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
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

        //this.testChannelReceive( fileSystem,volumeTree );
        //this.testRaid0Insert( fileSystem,volumeTree );
        //this.TestRaid0Receive( fileSystem, volumeTree );


        //this.testSimpleThread();
        //this.testDirectReceive( volumeTree );
        //this.testDirectExport( volumeTree );
        //this.testSpannedChannelReceive( volumeTree );
        //this.testSpannedChannelExport( volumeTree );
        Debug.trace( volumeTree.queryGUIDByPath( "逻辑卷三/逻辑卷一" ) );
    }

    private void testDirectReceive(VolumeManager volumeManager) throws IOException {
        TitanReceiveStorageObject titanReceiveStorageObject = new TitanReceiveStorageObject();
        titanReceiveStorageObject.setName("视频");
        titanReceiveStorageObject.setSize(201*1024*1024);
        String destDirPath = "D:\\文件系统\\大文件";
        File sourceFile = new File("D:\\井盖视频块\\4月13日 (2).mp4");
        Path path = sourceFile.toPath();
        FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
        TitanDirectChannelReceiveEntity64 titanDirectChannelReceiveEntity64 = new TitanDirectChannelReceiveEntity64(volumeManager,titanReceiveStorageObject,destDirPath,channel);
        titanDirectChannelReceiveEntity64.receive();
    }

    private void testDirectExport( VolumeManager volumeManager) throws IOException {
        File file = new File("D:\\文件系统\\大文件\\视频.mp4");
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        TitanExportStorageObject titanExportStorageObject = new TitanExportStorageObject();
        titanExportStorageObject.setSize( 201*1024*1024 );
        titanExportStorageObject.setSourceName("D:\\文件系统\\大文件\\视频_d95a91f4.storage");
        TitanDirectChannelExportEntity64 titanDirectChannelExportEntity64 = new TitanDirectChannelExportEntity64(volumeManager, titanExportStorageObject,channel);
        titanDirectChannelExportEntity64.export();
    }


    private void testChannelReceive( KOMFileSystem fileSystem, UniformVolumeManager volumeTree ) throws IOException {
        SimpleVolume simpleVolume  = volumeTree.get(GUIDs.GUID72("03e7f10-0003dd-0002-98")).evinceSimpleVolume();
        simpleVolume.extendLogicalVolume( GUIDs.GUID72("03e7f10-0003dd-0000-84") );
        File sourceFile = new File("D:\\井盖视频块\\4月13日 (2).mp4");
        Path path = sourceFile.toPath();
        FileNode fileNode = fileSystem.getFSNodeAllotment().newFileNode();
        fileNode.setName(sourceFile.getName());
        fileNode.setGuid( fileSystem.getGuidAllocator().nextGUID72() );
        //simpleVolume.channelReceive( fileSystem, fileNode,FileChannel.open(path, StandardOpenOption.READ));
    }

    private void testChannelExport( KOMFileSystem fileSystem, UniformVolumeManager volumeTree ) throws IOException {
        SimpleVolume simpleVolume  = volumeTree.get(GUIDs.GUID72("03e7f10-0003dd-0002-98")).evinceSimpleVolume();
        FileTreeNode fileTreeNode = fileSystem.get(GUIDs.GUID72("0271940-00035d-0001-58"));
        FileNode file = fileTreeNode.evinceFileNode();
        File file1 = new File("D:\\文件系统\\大文件\\视频.mp4");
        //simpleVolume.channelExport(fileSystem, file);
    }

    private void testSpannedChannelReceive( UniformVolumeManager volumeTree ) throws IOException, SQLException {
        LogicVolume volume = volumeTree.get(GUIDs.GUID72("056b342-0001d1-0006-48"));
        TitanReceiveStorageObject titanReceiveStorageObject = new TitanReceiveStorageObject();
        titanReceiveStorageObject.setName( "image" );
        titanReceiveStorageObject.setSize( 85 * 1024 * 1024 );
        File file = new File("E:\\MyFiles\\Picture\\Avatar/cat1.jpg");
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
        MiddleStorageObject middleStorageObject = volume.channelReceive(titanReceiveStorageObject, "s1", channel);
    }

    private void testSpannedChannelExport( UniformVolumeManager volumeTree ) throws IOException, SQLException {
        File file = new File("E:/1.jpg");
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        LogicVolume volume = volumeTree.get(GUIDs.GUID72("056b342-0001d1-0006-48"));
        TitanExportStorageObject titanExportStorageObject = new TitanExportStorageObject();
        titanExportStorageObject.setSize( 85 * 1024 *1024 );
        titanExportStorageObject.setStorageObjectGuid( GUIDs.GUID72("056b6f4-0000b1-0005-cc") );
        titanExportStorageObject.setSourceName("E:\\fs\\cluster\\s1\\image_8e3d1dde.storage");
        volume.channelExport( titanExportStorageObject, channel );
    }

    private void testRaid0Insert( KOMFileSystem fileSystem, UniformVolumeManager volumeTree ){
        VolumeAllotment volumeAllotment = volumeTree.getVolumeAllotment();
        VolumeCapacity64 volumeCapacity1 = volumeAllotment.newVolumeCapacity();
        volumeCapacity1.setDefinitionCapacity( 100*1024*1024 );
        VolumeCapacity64 volumeCapacity2 = volumeAllotment.newVolumeCapacity();
        volumeCapacity2.setDefinitionCapacity( 200*1024*1024 );

        LocalPhysicalVolume physicalVolume1 = volumeAllotment.newLocalPhysicalVolume();
        physicalVolume1.setType("PhysicalVolume");
        physicalVolume1.setVolumeCapacity( volumeCapacity1 );
        physicalVolume1.setName( "E" );
        MountPoint mountPoint1 = volumeAllotment.newMountPoint();
        mountPoint1.setMountPoint("E:/fs/cluster");
        physicalVolume1.setMountPoint( mountPoint1 );

        LocalPhysicalVolume physicalVolume2 = volumeAllotment.newLocalPhysicalVolume();
        physicalVolume2.setType("PhysicalVolume");
        physicalVolume2.setVolumeCapacity( volumeCapacity2 );
        physicalVolume2.setName( "D" );
        MountPoint mountPoint2 = volumeAllotment.newMountPoint();
        mountPoint2.setMountPoint( "E:/fs2/cluster" );
        physicalVolume2.setMountPoint( mountPoint2 );

        VolumeCapacity64 logicVolumeCapacity1 = volumeAllotment.newVolumeCapacity();
        logicVolumeCapacity1.setDefinitionCapacity( 100*1024*1024 );
        VolumeCapacity64 logicVolumeCapacity2 = volumeAllotment.newVolumeCapacity();
        logicVolumeCapacity2.setDefinitionCapacity( 200*1024*1024 );
        VolumeCapacity64 logicVolumeCapacity3 = volumeAllotment.newVolumeCapacity();
        logicVolumeCapacity3.setDefinitionCapacity( 300*1024*1024 );

        LocalSimpleVolume simpleVolume1 = volumeAllotment.newLocalSimpleVolume();
        simpleVolume1.setName( "逻辑卷一" );
        simpleVolume1.setType( "SimpleVolume" );
        simpleVolume1.setVolumeCapacity( logicVolumeCapacity1 );

        LocalSimpleVolume simpleVolume2 = volumeAllotment.newLocalSimpleVolume();
        simpleVolume2.setName( "逻辑卷二" );
        simpleVolume2.setVolumeCapacity( logicVolumeCapacity2 );
        simpleVolume2.setType( "SimpleVolume" );

        SpannedVolume spannedVolume = volumeAllotment.newLocalSpannedVolume();
        spannedVolume.setName( "逻辑卷三" );
        spannedVolume.setVolumeCapacity( logicVolumeCapacity3 );
        spannedVolume.setType( "SpannedVolume" );

        volumeTree.insertPhysicalVolume( physicalVolume1 );
        volumeTree.insertPhysicalVolume( physicalVolume2 );
        volumeTree.put( simpleVolume1 );
        volumeTree.put( simpleVolume2 );
        volumeTree.put( spannedVolume );
    }

    private void TestRaid0Receive( KOMFileSystem fileSystem, UniformVolumeManager volumeTree ) throws IOException {
        LogicVolume volume1 = volumeTree.get(GUIDs.GUID72("056b342-0001d1-0004-48"));
        volume1.extendLogicalVolume( GUIDs.GUID72("056b342-0001d1-0000-48") );
        LogicVolume volume2 = volumeTree.get(GUIDs.GUID72("056b342-0001d1-0005-48"));
        volume2.extendLogicalVolume(GUIDs.GUID72("056b342-0001d1-0002-48"));

//        LogicVolume volume3 = volumeTree.get(GUIDs.GUID72("0414fd8-00011e-0006-78"));
//        File sourceFile = new File("D:\\井盖视频块\\4月13日 (2).mp4");
//        Path path = sourceFile.toPath();
//        FileNode fileNode = fileSystem.getFSNodeAllotment().newFileNode();
//        fileNode.setName(sourceFile.getName());
//        fileNode.setGuid( fileSystem.getGuidAllocator().nextGUID72() );
//        fileNode.setDefinitionSize(200*1024*1024);
        ///volume3.channelReceive( fileSystem,fileNode,FileChannel.open(path, StandardOpenOption.READ) );
    }

    private void testSimpleThread() throws Exception {
        MasterVolumeGram gram = new MasterVolumeGram( "volume_master", this );
        this.getTaskManager().add( gram );

        PoopyButtholeThread poopyButthole = new PoopyButtholeThread( gram, new PoopJob( 1234 ) );
        gram.getTaskManager().add( poopyButthole );


        PoopyButtholeThread poopy1 = new PoopyButtholeThread( gram, new EatJob( "shit" ) );
        gram.getTaskManager().add( poopy1 );


        poopy1.start();
        poopyButthole.start();


        gram.getTaskManager().syncWaitingTerminated();
        Debug.trace( "done~" );

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


class PoopJob implements VolumeJob {
    int id ;
    public PoopJob( int id ) {
        this.id = id;
    }

    @Override
    public void execute() {
        Debug.trace( "I am pooping." + this.id );
        Debug.sleep( 5000 );
        Debug.trace( "done" );
    }
}

class EatJob implements VolumeJob {
    String name ;
    public EatJob( String name ) {
        this.name = name;
    }

    @Override
    public void execute() {
        Debug.trace( "I am eating." + this.name );
        Debug.sleep( 5000 );
        Debug.trace( "done" );
    }
}

class PoopyButtholeThread extends ArchStripedTaskThread {
    public PoopyButtholeThread ( Processum parent, VolumeJob job ) {
        super( "PoopyButthole", parent, job );
    }

    @Override
    protected void executeSingleJob() {
        super.executeSingleJob();
    }
}

