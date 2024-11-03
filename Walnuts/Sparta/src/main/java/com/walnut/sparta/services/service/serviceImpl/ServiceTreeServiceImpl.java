//package com.walnut.sparta.services.service.serviceImpl;
//
//import com.pinecone.framework.util.Debug;
//import com.pinecone.framework.util.id.GUID;
//import com.pinecone.framework.util.uoi.UOI;
//import com.pinecone.hydra.service.kom.nodes.ServiceTreeNode;
//import com.pinecone.hydra.service.kom.source.ServiceMasterManipulator;
//import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
//import com.pinecone.hydra.service.ibatis.ServiceTrieTreeMapper;
//import com.walnut.sparta.services.service.ServiceTreeService;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import java.util.List;
//
//@Service
//
//public class ServiceTreeServiceImpl implements ServiceTreeService {
//    @Resource
//    private ServiceTrieTreeMapper trieTreeManipulator;
//    @Resource
//    private ServiceMasterManipulator serviceMasterManipulator;
//
//    private MetaNodeOperatorProxy               metaNodeOperatorProxy;
//
//    @PostConstruct
//    public void init() {
//        this.metaNodeOperatorProxy = new MetaNodeOperatorProxy(this.serviceMasterManipulator);
//    }
//
//
//    @Override
//    public void addNodeToParent(GUID nodeGUID, GUID parentGUID) {
//        //将节点加入指定位置
//        this.trieTreeManipulator.insertOwnedNode(nodeGUID,parentGUID);
//        //添加后要更新节点路径
//        //递归查询所有要更新的节点
//        upDateAllPath(nodeGUID);
//    }
//
//    @Override
//    public void removeNode(GUID nodeGUID) {
//        //像文件夹一样删除父文件会连带一起输出子文件
//        removeAllNode(nodeGUID);
//    }
//
//    private void removeAllNode(GUID nodeGUID){
//        List<GUIDDistributedTrieNode> childNodes = this.trieTreeManipulator.getChild(nodeGUID);
//        this.trieTreeManipulator.purge(nodeGUID);
//        this.trieTreeManipulator.removePath(nodeGUID);
//        if (childNodes==null) return;
//        for (GUIDDistributedTrieNode guidDistributedTrieNode :childNodes){
//            removeNode(guidDistributedTrieNode.getGuid());
//        }
//    }
//
//    private void upDateAllPath(GUID guid){
//        updatePath(guid);
//        List<GUIDDistributedTrieNode> childNodes = this.trieTreeManipulator.getChild(guid);
//        Debug.trace("节点"+guid+"的子节点有"+childNodes.toString());
//        for(GUIDDistributedTrieNode guidDistributedTrieNode :childNodes){
//            if (guidDistributedTrieNode !=null){
//                upDateAllPath(guidDistributedTrieNode.getGuid());
//            }
//        }
//    }
//    private void updatePath(GUID guid){
//        GUIDDistributedTrieNode node = this.trieTreeManipulator.getNode(guid);
//        String nodeName = getNodeName(node);
//        String pathString="";
//        pathString=pathString+nodeName;
//        while (node.getParentGUIDs() != null){
//            for (GUID parentGUID : node.getParentGUIDs()){
//                node = this.trieTreeManipulator.getNode(parentGUID);
//                nodeName = getNodeName(node);
//                pathString = nodeName + "." + pathString;
//            }
//        }
//        this.trieTreeManipulator.updatePath(guid,pathString);
//    }
//    private String getNodeName(GUIDDistributedTrieNode node){
//        UOI type = node.getType();
//        ServiceTreeNode newInstance = (ServiceTreeNode)type.newInstance();
//        MetaNodeOperator operator = metaNodeOperatorProxy.getOperator(newInstance.getMetaType());
//        ServiceTreeNode serviceTreeNode = operator.get(node.getGuid());
//        Debug.trace("获取到了节点"+serviceTreeNode);
//        return serviceTreeNode.getName();
//    }
//}
