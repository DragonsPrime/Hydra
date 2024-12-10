package com.pinecone.hydra.umct.protocol.compiler;

import java.util.List;

import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.json.JSONEncoder;
import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.umct.mapping.MethodDigestUtils;
import com.pinecone.hydra.umct.protocol.function.MethodTemplates;

public class GenericMethodDigest implements MethodDigest {
    protected ClassDigest             mClassDigest;

    protected String                  mszName;

    protected String                  mszRawName;

    protected DataStructureEntity     mArgumentTemplate;

    protected Class<?>                mReturnType;

    protected List<IfaceParamsDigest> mIfaceParamsDigests;

    public GenericMethodDigest( ClassDigest classDigest, String szName, String szRawName, Class<?>[] parameters, Class<?> returnType, List<IfaceParamsDigest> ifaceParamsDigests) {
        this.mClassDigest        = classDigest;
        this.mszName             = szName;
        this.mszRawName          = szRawName;
        this.mReturnType         = returnType;
        this.mIfaceParamsDigests = ifaceParamsDigests;

        if( parameters == null || parameters.length == 0 ) {
            this.mArgumentTemplate   = null;
        }
        else {
            this.mArgumentTemplate   = MethodTemplates.from( null,classDigest.getClassName() + Namespace.DEFAULT_SEPARATOR + szName, parameters );
        }
    }

    public GenericMethodDigest( ClassDigest classDigest, String szName, Class<?>[] parameters, Class<?> returnType, List<IfaceParamsDigest> ifaceParamsDigests) {
        this( classDigest, szName, szName, parameters, returnType, ifaceParamsDigests);
    }

    @Override
    public void apply( List<IfaceParamsDigest> ifaceParamsDigests) {
        this.mIfaceParamsDigests = ifaceParamsDigests;
    }

    @Override
    public List<String> getArgumentsKey() {
        return MethodDigestUtils.getArgumentsKey( this.getParamsDigests(), this.getArgumentTemplate() );
    }

    @Override
    public ClassDigest getClassDigest() {
        return this.mClassDigest;
    }

    @Override
    public String getName() {
        return this.mszName;
    }

    @Override
    public String getFullName() {
        return this.mClassDigest.getClassName() + Namespace.DEFAULT_SEPARATOR + this.getName();
    }

    @Override
    public String getRawName() {
        return this.mszRawName;
    }

    @Override
    public DataStructureEntity getArgumentTemplate() {
        return this.mArgumentTemplate;
    }

    @Override
    public Class<?> getReturnType() {
        return this.mReturnType;
    }

    @Override
    public List<IfaceParamsDigest> getParamsDigests() {
        return this.mIfaceParamsDigests;
    }

    @Override
    public String toJSONString() {
        return JSONEncoder.stringifyMapFormat( new KeyValue[]{
                new KeyValue<>( "name"        , this.getName()                                 ),
                new KeyValue<>( "rawName"     , this.getRawName()                              ),
                new KeyValue<>( "arguments"   , this.getArgumentTemplate().getSegments()       ),
                new KeyValue<>( "return"      , this.getReturnType()                           ),
        } );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
