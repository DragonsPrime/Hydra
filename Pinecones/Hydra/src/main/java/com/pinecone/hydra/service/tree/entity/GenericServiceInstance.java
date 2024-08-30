package com.pinecone.hydra.service.tree.entity;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.tree.GenericNodeCommonData;
import com.pinecone.hydra.service.tree.meta.GenericServiceNodeMeta;
import com.pinecone.hydra.service.tree.source.CommonDataManipulator;
import com.pinecone.hydra.service.tree.source.DefaultMetaNodeManipulator;
import com.pinecone.hydra.service.tree.source.ServiceFamilyTreeManipulator;
import com.pinecone.hydra.service.tree.source.ServiceMetaManipulator;
import com.pinecone.hydra.unit.udsn.GUIDDistributedScopeNode;
import com.pinecone.hydra.unit.udsn.source.ScopeTreeManipulator;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GenericServiceInstance implements MetaNodeInstance {
    private DefaultMetaNodeManipulator          defaultMetaNodeManipulator;
    private Map<GUID, MetaNodeWideEntity>       cacheMap = new HashMap<>();
    private ServiceFamilyTreeManipulator        serviceFamilyTreeManipulator;
    private ServiceMetaManipulator              serviceMetaManipulator;
    private CommonDataManipulator               commonDataManipulator;
    private ScopeTreeManipulator                scopeTreeManipulator;

    public GenericServiceInstance(DefaultMetaNodeManipulator defaultMetaNodeManipulator){
        this.defaultMetaNodeManipulator       =   defaultMetaNodeManipulator;
        this.serviceFamilyTreeManipulator     =   defaultMetaNodeManipulator.getServiceFamilyTreeManipulator();
        this.serviceMetaManipulator           =   defaultMetaNodeManipulator.getServiceMetaManipulator();
        this.commonDataManipulator            =   defaultMetaNodeManipulator.getCommonDataManipulator();
        this.scopeTreeManipulator             =   defaultMetaNodeManipulator.getScopeTreeManipulator();
    }
    @Override
    public MetaNodeWideEntity get(GUID guid) {
        MetaNodeWideEntity metaNodeWideEntity = this.cacheMap.get(guid);

        // 如果缓存中没有，则从数据库或其他来源获取
        if (metaNodeWideEntity == null) {
            metaNodeWideEntity = this.getWideData(guid);
            // 将获取到的数据放入缓存
            this.put(guid, metaNodeWideEntity);
        }

        while (metaNodeWideEntity.getParentGUID() != null) {
            GUID parentGUID = metaNodeWideEntity.getParentGUID();
            MetaNodeWideEntity parentData = this.cacheMap.get(parentGUID);

            // 如果父级数据不在缓存中，则获取它
            if (parentData == null) {
                parentData = this.getWideData(parentGUID);
                this.put(parentGUID, parentData);
            }

            this.inherit(metaNodeWideEntity, parentData);
        }

        return metaNodeWideEntity;
    }

    @Override
    public void put(GUID guid, MetaNodeWideEntity metaNodeWideEntity) {
        this.cacheMap.put(guid, metaNodeWideEntity);
    }

    @Override
    public void remove(GUID guid) {
        List<GUIDDistributedScopeNode> childNodes = this.scopeTreeManipulator.getChildNode(guid);
        if (childNodes.isEmpty()){
            clear(guid);
        }else {
            clear(guid);
            for(GUIDDistributedScopeNode node : childNodes){
                List<GUID> parentNode = this.scopeTreeManipulator.getParentNode(node.getGuid());
                if (parentNode.isEmpty()){
                    remove(node.getGuid());
                }else {
                    this.scopeTreeManipulator.removeInheritance(node.getGuid(),guid);
                }
            }
        }
    }

    private void clear(GUID guid) {
        GUIDDistributedScopeNode node = this.scopeTreeManipulator.getNode(guid);
        this.serviceMetaManipulator.remove(node.getBaseDataGUID());
        this.commonDataManipulator.remove(node.getNodeMetadataGUID());
        this.scopeTreeManipulator.removePath(guid);
        this.scopeTreeManipulator.removeNode(guid);
        this.serviceFamilyTreeManipulator.removeByParentGUID(guid);
        this.serviceFamilyTreeManipulator.removeByChildGUID(guid);
        this.cacheMap.remove(guid);
    }

    private void inherit(MetaNodeWideEntity chileMetaNodeWideEntity, MetaNodeWideEntity patentMetaNodeWideEntity){
        Class<? extends MetaNodeWideEntity> clazz = chileMetaNodeWideEntity.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field:fields){
            field.setAccessible(true);
            try {
                Object value1 = field.get(chileMetaNodeWideEntity);
                Object value2 = field.get(patentMetaNodeWideEntity);
                if (Objects.isNull(value1)){
                    field.set(chileMetaNodeWideEntity,value2);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private MetaNodeWideEntity getWideData(GUID guid){
        GenericApplicationWideEntityMeta genericApplicationWideEntity = new GenericApplicationWideEntityMeta();
        GUIDDistributedScopeNode node = this.scopeTreeManipulator.getNode(guid);
        GUID parentGUID = this.serviceFamilyTreeManipulator.getParentByChildGUID(guid);
        GenericNodeCommonData commonData = this.commonDataManipulator.getNodeMetadata(node.getNodeMetadataGUID());
        GenericServiceNodeMeta serviceMeta = this.serviceMetaManipulator.getServiceMeta(node.getBaseDataGUID());
        Debug.trace(commonData);
        genericApplicationWideEntity.setParentGUID(parentGUID);
        genericApplicationWideEntity.setGuid(guid);
        genericApplicationWideEntity.setScenario(commonData.getScenario());
        genericApplicationWideEntity.setPrimaryImplLang(commonData.getPrimaryImplLang());
        genericApplicationWideEntity.setExtraInformation(commonData.getExtraInformation());
        genericApplicationWideEntity.setLevel(commonData.getLevel());
        genericApplicationWideEntity.setDescription(commonData.getDescription());
        genericApplicationWideEntity.setName(serviceMeta.getName());
        genericApplicationWideEntity.setPath(serviceMeta.getPath());
        genericApplicationWideEntity.setType(serviceMeta.getType());
        genericApplicationWideEntity.setResourceType(serviceMeta.getResourceType());
        genericApplicationWideEntity.setCreateTime(serviceMeta.getCreateTime());
        genericApplicationWideEntity.setUpdateTime(serviceMeta.getUpdateTime());
        return genericApplicationWideEntity;
    }
}
