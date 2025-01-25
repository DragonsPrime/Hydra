package com.walnuts.sparta.account.api.controller.v2;


import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.AccountManager;
import com.pinecone.hydra.account.entity.GenericDomain;
import com.pinecone.hydra.account.entity.GenericGroup;
import com.pinecone.ulf.util.guid.GUIDs;
import com.walnuts.sparta.account.api.response.BasicResultResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.awt.image.RasterFormatException;

@RestController
@RequestMapping( "/api/v2/account" )
@CrossOrigin
public class AccountController {
    @Resource
    private AccountManager  primaryAccount;

    @PutMapping("/create/domain")
    public BasicResultResponse<String> createDomain( @RequestParam("doMainName") String doMainName ){
        GenericDomain domain = new GenericDomain();
        domain.setName( doMainName );
        this.primaryAccount.put(domain);
        return BasicResultResponse.success();
    }

    @PutMapping("/create/group")
    public BasicResultResponse<String> createGroup( @RequestParam("parentGuid") String parentGuid, @RequestParam("groupName") String groupName ){
        GenericGroup genericGroup = new GenericGroup();
        genericGroup.setName(groupName);
        this.primaryAccount.put( genericGroup );
        this.primaryAccount.addChildren(GUIDs.GUID72(parentGuid), genericGroup.getGuid() );
        return BasicResultResponse.success();
    }

    @GetMapping("/query/path")
    public String queryNodeByPath( @RequestParam("path") String path ){
        GUID guid = this.primaryAccount.queryGUIDByPath(path);
        return BasicResultResponse.success(this.primaryAccount.get(guid)).toJSONString();
    }


}
