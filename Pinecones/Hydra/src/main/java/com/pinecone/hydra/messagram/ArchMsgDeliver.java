package com.pinecone.hydra.messagram;

import com.pinecone.framework.util.Debug;

import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.express.Package;
import com.pinecone.hydra.umc.msg.Status;
import com.pinecone.hydra.umc.msg.UMCHead;
import com.pinecone.hydra.umc.msg.UMCMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public abstract class ArchMsgDeliver implements MessageDeliver {
    protected String          mszName;
    protected Hydrarum        mSystem;
    protected MessageExpress  mExpress;
    protected ArchMessagram   mMessagram;

    public ArchMsgDeliver( String szName, MessageExpress express ) {
        this.mszName     = szName;
        this.mExpress    = express;
        this.mSystem     = this.mExpress.getSystem();
        this.mMessagram  = this.mExpress.getMessagram();
    }


    @Override
    public String getName() {
        return this.mszName;
    }

    @Override
    public Hydrarum getSystem() {
        return this.mSystem;
    }

    @Override
    public MessageExpress  getExpress() {
        return this.mExpress;
    }

    public ArchMessagram getMessagram(){
        return this.mMessagram;
    }

    @Override
    public abstract String getServiceKeyword() ;


    protected MessagePackage wrap( Package that ) {
        return (MessagePackage) that;
    }

    protected abstract void prepareDispatch( Package that ) throws IOException;

    protected abstract boolean sift( Package that );

    protected boolean isMyJob( Package that, String szServiceKey ) {
        return szServiceKey != null;
    }

    protected void messageDispatch( Package that ) throws IOException {
        MessagePackage msgPackage = this.wrap( that );
        UMCMessage msg            = msgPackage.getMessage();

        if( this.sift( that ) ) {
            msgPackage.getTransmit().sendInformMsg(
                    (new JSONMaptron()).put( "What", "Illegal message." ), Status.IllegalMessage
            );
            return;
        }

        UMCHead head                    = msg.getHead();
        String szServiceKey             = (String) head.getExHeaderVal( this.getServiceKeyword() );
        if( szServiceKey == null ) {
            szServiceKey = (String) head.getExHeaderVal( "/" );
        }


//        try ( ByteArrayInputStream byteStream = new ByteArrayInputStream( (byte[]) msg.getExHead() ); ObjectInputStream objectStream = new ObjectInputStream(byteStream) ) {
//            try{
//                Debug.trace( objectStream.readObject() );
//            }
//            catch ( ClassNotFoundException e ) {
//
//            }
//        }
        Debug.trace( msg.getExHead() );
        if( msg.evinceTransferMessage() != null ) {
            InputStream is = (InputStream)msg.evinceTransferMessage().getBody();
            Debug.trace( msg.getExHead(), new String( is.readAllBytes() ) );
        }



        if( this.isMyJob( that, szServiceKey ) ) {
            msgPackage.entrust( this );

            switch ( szServiceKey ) {
                case "close": {
                    msgPackage.getMessageSource().release();
                    break;
                }
                case "SystemShutdown": {
                    this.getSystem().kill();
                    break;
                }
                case "ReceiveConfirm": {
                    return;
                }
                default: {
                    try{
                        this.doMessagelet( szServiceKey, that );
                    }
                    catch ( IllegalArgumentException e ) {
                        msgPackage.getTransmit().sendInformMsg(
                                (new JSONMaptron()).put( "What", "Messagelet not found." ), Status.MappingNotFound
                        );
                    }
                }
            }
        }
    }

    protected abstract void doMessagelet( String szMessagelet, Package that ) ;

    @Override
    public void toDispatch( Package that ) throws IOException {
        this.prepareDispatch( that );
        this.messageDispatch( that );
    }

}