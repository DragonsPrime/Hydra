package com.pinecone.hydra.umct.husky.compiler;
import com.pinecone.hydra.umct.mapping.GenericParamsDigest;

public class GenericIfaceParamsDigest extends GenericParamsDigest implements IfaceParamsDigest {
    protected MethodDigest mMethodDigest;

    public GenericIfaceParamsDigest( MethodDigest methodDigest, int parameterIndex, String name, String value, String defaultValue, boolean required ) {
        super( parameterIndex, name, value, defaultValue, required );
        this.mMethodDigest   = methodDigest;
    }


    @Override
    public MethodDigest getMethodDigest() {
        return this.mMethodDigest;
    }

    @Override
    public String toJSONString() {
        return super.toJSONString();
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
