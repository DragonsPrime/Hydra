package com.sparta;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.hydra.file.ibatis.hydranium.FileMappingDriver;
import com.pinecone.hydra.storage.TitanFileChannelChanface;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.UniformObjectFileSystem;
import com.pinecone.hydra.storage.file.entity.FSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.transmit.exporter.TitanFileExportEntity64;
import com.pinecone.hydra.storage.file.transmit.receiver.TitanFileReceiveEntity64;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.volume.ibatis.hydranium.VolumeMappingDriver;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;
import com.pinecone.framework.util.id.GuidAllocator;
import com.sauron.radium.Radium;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
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
        this.testChannelExport( fileSystem, volumeManager );

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

    private void testChannelReceive( KOMFileSystem fileSystem, UniformVolumeManager volumeManager ) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        //LogicVolume volume = volumeManager.get(GUIDs.GUID72( "09d62c0-00037e-0006-c8" ));
        FSNodeAllotment fsNodeAllotment = fileSystem.getFSNodeAllotment();
        File file = new File("D:/井盖视频块/我的视频.mp4");
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
        TitanFileChannelChanface titanFileChannelKChannel = new TitanFileChannelChanface( channel );
        FileNode fileNode = fsNodeAllotment.newFileNode();
        fileNode.setDefinitionSize( file.length() );
        fileNode.setName( file.getName() );
        String destDirPath = "D:/井盖视频块/我的视频.mp4";
        TitanFileReceiveEntity64 receiveEntity = new TitanFileReceiveEntity64( fileSystem, destDirPath, fileNode,titanFileChannelKChannel,volumeManager );
        fileSystem.receive( receiveEntity );
    }

    private void testChannelExport( KOMFileSystem fileSystem, UniformVolumeManager volumeManager ) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        FileNode fileNode = (FileNode) fileSystem.get(fileSystem.queryGUIDByPath("D:/井盖视频块/我的视频.mp4"));
        File file = new File("D:\\文件系统\\大文件\\我的视频.mp4");
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        TitanFileChannelChanface kChannel = new TitanFileChannelChanface( channel );
        TitanFileExportEntity64 exportEntity = new TitanFileExportEntity64( fileSystem, volumeManager, fileNode, kChannel );
        fileSystem.export( exportEntity );
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
