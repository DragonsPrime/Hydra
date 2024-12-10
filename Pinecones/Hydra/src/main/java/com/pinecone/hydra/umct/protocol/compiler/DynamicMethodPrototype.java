package com.pinecone.hydra.umct.protocol.compiler;

import java.util.List;

import com.google.protobuf.Descriptors;
import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.json.JSONEncoder;
import com.pinecone.hydra.umct.protocol.function.ArgumentRequest;
import com.pinecone.hydra.umct.protocol.function.GenericArgumentRequest;

public class DynamicMethodPrototype extends GenericMethodDigest implements MethodPrototype {
    protected Descriptors.Descriptor        mArgumentsDescriptor;

    protected Descriptors.Descriptor        mReturnDescriptor;

    public DynamicMethodPrototype( ClassDigest classDigest, String szName, String szRawName, Class<?>[] parameters, Class<?> returnType, CompilerEncoder encoder, List<IfaceParamsDigest> ifaceParamsDigests) {
        super( classDigest, szName, szRawName, parameters, returnType, ifaceParamsDigests);

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

    @Override
    public ArgumentRequest conformRequest() {
        DataStructureEntity protoEntity = this.getArgumentTemplate();
        return new GenericArgumentRequest( protoEntity.getName(), protoEntity );
    }

    @Override
    public ArgumentRequest conformRequest( Object[] args ) {
        DataStructureEntity protoEntity = this.getArgumentTemplate();
        ArgumentRequest request = new GenericArgumentRequest( protoEntity.getName(), protoEntity );
        if( args != null ) {
            for ( int i = 0; i < args.length; ++i ) {
                request.setField( i, args[ i ] );
            }
        }

        return request;
    }


    @Override
    public String toJSONString() {
        List<Descriptors.FieldDescriptor > argFields = null;
        if( this.getArgumentsDescriptor() != null ) {
            argFields = this.getArgumentsDescriptor().getFields();
        }

        List<Descriptors.FieldDescriptor > retFields = null;
        if( this.getReturnDescriptor() != null ) {
            retFields = this.getReturnDescriptor().getFields();
        }

        return JSONEncoder.stringifyMapFormat( new KeyValue[]{
                new KeyValue<>( "name"             , this.getName()                                 ),
                new KeyValue<>( "rawName"          , this.getRawName()                              ),
                new KeyValue<>( "protoArguments"   , argFields                                      ),
                new KeyValue<>( "protoReturn"      , retFields                                      ),
                new KeyValue<>( "__ClassName__"    , this.className()                               ),
        } );
    }
}
