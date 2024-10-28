package com.pinecone.hydra.task.tree;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;
import com.pinecone.hydra.system.ko.UOIUtils;
import com.pinecone.hydra.task.entity.GenericTaskCommonData;
import com.pinecone.hydra.task.entity.GenericTaskNode;
import com.pinecone.hydra.task.entity.GenericTaskNodeMeta;
import com.pinecone.hydra.task.entity.TaskNode;
import com.pinecone.hydra.task.source.TaskCommonDataManipulator;
import com.pinecone.hydra.task.source.TaskMasterManipulator;
import com.pinecone.hydra.task.source.TaskNodeManipulator;
import com.pinecone.hydra.task.source.TaskNodeMetaManipulator;
import com.pinecone.hydra.unit.udtt.DistributedTrieTree;
import com.pinecone.hydra.unit.udtt.DistributedTreeNode;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.GenericDistributedTrieTree;
import com.pinecone.hydra.unit.udtt.source.TreeMasterManipulator;
import com.pinecone.ulf.util.id.GuidAllocator;
import com.pinecone.ulf.util.id.GUIDs;

import java.time.LocalDateTime;
import java.util.List;

public class GenericDistributedTaskMetaTree implements DistributedTaskMetaTree{
    protected Hydrarum                      hydrarum;
    private DistributedTrieTree             distributedTrieTree;

    private TaskNodeManipulator             taskNodeManipulator;

    private TaskNodeMetaManipulator         taskNodeMetaManipulator;

    private TaskCommonDataManipulator       taskCommonDataManipulator;
    public GenericDistributedTaskMetaTree( Hydrarum hydrarum, KOIMasterManipulator masterManipulator ){
        this.hydrarum = hydrarum;
        TaskMasterManipulator taskMasterManipulator = (TaskMasterManipulator) masterManipulator;

        KOISkeletonMasterManipulator skeletonMasterManipulator = taskMasterManipulator.getSkeletonMasterManipulator();
        TreeMasterManipulator        treeMasterManipulator     = (TreeMasterManipulator) skeletonMasterManipulator;

        this.taskCommonDataManipulator = taskMasterManipulator.getTaskCommonDataManipulator();
        this.taskNodeMetaManipulator   = taskMasterManipulator.getTaskNodeMetaManipulator();
        this.taskNodeManipulator       = taskMasterManipulator.getTaskNodeManipulator();
        this.distributedTrieTree = new GenericDistributedTrieTree(treeMasterManipulator);
    }

    public GenericDistributedTaskMetaTree( Hydrarum hydrarum ) {
        this.hydrarum = hydrarum;
    }

    public GenericDistributedTaskMetaTree( KOIMappingDriver driver ) {
        this(
                driver.getSystem(),
                driver.getMasterManipulator()
        );
    }

    @Override
    public String getPath(GUID guid) {
        String path = this.distributedTrieTree.getCachePath(guid);
        if (path!=null) return path;
        DistributedTreeNode node = this.distributedTrieTree.getNode(guid);
        Debug.trace(node.toString());
        String assemblePath = this.getNodeName(node);
        while (!node.getParentGUIDs().isEmpty()){
            List<GUID> parentGuids = node.getParentGUIDs();
            node = this.distributedTrieTree.getNode(parentGuids.get(0));
            String nodeName = this.getNodeName(node);
            assemblePath = nodeName + "." + assemblePath;
        }
        this.distributedTrieTree.insertCachePath(guid, assemblePath);
        return assemblePath;
    }

    @Override
    public GUID insert(TreeNode treeNode) {
        GenericTaskNode taskNode = (GenericTaskNode) treeNode;
        GuidAllocator guidAllocator = GUIDs.newGuidAllocator();

        GenericTaskNodeMeta genericTaskNodeMeta = taskNode.getGenericTaskNodeMeta();
        GUID TaskNodeMetaGuid = guidAllocator.nextGUID72();
        genericTaskNodeMeta.setGuid(TaskNodeMetaGuid);

        GenericTaskCommonData genericTaskCommonData = taskNode.getGenericTaskCommonData();
        GUID TaskCommonDataGuid = guidAllocator.nextGUID72();
        genericTaskCommonData.setGuid(TaskCommonDataGuid);
        genericTaskCommonData.setCreateTime(LocalDateTime.now());
        genericTaskCommonData.setUpdateTime(LocalDateTime.now());

        GUIDDistributedTrieNode guidDistributedTrieNode = new GUIDDistributedTrieNode();
        GUID nodeGuid = guidAllocator.nextGUID72();
        guidDistributedTrieNode.setGuid(nodeGuid);
        guidDistributedTrieNode.setNodeMetadataGUID(TaskNodeMetaGuid);
        guidDistributedTrieNode.setBaseDataGUID(TaskCommonDataGuid);
        guidDistributedTrieNode.setType(UOIUtils.createLocalJavaClass(taskNode.getClass().getName()));
        taskNode.setGuid(nodeGuid);

        this.distributedTrieTree.insert(guidDistributedTrieNode);
        this.taskNodeManipulator.insert(taskNode);
        this.taskNodeMetaManipulator.insert(genericTaskNodeMeta);
        this.taskCommonDataManipulator.insert(genericTaskCommonData);
        return nodeGuid;
    }

    @Override
    public TreeNode get(GUID guid) {
        GUIDDistributedTrieNode node = this.distributedTrieTree.getNode(guid);
        TaskNode taskNode = this.taskNodeManipulator.getTaskNode(guid);
        GenericTaskNodeMeta taskNodeMeta = (GenericTaskNodeMeta) this.taskNodeMetaManipulator.getTaskNodeMeta(node.getNodeMetadataGUID());
        GenericTaskCommonData taskCommonData = (GenericTaskCommonData) this.taskCommonDataManipulator.getTaskCommonData(node.getAttributesGUID());
        taskNode.setGenericTaskNodeMeta(taskNodeMeta);
        taskNode.setGenericTaskCommonData(taskCommonData);
        return taskNode;
    }

    @Override
    public TreeNode parsePath(String path) {
        GUID guid = this.distributedTrieTree.queryGUIDByPath( path );
        if (guid != null){
            return this.get(guid);
        }
        else{
            String[] parts = this.processPath(path).split("\\.");
            List<GUID> nodeByName = this.taskNodeManipulator.getGuidsByName(parts[parts.length - 1]);
            for(GUID nodeGuid :nodeByName){
                if (this.getPath(nodeGuid).equals(path)){
                    return this.get(nodeGuid);
                }
            }
        }
        return null;
    }

    @Override
    public void remove(GUID guid) {
        List<GUIDDistributedTrieNode> childNodes = this.distributedTrieTree.getChildren(guid);
        if (childNodes == null || childNodes.isEmpty()){
            this.removeNode(guid);
        }
        else {
            for(GUIDDistributedTrieNode childNode : childNodes){
                List<GUID> parentNodes = this.distributedTrieTree.fetchParentGuids(childNode.getGuid());
                if (parentNodes.size() > 1){
                    this.distributedTrieTree.removeInheritance(childNode.getGuid(),guid);
                }else {
                    this.remove(childNode.getGuid());
                }
            }
            this.removeNode(guid);
        }
    }

    @Override
    public TreeNode getSelf(GUID guid) {
        return null;
    }

    private String getNodeName(DistributedTreeNode node){
        return this.taskNodeManipulator.getTaskNode(node.getGuid()).getName();
    }

    private String processPath(String path) {
        // 使用正则表达式移除所有的括号及其内容
        return path.replaceAll("\\(.*?\\)", "");
    }

    private void removeNode(GUID guid){
        GUIDDistributedTrieNode node = this.distributedTrieTree.getNode(guid);
        this.distributedTrieTree.purge(guid);
        this.distributedTrieTree.removeCachePath( guid );
        this.taskNodeManipulator.remove(guid);
        this.taskNodeMetaManipulator.remove(node.getNodeMetadataGUID());
        this.taskCommonDataManipulator.remove(node.getAttributesGUID());
    }
}
