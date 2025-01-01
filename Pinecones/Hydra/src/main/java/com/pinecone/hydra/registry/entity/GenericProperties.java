package com.pinecone.hydra.registry.entity;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.unit.UniScopeMap;
import com.pinecone.framework.unit.UniScopeMaptron;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.registry.KOMRegistry;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenericProperties extends ArchConfigNode implements Properties {
    protected Properties parent;

    protected UniScopeMap<String, Property > properties = new UniScopeMaptron<>();

    public GenericProperties() {
    }

    public GenericProperties( KOMRegistry registry ) {
        super( registry );
    }

    @Override
    public Properties getAffinityParent() {
        return this.parent;
    }

    @Override
    public void setAffinityParent( Properties parent ) {
        this.parent = parent;
    }

    public Properties getOwner( String szKey ) {
        Properties owned = this;
        while ( owned != null ) {
            if( owned.hasOwnProperty( szKey ) ) {
                break;
            }

            owned = owned.getAffinityParent();
        }
        return owned;
    }

    @Override
    public void put( String key, Object val ) {
        Property p = new GenericProperty( this );
        p.setKey( key );
        p.setValue( val );

        this.putProperty( p );
    }

    @Override
    public void puts( Map<String, Object > map ) {
        for( Map.Entry<String, Object > kv : map.entrySet() ) {
            this.put( kv.getKey(), kv.getValue() );
        }
    }

    @Override
    public void putProperty( Property property ) {
        String szKey     = property.getKey();
        Properties owned = this.getOwner( szKey );

        property.setCreateTime( LocalDateTime.now() );
        property.setUpdateTime( LocalDateTime.now() );
        if( owned == null ) {
            // Insert to current scope.
            property.setGuid( this.guid );
            this.properties.put( property.getKey(), property );
            this.registry.putProperty( property, this.guid );
        }
        else {
            owned.updateFromDummy( property );
        }
    }

    @Override
    public void remove( String key ) {
        Properties owner = this.getOwner( key );
        if( owner != null ) {
            this.properties.remove( key );
            this.registry.removeProperty( owner.getGuid(), key );
        }
    }

    @Override
    public void update( Property property ) {
        if( property.getGuid().equals( this.guid ) ) {
            Property p = this.get( property.getKey() );
            // If p == property, which is owned element, no needs to copy.
            if( p != null && p != property ) {
                p.from( property );
                property = p;
            }
        }

        this.registry.updateProperty( property );
    }

    @Override
    public void updateFromDummy( Property dummy ) {
        Property p = this.get( dummy.getKey() );
        // If p == property, which is owned element, no needs to copy.
        if( p != null ) {
            p.from( dummy );
        }
        this.registry.updateProperty( p );
    }

    @Override
    public void set( String key, Object val ) {
        Property p = this.get( key );
        if( p != null ) {
            p.setValue( val );
        }

        this.registry.updateProperty( p );
    }

    @Override
    public Property get( String key ) {
        return this.properties.get( key );
    }

    @Override
    public Object getValue( String key ) {
        Property property = this.get( key );
        if( property != null ) {
            return property.getValue();
        }
        return null;
    }

    @Override
    public boolean containsKey( String key ) {
        return this.properties.containsKey( key );
    }

    @Override
    public boolean containsKey( Object key ) {
        return this.properties.containsKey( key );
    }

    @Override
    public boolean hasOwnProperty( Object key ) {
        return this.properties.hasOwnProperty( key );
    }

    @Override
    public int size() {
        return this.properties.size();
    }

    @Override
    public boolean isEmpty() {
        return !this.properties.isEmpty();
    }

    @Override
    public Collection<Object > values() {
        ArrayList<Object > values = new ArrayList<>();
        for( Property p : this.properties.values() ){
            values.add(p.getValue());
        }
        return values;
    }

    @Override
    public Set<String > keySet() {
        HashSet<String > keys = new HashSet<>();
        for ( Property p : this.properties.values() ){
            keys.add( p.getKey() );
        }
        return keys;
    }

    @Override
    public Set<Property > entrySet() {
        HashSet<Property> propertyHashSet = new HashSet<>();
        for( Property p : this.properties.values() ){
            propertyHashSet.add(p);
        }
        return propertyHashSet;
    }

    @Override
    public void copyValueTo( GUID destinationGuid ) {
        if ( destinationGuid != null ){
            this.registry.copyPropertiesTo( this.guid, destinationGuid );
        }
    }


    @Override
    public Collection<Property > getProperties() {
        return this.properties.values();
    }

    @Override
    public Map<String, Object > toMap() {
        Map<String, Object > jo = new LinkedHashMap<>();
        LinkedHashMap<String, Property > overridden = new LinkedHashMap<>();
        this.properties.overrideTo( overridden );

        for( Property property : overridden.values() ) {
            jo.put( property.getKey(), property.getValue() );
        }
        return jo;
    }

    @Override
    public UniScopeMap<String, Property > getPropertiesMap() {
        return this.properties;
    }

    @Override
    public void setProperties( List<Property > properties ) {
        this.properties = new UniScopeMaptron<>();
        for( Property p : properties ) {
            this.properties.put( p.getKey(), p );
        }
    }

    @Override
    public void setProperties( UniScopeMap<String, Property > properties ) {
        this.properties = properties;
    }

    @Override
    public void setThisProperties( Map<String, Property> properties ) {
        this.properties.setThisScope( properties );
    }

    @Override
    public void setParentProperties( UniScopeMap<String, Property> parent ) {
        this.properties.setParent( parent );
    }

    @Override
    public KOMRegistry parentRegistry() {
        return this.registry;
    }

    @Override
    public void copyTo( String path ) {
        this.copyTo( this.registry.affirmProperties( path ).getGuid() );
    }

    @Override
    public void copyTo( GUID destinationGuid ) {
        Properties thisCopy = null;
        RegistryTreeNode tn = this.registry.get( destinationGuid );
        if( tn.evinceProperties() == null ) {
            List<TreeNode > destChildren = this.registry.getChildren( destinationGuid );
            for( TreeNode node : destChildren ) {
                if( this.getName().equals( node.getName() ) ) {
                    if( node instanceof Properties  ) {
                        thisCopy = (Properties) node;
                        break;
                    }
                    else {
                        throw new IllegalArgumentException(
                                String.format( "Existed child-destination [%s] should be properties.", this.getName() )
                        );
                    }
                }
            }
        }
        else {
            thisCopy = (Properties) tn;
        }

        // Child-Destination non-exist.
        if( thisCopy == null ) {
            thisCopy = new GenericProperties( this.registry );
            this.putNewCopy( thisCopy, destinationGuid );
        }

        this.copyMetaTo( thisCopy.getGuid() );
        this.copyValueTo( thisCopy.getGuid() );
    }

    @Override
    public String toJSONString() {
        try{
            PropertyJSONEncoder encoder = new PropertyJSONEncoder();
            try( StringWriter writer = new StringWriter() ){
                encoder.write( this, writer );
                return writer.toString();
            }
        }
        catch ( IOException e ) {
            throw new ProxyProvokeHandleException( e );
        }
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
