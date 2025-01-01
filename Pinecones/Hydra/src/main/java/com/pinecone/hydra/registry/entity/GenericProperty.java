package com.pinecone.hydra.registry.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;

import java.time.LocalDateTime;

public class GenericProperty implements Property {
    private    long          enumId;
    private    GUID          guid;
    private    String        key;
    private    String        type;
    private    LocalDateTime createTime;
    private    LocalDateTime updateTime;
    private    Object        rawValue;  //TODO
    private    Object        value;

    protected  Properties    properties;

    public GenericProperty() {

    }

    public GenericProperty( Properties properties ) {
        this.properties = properties;
    }

    public GenericProperty(
            Properties properties,
            long enumId, GUID guid, String key, String type, LocalDateTime createTime,
            LocalDateTime updateTime, String value
    ) {
        this( properties );

        this.enumId = enumId;
        this.guid = guid;
        this.key = key;
        this.type = type;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.rawValue = value;
    }

    @Override
    public long getEnumId() {
        return this.enumId;
    }

    @Override
    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }

    @Override
    public GUID getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(GUID guid) {
        this.guid = guid;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    @Override
    public void setCreateTime(LocalDateTime createTime) {
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
    public Object getRawValue() {
        return this.rawValue;
    }

    protected String queryType( Object val ) {
        return this.parentProperties().parentRegistry().getPropertyTypeConverter().queryType( val );
    }

    protected Object converterValue( String val, String type ) {
        return this.parentProperties().parentRegistry().getPropertyTypeConverter().converter( val, type );
    }

    @Override
    public void setRawValue( Object rawValue ) {
        this.rawValue = rawValue;
        this.value    = this.converterValue( this.rawValue.toString(), this.type );
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public void setValue( Object value ) {
        this.rawValue = value.toString();
        this.type     = this.queryType( value );
        this.value    = this.converterValue( this.rawValue.toString(), this.type );
    }

    @Override
    public boolean isStringBasedType() {
        return PropertyTypes.isStringBasedType( this.type );
    }

    @Override
    public void fromValue( Property that ) {
        this.key          = that.getKey();
        this.type         = that.getType();
        this.rawValue        = that.getValue();
    }

    @Override
    public void from( Property that ) {
        this.fromValue( that );
        this.createTime   = that.getCreateTime();
        this.updateTime   = that.getUpdateTime();
    }

    @Override
    public void copy( Property that ) {
        this.setEnumId( that.getEnumId() );
        this.setGuid( that.getGuid() );

        this.from( that );
    }

    @Override
    public Properties parentProperties() {
        return this.properties;
    }

    @Override
    public void setParentProperties( Properties parentProperties ) {
        this.properties = parentProperties;
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
