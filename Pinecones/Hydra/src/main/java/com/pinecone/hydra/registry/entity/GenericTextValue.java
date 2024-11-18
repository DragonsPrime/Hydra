package com.pinecone.hydra.registry.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;

import java.time.LocalDateTime;

public class GenericTextValue implements TextValue {
    private long enumId;
    private GUID guid;
    private String value;
    private String type;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public GenericTextValue() {
    }

    public GenericTextValue( GUID guid, String value, String type ) {
        this.setGuid( guid );
        this.setValue( value );
        this.setType( type );
        this.setCreateTime( LocalDateTime.now() );
        this.setUpdateTime( LocalDateTime.now() );
    }

    public GenericTextValue( long enumId, GUID guid, String value, String type, LocalDateTime createTime, LocalDateTime updateTime ) {
        this.enumId = enumId;
        this.guid = guid;
        this.value = value;
        this.type = type;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    /**
     * 获取
     * @return enumId
     */
    @Override
    public long getEnumId() {
        return this.enumId;
    }

    /**
     * 设置
     * @param enumId
     */
    @Override
    public void setEnumId(long enumId) {
        this.enumId = enumId;
    }

    /**
     * 获取
     * @return guid
     */
    @Override
    public GUID getGuid() {
        return this.guid;
    }

    /**
     * 设置
     * @param guid
     */
    @Override
    public void setGuid(GUID guid) {
        this.guid = guid;
    }

    /**
     * 获取
     * @return value
     */
    @Override
    public String getValue() {
        return this.value;
    }

    /**
     * 设置
     * @param value
     */
    @Override
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 获取
     * @return type
     */
    @Override
    public String getType() {
        return this.type;
    }

    /**
     * 设置
     * @param type
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取
     * @return createTime
     */
    @Override
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    /**
     * 设置
     * @param createTime
     */
    @Override
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取
     * @return updateTime
     */
    @Override
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置
     * @param updateTime
     */
    @Override
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }



    public static TextValue newUpdateTextValue( GUID guid, String text, String format ) {
        TextValue textValue = new GenericTextValue();
        textValue.setGuid( guid );
        textValue.setUpdateTime(LocalDateTime.now());
        textValue.setValue(text);
        textValue.setType(format);

        return textValue;
    }
}
