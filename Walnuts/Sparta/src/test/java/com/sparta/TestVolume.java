package com.sparta;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.file.ibatis.hydranium.FileMappingDriver;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.UniformObjectFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.FileTreeNode;
import com.pinecone.hydra.storage.volume.UniformVolumeTree;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.MountPoint;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.VolumeAllotment;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity;
import com.pinecone.hydra.storage.volume.entity.local.LocalPhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalSimpleVolume;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.volume.ibatis.hydranium.VolumeMappingDriver;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;
import com.pinecone.ulf.util.id.GUIDs;
import com.sauron.radium.Radium;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;


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

        UniformVolumeTree volumeTree = new UniformVolumeTree( koiMappingDriver );
        VolumeAllotment volumeAllotment = volumeTree.getVolumeAllotment();

        //this.testInsert( volumeTree );
        //this.testChannelReceive( fileSystem,volumeTree );
        //this.testRaid0Insert( fileSystem,volumeTree );
        this.TestRaid0Receive( fileSystem, volumeTree );
    }
    private void testBaseInsert(UniformVolumeTree volumeTree ){
        VolumeAllotment volumeAllotment = volumeTree.getVolumeAllotment();
        VolumeCapacity physicalVolumeCapacity = volumeAllotment.newVolumeCapacity();
        VolumeCapacity logicVolumeCapacity = volumeAllotment.newVolumeCapacity();
        physicalVolumeCapacity.setDefinitionCapacity(1000);
        logicVolumeCapacity.setDefinitionCapacity( 1000 );

        LocalPhysicalVolume physicalVolume = volumeAllotment.newLocalPhysicalVolume();
        physicalVolume.setName("D");
        physicalVolume.setType("PhysicalVolume");
        MountPoint mountPoint = volumeAllotment.newMountPoint();
        mountPoint.setMountPoint("D:\\文件系统");
        mountPoint.setVolumeGuid( physicalVolume.getGuid() );
        physicalVolume.setMountPoint( mountPoint );
        physicalVolume.setVolumeCapacity( physicalVolumeCapacity );

        LocalSimpleVolume simpleVolume = volumeAllotment.newLocalSimpleVolume();
        simpleVolume.setName("简单卷一");
        simpleVolume.setType("SimpleVolume");
        simpleVolume.setVolumeCapacity( logicVolumeCapacity );

        volumeTree.insertPhysicalVolume( physicalVolume );
        volumeTree.put( simpleVolume );
    }

    private void testChannelReceive(KOMFileSystem fileSystem, UniformVolumeTree volumeTree ) throws IOException {
        SimpleVolume simpleVolume  = volumeTree.get(GUIDs.GUID72("03e7f10-0003dd-0002-98")).evinceSimpleVolume();
        simpleVolume.extendLogicalVolume( GUIDs.GUID72("03e7f10-0003dd-0000-84") );
        File sourceFile = new File("D:\\井盖视频块\\4月13日 (2).mp4");
        Path path = sourceFile.toPath();
        FileNode fileNode = fileSystem.getFSNodeAllotment().newFileNode();
        fileNode.setName(sourceFile.getName());
        fileNode.setGuid( fileSystem.getGuidAllocator().nextGUID72() );
        simpleVolume.channelReceive( fileSystem, fileNode,FileChannel.open(path, StandardOpenOption.READ));
    }

    private void testChannelExport( KOMFileSystem fileSystem, UniformVolumeTree volumeTree ) throws IOException {
        SimpleVolume simpleVolume  = volumeTree.get(GUIDs.GUID72("03e7f10-0003dd-0002-98")).evinceSimpleVolume();
        FileTreeNode fileTreeNode = fileSystem.get(GUIDs.GUID72("0271940-00035d-0001-58"));
        FileNode file = fileTreeNode.evinceFileNode();
        File file1 = new File("D:\\文件系统\\大文件\\视频.mp4");
        simpleVolume.channelExport(fileSystem, file);
    }

    private void testRaid0Insert( KOMFileSystem fileSystem, UniformVolumeTree volumeTree ){
        VolumeAllotment volumeAllotment = volumeTree.getVolumeAllotment();
        VolumeCapacity volumeCapacity1 = volumeAllotment.newVolumeCapacity();
        volumeCapacity1.setDefinitionCapacity( 100*1024*1024 );
        VolumeCapacity volumeCapacity2 = volumeAllotment.newVolumeCapacity();
        volumeCapacity2.setDefinitionCapacity( 200*1024*1024 );

        LocalPhysicalVolume physicalVolume1 = volumeAllotment.newLocalPhysicalVolume();
        physicalVolume1.setType("PhysicalVolume");
        physicalVolume1.setVolumeCapacity( volumeCapacity1 );
        physicalVolume1.setName( "D" );
        MountPoint mountPoint1 = volumeAllotment.newMountPoint();
        mountPoint1.setMountPoint("D:\\文件系统\\簇1");
        physicalVolume1.setMountPoint( mountPoint1 );

        LocalPhysicalVolume physicalVolume2 = volumeAllotment.newLocalPhysicalVolume();
        physicalVolume2.setType("PhysicalVolume");
        physicalVolume2.setVolumeCapacity( volumeCapacity2 );
        physicalVolume2.setName( "E" );
        MountPoint mountPoint2 = volumeAllotment.newMountPoint();
        mountPoint2.setMountPoint( "D:\\文件系统\\簇2" );
        physicalVolume2.setMountPoint( mountPoint2 );

        VolumeCapacity logicVolumeCapacity1 = volumeAllotment.newVolumeCapacity();
        logicVolumeCapacity1.setDefinitionCapacity( 100*1024*1024 );
        VolumeCapacity logicVolumeCapacity2 = volumeAllotment.newVolumeCapacity();
        logicVolumeCapacity2.setDefinitionCapacity( 200*1024*1024 );
        VolumeCapacity logicVolumeCapacity3 = volumeAllotment.newVolumeCapacity();
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

    private void TestRaid0Receive( KOMFileSystem fileSystem, UniformVolumeTree volumeTree ) throws IOException {
//        LogicVolume volume1 = volumeTree.get(GUIDs.GUID72("0414fd8-00011e-0004-78"));
//        volume1.extendLogicalVolume( GUIDs.GUID72("0414fd8-00011e-0000-9c") );
//        LogicVolume volume2 = volumeTree.get(GUIDs.GUID72("0414fd8-00011e-0005-78"));
//        volume2.extendLogicalVolume(GUIDs.GUID72("0414fd8-00011e-0002-78"));

        LogicVolume volume3 = volumeTree.get(GUIDs.GUID72("0414fd8-00011e-0006-78"));
        File sourceFile = new File("D:\\井盖视频块\\4月13日 (2).mp4");
        Path path = sourceFile.toPath();
        FileNode fileNode = fileSystem.getFSNodeAllotment().newFileNode();
        fileNode.setName(sourceFile.getName());
        fileNode.setGuid( fileSystem.getGuidAllocator().nextGUID72() );
        fileNode.setDefinitionSize(200*1024*1024);
        volume3.channelReceive( fileSystem,fileNode,FileChannel.open(path, StandardOpenOption.READ) );
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
