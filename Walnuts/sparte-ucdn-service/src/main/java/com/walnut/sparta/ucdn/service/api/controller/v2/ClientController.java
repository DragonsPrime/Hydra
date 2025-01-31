package com.walnut.sparta.ucdn.service.api.controller.v2;


import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.io.TitanOutputStreamChanface;
import com.pinecone.hydra.storage.bucket.BucketInstrument;
import com.pinecone.hydra.storage.bucket.entity.Site;
import com.pinecone.hydra.storage.bucket.source.SiteManipulator;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.Folder;
import com.pinecone.hydra.storage.file.transmit.exporter.TitanFileExportEntity64;
import com.pinecone.hydra.storage.version.VersionManage;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.walnut.sparta.ucdn.service.infrastructure.constants.PolicyConstants;
import com.walnut.sparta.ucdn.service.infrastructure.exception.IllegalPathException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;

@RestController
@CrossOrigin
public class ClientController {

    @Resource
    private KOMFileSystem               primaryFileSystem;

    @Resource
    private UniformVolumeManager        primaryVolume;

    @Resource
    private VersionManage               primaryVersion;

    @Resource
    private BucketInstrument            bucketInstrument;

    @GetMapping("/titan/version")
    public String queryVersion(HttpServletRequest request, HttpServletResponse response){
        return "undefined";
    }

    @GetMapping("/**")
    public void  getFile(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException,IllegalPathException {
        String servletPath = request.getServletPath();
        String[] pathPart = servletPath.split(PolicyConstants.FORWARD_SLASH);

        if( pathPart.length < 3 ){
            //todo 路径500问题
            throw new IllegalPathException();
        }

        String siteName = pathPart[1];

        //todo 统一路径解析
        StringBuilder filePath = new StringBuilder();
        for( int i = 2; i < pathPart.length; i++ ){
            if( i == pathPart.length - 1 ){
                int dotIndex = pathPart[i].lastIndexOf(PolicyConstants.PERIOD);
                String baseName = pathPart[i].substring(0, dotIndex);
                filePath.append(baseName);
            }else {
                filePath.append(pathPart[i]).append(PolicyConstants.FORWARD_SLASH);
            }

        }


        SiteManipulator siteManipulator = this.bucketInstrument.getSiteManipulator();
        Site site = siteManipulator.querySiteByName(siteName);

        String realFilePath = this.primaryFileSystem.getPath(site.getMountPointGuid()) + PolicyConstants.FORWARD_SLASH + filePath;

        //后续升级成责任链获得更好的扩展性
        Map<String, String[]> parameterMap = request.getParameterMap();

        String version = "";
        if( parameterMap.get("version") != null ){
            version = parameterMap.get("version")[0];
        }

        ServletOutputStream outputStream = response.getOutputStream();
        TitanOutputStreamChanface kChannel = new TitanOutputStreamChanface(outputStream);

        Folder folder = (Folder) this.primaryFileSystem.get(this.primaryFileSystem.queryGUIDByPath(realFilePath));
        GUID storageObjectGuid = this.primaryVersion.queryObjectGuid(version, folder.getGuid());
        FileNode storageObject = (FileNode) this.primaryFileSystem.get(storageObjectGuid);

        TitanFileExportEntity64 entity = new TitanFileExportEntity64(this.primaryFileSystem, this.primaryVolume, storageObject, kChannel);
        this.primaryFileSystem.export( entity );

    }



}
