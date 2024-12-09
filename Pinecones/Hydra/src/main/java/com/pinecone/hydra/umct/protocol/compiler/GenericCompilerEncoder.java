package com.pinecone.hydra.umct.protocol.compiler;

import java.util.Set;

import com.google.protobuf.Descriptors;
import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.ulf.util.protobuf.FieldProtobufEncoder;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufEncoder;
import com.pinecone.ulf.util.protobuf.Options;

public class GenericCompilerEncoder implements CompilerEncoder {
    protected FieldProtobufEncoder mEncoder;
    protected Options              mOptions;
    protected Set<String>          mExceptedKeys;
    protected String               mszEntityExtend;

    public GenericCompilerEncoder( FieldProtobufEncoder encoder, Options options, Set<String> exceptedKeys, String szEntityExtend ) {
        this.mEncoder        = encoder;
        this.mOptions        = options;
        this.mExceptedKeys   = exceptedKeys;
        this.mszEntityExtend = szEntityExtend;
    }

    public GenericCompilerEncoder( String szEntityExtend ) {
        this( new GenericFieldProtobufEncoder(), Options.DefaultOptions, Set.of(), szEntityExtend );
    }

    @Override
    public FieldProtobufEncoder getEncoder() {
        return this.mEncoder;
    }

    @Override
    public Options getOptions() {
        return this.mOptions;
    }

    @Override
    public Set<String> getExceptedKeys() {
        return this.mExceptedKeys;
    }

    @Override
    public String getEntityExtend() {
        return this.mszEntityExtend;
    }

    @Override
    public Descriptors.Descriptor transform( DataStructureEntity entity ) {
        String szEntityName = this.getOptions().normalizeDescriptorName( entity.getName() + this.getEntityExtend() );
        return this.getEncoder().transform(
                entity.getSegments(), szEntityName, this.getExceptedKeys(), this.getOptions()
        );
    }
}
