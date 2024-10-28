package com.pinecone.hydra.registry.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.registry.KOMRegistry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Namespace extends ElementNode {
    long getEnumId();

    void setEnumId( long enumId );

    GUID getGuid();

    void setGuid( GUID guid );

    String getName();

    void setName( String name );

    LocalDateTime getCreateTime();

    @Override
    default Namespace evinceNamespace() {
        return this;
    }

    void setCreateTime( LocalDateTime createTime );

    LocalDateTime getUpdateTime();

    void setUpdateTime( LocalDateTime updateTime );

    NamespaceMeta getNamespaceWithMeta();

    void setNamespaceMeta(NamespaceMeta namespaceMeta);

    Attributes getAttributes();

    void setAttributes(Attributes attributes);

    Map<String, RegistryTreeNode > getChildren();

    List<GUID > fetchChildrenGuids();

    void setChildrenGuids( List<GUID> contentGuids, int depth );

    List<RegistryTreeNode > listItem();


    void put ( String key, RegistryTreeNode val );

    void remove ( String key );

    KOMRegistry parentRegistry();

    boolean containsKey  ( String key );



    JSONObject toJSONObject();

    ConfigNode getConfigNode(String key );

    Namespace getNamespace( String key );




    int size();

    boolean isEmpty();

    Set<String > keySet();

    Set<Map.Entry<String,RegistryTreeNode>> entrySet();

    void copyTo( String path ) ;

    /**
     * Copy itself and its owned elements into destination.
     * 复制自己和自己的孩子元素到目的地址.
     * @param destinationGuid Guid of destination.
     */
    void copyTo( GUID destinationGuid );

    /**
     * Only copy its owned elements into destination.
     * 仅复制自己的孩子元素到目的地址.
     * @param destinationGuid Guid of destination.
     */
    void copyChildrenTo( GUID destinationGuid );

    void copyNamespaceMetaTo( GUID destinationGuid );
}
