package com.pinecone.hydra.umct;

import com.pinecone.framework.unit.trie.TrieMap;
import com.pinecone.framework.unit.trie.TrieSegmentor;
import com.pinecone.framework.unit.trie.UniTrieMaptron;
import com.pinecone.framework.util.Debug;

import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.express.Package;
import com.pinecone.hydra.umc.msg.Status;
import com.pinecone.hydra.umc.msg.UMCHead;
import com.pinecone.hydra.umc.msg.UMCMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public abstract class ArchMsgDeliver implements MessageDeliver {
    protected String                                      mszName;
    protected Hydrarum                                    mSystem;
    protected MessageExpress                              mExpress;
    protected ArchMessagram                               mMessagram;
    protected TrieMap<String, MessageController >         mRoutingTable;

    public ArchMsgDeliver( String szName, MessageExpress express ) {
        this.mszName       = szName;
        this.mExpress      = express;
        this.mSystem       = this.mExpress.getSystem();
        this.mMessagram    = this.mExpress.getMessagram();
        this.mRoutingTable = new UniTrieMaptron<>(HashMap::new, new TrieSegmentor() {
            @Override
            public String[] segments( String szPathKey ) {
                return szPathKey.split( ".|\\/" );
            }

            @Override
            public String getSeparator() {
                return StringUtils.FOLDER_SEPARATOR;
            }
        });
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
    public TrieMap<String, MessageController > getRoutingTable() {
        return this.mRoutingTable;
    }

    public void registerController( String addr, MessageController controller ){
        this.mRoutingTable.put( addr, controller );
    }

    @Override
    public abstract String getServiceKeyword() ;


    protected UMCConnection wrap( Package that ) {
        return (UMCConnection) that;
    }

    protected abstract void prepareDispatch( Package that ) throws IOException;

    protected abstract boolean sift( Package that );

    protected boolean isMyJob( Package that, String szServiceKey ) {
        return szServiceKey != null;
    }

    protected void messageDispatch( Package that ) throws IOException {
        UMCConnection connection  = this.wrap( that );
        UMCMessage msg            = connection.getMessage();

        if( this.sift( that ) ) {
            connection.getTransmit().sendInformMsg(
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


        String addr = connection.getMessage().getHead().getExHeaderVal( this.getServiceKeyword() ).toString();
        MessageController controller = this.mRoutingTable.get( addr );
        if( controller != null ) {

        }
        else {
            throw new DenialServiceException( "It's none of my business." );
        }
        if( this.isMyJob( that, szServiceKey ) ) {
            connection.entrust( this );

            switch ( szServiceKey ) {
                case "close": {
                    connection.getMessageSource().release();
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
                        connection.getTransmit().sendInformMsg(
                                (new JSONMaptron()).put( "What", "Messagelet not found." ), Status.MappingNotFound
                        );
                    }
                }
            }
        }
        else {

        }
    }

    protected abstract void doMessagelet( String szMessagelet, Package that ) ;

    @Override
    public void toDispatch( Package that ) throws IOException {
        this.prepareDispatch( that );
        this.messageDispatch( that );
    }

}