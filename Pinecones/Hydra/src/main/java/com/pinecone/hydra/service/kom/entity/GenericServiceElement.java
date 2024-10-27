package com.pinecone.hydra.service.kom.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.id.Identification;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.hydra.service.kom.ServicesInstrument;
import com.pinecone.hydra.service.kom.source.ServiceNodeManipulator;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.ulf.util.id.GuidAllocator;

import java.time.LocalDateTime;
import java.util.Set;

public class GenericServiceElement extends ArchElementNode implements ServiceElement {
    protected GUIDDistributedTrieNode distributedTreeNode;

    protected GUID metaGuid;

    protected String path;

    // 类型
    protected String type;

    // 服务别名
    protected String alias;

    // 资源类型
    protected String resourceType;

    // 服务类型
    protected String serviceType;

    protected LocalDateTime createTime;
    protected LocalDateTime updateTime;

    protected ServicesInstrument servicesInstrument;
    protected ServiceNodeManipulator serviceNodeManipulator;


    public GenericServiceElement() {
        super();
        this.createTime = LocalDateTime.now();
        this.createTime = LocalDateTime.now();
    }

    public GenericServiceElement( ServicesInstrument servicesInstrument ) {
        this.servicesInstrument = servicesInstrument;
        GuidAllocator guidAllocator = this.servicesInstrument.getGuidAllocator();
        this.setGuid( guidAllocator.nextGUID72() );
    }

    public GenericServiceElement( ServicesInstrument servicesInstrument, ServiceNodeManipulator serviceNodeManipulator ) {
        this(servicesInstrument);
        this.serviceNodeManipulator = serviceNodeManipulator;
    }



    @Override
    public GUID getMetaGuid(){
        return this.metaGuid;
    }

    @Override
    public void setMetaGuid( GUID metaGuid ){
        this.metaGuid = metaGuid;
    }

    @Override
    public GUIDDistributedTrieNode getDistributedTreeNode() {
        return this.distributedTreeNode;
    }

    @Override
    public void setDistributedTreeNode(GUIDDistributedTrieNode distributedTreeNode) {
        this.distributedTreeNode = distributedTreeNode;
    }


    /**
     *  Overridden to keep keys in prior json-decode.
     */
    @Override
    public GUID getGuid() {
        return super.getGuid();
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public void setPath( String path ) {
        this.path = path;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType( String type ) {
        this.type = type;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public void setAlias( String alias ) {
        this.alias = alias;
    }

    @Override
    public String getResourceType() {
        return this.resourceType;
    }

    @Override
    public void setResourceType( String resourceType ) {
        this.resourceType = resourceType;
    }

    @Override
    public String getServiceType() {
        return this.serviceType;
    }

    @Override
    public void setServiceType( String serviceType ) {
        this.serviceType = serviceType;
    }

    @Override
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    @Override
    public void setCreateTime( LocalDateTime createTime ) {
        this.createTime = createTime;
    }

    @Override
    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    @Override
    public void setUpdateTime( LocalDateTime updateTime ) {
        this.updateTime = updateTime;
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this, Set.of( "" ) );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}