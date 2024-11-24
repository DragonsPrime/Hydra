package com.walnut.sparta.services.controller;

import javax.annotation.Resource;

import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.walnut.sparta.system.BasicResultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/v2/service/uofs/" )
public class FileSystemController {
    @Resource
    private KOMFileSystem primaryFileSystem;

    @GetMapping("/bucket")
    public BasicResultResponse<String> createBucket(@RequestParam String bucketName){
        return BasicResultResponse.success(bucketName);
    }


    @GetMapping("/miao")
    public BasicResultResponse<String> miao(@RequestParam String arg){
        return BasicResultResponse.success(arg );
    }

}
