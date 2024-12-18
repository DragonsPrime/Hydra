package org.example.sparteucdnservice.api.controller.v2;


import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.TitanFileChannelChanface;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.FileTreeNode;
import com.pinecone.hydra.storage.file.transmit.exporter.TitanFileExportEntity64;
import com.pinecone.hydra.storage.version.VersionManage;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import org.example.sparteucdnservice.api.response.BasicResultResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;

@RestController
@RequestMapping( "/api/v2/ucdn/client" )
@CrossOrigin
public class ClientController {

    @Resource
    private KOMFileSystem               primaryFileSystem;

    @Resource
    private UniformVolumeManager        primaryVolume;

    @Resource
    private VersionManage               primaryVersion;

    @GetMapping("/getFile/{filePath}/{version}")
    public BasicResultResponse<File> getFile(@PathVariable String filePath, @PathVariable String version) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // 先通过filePath和version获取到系统内文件
        GUID guid = this.primaryFileSystem.queryGUIDByPath(filePath);
        FileNode fileNode = (FileNode)this.primaryFileSystem.get(guid);

        GUID fileGuid = this.primaryVersion.queryObjectGuid(version, fileNode.getGuid());
        FileNode fileObject = (FileNode) this.primaryFileSystem.get(fileGuid);

        File tempFile = File.createTempFile("temp",".temp");
        FileChannel channel = FileChannel.open(tempFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        TitanFileChannelChanface kChannel = new TitanFileChannelChanface(channel);
        TitanFileExportEntity64 exportEntity = new TitanFileExportEntity64(this.primaryFileSystem, this.primaryVolume, fileObject, kChannel);
        this.primaryFileSystem.export( exportEntity );

        return BasicResultResponse.success( tempFile );
    }

}
