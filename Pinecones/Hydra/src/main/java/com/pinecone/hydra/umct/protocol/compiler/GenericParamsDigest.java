package com.pinecone.hydra.umct.protocol.compiler;

import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.json.JSONEncoder;

public class GenericParamsDigest implements ParamsDigest {
    protected MethodDigest mMethodDigest;

    protected int          mParameterIndex;

    protected String       mszName;

    protected String       mszValue;

    protected String       mszDefaultValue;

    protected boolean      mRequired;

    public GenericParamsDigest( MethodDigest methodDigest, int parameterIndex, String name, String value, String defaultValue, boolean required ) {
        this.mMethodDigest   = methodDigest;
        this.mParameterIndex = parameterIndex;
        this.mszName         = name;
        this.mszValue        = value;
        this.mRequired       = required;
        this.mszDefaultValue = defaultValue;
    }


    @Override
    public MethodDigest getMethodDigest() {
        return this.mMethodDigest;
    }

    @Override
    public int getParameterIndex() {
        return this.mParameterIndex;
    }

    @Override
    public String getName() {
        return this.mszName;
    }

    @Override
    public String getValue() {
        return this.mszValue;
    }

    @Override
    public boolean isRequired() {
        return this.mRequired;
    }

    @Override
    public String getDefaultValue() {
        return this.mszDefaultValue;
    }

    @Override
    public String toJSONString() {
        return JSONEncoder.stringifyMapFormat( new KeyValue[]{
                new KeyValue<>( "index"         , this.getParameterIndex()                       ),
                new KeyValue<>( "name"          , this.getName()                                 ),
                new KeyValue<>( "value"         , this.getValue()                                ),
                new KeyValue<>( "defaultValue"  , this.getDefaultValue()                         ),
                new KeyValue<>( "required"      , this.isRequired()                              ),
        } );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
