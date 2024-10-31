package com.pinecone.hydra.service.kom.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.hometype.BeanColonist;
import com.pinecone.framework.util.json.hometype.BeanJSONDecoder;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.hydra.service.ArchServiceFamilyMeta;
import com.pinecone.hydra.service.kom.ServicesInstrument;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.ulf.util.id.GuidAllocator;

public abstract class ArchElementNode extends ArchServiceFamilyMeta implements ElementNode {
    protected long                       enumId;

    protected GUIDDistributedTrieNode    distributedTreeNode;
    protected ServicesInstrument         servicesInstrument;

    public ArchElementNode() {
        super();
    }

    public ArchElementNode( Map<String, Object > joEntity ) {
        super( joEntity );
        BeanJSONDecoder.BasicDecoder.decode( this, joEntity );
    }

    public ArchElementNode( ServicesInstrument servicesInstrument ) {
        this.apply( servicesInstrument );
    }

    public void apply( ServicesInstrument servicesInstrument ) {
        this.servicesInstrument = servicesInstrument;
        GuidAllocator guidAllocator = this.servicesInstrument.getGuidAllocator();
        this.setGuid( guidAllocator.nextGUID72() );
    }

    @Override
    public ArchElementNode apply( Map<String, Object > joEntity ) {
        super.apply( joEntity );
        BeanJSONDecoder.BasicDecoder.decode( this, joEntity );

        return this;
    }

    @Override
    public long getEnumId() {
        return this.enumId;
    }

    @Override
    public void setEnumId( long enumId ) {
        this.enumId = enumId;
    }

    @Override
    public GUID getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid( GUID guid ) {
        this.guid = guid;
    }

    @Override
    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public void setScenario( String scenario ) {
        this.scenario = scenario;
    }

    @Override
    public void setPrimaryImplLang( String primaryImplLang ) {
        this.primaryImplLang = primaryImplLang;
    }

    @Override
    public void setExtraInformation( String extraInformation ) {
        this.extraInformation = extraInformation;
    }

    @Override
    public void setLevel( String level ) {
        this.level = level;
    }

    @Override
    public void setDescription( String description ) {
        this.description = description;
    }

    @Override
    public GUIDDistributedTrieNode getDistributedTreeNode() {
        return this.distributedTreeNode;
    }

    @Override
    public void setDistributedTreeNode( GUIDDistributedTrieNode distributedTreeNode ) {
        this.distributedTreeNode = distributedTreeNode;
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }


    protected List<ElementNode > fetchChildren() {
        List<GUID > guids = this.fetchChildrenGuids();
        List<ElementNode > elementNodes = new ArrayList<>();
        for( GUID guid : guids ){
            ElementNode elementNode = (ElementNode) this.servicesInstrument.get( guid );
            elementNodes.add( elementNode );
        }
        return elementNodes;
    }

    protected List<GUID > fetchChildrenGuids() {
        return this.servicesInstrument.fetchChildrenGuids( this.getGuid() );
    }

    protected void addChild( ElementNode child ) {
        GUID childId;
        boolean bContainsChild = this.containsChild( child.getName() );
        if( bContainsChild ) {
            return;
        }
        else {
            childId = this.servicesInstrument.put( child );
        }


        this.servicesInstrument.affirmOwnedNode( this.guid, childId );
    }

    protected boolean containsChild( String childName ) {
        return this.servicesInstrument.containsChild( this.guid, childName );
    }

    @Override
    public JSONObject toJSONObject() {
        return BeanColonist.DirectColonist.populate( this, ServoElement.UnbeanifiedKeys );
    }
}