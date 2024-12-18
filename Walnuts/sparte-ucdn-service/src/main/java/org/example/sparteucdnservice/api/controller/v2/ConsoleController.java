package org.example.sparteucdnservice.api.controller.v2;


import com.pinecone.hydra.storage.TitanFileChannelChanface;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.transmit.receiver.TitanFileReceiveEntity64;
import com.pinecone.hydra.storage.version.VersionManage;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import org.example.sparteucdnservice.api.response.BasicResultResponse;
import org.example.sparteucdnservice.infrastructure.dto.UploadDTO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping( "/api/v2/ucdn/console" )
@CrossOrigin
public class ConsoleController {
    @Resource
    private KOMFileSystem           primaryFileSystem;

    @Resource
    private UniformVolumeManager    primaryVolume;

    @Resource
    private VersionManage           primaryVersion;

    public BasicResultResponse<String> upload(@RequestBody UploadDTO dto) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        FSNodeAllotment fsNodeAllotment = this.primaryFileSystem.getFSNodeAllotment();
        MultipartFile multipartFile = dto.getFile();
        File tempFile = File.createTempFile("upload",".temp");
        multipartFile.transferTo(tempFile);

        FileChannel channel = FileChannel.open(tempFile.toPath(), StandardOpenOption.READ);
        TitanFileChannelChanface titanFileChannelKChannel = new TitanFileChannelChanface( channel );
        FileNode fileNode = fsNodeAllotment.newFileNode();
        fileNode.setDefinitionSize( tempFile.length() );
        fileNode.setName( tempFile.getName() );
        TitanFileReceiveEntity64 receiveEntity = new TitanFileReceiveEntity64( this.primaryFileSystem, dto.getFilePath(), fileNode,titanFileChannelKChannel,this.primaryVolume );

        this.primaryFileSystem.receive( receiveEntity );
        return BasicResultResponse.success();
    }
}
