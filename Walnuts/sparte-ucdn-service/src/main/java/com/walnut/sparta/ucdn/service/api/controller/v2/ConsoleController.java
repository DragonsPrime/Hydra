package com.walnut.sparta.ucdn.service.api.controller.v2;


import com.pinecone.hydra.storage.TitanFileChannelChanface;
import com.pinecone.hydra.storage.bucket.BucketInstrument;
import com.pinecone.hydra.storage.bucket.entity.Site;
import com.pinecone.hydra.storage.bucket.source.SiteManipulator;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.FileTreeNode;
import com.pinecone.hydra.storage.file.entity.Folder;
import com.pinecone.hydra.storage.file.transmit.receiver.TitanFileReceiveEntity64;
import com.pinecone.hydra.storage.version.VersionManage;
import com.pinecone.hydra.storage.version.entity.TitanVersion;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.walnut.sparta.ucdn.service.api.response.BasicResultResponse;
import com.walnut.sparta.ucdn.service.infrastructure.constants.PolicyConstants;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Resource
    private BucketInstrument        bucketInstrument;

    /**
     * 上传文件
     * @param filePath 目标路径
     * @param version 版本号
     * @param file 文件
     * @param siteName 站点
     * @return 返回操作结果
     */
    @PostMapping("/upload")
    public BasicResultResponse<String> upload(@RequestParam("siteName") String siteName, @RequestParam("filePath") String filePath, @RequestParam("version") String version, @RequestParam("file") MultipartFile file) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        SiteManipulator siteManipulator = this.bucketInstrument.getSiteManipulator();
        Site site = siteManipulator.querySiteByName(siteName);
        if( site == null ){
            return BasicResultResponse.error("站点不存在");
        }
        int dotIndex = filePath.lastIndexOf(PolicyConstants.PERIOD);
        String baseName = filePath.substring(0, dotIndex);
        String extension = filePath.substring(dotIndex + 1);
        String realFilePath = this.primaryFileSystem.getPath(site.getMountPointGuid()) + PolicyConstants.FORWARD_SLASH + baseName;

        FSNodeAllotment fsNodeAllotment = this.primaryFileSystem.getFSNodeAllotment();
        Folder node = this.primaryFileSystem.affirmFolder(realFilePath);
        String storageObjectPath = realFilePath + PolicyConstants.VERSION_PREFIX+ PolicyConstants.FORWARD_SLASH + version +PolicyConstants.PERIOD+ extension;
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
