package com.pinecone.framework.lang;

import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.json.JSON;

public class GenericFieldEntity implements FieldEntity {
    protected String mszName;

    protected Class<?> mType;

    protected Object mValue;

    public GenericFieldEntity( String szName, Object value, Class<?> type ) {
        this.mszName = szName;
        this.mType   = type;
        this.mValue  = value;
    }

    public GenericFieldEntity( String szName, Object value ) {
        this( szName, value, value.getClass() );
    }

    public GenericFieldEntity( String szName, Class<?> type ) {
        this( szName, null, type );
    }

    @Override
    public String getName() {
        return this.mszName;
    }

    @Override
    public Class<?> getType() {
        return this.mType;
    }

    @Override
    public Object getValue() {
        return this.mValue;
    }

    @Override
    public void setValue( Object value ) {
        this.mValue = value;
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public String toJSONString() {
        return "{" + StringUtils.jsonQuote( this.mszName.toString() ) + ":" + JSON.stringify( this.mValue ) + "}";
    }
}
