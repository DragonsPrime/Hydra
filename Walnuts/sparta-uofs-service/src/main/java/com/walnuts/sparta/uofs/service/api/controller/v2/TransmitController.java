package com.walnuts.sparta.uofs.service.api.controller.v2;


import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.TitanFileChannelChanface;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.transmit.exporter.TitanFileExportEntity64;
import com.pinecone.hydra.storage.file.transmit.receiver.TitanFileReceiveEntity64;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.walnuts.sparta.uofs.service.api.response.BasicResultResponse;
import com.walnuts.sparta.uofs.service.domain.dto.DownloadObjectByChannelDTO;
import com.walnuts.sparta.uofs.service.domain.dto.UpdateObjectByChannelDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;

import static org.apache.commons.io.FilenameUtils.getExtension;

@RestController
@RequestMapping( "/api/v2/uofs/transmit" )
public class TransmitController {
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
    public BasicResultResponse<String> updateObjectByChannel(UpdateObjectByChannelDTO dto ) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
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
