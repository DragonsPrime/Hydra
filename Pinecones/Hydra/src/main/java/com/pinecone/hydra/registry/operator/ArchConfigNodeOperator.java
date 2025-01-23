package com.pinecone.hydra.registry.operator;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.registry.KOMRegistry;
import com.pinecone.hydra.registry.entity.ConfigNode;
import com.pinecone.hydra.registry.entity.ConfigNodeMeta;
import com.pinecone.hydra.registry.entity.ArchConfigNode;
import com.pinecone.hydra.registry.entity.Attributes;
import com.pinecone.hydra.registry.entity.RegistryTreeNode;
import com.pinecone.hydra.registry.source.RegistryMasterManipulator;
import com.pinecone.hydra.registry.source.RegistryConfigNodeManipulator;
import com.pinecone.hydra.registry.source.RegistryNodeMetaManipulator;
import com.pinecone.hydra.unit.imperium.ImperialTreeNode;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
import com.pinecone.framework.util.id.GuidAllocator;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class ArchConfigNodeOperator extends ArchRegistryOperator {
    protected Map<GUID, ConfigNode>        cacheMap = new HashMap<>();

    protected RegistryConfigNodeManipulator registryConfigNodeManipulator;
    protected RegistryNodeMetaManipulator   configNodeMetaManipulator;

    public ArchConfigNodeOperator( RegistryOperatorFactory factory ) {
        this( factory.getMasterManipulator(), (KOMRegistry) factory.getRegistry() );
        this.factory = factory;
    }

    public ArchConfigNodeOperator( RegistryMasterManipulator masterManipulator, KOMRegistry registry ) {
        super( masterManipulator, registry );

        this.registryConfigNodeManipulator = this.registryMasterManipulator.getConfigNodeManipulator();
        this.configNodeMetaManipulator     = this.registryMasterManipulator.getNodeMetaManipulator();
    }

    @Override
    public GUID insert( TreeNode treeNode ) {
        ArchConfigNode configNode   = (ArchConfigNode) treeNode;
        ImperialTreeNode imperialTreeNode = this.affirmPreinsertionInitialize( treeNode );
        GuidAllocator guidAllocator = this.registry.getGuidAllocator();
        GUID guid72                 = configNode.getGuid();


        GUID configNodeMetaGuid = guidAllocator.nextGUID();
        ConfigNodeMeta configNodeMeta = configNode.getConfigNodeMeta();
        if ( configNodeMeta != null ){
            configNodeMeta.setGuid(configNodeMetaGuid);
            this.configNodeMetaManipulator.insert(configNodeMeta);
        }
        else {
            configNodeMetaGuid = null;
        }


        GUID commonDataGuid = guidAllocator.nextGUID();
        Attributes attributes = configNode.getAttributes();
        if (attributes != null){
            attributes.setGuid(commonDataGuid);
            this.attributesManipulator.insert(attributes);
        }
        else {
            commonDataGuid = null;
        }


        imperialTreeNode.setBaseDataGUID( commonDataGuid );
        imperialTreeNode.setNodeMetadataGUID( configNodeMetaGuid );
        this.imperialTree.insert(imperialTreeNode);
        this.registryConfigNodeManipulator.insert( configNode );
        return guid72;
    }

    @Override
    public void purge( GUID guid ) {
        //ConfigNode为叶子节点只需要删除节点信息与引用继承关系
        GUIDImperialTrieNode node = this.imperialTree.getNode(guid);
        this.imperialTree.purge( guid );
        this.registryConfigNodeManipulator.remove(guid);
        this.attributesManipulator.remove(node.getAttributesGUID());
        this.configNodeMetaManipulator.remove(node.getNodeMetadataGUID());
        this.imperialTree.removeCachePath(guid);
    }

    @Override
    public RegistryTreeNode get( GUID guid ) {
        ConfigNode rootConfig = this.cacheMap.get( guid );
        if ( rootConfig == null ) {
            rootConfig = this.getConfigNodeWideData( guid );
            ConfigNode thisConfig = rootConfig;
            while ( true ) {
                GUID affinityGuid = thisConfig.getDataAffinityGuid();
                if ( affinityGuid != null ){
                    ConfigNode parent = this.getConfigNodeWideData( affinityGuid );
                    this.inherit( thisConfig, parent );
                    thisConfig = parent;
                }
                else {
                    break;
                }
            }
            this.cacheMap.put( guid, rootConfig );
        }
        return rootConfig;
    }

    @Override
    public RegistryTreeNode getSelf( GUID guid ) {
        return this.getConfigNodeWideData( guid );
    }

    @Override
    public void update( TreeNode treeNode ) {
        ConfigNode configNode = (ConfigNode) treeNode;
        ConfigNodeMeta configNodeMeta = configNode.getConfigNodeMeta();
        Attributes attributes = configNode.getAttributes();
        configNode.setUpdateTime(LocalDateTime.now());
        if (configNodeMeta != null){
            this.configNodeMetaManipulator.update(configNodeMeta);
        }
        if (attributes != null){
            this.attributesManipulator.update(attributes);
        }
        this.registryConfigNodeManipulator.update(configNode);
    }

    @Override
    public void updateName( GUID guid, String name ) {
        this.registryConfigNodeManipulator.updateName( guid, name );
    }

    protected ConfigNode getConfigNodeWideData( GUID guid ){
        GUIDImperialTrieNode node = this.imperialTree.getNode( guid );
        ConfigNode cn = this.registryConfigNodeManipulator.getConfigNode( guid );
        if( cn instanceof ArchConfigNode ) {
            ((ArchConfigNode) cn).apply( this.registry );
        }

        ConfigNodeMeta configNodeMeta = this.configNodeMetaManipulator.getConfigNodeMeta( node.getNodeMetadataGUID() );

        //Notice: Registry attributes is difference from other tree, -- that is, same as DOM;
        //        So in this case, this field is deprecated.
        //Attributes         attributes = this.attributesManipulator.getAttributes( node.getAttributesGUID(), cn );

        Attributes         attributes = this.attributesManipulator.getAttributes( guid, cn );
        cn.setAttributes    ( attributes );
        cn.setConfigNodeMeta( configNodeMeta );
        return cn;
    }

    protected void inherit(ConfigNode self, ConfigNode prototype ){
        Class<? extends ConfigNode> clazz = self.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for ( Field field : fields ){
            field.setAccessible(true);
            try {
                Object value1 = field.get( self );
                Object value2 = field.get( prototype );
                if ( Objects.isNull(value1) || (value1 instanceof List && ((List<?>) value1).isEmpty()) ){
                    field.set(self,value2);
                }
            }
            catch ( IllegalAccessException e ) {
                throw new ProxyProvokeHandleException(e);
            }
        }
    }
}
