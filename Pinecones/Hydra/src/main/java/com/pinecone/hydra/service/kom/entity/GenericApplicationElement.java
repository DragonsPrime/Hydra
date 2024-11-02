package com.pinecone.hydra.service.kom.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.hometype.BeanColonist;
import com.pinecone.framework.util.json.hometype.BeanJSONDecoder;
import com.pinecone.hydra.service.kom.ServicesInstrument;

import java.util.List;
import java.util.Map;

public class GenericApplicationElement extends ArchServoElement implements ApplicationElement {
    protected String                     deploymentMethod;

    public GenericApplicationElement() {
        super();
    }

    public GenericApplicationElement( Map<String, Object > joEntity ) {
        super( joEntity );
        BeanJSONDecoder.BasicDecoder.decode( this, joEntity );
    }

    public GenericApplicationElement( Map<String, Object > joEntity, ServicesInstrument servicesInstrument ) {
        super( joEntity, servicesInstrument );
        BeanJSONDecoder.BasicDecoder.decode( this, joEntity );
    }

    public GenericApplicationElement( ServicesInstrument servicesInstrument ) {
        super( servicesInstrument );
    }

    @Override
    public String getDeploymentMethod() {
        return this.deploymentMethod;
    }

    @Override
    public void setDeploymentMethod( String deploymentMethod ) {
        this.deploymentMethod = deploymentMethod;
    }

    @Override
    public List<ElementNode > fetchChildren() {
        return super.fetchChildren();
    }

    @Override
    public List<GUID > fetchChildrenGuids() {
        return super.fetchChildrenGuids();
    }

    @Override
    public void addChild( ElementNode child ) {
        if( child instanceof FolderElement ) {
            throw new IllegalArgumentException( "Foisting `FolderElement` into application node is not accepted." );
        }
        super.addChild( child );
    }

    @Override
    public boolean containsChild( String childName ) {
        return super.containsChild( childName );
    }

    @Override
    public JSONObject toJSONObject() {
        List<ElementNode > children = this.fetchChildren();
        JSONObject jo         = BeanColonist.DirectColonist.populate( this, ServoElement.UnbeanifiedKeys );
        JSONObject joChildren = new JSONMaptron();

        for( ElementNode node : children ) {
            joChildren.put( node.getName(), node.toJSONObject() );
        }
        jo.put( "services", joChildren );
        return jo;
    }
}