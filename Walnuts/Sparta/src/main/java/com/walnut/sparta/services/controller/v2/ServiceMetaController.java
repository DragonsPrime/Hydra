//package com.walnut.sparta.services.controller.v2;
//
//import com.pinecone.framework.util.id.GUID;
//import com.pinecone.hydra.service.kom.ServicesInstrument;
//import com.pinecone.hydra.service.kom.nodes.GenericApplicationNode;
//import com.pinecone.hydra.service.kom.nodes.GenericNamespace;
//import com.pinecone.hydra.service.kom.nodes.GenericServiceNode;
//import com.pinecone.hydra.service.kom.nodes.ServiceTreeNode;
//import com.pinecone.hydra.service.kom.source.ServiceMasterManipulator;
//import com.pinecone.hydra.service.kom.source.ServiceFamilyTreeManipulator;
//import com.pinecone.hydra.service.kom.entity.GenericMetaNodeInstanceFactory;
//import com.pinecone.hydra.service.kom.entity.MetaNodeWideEntity;
//import com.pinecone.hydra.service.kom.entity.MetaNodeInstanceFactory;
//import com.pinecone.ulf.util.id.GUID72;
//import com.pinecone.hydra.service.kom.CentralServicesInstrument;
//import com.walnut.sparta.services.drivers.ServiceMasterTreeManipulatorImpl;
//import com.walnut.sparta.system.BasicResultResponse;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//
//@RestController
//@RequestMapping( "/api/v2/serviceMeta" )
//public class ServiceMetaController {
//    @Resource
//    private ServiceMasterManipulator serviceMasterManipulator;
//
//    @Resource
//    private ServiceMasterTreeManipulatorImpl treeManipulatorSharer;
//
//    private ServicesInstrument servicesTree;
//
//    MetaNodeInstanceFactory metaNodeInstanceFactory;
//
//    @PostConstruct
//    public void init() {
//        this.servicesTree = new CentralServicesInstrument( null,serviceMasterManipulator);
//        this.metaNodeInstanceFactory = new GenericMetaNodeInstanceFactory(this.serviceMasterManipulator,treeManipulatorSharer);
//    }
//
//    /**
//     * 渲染单节点信息
//     * @param guid 节点UUID
//     * @return 返回节点信息
//     */
//    @GetMapping("/queryNodeInfoByGUID/{guid}")
//    public BasicResultResponse<ServiceTreeNode> queryNodeInfoByGUID(@PathVariable("guid") String guid ){
//        GUID72 guid72 = new GUID72( guid );
//        return BasicResultResponse.success(this.servicesTree.getNode( guid72 ));
//    }
//
//    /**
//     * 用于将路径反解析为节点信息
//     * @param path 节点路径
//     * @return 返回节点信息
//     */
//    @GetMapping("/queryNodeInfoByPath")
//    public BasicResultResponse<ServiceTreeNode> queryNodeInfoByPath( @RequestParam("path") String path ){
//        ServiceTreeNode node = this.servicesTree.parsePath( path );
//        if( node == null ) {
//            return BasicResultResponse.error( "No such node" );
//        }
//        return BasicResultResponse.success( this.servicesTree.parsePath(path) );
//    }
//
//    /**
//     * 创建一个服务节点
//     * @param serviceNode 服务节点信息
//     * @return 创建的节点的GUID
//     */
//    @PostMapping("/putServiceNode")
//    public BasicResultResponse<String> putServiceNode( @RequestBody GenericServiceNode serviceNode ){
//        return BasicResultResponse.success(this.servicesTree.addNode( serviceNode ).toString());
//    }
//
//    /**
//     * 创建一个应用节点
//     * @param applicationNode 应用节点信息
//     * @return  创建的节点的GUID
//     */
//    @PostMapping("/putApplicationNode")
//    public BasicResultResponse<String> putApplicationNode( @RequestBody GenericApplicationNode applicationNode ){
//        return BasicResultResponse.success(this.servicesTree.addNode(applicationNode).toString());
//    }
//
//    /**
//     * 创建一个分类节点
//     * @param classificationNode 分类节点信息
//     * @return 创建的节点的GUID
//     */
//    @PostMapping("/putClassificationNode")
//    public BasicResultResponse<String> putClassificationNode( @RequestBody GenericNamespace classificationNode ){
//        return BasicResultResponse.success(this.servicesTree.addNode(classificationNode).toString());
//    }
//
//    /**
//     * 删除节点
//     * @param guid 节点的guid
//     * @return 返回删除情况
//     */
//    @DeleteMapping("/removeSingleNode")
//    public BasicResultResponse<String> removeSingleNode(@RequestParam("guid") String guid){
//        this.servicesTree.removeNode( new GUID72( guid ) );
//        return BasicResultResponse.success();
//    }
//
//    /**
//     * 渲染单节点所有信息（含继承）
//     * @param guid 节点UUID
//     * @return 返回节点信息
//     */
//    @GetMapping("/queryNodeWideInfo/{guid}")
//    public BasicResultResponse<MetaNodeWideEntity> queryNodeWideInfo(@PathVariable("guid") String guid ){
//        GUID72 guid72 = new GUID72( guid );
//        return BasicResultResponse.success(this.servicesTree.getWideMeta(guid72));
//    }
//
//    /**
//     * 删除节点（完全移除）
//     * @param guid 节点的guid
//     * @return 返回移除结果
//     */
//    @GetMapping("/remove")
//    public BasicResultResponse<String> remove(@RequestParam("guid") String guid){
//        GUID72 guid72 = new GUID72( guid );
//        this.servicesTree.remove(guid72);
//        return BasicResultResponse.success();
//    }
//
//    /**
//     * 用于添加继承关系
//     * @param childNode 子节点GUID
//     * @param parentNode 父节点GUID
//     * @return 返回继承信息
//     */
//    @PostMapping("/inherit")
//    public BasicResultResponse<String> inherit(@RequestParam("childNode") GUID childNode,@RequestParam("parentNode") GUID parentNode){
//        ServiceFamilyTreeManipulator serviceFamilyTreeManipulator = this.serviceMasterManipulator.getServiceFamilyTreeManipulator();
//        serviceFamilyTreeManipulator.insert(childNode,parentNode);
//        return BasicResultResponse.success();
//    }
//
//    /**
//     * 用于渲染路径信息
//     * @param guid 节点UUID
//     * @return 返回路径信息
//     */
//    @GetMapping("/getPath/{GUID}")
//    public BasicResultResponse<String> getPath(@PathVariable("GUID") String guid){
//        return BasicResultResponse.success( this.servicesTree.getPath( new GUID72(guid) ) );
//    }
//}
