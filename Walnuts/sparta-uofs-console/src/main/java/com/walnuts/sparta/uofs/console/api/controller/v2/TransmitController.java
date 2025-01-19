package com.walnuts.sparta.uofs.console.api.controller.v2;


import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.TitanFileChannelChanface;
import com.pinecone.hydra.storage.TitanOutputStreamChanface;
import com.pinecone.hydra.storage.bucket.BucketInstrument;
import com.pinecone.hydra.storage.bucket.entity.Site;
import com.pinecone.hydra.storage.bucket.source.SiteManipulator;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.FileTreeNode;
import com.pinecone.hydra.storage.file.entity.Folder;
import com.pinecone.hydra.storage.file.transmit.exporter.TitanFileExportEntity64;
import com.pinecone.hydra.storage.file.transmit.receiver.TitanFileReceiveEntity64;
import com.pinecone.hydra.storage.version.VersionManage;
import com.pinecone.hydra.storage.version.entity.TitanVersion;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.ulf.util.id.GUIDs;
import com.walnuts.sparta.uofs.console.api.response.BasicResultResponse;
import com.walnuts.sparta.uofs.console.domain.dto.DownloadObjectByChannelDTO;
import com.walnuts.sparta.uofs.console.domain.dto.UpdateObjectByChannelDTO;
import com.walnuts.sparta.uofs.console.infrastructure.UOFSConsoleContents;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.Map;

import static org.apache.commons.io.FilenameUtils.getExtension;

@RestController
@RequestMapping( "/api/v2/uofs/transmit" )
@CrossOrigin
public class TransmitController {
    @Resource
    private KOMFileSystem primaryFileSystem;

    @Resource
    private UniformVolumeManager primaryVolume;

    @Resource
    private BucketInstrument bucketInstrument;

    @Resource
    private VersionManage primaryVersion;

    /**
     * 使用channel上传对象
     * @param dto 上传所需数据
     * @return 返回操作结果
     * @throws IOException
     * @throws SQLException
     */
    @PostMapping("/channel/update")
    public  BasicResultResponse<String> updateObjectByChannel(UpdateObjectByChannelDTO dto ) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        MultipartFile object = dto.getObject();
        File file = File.createTempFile( "uofs","."+ getExtension(object.getOriginalFilename()) );
        object.transferTo( file );
        Chanface chanface = this.getKChannel(file);

        FSNodeAllotment fsNodeAllotment = this.primaryFileSystem.getFSNodeAllotment();
        FileNode fileNode = fsNodeAllotment.newFileNode();
        fileNode.setDefinitionSize( file.length() );
        fileNode.setName( file.getName() );

        TitanFileReceiveEntity64 receiveEntity = new TitanFileReceiveEntity64(
                this.primaryFileSystem, dto.getDestDirPath(), fileNode, chanface, this.primaryVolume
        );

        this.primaryFileSystem.receive( receiveEntity );
        return BasicResultResponse.success();
    }

    /**
     * 使用channel将对象下载到本地
     * @param dto 下载所需的数据
     * @return 返回操作结果
     * @throws IOException
     * @throws SQLException
     */
    @PostMapping("/channel/download")
    public BasicResultResponse<String> downloadObjectByChannel( DownloadObjectByChannelDTO dto ) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        File file = new File( dto.getTargetPath());
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        TitanFileChannelChanface titanFileChannelKChannel = new TitanFileChannelChanface( channel );

        FileNode fileNode = (FileNode) this.primaryFileSystem.get(this.primaryFileSystem.queryGUIDByPath(dto.getDestDirPath()));
        TitanFileExportEntity64 exportEntity = new TitanFileExportEntity64( this.primaryFileSystem, this.primaryVolume, fileNode, titanFileChannelKChannel );
        primaryFileSystem.export( exportEntity );
        return BasicResultResponse.success();
    }

    @GetMapping("/download/guid")
    public void  getFile(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        String[] guids = parameterMap.get("guid");
        GUID storageObjectGuid = null;
        if( guids != null ){
            storageObjectGuid = GUIDs.GUID72( guids[0] );
        }

        ServletOutputStream outputStream = response.getOutputStream();
        TitanOutputStreamChanface kChannel = new TitanOutputStreamChanface(outputStream);

        FileNode storageObject = (FileNode) this.primaryFileSystem.get(storageObjectGuid);

        TitanFileExportEntity64 entity = new TitanFileExportEntity64(this.primaryFileSystem, this.primaryVolume, storageObject, kChannel);
        this.primaryFileSystem.export( entity );

    }

    /**
     * 上传文件
     * @param filePath 目标路径
     * @param version 版本号
     * @param file 文件
     * @param siteName 站点
     * @return 返回操作结果
     */
    @PostMapping("/CDNUpload")
    public BasicResultResponse<String> CDNUpload(@RequestParam("siteName") String siteName, @RequestParam("filePath") String filePath, @RequestParam("version") String version, @RequestParam("file") MultipartFile file) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        SiteManipulator siteManipulator = this.bucketInstrument.getSiteManipulator();
        Site site = siteManipulator.querySiteByName(siteName);
        if( site == null ){
            return BasicResultResponse.error("站点不存在");
        }
        int dotIndex = filePath.lastIndexOf(UOFSConsoleContents.PERIOD);
        String baseName = filePath.substring(0, dotIndex);
        String extension = filePath.substring(dotIndex + 1);
        String realFilePath = this.primaryFileSystem.getPath(site.getMountPointGuid()) + UOFSConsoleContents.FORWARD_SLASH + baseName;

        FSNodeAllotment fsNodeAllotment = this.primaryFileSystem.getFSNodeAllotment();
        Folder node = this.primaryFileSystem.affirmFolder(realFilePath);
        String storageObjectPath = realFilePath + UOFSConsoleContents.VERSION_PREFIX+ UOFSConsoleContents.FORWARD_SLASH + version +UOFSConsoleContents.PERIOD+ extension;
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

    /**
     *
     * @param filePath 文件要上传的路径
     * @param file 文件本体
     * @return
     */
    @PostMapping("/upload")
    public BasicResultResponse<String> upload(@RequestParam("filePath") String filePath, @RequestParam("file") MultipartFile file ) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        File tempFile = File.createTempFile("upload",".temp");
        file.transferTo(tempFile);

        FSNodeAllotment fsNodeAllotment = this.primaryFileSystem.getFSNodeAllotment();
        FileChannel channel = FileChannel.open(tempFile.toPath(), StandardOpenOption.READ);
        TitanFileChannelChanface titanFileChannelKChannel = new TitanFileChannelChanface( channel );
        FileNode fileNode = fsNodeAllotment.newFileNode();
        fileNode.setDefinitionSize( tempFile.length() );
        fileNode.setName( tempFile.getName() );
        TitanFileReceiveEntity64 receiveEntity = new TitanFileReceiveEntity64( this.primaryFileSystem,filePath, fileNode,titanFileChannelKChannel,this.primaryVolume );

        this.primaryFileSystem.receive( receiveEntity );
        return BasicResultResponse.success();
    }

    @PostMapping("/stream")
    public String handleStreamUpload(HttpServletRequest request) throws IOException {
        try (InputStream inputStream = request.getInputStream()) {
            // 处理输入流
            return "File stream processed.";
        }
    }

    private Chanface getKChannel(File file ) throws IOException {
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
        return new TitanFileChannelChanface( channel );
    }
}
