package org.example.sparteucdnservice.api.controller.v2;


import com.pinecone.hydra.storage.TitanFileChannelChanface;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.FileTreeNode;
import com.pinecone.hydra.storage.file.entity.Folder;
import com.pinecone.hydra.storage.file.transmit.receiver.TitanFileReceiveEntity64;
import com.pinecone.hydra.storage.version.VersionManage;
import com.pinecone.hydra.storage.version.entity.TitanVersion;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import org.example.sparteucdnservice.api.response.BasicResultResponse;
import org.example.sparteucdnservice.infrastructure.constants.PolicyConstants;
import org.example.sparteucdnservice.infrastructure.dto.UploadDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping( "/api/v2/ucdn/console" )
@CrossOrigin
public class ConsoleController {
    @Resource
    private KOMFileSystem           primaryFileSystem;

    @Resource
    private UniformVolumeManager    primaryVolume;

    @Resource
    private VersionManage           primaryVersion;

    /**
     * 上传文件
     * @param filePtah 目标路径
     * @param version 版本号
     * @param file 文件
     * @return 返回操作结果
     */
    @PostMapping("/upload")
    public BasicResultResponse<String> upload(@RequestParam("filePath") String filePtah, @RequestParam("version") String version, @RequestParam("file") MultipartFile file) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        FSNodeAllotment fsNodeAllotment = this.primaryFileSystem.getFSNodeAllotment();
        Folder node = this.primaryFileSystem.affirmFolder(filePtah);
        String storageObjectPath = filePtah + PolicyConstants.VERSION_PREFIX+ "/" + version;
        File tempFile = File.createTempFile("upload",".temp");
        file.transferTo(tempFile);

        FileChannel channel = FileChannel.open(tempFile.toPath(), StandardOpenOption.READ);
        TitanFileChannelChanface titanFileChannelKChannel = new TitanFileChannelChanface( channel );
        FileNode fileNode = fsNodeAllotment.newFileNode();
        fileNode.setDefinitionSize( tempFile.length() );
        fileNode.setName( tempFile.getName() );
        TitanFileReceiveEntity64 receiveEntity = new TitanFileReceiveEntity64( this.primaryFileSystem,storageObjectPath, fileNode,titanFileChannelKChannel,this.primaryVolume );

        this.primaryFileSystem.receive( receiveEntity );

        FileTreeNode storageObject = this.primaryFileSystem.get(this.primaryFileSystem.queryGUIDByPath(storageObjectPath));
        TitanVersion titanVersion = new TitanVersion();
        titanVersion.setVersion( version );
        titanVersion.setFileGuid( node.getGuid() );
        titanVersion.setTargetStorageObjectGuid( storageObject.getGuid() );

        this.primaryVersion.insert( titanVersion );
        return BasicResultResponse.success();
    }
}
