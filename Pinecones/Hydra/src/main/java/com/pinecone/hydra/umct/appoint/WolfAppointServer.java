package com.pinecone.hydra.umct.appoint;

import com.pinecone.hydra.express.Deliver;
import com.pinecone.hydra.servgram.Servgramium;
import com.pinecone.hydra.umc.wolfmc.server.UlfServer;
import com.pinecone.hydra.umct.MessageDeliver;
import com.pinecone.hydra.umct.MessageExpress;
import com.pinecone.hydra.umct.ProtoletMsgDeliver;
import com.pinecone.hydra.umct.UMCTExpress;
import com.pinecone.hydra.umct.WolfMCExpress;
import com.pinecone.hydra.umct.protocol.compiler.BytecodeIfacCompiler;
import com.pinecone.hydra.umct.protocol.compiler.CompilerEncoder;
import com.pinecone.hydra.umct.protocol.compiler.InterfacialCompiler;
import com.pinecone.ulf.util.protobuf.GenericFieldProtobufDecoder;

import javassist.ClassPool;

public class WolfAppointServer extends ArchAppointNode implements AppointServer {
    protected UlfServer      mRecipient;
    protected UMCTExpress    mUMCTExpress;
    protected MessageDeliver mDefaultDeliver;

    public WolfAppointServer( UlfServer messenger, InterfacialCompiler compiler, UMCTExpress express ){
        super( (Servgramium) messenger ,compiler, new GenericFieldProtobufDecoder() );
        this.mRecipient   = messenger;
        this.mUMCTExpress = express;
        this.mRecipient.apply( express );

        this.mDefaultDeliver = new ProtoletMsgDeliver( AppointServer.DefaultEntityName, this.mUMCTExpress, compiler.getCompilerEncoder() );
        this.mUMCTExpress.register( this.mDefaultDeliver  );
    }

    public WolfAppointServer( UlfServer messenger, CompilerEncoder encoder, UMCTExpress express ){
        this( messenger, new BytecodeIfacCompiler(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader(), encoder
        ), express );
    }

    public WolfAppointServer( UlfServer messenger, UMCTExpress express ){
        this( messenger, new BytecodeIfacCompiler(
                ClassPool.getDefault(), messenger.getTaskManager().getClassLoader()
        ), express );
    }

    public WolfAppointServer( UlfServer messenger ){
        this(
                messenger,
                new BytecodeIfacCompiler( ClassPool.getDefault(), messenger.getTaskManager().getClassLoader() ),
                new WolfMCExpress( AppointServer.DefaultEntityName, messenger.getSystem() )
        );
    }

    @Override
    public UlfServer getMessageNode() {
        return this.mRecipient;
    }

    @Override
    public WolfAppointServer apply( UMCTExpress handler ) {
        this.mUMCTExpress = handler;
        this.mRecipient.apply( handler );
        return this;
    }

    @Override
    public UMCTExpress getUMCTExpress() {
        return this.mUMCTExpress;
    }

    @Override
    public MessageExpress register( Deliver deliver ) {
        return this.mUMCTExpress.register( deliver );
    }

    @Override
    public MessageExpress  fired   ( Deliver deliver ) {
        return this.mUMCTExpress.fired( deliver );
    }

    @Override
    public MessageDeliver getDeliver( String name ) {
        return this.mUMCTExpress.getDeliver( name );
    }

    @Override
    public MessageDeliver getDefaultDeliver() {
        return this.mDefaultDeliver;
    }
}
