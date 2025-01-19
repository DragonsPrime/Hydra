package com.pinecone.hydra.registry.entity;

import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.JSONEncoder;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.registry.KOMRegistry;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
import com.pinecone.framework.util.id.GuidAllocator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericNamespace extends ArchElementNode implements Namespace {
    protected NamespaceMeta                   namespaceMeta;

    protected KOMRegistry registry;
    protected Map<String, RegistryTreeNode >  children;
    protected List<GUID >                     childrenGuids;


    public GenericNamespace() {

    }

    public GenericNamespace(KOMRegistry registry ) {
        this.registry = registry;

        GuidAllocator guidAllocator = this.registry.getGuidAllocator();
        this.setGuid( guidAllocator.nextGUID() );
        this.setCreateTime( LocalDateTime.now() );
    }


    public void apply( KOMRegistry registry ) {
        this.registry = registry;
    }

    @Override
    public NamespaceMeta getNamespaceWithMeta() {
        return this.namespaceMeta;
    }

    @Override
    public void setNamespaceMeta( NamespaceMeta namespaceMeta ) {
        this.namespaceMeta = namespaceMeta;
    }

    /** Thread unsafe */
    @Override
    public Map<String, RegistryTreeNode > getChildren() {
        if( this.children == null ) {
            Map<String, RegistryTreeNode> nodeHashMap = new LinkedHashMap<>();
            for( GUID guid : this.childrenGuids ){
                RegistryTreeNode registryTreeNode = this.registry.get( guid );
                nodeHashMap.put( registryTreeNode.getName(), registryTreeNode );
            }
            this.children = nodeHashMap;
        }
        return this.children;
    }

    @Override
    public List<GUID > fetchChildrenGuids() {
        return this.childrenGuids;
    }

    @Override
    public void setChildrenGuids( List<GUID > contentGuids, int depth ) {
        this.childrenGuids = contentGuids;
    }

    @Override
    public List<RegistryTreeNode > listItem() {
        ArrayList<RegistryTreeNode > registryTreeNodes = new ArrayList<>();
        registryTreeNodes.addAll( this.getChildren().values() );
        return registryTreeNodes;
    }

    @Override
    public void put( RegistryTreeNode child ) {
        String key = child.getName();
        if ( this.getChildren().get( key ) != null ){
            throw new IllegalArgumentException( "key is exist." );
        }
        this.getChildren().put( key, child );
        this.registry.affirmOwnedNode( this.guid, child.getGuid() );
    }

    @Override
    public void remove( String key ) {
        RegistryTreeNode registryTreeNode = this.getChildren().get(key);
        this.registry.remove(registryTreeNode.getGuid());
        this.getChildren().remove(key);
    }

    @Override
    public KOMRegistry parentRegistry() {
        return this.registry;
    }

    @Override
    public boolean containsKey( String key ) {
        return this.getChildren().containsKey(key);
    }

    @Override
    public JSONObject toJSONObject() {
        Map<String, RegistryTreeNode > children = this.getChildren();
        JSONObject jo = new JSONMaptron();

        for( Map.Entry<String, RegistryTreeNode > kv : children.entrySet() ) {
            if( kv.getValue().evinceNamespace() != null ) {
                jo.put( kv.getKey(), kv.getValue().evinceNamespace().toJSONObject() );
            }
            else if( kv.getValue().evinceProperties() != null ) {
                jo.put( kv.getKey(), kv.getValue().evinceProperties().toJSONObject() );
            }
            else if( kv.getValue().evinceTextFile() != null ) {
                jo.put( kv.getKey(), kv.getValue().evinceTextFile().toJSON() );
            }
        }
        return jo;
    }

    @Override
    public ConfigNode getConfigNode(String key ) {
        return (ConfigNode) this.getChildren().get(key);
    }

    @Override
    public Namespace getNamespace( String key ) {
        return (Namespace) this.getChildren().get( key );
    }

    @Override
    public int size() {
        return this.childrenGuids.size();
    }

    @Override
    public boolean isEmpty() {
        return this.childrenGuids.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return this.getChildren().keySet();
    }

    @Override
    public Set<Map.Entry<String, RegistryTreeNode > > entrySet() {
        return this.getChildren().entrySet();
    }

    @Override
    public void copyTo( String path ) {
        this.copyTo( this.registry.affirmNamespace( path ).getGuid() );
    }

    @Override
    public void copyTo( GUID destinationGuid ) {
        List<TreeNode > destChildren = this.registry.getChildren( destinationGuid );
        Namespace thisCopy = null;
        for( TreeNode node : destChildren ) {
            if( this.getName().equals( node.getName() ) ) {
                if( node instanceof Namespace) {
                    thisCopy = (Namespace) node;
                    break;
                }
                else {
                    throw new IllegalArgumentException(
                            String.format( "Existed child-destination [%s] should be namespace.", this.getName() )
                    );
                }
            }
        }

        // Child-Destination non-exist.
        if( thisCopy == null ) {
            this.copyNamespaceMetaTo( destinationGuid );

            thisCopy = new GenericNamespace( this.registry );
            thisCopy.setName( this.getName() );
            thisCopy.setNamespaceMeta( this.getNamespaceWithMeta() );

            this.registry.put( thisCopy );
            this.registry.getMasterTrieTree().setGuidLineage( thisCopy.getGuid(), destinationGuid );
        }

        this.copyChildrenTo( thisCopy.getGuid() );
    }

    @Override
    public void copyChildrenTo( GUID destinationGuid ) {
        Collection<RegistryTreeNode > childrenNodes = this.getChildren().values();
        for ( RegistryTreeNode node : childrenNodes ) {
            node.copyTo( destinationGuid );
        }
    }

    @Override
    public void moveTo( String path ) {
        this.moveTo( this.registry.affirmNamespace( path ).getGuid() );
    }

    @Override
    public void moveTo( GUID destinationGuid ) {
        this.registry.getMasterTrieTree().moveTo( this.guid, destinationGuid );
    }

    @Override
    public void copyNamespaceMetaTo( GUID destinationGuid ) {

    }

    @Override
    public String toJSONString() {
        return JSONEncoder.stringifyMapFormat( new KeyValue[]{
                new KeyValue<>( "name"        , this.getName()            ),
                new KeyValue<>( "guid"        , this.getGuid()            ),
                new KeyValue<>( "createTime"  , this.getCreateTime()      ),
                new KeyValue<>( "updateTime"  , this.getUpdateTime()      ),
                new KeyValue<>( "childrenSize", this.childrenGuids.size() ),
        } );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
