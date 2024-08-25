package com.walnut.sparta.services.pojo;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.lang.GenericDynamicFactory;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.service.tree.ApplicationNodeMeta;
import com.pinecone.hydra.service.tree.nodes.GenericApplicationNode;
import com.pinecone.hydra.service.tree.nodes.GenericClassificationNode;
import com.pinecone.hydra.service.tree.nodes.GenericServiceNode;
import com.pinecone.hydra.service.tree.FunctionalNodeMeta;
import com.pinecone.hydra.service.tree.MetaNodeOperator;
import com.pinecone.hydra.service.tree.ServiceTreeMapper;
import com.pinecone.hydra.service.tree.source.ApplicationNodeManipulator;
import com.pinecone.hydra.service.tree.source.ClassifNodeManipulator;
import com.pinecone.hydra.unit.udsn.GUIDDistributedScopeNode;
import com.pinecone.hydra.service.tree.source.ServiceNodeManipulator;
import com.pinecone.hydra.service.tree.MetaNodeOperatorProxy;
import com.pinecone.hydra.unit.udsn.GenericDistributedScopeTree;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DistributedScopeService implements Pinenut {
    private ServiceTreeMapper serviceTreeMapper;
    private ApplicationNodeManipulator applicationNodeManipulator;
    private ServiceNodeManipulator serviceNodeManipulator;
    private ClassifNodeManipulator classifNodeManipulator;
    private MetaNodeOperator classifFunctionalNodeOperation;
    private MetaNodeOperator applicationFunctionalNodeOperation;
    private MetaNodeOperator serviceFunctionalNodeOperation;
    private MetaNodeOperatorProxy metaNodeOperatorProxy;

    public DistributedScopeService(MetaNodeOperator classifFunctionalNodeOperation, MetaNodeOperator applicationFunctionalNodeOperation,
                                   MetaNodeOperator serviceFunctionalNodeOperation, ServiceTreeMapper serviceTreeMapper,
                                   ApplicationNodeManipulator applicationNodeManipulator, ServiceNodeManipulator serviceNodeManipulator, ClassifNodeManipulator classifNodeManipulator){
        this.classifFunctionalNodeOperation = classifFunctionalNodeOperation;
        this.applicationFunctionalNodeOperation = applicationFunctionalNodeOperation;
        this.serviceFunctionalNodeOperation = serviceFunctionalNodeOperation;
        this.serviceTreeMapper=serviceTreeMapper;
        this.applicationNodeManipulator=applicationNodeManipulator;
        this.serviceNodeManipulator = serviceNodeManipulator;
        this.classifNodeManipulator=classifNodeManipulator;
        this.metaNodeOperatorProxy=new MetaNodeOperatorProxy();
        this.metaNodeOperatorProxy.registration(ApplicationNodeMeta.class.getName(), applicationFunctionalNodeOperation);
        this.metaNodeOperatorProxy.registration(ClassificationNodeMeta.class.getName(), classifFunctionalNodeOperation);
        this.metaNodeOperatorProxy.registration(ServiceNodeMeta.class.getName(), serviceFunctionalNodeOperation);
    }

    //保存节点
    //这里有个问题，将这个移入Operator中但是现在这里的逻辑就是在获取Operator,要不要传入szClassFullName
    public GUID saveApplicationNode(com.walnut.sparta.services.pojo.ApplicationNodeMeta applicationNodeInformation){
        GenericDynamicFactory genericDynamicFactory = new GenericDynamicFactory();
        try {
            Object nodeInformation = genericDynamicFactory.loadInstance("com.walnut.sparta.pojo.ApplicationFunctionalNodeInformation", null, null);
            Class<?> nodeInformationClass = nodeInformation.getClass();
            MetaNodeOperator nodeOperation = this.metaNodeOperatorProxy.getNodeOperation(nodeInformationClass.getName());
            return nodeOperation.insert(applicationNodeInformation);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public GUID saveServiceNode(ServiceNodeMeta serviceNodeInformation){
        GenericDynamicFactory genericDynamicFactory = new GenericDynamicFactory();
        try {
            Object nodeInformation = genericDynamicFactory.loadInstance("com.walnut.sparta.pojo.ServiceFunctionalNodeInformation", null, null);
            Class<?> nodeInformationClass = nodeInformation.getClass();
            MetaNodeOperator nodeOperation = this.metaNodeOperatorProxy.getNodeOperation(nodeInformationClass.getName());
            return nodeOperation.insert(serviceNodeInformation);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public GUID saveClassifNode(ClassificationNodeMeta classifNodeInformation){
        GenericDynamicFactory genericDynamicFactory = new GenericDynamicFactory();
        try {
            Object nodeInformation = genericDynamicFactory.loadInstance("com.walnut.sparta.pojo.ClassifFunctionalNodeInformation", null, null);
            Class<?> nodeInformationClass = nodeInformation.getClass();
            MetaNodeOperator nodeOperation = this.metaNodeOperatorProxy.getNodeOperation(nodeInformationClass.getName());
            return nodeOperation.insert(classifNodeInformation);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    //删除节点
    public void deleteNode(GUID UUID){
        GUIDDistributedScopeNode node = this.serviceTreeMapper.selectNode(UUID);
        UOI type = node.getType();
        GenericDynamicFactory genericDynamicFactory = new GenericDynamicFactory();
        try {
            Object nodeInformation = genericDynamicFactory.loadInstance(type.getObjectName(), null, null);
            Class<?> nodeInformationClass = nodeInformation.getClass();
            MetaNodeOperator nodeOperation = this.metaNodeOperatorProxy.getNodeOperation(nodeInformationClass.getName());
            nodeOperation.remove(UUID);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    //查找节点信息
    public FunctionalNodeMeta selectNode(GUID guid){
        //先查看缓存表中是否存在路径信息，不存在则补齐
        String path = this.serviceTreeMapper.selectPath(guid);
        if (path==null){
            GUIDDistributedScopeNode node = this.serviceTreeMapper.selectNode(guid);
            String nodeName = getNodeName(node);
            String pathString="";
            pathString=pathString+nodeName;
            while (node.getParentGUID() != null){
                node=this.serviceTreeMapper.selectNode(node.getParentGUID());
                nodeName = getNodeName(node);
                pathString=nodeName + "." + pathString;
            }
            this.serviceTreeMapper.savePath(pathString,guid);
        }
        GUIDDistributedScopeNode node = this.serviceTreeMapper.selectNode(guid);
        UOI type = node.getType();
        GenericDynamicFactory genericDynamicFactory = new GenericDynamicFactory();
        try {
            Object nodeInformation = genericDynamicFactory.loadInstance(type.getObjectName(), null, null);
            Class<?> nodeInformationClass = nodeInformation.getClass();
            MetaNodeOperator nodeOperation = this.metaNodeOperatorProxy.getNodeOperation(nodeInformationClass.getName());
            return nodeOperation.get(guid);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private String getNodeName(GUIDDistributedScopeNode node){
        UOI type = node.getType();
        GenericDynamicFactory genericDynamicFactory = new GenericDynamicFactory();
        try {
            Object nodeInformation = genericDynamicFactory.loadInstance(type.getObjectName(), null, null);
            Class<?> nodeInformationClass = nodeInformation.getClass();
            MetaNodeOperator nodeOperation = this.metaNodeOperatorProxy.getNodeOperation(nodeInformationClass.getName());
            FunctionalNodeMeta functionalNodeMeta = nodeOperation.get(node.getGuid());
            return functionalNodeMeta.getName();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void updatePath(GUID guid){
        GUIDDistributedScopeNode node = this.serviceTreeMapper.selectNode(guid);
        String nodeName = getNodeName(node);
        String pathString="";
        pathString=pathString+nodeName;
        while (node.getParentGUID() != null){
            node=this.serviceTreeMapper.selectNode(node.getParentGUID());
            nodeName = getNodeName(node);
            pathString=nodeName + "." + pathString;
        }
        this.serviceTreeMapper.updatePath(guid,pathString);
    }

    public FunctionalNodeMeta parsePath(String path) {
        GenericDistributedScopeTree distributedScopeTree = new GenericDistributedScopeTree(this.serviceTreeMapper,
                this.applicationNodeManipulator,
                this.serviceNodeManipulator,
                this.classifNodeManipulator,
                new MetaNodeOperatorProxy());
        // 先查看缓存表中是否存在路径信息
        GUID guid = this.serviceTreeMapper.parsePath(path);
        if (guid != null) {
            return selectNode(guid);
        }

        // 如果不存在，则根据路径信息获取节点信息并且更新缓存表
        // 分割路径，并处理括号
        String[] parts = processPath(path).split("\\.");

        // 根据最后一个节点尝试查找 ServiceNode
        List<GenericServiceNode> genericServiceNodes = this.serviceNodeManipulator.selectServiceNodeByName(parts[parts.length - 1]);
        for (GenericServiceNode genericServiceNode : genericServiceNodes) {
            String nodePath = distributedScopeTree.getPath(genericServiceNode.getGuid());
            if (nodePath.equals(path)) {
                return selectNode(genericServiceNode.getGuid());
            }
        }

        // 根据最后一个节点尝试查找 ApplicationNode
        List<GenericApplicationNode> genericApplicationNodes = this.applicationNodeManipulator.fetchApplicationNodeByName(parts[parts.length - 1]);
        for (GenericApplicationNode genericApplicationNode : genericApplicationNodes) {
            String nodePath = distributedScopeTree.getPath(genericApplicationNode.getGuid());
            if (nodePath.equals(path)) {
                return selectNode(genericApplicationNode.getGuid());
            }
        }

        // 根据最后一个节点尝试查找 ClassificationNode
        List<GenericClassificationNode> genericClassificationNodes = this.classifNodeManipulator.fetchClassifNodeByName(parts[parts.length - 1]);
        for (GenericClassificationNode genericClassificationNode : genericClassificationNodes) {
            String nodePath = distributedScopeTree.getPath(genericClassificationNode.getGuid());
            if (nodePath.equals(path)) {
                return selectNode(genericClassificationNode.getGuid());
            }
        }

        return null;
    }

    private String processPath(String path) {
        // 使用正则表达式移除所有的括号及其内容
        return path.replaceAll("\\(.*?\\)", "");
    }
}
