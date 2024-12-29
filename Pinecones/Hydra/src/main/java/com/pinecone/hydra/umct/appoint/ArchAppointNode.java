package com.pinecone.hydra.umct.appoint;

import com.pinecone.framework.system.GenericMasterTaskManager;
import com.pinecone.framework.system.executum.ArchProcessum;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.servgram.ArchServgramium;
import com.pinecone.hydra.servgram.Servgram;
import com.pinecone.hydra.servgram.Servgramium;
import com.pinecone.hydra.umc.msg.MessageNode;
import com.pinecone.hydra.umct.ServiceException;
import com.pinecone.hydra.umct.husky.compiler.ClassDigest;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.husky.compiler.MethodDigest;
import com.pinecone.hydra.umct.husky.machinery.PMCTContextMachinery;
import com.pinecone.ulf.util.protobuf.FieldProtobufDecoder;
import com.pinecone.ulf.util.protobuf.FieldProtobufEncoder;

public abstract class ArchAppointNode extends ArchServgramium implements AppointNode {
    protected PMCTContextMachinery mPMCTContextMachinery;

    protected ArchAppointNode( Servgramium sharded, PMCTContextMachinery machinery ) {
        super( sharded, true );
        this.mAffiliateThread       = sharded.getAffiliateThread();
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
}
