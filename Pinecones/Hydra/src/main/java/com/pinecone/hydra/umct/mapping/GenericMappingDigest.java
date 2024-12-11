package com.pinecone.hydra.umct.mapping;

import java.lang.reflect.Method;
import java.util.List;

import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.json.JSONEncoder;
import com.pinecone.hydra.umc.msg.UMCMethod;
import com.pinecone.hydra.umct.protocol.function.MethodTemplates;

public class GenericMappingDigest implements MappingDigest {
    protected String[]                mszAddresses;

    protected UMCMethod[]             mInterceptMethods;

    protected DataStructureEntity     mArgumentTemplate;

    protected Class<?>                mReturnType;

    protected Class<?>                mClassType;

    protected Method                  mMappedMethod;

    protected List<ParamsDigest>      mParamsDigests;

    protected GenericMappingDigest() {

    }

    public GenericMappingDigest(
            String[] szAddresses, Class<?>[] parameters, Class<?> returnType, Class<?> classType, Method method, List<ParamsDigest> paramsDigests, UMCMethod[] interceptMethods
    ) {
        this.mszAddresses        = szAddresses;
        this.mReturnType         = returnType;
        this.mParamsDigests      = paramsDigests;
        this.mClassType          = classType;
        this.mMappedMethod       = method;
        this.mInterceptMethods   = interceptMethods;

        if( parameters == null || parameters.length == 0 ) {
            this.mArgumentTemplate   = null;
        }
        else {
            String szDominatedAddress;
            if ( szAddresses.length > 0 ) {
                szDominatedAddress = szAddresses[0];
            }
            else {
                szDominatedAddress = "";
                // Using anonymous address. In fact, there is pointless for this argument template, which the address is for Iface only.
            }

            this.mArgumentTemplate   = MethodTemplates.from( null, szDominatedAddress, parameters );
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
    public String[] getAddresses() {
        return this.mszAddresses;
    }

    @Override
    public UMCMethod[] getInterceptMethods() {
        return this.mInterceptMethods;
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
                new KeyValue<>( "addresses"    , this.getAddresses()                            ),
                new KeyValue<>( "return"       , this.getReturnType().getName()                 ),
                new KeyValue<>( "mappedClass"  , this.getClassType().getName()                  ),
                new KeyValue<>( "mappedMethod" , this.getMappedMethod().getName()               ),
        } );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
