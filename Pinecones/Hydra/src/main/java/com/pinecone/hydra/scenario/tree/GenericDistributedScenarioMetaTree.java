package com.pinecone.hydra.scenario.tree;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
import com.pinecone.hydra.scenario.entity.GenericNamespaceNode;
import com.pinecone.hydra.scenario.entity.GenericNamespaceNodeMeta;
import com.pinecone.hydra.scenario.entity.GenericScenarioCommonData;
import com.pinecone.hydra.scenario.entity.NamespaceNode;
import com.pinecone.hydra.scenario.entity.NamespaceNodeMeta;
import com.pinecone.hydra.scenario.entity.ScenarioCommonData;
import com.pinecone.hydra.scenario.source.ScenarioMasterManipulator;
import com.pinecone.hydra.scenario.source.NamespaceNodeManipulator;
import com.pinecone.hydra.scenario.source.NamespaceNodeMetaManipulator;
import com.pinecone.hydra.scenario.source.ScenarioCommonDataManipulator;
import com.pinecone.hydra.system.ko.UOIUtils;
import com.pinecone.hydra.unit.imperium.ImperialTree;
import com.pinecone.hydra.unit.imperium.ImperialTreeNode;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;
import com.pinecone.hydra.unit.imperium.RegimentedImperialTree;
import com.pinecone.hydra.unit.imperium.source.TreeMasterManipulator;
import com.pinecone.ulf.util.guid.GUIDs;
import com.pinecone.framework.util.id.GuidAllocator;

import java.time.LocalDateTime;
import java.util.List;

public class GenericDistributedScenarioMetaTree implements DistributedScenarioMetaTree{
    protected Hydrarum                       hydrarum;
    private ImperialTree distributedScenarioTree;
    private ScenarioMasterManipulator       scenarioMasterManipulator;

    private NamespaceNodeMetaManipulator    namespaceNodeMetaManipulator;
    private NamespaceNodeManipulator        namespaceNodeManipulator;
    private ScenarioCommonDataManipulator   scenarioCommonDataManipulator;

    public GenericDistributedScenarioMetaTree( Hydrarum hydrarum, KOIMasterManipulator masterManipulator){
        this.hydrarum                       =   hydrarum;
        this.scenarioMasterManipulator      =   (ScenarioMasterManipulator) masterManipulator;
        KOISkeletonMasterManipulator skeletonMasterManipulator = this.scenarioMasterManipulator.getSkeletonMasterManipulator();
        TreeMasterManipulator        treeMasterManipulator     = (TreeMasterManipulator) skeletonMasterManipulator;
        this.distributedScenarioTree        =   new RegimentedImperialTree(treeMasterManipulator);
        this.namespaceNodeManipulator       =   this.scenarioMasterManipulator.getNamespaceNodeManipulator();
        this.namespaceNodeMetaManipulator   =   this.scenarioMasterManipulator.getNSNodeMetaManipulator();
        this.scenarioCommonDataManipulator  =   this.scenarioMasterManipulator.getScenarioCommonDataManipulator();
    }

    public GenericDistributedScenarioMetaTree( Hydrarum hydrarum ) {
        this.hydrarum = hydrarum;
    }

    public GenericDistributedScenarioMetaTree( KOIMappingDriver driver ) {
        this(
                driver.getSystem(),
                driver.getMasterManipulator()
        );
    }

    @Override
    public String getPath(GUID guid) {
        String path = this.distributedScenarioTree.getCachePath(guid);
        if (path!=null) return path;
        ImperialTreeNode node = this.distributedScenarioTree.getNode(guid);
        Debug.trace(node.toString());
            String assemblePath = this.getNodeName(node);
            while (!node.getParentGUIDs().isEmpty()){
                List<GUID> parentGuids = node.getParentGUIDs();
                node = this.distributedScenarioTree.getNode(parentGuids.get(0));
                String nodeName = this.getNodeName(node);
                assemblePath = nodeName + "." + assemblePath;
            }
            this.distributedScenarioTree.insertCachePath(guid,assemblePath);
            return assemblePath;
    }

    @Override
    public GUID insert( TreeNode treeNode ) {
        GenericNamespaceNode namespaceNode = (GenericNamespaceNode) treeNode;
        GuidAllocator guidAllocator = GUIDs.newGuidAllocator();

        NamespaceNodeMeta namespaceNodeMeta = namespaceNode.getNamespaceNodeMeta();
        GUID namespaceNodeMetaGuid = guidAllocator.nextGUID();
        namespaceNodeMeta.setGuid(namespaceNodeMetaGuid);

        ScenarioCommonData scenarioCommonData = namespaceNode.getScenarioCommonData();
        GUID scenarioCommonDataGuid = guidAllocator.nextGUID();
        scenarioCommonData.setGuid(scenarioCommonDataGuid);
        scenarioCommonData.setCreateTime(LocalDateTime.now());
        scenarioCommonData.setUpdateTime(LocalDateTime.now());

        GUIDImperialTrieNode guidDistributedTrieNode = new GUIDImperialTrieNode();
        GUID nodeGuid = guidAllocator.nextGUID();
        namespaceNode.setGuid(nodeGuid);
        guidDistributedTrieNode.setGuid(nodeGuid);
        guidDistributedTrieNode.setNodeMetadataGUID(namespaceNodeMetaGuid);
        guidDistributedTrieNode.setBaseDataGUID(scenarioCommonDataGuid);
        guidDistributedTrieNode.setType(UOIUtils.createLocalJavaClass(namespaceNode.getClass().getName()));

        this.distributedScenarioTree.insert(guidDistributedTrieNode);
        this.namespaceNodeMetaManipulator.insert(namespaceNodeMeta);
        this.scenarioCommonDataManipulator.insert(scenarioCommonData);
        this.namespaceNodeManipulator.insert(namespaceNode);
        return null;
    }

    @Override
    public TreeNode get(GUID guid) {
        GUIDImperialTrieNode node = this.distributedScenarioTree.getNode(guid);
        NamespaceNode namespaceNode = this.namespaceNodeManipulator.getNamespaceNode(guid);
        GenericScenarioCommonData scenarioCommonData = (GenericScenarioCommonData) this.scenarioCommonDataManipulator.getScenarioCommonData(node.getAttributesGUID());
        GenericNamespaceNodeMeta namespaceNodeMeta = (GenericNamespaceNodeMeta) this.namespaceNodeMetaManipulator.getNamespaceNodeMeta(node.getNodeMetadataGUID());
        namespaceNode.setNamespaceNodeMeta(namespaceNodeMeta);
        namespaceNode.setScenarioCommonData(scenarioCommonData);
        return namespaceNode;
    }

    @Override
    public TreeNode parsePath(String path) {
        GUID guid = this.distributedScenarioTree.queryGUIDByPath( path );
        if (guid != null){
            return this.get(guid);
        }
        else{
            String[] parts = this.processPath(path).split("\\.");
            List<GUID> nodeByName = this.namespaceNodeManipulator.getGuidsByName(parts[parts.length - 1]);
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
        List<GUIDImperialTrieNode> childNodes = this.distributedScenarioTree.getChildren(guid);
        if (childNodes == null || childNodes.isEmpty()){
            this.removeNode(guid);
        }
        else {
            for(GUIDImperialTrieNode childNode : childNodes){
                List<GUID> parentNodes = this.distributedScenarioTree.fetchParentGuids(childNode.getGuid());
                if (parentNodes.size() > 1){
                    this.distributedScenarioTree.removeInheritance(childNode.getGuid(),guid);
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
        return this.namespaceNodeManipulator.getNamespaceNode(node.getGuid()).getName();
    }

    private String processPath(String path) {
        // 使用正则表达式移除所有的括号及其内容
        return path.replaceAll("\\(.*?\\)", "");
    }

    private void removeNode(GUID guid){
        GUIDImperialTrieNode node = this.distributedScenarioTree.getNode(guid);
        this.distributedScenarioTree.purge( guid );
        this.namespaceNodeManipulator.remove(guid);
        this.namespaceNodeMetaManipulator.remove(node.getNodeMetadataGUID());
        this.scenarioCommonDataManipulator.remove(node.getAttributesGUID());
        this.distributedScenarioTree.removeCachePath(guid);
    }

}
