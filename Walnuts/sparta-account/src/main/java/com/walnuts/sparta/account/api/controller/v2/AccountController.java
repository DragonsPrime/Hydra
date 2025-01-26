package com.walnuts.sparta.account.api.controller.v2;


import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.AccountManager;
import com.pinecone.hydra.account.entity.*;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
import com.pinecone.ulf.util.guid.GUIDs;
import com.walnuts.sparta.account.api.response.BasicResultResponse;
import com.walnuts.sparta.account.domain.Vo.accountLoginVo;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
@DeleteMapping("remove/domain")
    public BasicResultResponse<String> removeDomain( @RequestParam("domainGuid") String domainGuid ){

        List<TreeNode> children = this.primaryAccount.getChildren( GUIDs.GUID72(domainGuid));
        for (TreeNode treeNode : children)
            this.primaryAccount.remove(treeNode.getGuid());
        this.primaryAccount.remove( GUIDs.GUID72(domainGuid) );
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
@DeleteMapping("remove/group")
    public BasicResultResponse<String> removeGroup( @RequestParam("groupGuid") String groupGuid ){
        List<TreeNode> children = this.primaryAccount.getChildren(GUIDs.GUID72(groupGuid));
        System.out.println(children.isEmpty());
        System.out.println(groupGuid);
       if (children.isEmpty())
       { this.primaryAccount.remove(GUIDs.GUID72(groupGuid));
        return BasicResultResponse.success("删除成功");}
       return BasicResultResponse.error("Group is not empty");
    }
    @GetMapping("/query/path")
    public String queryNodeByPath( @RequestParam("path") String path ){
        GUID guid = this.primaryAccount.queryGUIDByPath(path);
        return BasicResultResponse.success(this.primaryAccount.get(guid)).toJSONString();
    }
@PutMapping("create/account")
    public BasicResultResponse<String> createAccount(
            @RequestParam("userName") String userName,
            @RequestParam("nickName") String nickName,
            @RequestParam("kernelCredential") String kernelCredential,
            @RequestParam("kernelGroupType") String kernelGroupType,
            @RequestParam("parentGuid") String parentGuid)
{
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String formattedTime = now.format(formatter);
    System.out.println("Account created at: " + formattedTime);
    GenericAccount account = new GenericAccount(this.primaryAccount);
    account.setName(userName);
    if (this.primaryAccount.queryAccountByName(account.getName())==null) {
        account.setNickName(nickName);
        GenericCredential credential = new GenericCredential();
        credential.setCredential(kernelCredential);
        credential.setType("TextPassword");
        credential.setName(userName);
        credential.setCreateTime(now);
        credential.setUpdateTime(now);
        credential.setGuid(GUIDs.Dummy72());
        this.primaryAccount.insertCredential(credential);
        account.setCredentialGuid(credential.getGuid());
        account.setKernelCredential(kernelCredential);
        account.setKernelGroupType(kernelGroupType);
        account.setCreateTime(now);
        account.setUpdateTime(now);
        this.primaryAccount.put(account);
        this.primaryAccount.addChildren(GUIDs.GUID72(parentGuid), account.getGuid());
        return BasicResultResponse.success();
    }
    return BasicResultResponse.error("Account already exists");
}
@PutMapping("/login")
    public BasicResultResponse<String> login(
            @RequestParam("userName") String userName,
            @RequestParam("kernelCredential") String kernelCredential)
{
    List<GUID> userGuidList =this.primaryAccount.queryAccountByName(userName);
    if (userGuidList== null)
        return BasicResultResponse.error("Account not found");

    GUID userGuid = userGuidList.get(0); // 用户名是唯一的
    boolean isLogin =this.primaryAccount.queryAccountByGuid(userGuid,kernelCredential);
    if (!isLogin) {
        return BasicResultResponse.error("Account or kernelCredential error ");
    }
    return BasicResultResponse.success("登录成功");
}
@GetMapping("/query/account")
    public BasicResultResponse<String> queryAccount(
            @RequestParam("userName") String userName)
{
    List<GUID> userGuidList =this.primaryAccount.queryAccountByName(userName);
    if (userGuidList== null)
        return BasicResultResponse.error("Account not found");
    GUID userGuid = userGuidList.get(0); // 假设用户名是唯一的
    GenericAccount account = (GenericAccount) this.primaryAccount.get(userGuid);
    accountLoginVo accountLoginVo = new accountLoginVo();
    BeanUtils.copyProperties(account,accountLoginVo);
    return BasicResultResponse.success(accountLoginVo.toJSONString());
}
@DeleteMapping("/remove/account")
    public BasicResultResponse<String> removeAccount(
            @RequestParam("userName") String userName)
{
    List<GUID> userGuidList =this.primaryAccount.queryAccountByName(userName);
    if (userGuidList== null)
        return BasicResultResponse.error("Account not found");
    GUID userGuid = userGuidList.get(0);
    this.primaryAccount.remove(userGuid);
    return BasicResultResponse.success("删除成功");
}
}
