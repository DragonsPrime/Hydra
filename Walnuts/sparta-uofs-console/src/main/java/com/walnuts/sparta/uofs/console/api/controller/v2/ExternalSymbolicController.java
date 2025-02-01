package com.walnuts.sparta.uofs.console.api.controller.v2;


import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.direct.GenericExternalFolder;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.ExternalSymbolic;
import com.walnuts.sparta.uofs.console.api.response.BasicResultResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.xml.transform.Result;
import java.io.File;

@RestController
@RequestMapping( "/api/v2/uofs/externalSymbolic" )
@CrossOrigin
public class ExternalSymbolicController {
    @Resource
    private KOMFileSystem primaryFileSystem;

    /**
     * 获取外部目录所有内容
     * @param path 路径
     * @return 返回内容
     */
    @GetMapping("/listItem")
    public String listItem(@RequestParam("path") String path){
        ElementNode elementNode = this.primaryFileSystem.queryElement(path);
        if(elementNode != null){
            ExternalSymbolic externalSymbolic = (ExternalSymbolic) elementNode;
            GenericExternalFolder externalFolder = new GenericExternalFolder(new File(externalSymbolic.getReparsedPoint()));
            return BasicResultResponse.success(externalFolder.listItem()).toJSONString();
        }
        return BasicResultResponse.success().toJSONString();
    }
}
