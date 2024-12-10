package com.pinecone.hydra.umct.mapping;

import java.lang.reflect.Method;
import java.util.List;

import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.json.JSONEncoder;
import com.pinecone.hydra.umct.protocol.function.MethodTemplates;

public class GenericMappingDigest implements MappingDigest {
    protected String                  mszAddress;

    protected DataStructureEntity     mArgumentTemplate;

    protected Class<?>                mReturnType;

    protected Class<?>                mClassType;

    protected Method                  mMappedMethod;

    protected List<ParamsDigest>      mParamsDigests;

    public GenericMappingDigest( String szAddress, Class<?>[] parameters, Class<?> returnType, Class<?> classType, Method method, List<ParamsDigest> paramsDigests ) {
        this.mszAddress          = szAddress;
        this.mReturnType         = returnType;
        this.mParamsDigests      = paramsDigests;
        this.mClassType          = classType;
        this.mMappedMethod       = method;

        if( parameters == null || parameters.length == 0 ) {
            this.mArgumentTemplate   = null;
        }
        else {
            this.mArgumentTemplate   = MethodTemplates.from( null, szAddress, parameters );
        }
    }

    @Override
    public void apply( List<ParamsDigest> ifaceParamsDigests ) {
        this.mParamsDigests = ifaceParamsDigests;
    }

    @Override
    public List<String> getArgumentsKey() {
        return MethodDigestUtils.getArgumentsKey( this.getParamsDigests(), this.getArgumentTemplate() );
    }

    @Override
    public String getAddress() {
        return this.mszAddress;
    }

    @Override
    public Method getMappedMethod() {
        return this.mMappedMethod;
    }

    @Override
    public Class<?> getClassType() {
        return this.mClassType;
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
    public List<ParamsDigest> getParamsDigests() {
        return this.mParamsDigests;
    }

    @Override
    public String toJSONString() {
        return JSONEncoder.stringifyMapFormat( new KeyValue[]{
                new KeyValue<>( "address"      , this.getAddress()                              ),
                new KeyValue<>( "return"       , this.getReturnType()                           ),
                new KeyValue<>( "mappedClass"  , this.getClassType().getName()                  ),
                new KeyValue<>( "mappedMethod" , this.getMappedMethod().getName()               ),
        } );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
