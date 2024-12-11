package com.pinecone.hydra.umct.husky.compiler;

import java.lang.reflect.Method;
import java.util.List;

import com.google.protobuf.Descriptors;
import com.pinecone.hydra.umc.msg.UMCMethod;
import com.pinecone.hydra.umct.mapping.GenericMappingDigest;
import com.pinecone.hydra.umct.mapping.MappingDigest;
import com.pinecone.hydra.umct.mapping.ParamsDigest;

public class GenericIfaceMappingDigest extends GenericMappingDigest implements IfaceMappingDigest {
    protected Descriptors.Descriptor        mArgumentsDescriptor;

    protected Descriptors.Descriptor        mReturnDescriptor;

    public GenericIfaceMappingDigest(
            String[] szAddresses, Class<?>[] parameters, Class<?> returnType, Class<?> classType, Method method, List<ParamsDigest> paramsDigests, UMCMethod[] interceptMethods, CompilerEncoder encoder
    ) {
        super( szAddresses, parameters, returnType, classType, method, paramsDigests, interceptMethods );

        this.encode( encoder );
    }

    public GenericIfaceMappingDigest( MappingDigest mappingDigest, CompilerEncoder encoder ) {
        this.mszAddresses       = mappingDigest.getAddresses();
        this.mInterceptMethods  = mappingDigest.getInterceptMethods();
        this.mArgumentTemplate  = mappingDigest.getArgumentTemplate();
        this.mReturnType        = mappingDigest.getReturnType();
        this.mClassType         = mappingDigest.getClassType();
        this.mMappedMethod      = mappingDigest.getMappedMethod();
        this.mParamsDigests     = mappingDigest.getParamsDigests();

        this.encode( encoder );
    }

    protected void encode( CompilerEncoder encoder ) {
        if( this.mArgumentTemplate != null ) {
            this.mArgumentsDescriptor = encoder.transform( this.mArgumentTemplate );
        }

        if( this.mReturnType != null && !this.mReturnType.equals( void.class ) ) {
            this.mReturnDescriptor    = encoder.getEncoder().transform( this.mReturnType, null, encoder.getExceptedKeys() );
        }
    }

    @Override
    public Descriptors.Descriptor getArgumentsDescriptor() {
        return this.mArgumentsDescriptor;
    }

    @Override
    public Descriptors.Descriptor getReturnDescriptor() {
        return this.mReturnDescriptor;
    }
}
