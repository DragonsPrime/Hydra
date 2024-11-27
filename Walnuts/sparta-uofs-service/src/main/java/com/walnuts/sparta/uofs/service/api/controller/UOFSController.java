package com.walnuts.sparta.uofs.service.api.controller;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.TitanFileChannelKChannel;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.FileTreeNode;
import com.pinecone.hydra.storage.file.entity.Folder;
import com.pinecone.hydra.storage.file.transmit.exporter.channel.GenericChannelExporterEntity;
import com.pinecone.hydra.storage.file.transmit.receiver.channel.GenericChannelReceiveEntity;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.ulf.util.id.GUIDs;
import com.walnuts.sparta.uofs.service.api.response.BasicResultResponse;
import com.walnuts.sparta.uofs.service.domain.dto.DownloadObjectByChannelDTO;
import com.walnuts.sparta.uofs.service.domain.dto.UpdateObjectByChannelDTO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.List;

import static org.apache.commons.io.FilenameUtils.getExtension;

@RestController
@RequestMapping( "/api/service/uofs" )
public class UOFSController {
    @Resource
    private KOMFileSystem primaryFileSystem;

    @Resource
    private UniformVolumeManager primaryVolume;

    /**
     * 使用channel上传对象
     * @param dto 上传所需数据
     * @return 返回操作结果
     * @throws IOException
     * @throws SQLException
     */
    @PostMapping("/channel/update")
    public BasicResultResponse<String> updateObjectByChannel( UpdateObjectByChannelDTO dto ) throws IOException, SQLException {
        MultipartFile object = dto.getObject();
        File file = File.createTempFile( "uofs","."+ getExtension(object.getOriginalFilename()) );
        object.transferTo( file );
        KChannel kChannel = this.getKChannel(file);

        FSNodeAllotment fsNodeAllotment = this.primaryFileSystem.getFSNodeAllotment();
        FileNode fileNode = fsNodeAllotment.newFileNode();
        fileNode.setDefinitionSize( file.length() );
        fileNode.setName( file.getName() );

        GenericChannelReceiveEntity receiveEntity = new GenericChannelReceiveEntity(this.primaryFileSystem, dto.getDestDirPath(), fileNode, kChannel);
        this.primaryFileSystem.receive( primaryVolume.get(GUIDs.GUID72( dto.getVolumeGuid() ) ), receiveEntity );
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
    public BasicResultResponse<String> downloadObjectByChannel( DownloadObjectByChannelDTO dto ) throws IOException, SQLException {
        File file = new File( dto.getTargetPath());
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        TitanFileChannelKChannel titanFileChannelKChannel = new TitanFileChannelKChannel( channel );

        FileNode fileNode = (FileNode) this.primaryFileSystem.get(this.primaryFileSystem.queryGUIDByPath(dto.getDestDirPath()));
        GenericChannelExporterEntity exporterEntity = new GenericChannelExporterEntity(this.primaryFileSystem, fileNode, titanFileChannelKChannel);
        this.primaryFileSystem.export( this.primaryVolume, exporterEntity );

        return BasicResultResponse.success();
    }

    /**
     * 获取文件夹下所有内容
     * @param folderGuid 文件夹guid
     * @returnS
     */
    @GetMapping("/folder/listItem")
    public String listItem(@RequestParam String folderGuid ){
        Folder folder = this.primaryFileSystem.getFolder(GUIDs.GUID72(folderGuid));
        List<FileTreeNode> fileTreeNodes = folder.listItem();
        return  BasicResultResponse.success(fileTreeNodes).toJSONString() ;
    }

    /**
     * 创建文件夹
     * @param destDirPath 文件夹路径
     * @return 返回操作状态
     */
    @GetMapping("/creat/folder")
    public BasicResultResponse<String> createFolder( @RequestParam String destDirPath ){
        this.primaryFileSystem.affirmFolder( destDirPath );
        return BasicResultResponse.success();
    }

    /**
     * 创建文件
     * @param filePath 文件路径
     * @return 返回操作状态
     */
    @GetMapping("/creat/file")
    public BasicResultResponse<String> createFile( @RequestParam String filePath ){
        this.primaryFileSystem.affirmFileNode( filePath );
        return BasicResultResponse.success();
    }

    /**
     * 获取所有根文件夹
     * @return 返回根信息
     */
    @GetMapping("/list/root")
    public String listRoot(){
        List<FileTreeNode> roots = this.primaryFileSystem.fetchRoot();
        return BasicResultResponse.success( roots ).toJSONString();
    }

    /**
     * 获取文件或文件夹属性
     * @param nodeGuid 文件或文件夹guid
     * @return 返回属性信息
     */
    @GetMapping("/attribute")
    public BasicResultResponse< FileTreeNode > attribute( @RequestParam("nodeGuid") String nodeGuid ){
        FileTreeNode fileTreeNode = this.primaryFileSystem.get(GUIDs.GUID72(nodeGuid));
        return BasicResultResponse.success( fileTreeNode );
    }

    /**
     * 移除文件夹或者文件
     * @param fileGuid 文件夹或者文件guid
     * @return 返回操作结果
     */
    @DeleteMapping("/remove/file")
    public BasicResultResponse<String> removeFile( String fileGuid ){
        this.primaryFileSystem.remove( GUIDs.GUID72( fileGuid ) );
        return BasicResultResponse.success();
    }


    


    private KChannel getKChannel( File file ) throws IOException {
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
        return new TitanFileChannelKChannel( channel );
    }


}
