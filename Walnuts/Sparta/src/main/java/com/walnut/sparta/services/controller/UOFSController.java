package com.walnut.sparta.services.controller;

import com.pinecone.framework.system.CascadeSystem;
import com.sauron.radium.Radium;
import com.walnut.sparta.services.dto.updateObjectDto;
import com.walnut.sparta.system.BasicResultResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/v2/UOFSController" )
public class UOFSController {

    public BasicResultResponse<String> updateObject(@RequestBody updateObjectDto dto){
        return null;
    }
}
