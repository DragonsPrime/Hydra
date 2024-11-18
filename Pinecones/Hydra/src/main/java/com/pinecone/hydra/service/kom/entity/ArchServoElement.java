package com.pinecone.hydra.service.kom.entity;

import java.time.LocalDateTime;
import java.util.Map;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanMapDecoder;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.hydra.service.kom.ServicesInstrument;

public abstract class ArchServoElement extends ArchElementNode implements ServoElement {
    protected GUID                       metaGuid;
    protected String                     path;
    protected String                     type;
    protected String                     alias;
    protected String                     resourceType;
    protected LocalDateTime              createTime;
    protected LocalDateTime              updateTime;


    public ArchServoElement() {
        super();
        this.createTime = LocalDateTime.now();
        this.createTime = LocalDateTime.now();
    }

    public ArchServoElement( Map<String, Object > joEntity ) {
        super( joEntity );
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        BeanMapDecoder.BasicDecoder.decode( this, joEntity );
    }

    public ArchServoElement( Map<String, Object > joEntity, ServicesInstrument servicesInstrument ) {
        this( joEntity );
        this.apply( servicesInstrument );
    }

    public ArchServoElement( ServicesInstrument servicesInstrument ) {
        super( servicesInstrument );
    }

    @Override
    public ArchElementNode apply( Map<String, Object > joEntity ) {
        super.apply( joEntity );
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        BeanMapDecoder.BasicDecoder.decode( this, joEntity );

        return this;
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
    public GUID getMetaGuid() {
        return this.metaGuid;
    }

    @Override
    public void setMetaGuid( GUID metaGuid ) {
        this.metaGuid = metaGuid;
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
        return BeanJSONEncoder.BasicEncoder.encode( this, ServoElement.UnbeanifiedKeys );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
