package com.walnut.sparta.ucdn.service.api.controller.v2;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.bucket.BucketInstrument;
import com.pinecone.hydra.storage.bucket.entity.GenericSite;
import com.pinecone.hydra.storage.bucket.entity.Site;
import com.pinecone.hydra.storage.bucket.source.SiteManipulator;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.Folder;
import com.walnut.sparta.ucdn.service.api.response.BasicResultResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping( "/api/v2/ucdn/site" )
@CrossOrigin
public class SiteController implements Pinenut {
    @Resource
    private BucketInstrument bucketInstrument;

    @Resource
    private KOMFileSystem primaryFileSystem;

    /**
     * 创建站点
     * @param siteName 站点名
     * @return 返回操作结果
     */
    @PutMapping("/createSite")
    public BasicResultResponse<String> createSite(@RequestParam("siteName") String siteName){
        Folder folder = this.primaryFileSystem.affirmFolder(siteName);
        GenericSite site = new GenericSite();
        site.setSiteName( siteName );
        site.setMountPointGuid( folder.getGuid() );
        this.bucketInstrument.createSite( site );

        return BasicResultResponse.success();
    }

    /**
     * 删除站点
     * @param siteName 站点名
     * @return 操作结果
     */
    @DeleteMapping("/deleteSite")
    public BasicResultResponse<String> removeSite( @RequestParam("siteName") String siteName ){
        this.bucketInstrument.removeSite(siteName);

        return BasicResultResponse.success();
    }

    /**
     * 获取全部站点
     * @return 返回全部站点
     */
    @GetMapping("/listSize")
    public String listSite(){
        List<Site> sites = this.bucketInstrument.listSite();
        return BasicResultResponse.success(sites).toJSONString();
    }

}
