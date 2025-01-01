package com.pinecone.hydra.conduct.tree;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
import com.pinecone.hydra.system.ko.UOIUtils;
import com.pinecone.hydra.conduct.entity.GenericTaskCommonData;
import com.pinecone.hydra.conduct.entity.GenericTaskNode;
import com.pinecone.hydra.conduct.entity.GenericTaskNodeMeta;
import com.pinecone.hydra.conduct.entity.TaskNode;
import com.pinecone.hydra.conduct.source.TaskCommonDataManipulator;
import com.pinecone.hydra.conduct.source.TaskMasterManipulator;
import com.pinecone.hydra.conduct.source.TaskNodeManipulator;
import com.pinecone.hydra.conduct.source.TaskNodeMetaManipulator;
import com.pinecone.hydra.unit.imperium.ImperialTree;
import com.pinecone.hydra.unit.imperium.ImperialTreeNode;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;
import com.pinecone.hydra.unit.imperium.RegimentedImperialTree;
import com.pinecone.hydra.unit.imperium.source.TreeMasterManipulator;
import com.pinecone.ulf.util.id.GuidAllocator;
import com.pinecone.ulf.util.id.GUIDs;

import java.time.LocalDateTime;
import java.util.List;

public class GenericDistributedTaskMetaTree implements DistributedTaskMetaTree{
    protected Hydrarum                      hydrarum;
    private ImperialTree                    imperialTree;

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
        this.imperialTree = new RegimentedImperialTree(treeMasterManipulator);
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
        String path = this.imperialTree.getCachePath(guid);
        if (path!=null) return path;
        ImperialTreeNode node = this.imperialTree.getNode(guid);
        Debug.trace(node.toString());
        String assemblePath = this.getNodeName(node);
        while (!node.getParentGUIDs().isEmpty()){
            List<GUID> parentGuids = node.getParentGUIDs();
            node = this.imperialTree.getNode(parentGuids.get(0));
            String nodeName = this.getNodeName(node);
            assemblePath = nodeName + "." + assemblePath;
        }
        this.imperialTree.insertCachePath(guid, assemblePath);
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

        GUIDImperialTrieNode guidDistributedTrieNode = new GUIDImperialTrieNode();
        GUID nodeGuid = guidAllocator.nextGUID72();
        guidDistributedTrieNode.setGuid(nodeGuid);
        guidDistributedTrieNode.setNodeMetadataGUID(TaskNodeMetaGuid);
        guidDistributedTrieNode.setBaseDataGUID(TaskCommonDataGuid);
        guidDistributedTrieNode.setType(UOIUtils.createLocalJavaClass(taskNode.getClass().getName()));
        taskNode.setGuid(nodeGuid);

        this.imperialTree.insert(guidDistributedTrieNode);
        this.taskNodeManipulator.insert(taskNode);
        this.taskNodeMetaManipulator.insert(genericTaskNodeMeta);
        this.taskCommonDataManipulator.insert(genericTaskCommonData);
        return nodeGuid;
    }

    @Override
    public TreeNode get(GUID guid) {
        GUIDImperialTrieNode node = this.imperialTree.getNode(guid);
        TaskNode taskNode = this.taskNodeManipulator.getTaskNode(guid);
        GenericTaskNodeMeta taskNodeMeta = (GenericTaskNodeMeta) this.taskNodeMetaManipulator.getTaskNodeMeta(node.getNodeMetadataGUID());
        GenericTaskCommonData taskCommonData = (GenericTaskCommonData) this.taskCommonDataManipulator.getTaskCommonData(node.getAttributesGUID());
        taskNode.setGenericTaskNodeMeta(taskNodeMeta);
        taskNode.setGenericTaskCommonData(taskCommonData);
        return taskNode;
    }

    @Override
    public TreeNode parsePath(String path) {
        GUID guid = this.imperialTree.queryGUIDByPath( path );
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
        List<GUIDImperialTrieNode> childNodes = this.imperialTree.getChildren(guid);
        if (childNodes == null || childNodes.isEmpty()){
            this.removeNode(guid);
        }
        else {
            for(GUIDImperialTrieNode childNode : childNodes){
                List<GUID> parentNodes = this.imperialTree.fetchParentGuids(childNode.getGuid());
                if (parentNodes.size() > 1){
                    this.imperialTree.removeInheritance(childNode.getGuid(),guid);
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

    private String getNodeName(ImperialTreeNode node){
        return this.taskNodeManipulator.getTaskNode(node.getGuid()).getName();
    }

    private String processPath(String path) {
        // 使用正则表达式移除所有的括号及其内容
        return path.replaceAll("\\(.*?\\)", "");
    }

    private void removeNode(GUID guid){
        GUIDImperialTrieNode node = this.imperialTree.getNode(guid);
        this.imperialTree.purge(guid);
        this.imperialTree.removeCachePath( guid );
        this.taskNodeManipulator.remove(guid);
        this.taskNodeMetaManipulator.remove(node.getNodeMetadataGUID());
        this.taskCommonDataManipulator.remove(node.getAttributesGUID());
    }
}
