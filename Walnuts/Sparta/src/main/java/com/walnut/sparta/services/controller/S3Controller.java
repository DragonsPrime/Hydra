package com.walnut.sparta.services.controller;

import com.walnut.sparta.system.BasicResultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/v2/UOFSController" )
public class S3Controller {
    @GetMapping("/bucket")
    public BasicResultResponse<String> createBucket(@RequestParam String bucketName){
        return BasicResultResponse.success(bucketName);
    }


}
