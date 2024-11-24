package com.sparta;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.file.ibatis.hydranium.FileMappingDriver;
import com.pinecone.hydra.storage.TitanKChannel;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.UniformObjectFileSystem;
import com.pinecone.hydra.storage.file.entity.FSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.FileTreeNode;
import com.pinecone.hydra.storage.file.entity.GenericFileNode;
import com.pinecone.hydra.storage.file.transmit.exporter.channel.GenericChannelExporterEntity;
import com.pinecone.hydra.storage.file.transmit.exporter.stream.GenericStreamExporterEntity;
import com.pinecone.hydra.storage.file.transmit.receiver.channel.ChannelReceiverEntity;
import com.pinecone.hydra.storage.file.transmit.receiver.channel.GenericChannelReceiveEntity;
import com.pinecone.hydra.storage.file.transmit.receiver.stream.GenericStreamReceiverEntity;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.unit.udtt.entity.EntityNode;
import com.pinecone.hydra.volume.ibatis.hydranium.VolumeMappingDriver;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;
import com.pinecone.ulf.util.id.GUIDs;
import com.pinecone.ulf.util.id.GuidAllocator;
import com.sauron.radium.Radium;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;

class Steve extends Radium {
    public Steve( String[] args, CascadeSystem parent ) {
        this( args, null, parent );
    }

    public Steve( String[] args, String szName, CascadeSystem parent ){
        super( args, szName, parent );
    }
    @Override
    public void vitalize () throws Exception {
        KOIMappingDriver koiMappingDriver = new FileMappingDriver(
                this, (IbatisClient)this.getMiddlewareManager().getRDBManager().getRDBClientByName( "MySQLKingHydranium" ), this.getDispenserCenter()
        );
        KOIMappingDriver koiVolumeMappingDriver = new VolumeMappingDriver(
                this, (IbatisClient)this.getMiddlewareManager().getRDBManager().getRDBClientByName( "MySQLKingHydranium" ), this.getDispenserCenter()
        );
        KOMFileSystem fileSystem = new UniformObjectFileSystem( koiMappingDriver );
        UniformVolumeManager volumeManager = new UniformVolumeManager(koiVolumeMappingDriver);
        GuidAllocator guidAllocator = fileSystem.getGuidAllocator();
        //Debug.trace( fileSystem.get( GUIDs.GUID72( "020c8b0-000006-0002-54" ) ) );
        //this.testInsert( fileSystem );
        //this.testUpload(fileSystem);
        //this.testDelete( fileSystem );
        //this.testChannelReceive( fileSystem, volumeManager );
        //this.testChannelExport( fileSystem, volumeManager );

    }

    private void testInsert( KOMFileSystem fileSystem ){
        fileSystem.affirmFolder("game/我的世界");
        fileSystem.affirmFileNode("game/我的世界/村民");
        fileSystem.affirmFileNode("game/我的世界/暮色森林/暮色惡魂");
        fileSystem.affirmFileNode("game/泰拉瑞亚/腐化之地/世界吞噬者");
        fileSystem.affirmFileNode("movie/生还危机/浣熊市");
    }


    private void testDelete(KOMFileSystem fileSystem ){
        fileSystem.remove( "game" );
        fileSystem.remove( "movie" );
    }

    private void testChannelReceive( KOMFileSystem fileSystem, UniformVolumeManager volumeManager ) throws IOException, SQLException {
        LogicVolume simpleVolume = volumeManager.get(GUIDs.GUID72( "08cdf4c-00013c-0006-4c" ));
        FSNodeAllotment fsNodeAllotment = fileSystem.getFSNodeAllotment();
        File file = new File("D:/井盖视频块/4月13日 (2).mp4");
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
        TitanKChannel titanKChannel = new TitanKChannel( channel );
        FileNode fileNode = fsNodeAllotment.newFileNode();
        fileNode.setDefinitionSize( file.length() );
        fileNode.setName( file.getName() );
        String destDirPath = "D:/文件系统/大文件/我的视频.mp4";
        GenericChannelReceiveEntity receiveEntity = new GenericChannelReceiveEntity( fileSystem,destDirPath,fileNode,titanKChannel );

        fileSystem.receive( simpleVolume, receiveEntity );
    }

    private void testChannelExport( KOMFileSystem fileSystem, UniformVolumeManager volumeManager ) throws IOException, SQLException {
        FileNode fileNode = (FileNode) fileSystem.get(fileSystem.queryGUIDByPath("D:/文件系统/大文件/我的图片.jpg"));
        File file = new File("D:/文件系统/大文件/我的图片.jpg");
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        TitanKChannel titanKChannel = new TitanKChannel( channel );
        GenericChannelExporterEntity exporterEntity = new GenericChannelExporterEntity( fileSystem, fileNode, titanKChannel );
        fileSystem.export( volumeManager, exporterEntity );
    }

}
public class TestUOFS {
    public static void main( String[] args ) throws Exception {
        Pinecone.init( (Object...cfg )->{
            Steve Steve = (Steve) Pinecone.sys().getTaskManager().add( new Steve( args, Pinecone.sys() ) );
            Steve.vitalize();
            return 0;
        }, (Object[]) args );
    }
}
