package com.pinecone.hydra.umb.wolf;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.pinecone.framework.lang.field.FieldEntity;
import com.pinecone.hydra.umb.broadcast.BroadcastControlAgent;
import com.pinecone.hydra.umb.broadcast.BroadcastControlNode;
import com.pinecone.hydra.umct.husky.compiler.ClassDigest;
import com.pinecone.hydra.umct.husky.compiler.CompilerEncoder;
import com.pinecone.hydra.umct.husky.compiler.DynamicMethodPrototype;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.husky.compiler.MethodDigest;
import com.pinecone.hydra.umct.husky.compiler.MethodPrototype;
import com.pinecone.hydra.umct.husky.machinery.PMCTContextMachinery;
import com.pinecone.ulf.util.protobuf.FieldProtobufDecoder;
import com.pinecone.ulf.util.protobuf.FieldProtobufEncoder;

public abstract class ArchBroadcastControlAgent implements BroadcastControlAgent {
    protected PMCTContextMachinery mPMCTContextMachinery;

    protected BroadcastControlNode mBroadcastControlNode;

    public ArchBroadcastControlAgent( BroadcastControlNode controlNode ) {
        this.mBroadcastControlNode = controlNode;
        this.mPMCTContextMachinery = controlNode.getPMCTTransformer();
    }


    @Override
    public InterfacialCompiler getInterfacialCompiler() {
        return this.mBroadcastControlNode.getInterfacialCompiler();
    }

    @Override
    public PMCTContextMachinery getPMCTTransformer() {
        return this.mPMCTContextMachinery;
    }

    @Override
    public FieldProtobufEncoder getFieldProtobufEncoder() {
        return this.mBroadcastControlNode.getFieldProtobufEncoder();
    }

    @Override
    public FieldProtobufDecoder getFieldProtobufDecoder() {
        return this.mBroadcastControlNode.getFieldProtobufDecoder();
    }



    @Override
    public ClassDigest queryClassDigest( String name ) {
        return this.mBroadcastControlNode.queryClassDigest( name );
    }

    @Override
    public MethodDigest queryMethodDigest( String name ) {
        return this.mBroadcastControlNode.queryMethodDigest( name );
    }

    @Override
    public void addClassDigest( ClassDigest that ) {
        this.mBroadcastControlNode.addClassDigest( that );
    }

    @Override
    public void addMethodDigest( MethodDigest that ) {
        this.mBroadcastControlNode.addMethodDigest( that );
    }

    @Override
    public ClassDigest compile( Class<? > clazz, boolean bAsIface ) {
        return this.mBroadcastControlNode.compile( clazz, bAsIface );
    }






    protected CompilerEncoder getCompilerEncoder() {
        return this.getInterfacialCompiler().getCompilerEncoder();
    }

    protected DynamicMessage reinterpretMsg( MethodPrototype prototype, Object[] args ) {
        FieldProtobufEncoder encoder = this.getFieldProtobufEncoder();
        Descriptors.Descriptor descriptor = prototype.getArgumentsDescriptor();

        FieldEntity[] types = prototype.getArgumentTemplate().getSegments();
        for ( int i = 0; i < args.length; ++i ) {
            Object v = args [ i ]; // Fuck duplicated codes.
            types[ i + 1 ].setValue( v );
        }

        return encoder.encode(
                descriptor, types, this.getCompilerEncoder().getExceptedKeys(), this.getCompilerEncoder().getOptions()
        );
    }

    protected DynamicMethodPrototype queryMethodPrototype( String szMethodAddress ) {
        DynamicMethodPrototype method = (DynamicMethodPrototype) this.queryMethodDigest( szMethodAddress );
        if ( method == null ) {
            throw new IllegalArgumentException( "Method address: `" + szMethodAddress + "` is invalid." );
        }

        return method;
    }
}
