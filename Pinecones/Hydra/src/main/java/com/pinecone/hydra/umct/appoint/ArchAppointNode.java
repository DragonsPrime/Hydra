package com.pinecone.hydra.umct.appoint;

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.pinecone.framework.lang.field.FieldEntity;
import com.pinecone.framework.system.GenericMasterTaskManager;
import com.pinecone.framework.system.executum.ArchProcessum;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.servgram.ArchServgramium;
import com.pinecone.hydra.servgram.Servgram;
import com.pinecone.hydra.servgram.Servgramium;
import com.pinecone.hydra.umc.msg.MessageNode;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umct.IlleagalResponseException;
import com.pinecone.hydra.umct.ServiceException;
import com.pinecone.hydra.umct.husky.compiler.ClassDigest;
import com.pinecone.hydra.umct.husky.compiler.CompilerEncoder;
import com.pinecone.hydra.umct.husky.compiler.DynamicMethodPrototype;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.husky.compiler.MethodDigest;
import com.pinecone.hydra.umct.husky.compiler.MethodPrototype;
import com.pinecone.hydra.umct.husky.machinery.PMCTContextMachinery;
import com.pinecone.ulf.util.protobuf.FieldProtobufDecoder;
import com.pinecone.ulf.util.protobuf.FieldProtobufEncoder;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufDecoder;

public abstract class ArchAppointNode extends ArchServgramium implements AppointNode {
    protected PMCTContextMachinery mPMCTContextMachinery;

    protected ArchAppointNode( Servgramium sharded ) {
        super( sharded, true );
        this.mAffiliateThread       = sharded.getAffiliateThread();
    }

    protected ArchAppointNode( Servgramium sharded, PMCTContextMachinery machinery ) {
        this( sharded );
        this.mPMCTContextMachinery = machinery;
    }

    public abstract MessageNode getMessageNode();

    @Override
    public InterfacialCompiler getInterfacialCompiler() {
        return this.mPMCTContextMachinery.getInterfacialCompiler();
    }

    @Override
    public PMCTContextMachinery getPMCTTransformer() {
        return this.mPMCTContextMachinery;
    }

    @Override
    public FieldProtobufEncoder getFieldProtobufEncoder() {
        return this.mPMCTContextMachinery.getFieldProtobufEncoder();
    }

    @Override
    public FieldProtobufDecoder getFieldProtobufDecoder() {
        return this.mPMCTContextMachinery.getFieldProtobufDecoder();
    }

    @Override
    public Thread getAffiliateThread() {
        return this.getMessageNode().getAffiliateThread();
    }

    @Override
    public ArchProcessum setThreadAffinity( Thread affinity ) {
        this.getMessageNode().setThreadAffinity( affinity );
        return super.setThreadAffinity(affinity);
    }

    @Override
    public boolean isTerminated() {
        return this.getMessageNode().isTerminated();
    }

    @Override
    public void interrupt() {
        this.getMessageNode().interrupt();
    }

    @Override
    public void kill() {
        this.getMessageNode().kill();
    }

    @Override
    public Processum parentExecutum() {
        return (Processum) this.getMessageNode().parentExecutum();
    }

    @Override
    public void apoptosis() {
        this.getMessageNode().apoptosis();
    }

    @Override
    public GenericMasterTaskManager getTaskManager() {
        return (GenericMasterTaskManager) this.getMessageNode().getTaskManager();
    }

    @Override
    public void execute() throws ServiceException {
        try{
            ( (Servgram) this.getMessageNode() ).execute();
        }
        catch ( Exception e ) {
            throw new ServiceException( e );
        }
    }



    @Override
    public ClassDigest queryClassDigest( String name ) {
        return this.mPMCTContextMachinery.queryClassDigest( name );
    }

    @Override
    public MethodDigest queryMethodDigest( String name ) {
        return this.mPMCTContextMachinery.queryMethodDigest( name );
    }

    @Override
    public void addClassDigest( ClassDigest that ) {
        this.mPMCTContextMachinery.addClassDigest( that );
    }

    @Override
    public void addMethodDigest( MethodDigest that ) {
        this.mPMCTContextMachinery.addMethodDigest( that );
    }

    @Override
    public ClassDigest compile( Class<? > clazz, boolean bAsIface ) {
        return this.mPMCTContextMachinery.compile( clazz, bAsIface );
    }




    protected CompilerEncoder getCompilerEncoder() {
        return this.getInterfacialCompiler().getCompilerEncoder();
    }

    protected DynamicMessage reinterpretMsg( MethodPrototype prototype, Object[] args ) {
        FieldProtobufEncoder encoder = this.getFieldProtobufEncoder();
        Descriptors.Descriptor descriptor = prototype.getArgumentsDescriptor();

        FieldEntity[] types = prototype.getArgumentTemplate().getSegments();
        for ( int i = 0; i < args.length; ++i ) {
            types[ i + 1 ].setValue( args [ i ] );
        }

        return encoder.encode(
                descriptor, types, this.getCompilerEncoder().getExceptedKeys(), this.getCompilerEncoder().getOptions()
        );
    }

    public Object unmarshalResponse( MethodPrototype digest, byte[] raw ) throws IlleagalResponseException {
        try{
            Descriptors.Descriptor retDes = digest.getReturnDescriptor();
            if ( retDes == null ) {
                // undefined response for `void` type-return.
                if ( digest.getReturnType() == void.class || digest.getReturnType() == Void.class ) {
                    return null;
                }

                throw new IlleagalResponseException( "Illegal undefined return type, what => " + digest.getReturnType() );
            }
            DynamicMessage rm = DynamicMessage.parseFrom( retDes, raw );
            GenericFieldProtobufDecoder decoder = new GenericFieldProtobufDecoder();
            return decoder.decode(
                    digest.getReturnType(), retDes, rm, this.getCompilerEncoder().getExceptedKeys(), this.getCompilerEncoder().getOptions()
            );
        }
        catch ( InvalidProtocolBufferException e ) {
            throw new IlleagalResponseException( e );
        }
    }

    public Object unmarshalResponse( MethodPrototype digest, UMCMessage msg ) throws IlleagalResponseException {
        return this.unmarshalResponse( digest, (byte[]) msg.getHead().getExtraHead() );
    }

    protected DynamicMethodPrototype queryMethodPrototype( String szMethodAddress ) {
        DynamicMethodPrototype method = (DynamicMethodPrototype) this.queryMethodDigest( szMethodAddress );
        if ( method == null ) {
            throw new IllegalArgumentException( "Method address `" + szMethodAddress + "` is invalid." );
        }

        return method;
    }
}
