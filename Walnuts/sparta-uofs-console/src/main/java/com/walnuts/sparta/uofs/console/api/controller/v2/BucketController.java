package com.walnuts.sparta.uofs.console.api.controller.v2;

import com.pinecone.hydra.storage.TitanFileChannelChanface;
import com.pinecone.hydra.storage.bucket.BucketInstrument;
import com.pinecone.hydra.storage.bucket.entity.Bucket;
import com.pinecone.hydra.storage.bucket.entity.GenericBucket;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.Folder;
import com.pinecone.hydra.storage.file.transmit.exporter.TitanFileExportEntity64;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.ulf.util.guid.GUIDs;
import com.walnuts.sparta.uofs.console.api.response.BasicResultResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping( "/api/v2/uofs/bucket" )
@CrossOrigin
public class BucketController {
    @Resource
    private KOMFileSystem primaryFileSystem;

    @Resource
    private BucketInstrument bucketInstrument;
    @Resource
    private TransmitController transmitController;

    @Resource
    private UniformVolumeManager primaryVolume;


    /**
     * 创建bucket
     * @param bucketName 桶名
     * @param accountGuid 用户Guid
     * @return 返回bucketGuid
     */
    @PutMapping("/{bucketName}")
    public BasicResultResponse<String> createBucket(@PathVariable String bucketName, @RequestBody String accountGuid ){
        Folder folder = this.primaryFileSystem.affirmFolder(bucketName);
        GenericBucket bucket = new GenericBucket();
        bucket.setBucketName( bucketName );
        bucket.setCreateTime(LocalDateTime.now());
        bucket.setMountPoint( folder.getGuid() );
        bucket.setUserGuid( GUIDs.GUID72( accountGuid ) );
        this.bucketInstrument.createBucket( bucket );
        return BasicResultResponse.success( bucket.getBucketGuid().toString() );
    }

    /**
     * 获取账号下的所有桶
     * @param accountGuid 用户账号guid
     * @return 返回所有桶信息
     */
    @GetMapping("/")
    public String listBuckets(@RequestParam("accountGuid") String accountGuid ){
        List<Bucket> buckets = this.bucketInstrument.queryBucketsByUserGuid(GUIDs.GUID72(accountGuid));
        return BasicResultResponse.success(buckets).toJSONString();
    }

    /**
     * 删除桶
     * @param bucketName 桶名
     * @param accountGuid 账号Guid
     * @return  返回操作结果
     */
    @DeleteMapping("/{bucketName}")
    public BasicResultResponse<String> deleteBucket( @PathVariable String bucketName, @RequestBody String accountGuid ){
        this.bucketInstrument.removeBucketByAccountAndBucketName( GUIDs.GUID72(accountGuid), bucketName  );
        this.primaryFileSystem.remove( bucketName );
        return BasicResultResponse.success();
    }

    /**
     *  获取存储对象
     * @param bucketName 桶名
     * @param objectName 对象名
     * @param targetPath 目标地址
     * @return 操作结果
     */
    @GetMapping("/{bucketName}/{objectName}")
    public BasicResultResponse<String> getObject(@PathVariable String bucketName, @PathVariable String objectName, @RequestBody String targetPath) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        FileNode fileNode = (FileNode) this.primaryFileSystem.get(this.primaryFileSystem.queryGUIDByPath(bucketName + "/" + objectName));
        File file = new File(targetPath);
        FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
        TitanFileChannelChanface kChannel = new TitanFileChannelChanface( channel );
        TitanFileExportEntity64 exportEntity = new TitanFileExportEntity64(this.primaryFileSystem, this.primaryVolume , fileNode, kChannel );
        this.primaryFileSystem.export( exportEntity );
        return BasicResultResponse.success();
    }

    @GetMapping("/*")
    public void test(HttpServletRequest httpRequest){

    }
    /**
     * 上传储存对象
     * @param bucketName 桶名
     * @param objectName 对象名
     * @return
     */
    @PutMapping("/{bucketName}/{objectName}")
    public BasicResultResponse<String> putObject(@PathVariable String bucketName, @PathVariable String objectName, @RequestBody MultipartFile file){
        return BasicResultResponse.success();
    }

    /**
     * 删除存储对象
     * @param bucketName 桶名
     * @param objectName 存储对象名
     * @return 返回操作信息
     */
    @DeleteMapping("/{bucketName}/{objectName}")
    public BasicResultResponse<String> deleteObject( @PathVariable String bucketName, @PathVariable String objectName ){
        return BasicResultResponse.success();
    }

    /**
     * 列出储存桶中的对象
     * @param bucketName 桶名
     * @return 返回对象列表
     */
    public BasicResultResponse<List<FileNode>> listObjects( @PathVariable String bucketName ){
        return BasicResultResponse.success();
    }
}
