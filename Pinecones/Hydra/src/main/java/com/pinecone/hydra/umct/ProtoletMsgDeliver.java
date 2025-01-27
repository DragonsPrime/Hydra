package com.pinecone.hydra.umct;

import java.io.IOException;

import com.pinecone.hydra.express.Package;
import com.pinecone.hydra.umct.decipher.PrototypeDecipher;
import com.pinecone.hydra.umct.husky.compiler.CompilerEncoder;
import com.pinecone.hydra.umct.husky.machinery.PMCTContextMachinery;

public class ProtoletMsgDeliver extends ArchMsgDeliver {

    protected CompilerEncoder      mCompilerEncoder;

    protected PMCTContextMachinery mPMCTContextMachinery;

    public ProtoletMsgDeliver( String name, MessageExpress express, PMCTContextMachinery machinery, CompilerEncoder encoder ) {
        this( name, express, ArchMessagram.DefaultServiceKey, machinery, encoder );
    }

    public ProtoletMsgDeliver( String name, MessageExpress express, String szServiceKey, PMCTContextMachinery machinery, CompilerEncoder encoder ) {
        super( name, express, new PrototypeDecipher( szServiceKey, encoder, machinery.getFieldProtobufDecoder() ), szServiceKey );
        this.mCompilerEncoder = encoder;
        this.mPMCTContextMachinery = machinery;
    }

    public ProtoletMsgDeliver( MessageExpress express, PMCTContextMachinery machinery, CompilerEncoder encoder ) {
        this( ProtoletMsgDeliver.class.getSimpleName(), express, machinery, encoder );
    }

    @Override
    protected void prepareDispatch( Package that ) throws IOException {

    }

    @Override
    protected boolean sift( Package that ) {
        return false;
    }

    @Override
    protected void doMessagelet( String szMessagelet, Package that ) {
        if ( this.getJunction() instanceof ArchMessagram ) {
            ( (ArchMessagram)this.getJunction() ).contriveByScheme( szMessagelet, (UMCConnection) that ).dispatch();
        }
    }

}