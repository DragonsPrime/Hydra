package com.pinecone.hydra.umct;

import java.io.IOException;

import com.pinecone.hydra.express.Package;
import com.pinecone.hydra.umct.decipher.PrototypeDecipher;
import com.pinecone.hydra.umct.protocol.compiler.CompilerEncoder;

public class ProtoletMsgDeliver extends ArchMsgDeliver {

    protected CompilerEncoder mCompilerEncoder;

    public ProtoletMsgDeliver( String name, MessageExpress express, CompilerEncoder encoder ) {
        this( name, express, ArchMessagram.DefaultServiceKey, encoder );
    }

    public ProtoletMsgDeliver( String name, MessageExpress express, String szServiceKey, CompilerEncoder encoder ) {
        super( name, express, new PrototypeDecipher( szServiceKey, encoder ), szServiceKey );
        this.mCompilerEncoder = encoder;
    }

    public ProtoletMsgDeliver( MessageExpress express, CompilerEncoder encoder ) {
        this( ProtoletMsgDeliver.class.getSimpleName(), express, encoder );
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
        this.getMessagram().contriveByScheme( szMessagelet, (UMCConnection) that ).dispatch();
    }

}