package com.pinecone.hydra.umct.appoint;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.pinecone.framework.system.GenericMasterTaskManager;
import com.pinecone.framework.system.executum.ArchProcessum;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.servgram.ArchServgramium;
import com.pinecone.hydra.servgram.Servgram;
import com.pinecone.hydra.servgram.Servgramium;
import com.pinecone.hydra.umc.msg.MessageNode;
import com.pinecone.hydra.umct.ServiceException;
import com.pinecone.hydra.umct.protocol.compiler.ClassDigest;
import com.pinecone.hydra.umct.protocol.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.protocol.compiler.MethodDigest;
import com.pinecone.ulf.util.protobuf.FieldProtobufDecoder;
import com.pinecone.ulf.util.protobuf.FieldProtobufEncoder;

public abstract class ArchAppointNode extends ArchServgramium implements AppointNode {
    protected InterfacialCompiler         mInterfacialCompiler;

    protected FieldProtobufEncoder        mFieldProtobufEncoder;

    protected FieldProtobufDecoder        mFieldProtobufDecoder;

    protected Map<String, ClassDigest >   mClassDigests;

    protected Map<String, MethodDigest >  mMethodDigests;

    protected ArchAppointNode( Servgramium sharded, InterfacialCompiler compiler, FieldProtobufDecoder decoder ) {
        super( sharded, true );
        this.mAffiliateThread       = sharded.getAffiliateThread();
        this.mInterfacialCompiler   = compiler;
        this.mClassDigests          = new LinkedHashMap<>();
        this.mMethodDigests         = new LinkedHashMap<>();

        this.mFieldProtobufEncoder  = compiler.getCompilerEncoder().getEncoder();
        this.mFieldProtobufDecoder  = decoder;
    }

    public abstract MessageNode getMessageNode();

    @Override
    public InterfacialCompiler getInterfacialCompiler() {
        return this.mInterfacialCompiler;
    }

    @Override
    public FieldProtobufEncoder getFieldProtobufEncoder() {
        return this.mFieldProtobufEncoder;
    }

    @Override
    public FieldProtobufDecoder getFieldProtobufDecoder() {
        return this.mFieldProtobufDecoder;
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
        return this.mClassDigests.get( name );
    }

    @Override
    public MethodDigest queryMethodDigest( String name ) {
        return this.mMethodDigests.get( name );
    }

    @Override
    public void addClassDigest( ClassDigest that ) {
        this.mClassDigests.put( that.getClassName(), that );
        List<MethodDigest> digests = that.getMethodDigests();
        for ( MethodDigest digest : digests ) {
            this.addMethodDigest( digest );
        }
    }

    @Override
    public void addMethodDigest( MethodDigest that ) {
        this.mMethodDigests.put( that.getFullName(), that );
    }

    @Override
    public ClassDigest compile( Class<? > clazz, boolean bAsIface ) {
        ClassDigest neo = this.mInterfacialCompiler.compile( clazz, bAsIface );
        this.addClassDigest( neo );
        return neo;
    }
}
