package com.walnuts.sparta.uofs.service.api.controller;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSON;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.TitanKChannel;
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
import com.walnuts.sparta.uofs.service.domain.dto.downloadObjectByChannelDto;
import com.walnuts.sparta.uofs.service.domain.dto.updateObjectByChannelDto;
import com.walnuts.sparta.uofs.service.domain.vo.FolderContentVo;
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
    public BasicResultResponse<String> updateObjectByChannel( updateObjectByChannelDto dto ) throws IOException, SQLException {
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
    public BasicResultResponse<String> downloadObjectByChannel( downloadObjectByChannelDto dto ) throws IOException, SQLException {
        File file = new File( dto.getTargetPath());
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        TitanKChannel titanKChannel = new TitanKChannel( channel );

        FileNode fileNode = (FileNode) this.primaryFileSystem.get(this.primaryFileSystem.queryGUIDByPath(dto.getDestDirPath()));
        GenericChannelExporterEntity exporterEntity = new GenericChannelExporterEntity(this.primaryFileSystem, fileNode, titanKChannel);
        this.primaryFileSystem.export( this.primaryVolume, exporterEntity );

        return BasicResultResponse.success();
    }

    /**
     * 获取文件夹下所有内容
     * @param folderGuid 文件夹guid
     * @returnS
     */
    @GetMapping("/getFolderContent")
    public String getFolderContent(@RequestParam String folderGuid ){
        Folder folder = this.primaryFileSystem.getFolder(GUIDs.GUID72(folderGuid));
        List<FileTreeNode> fileTreeNodes = folder.listItem();
        return  BasicResultResponse.success(fileTreeNodes).toJSONString() ;
    }


    private KChannel getKChannel( File file ) throws IOException {
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
        return new TitanKChannel( channel );
    }


}
