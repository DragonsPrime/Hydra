package com.pinecone.hydra.umct;

import com.pinecone.framework.unit.trie.TrieMap;
import com.pinecone.framework.unit.trie.TrieSegmentor;
import com.pinecone.framework.unit.trie.UniTrieMaptron;

import com.pinecone.framework.util.StringUtils;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.express.Package;
import com.pinecone.hydra.umc.msg.Status;
import com.pinecone.hydra.umc.msg.UMCHead;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umct.decipher.HeaderDecipher;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public abstract class ArchMsgDeliver implements MessageDeliver {
    protected String                                      mszName;
    protected Hydrarum                                    mSystem;
    protected MessageExpress                              mExpress;
    protected ArchMessagram                               mMessagram;
    protected TrieMap<String, MessageController >         mRoutingTable;
    protected HeaderDecipher                              mHeaderDecipher;
    protected String                                      mszServicePathKey;

    public ArchMsgDeliver(String szName, MessageExpress express, HeaderDecipher headerDecipher, String szServicePathKey ) {
        this.mszName           = szName;
        this.mExpress          = express;
        this.mSystem           = this.mExpress.getSystem();
        this.mMessagram        = this.mExpress.getMessagram();
        this.mHeaderDecipher   = headerDecipher;
        this.mszServicePathKey = szServicePathKey;
        this.mRoutingTable     = new UniTrieMaptron<>(HashMap::new, new TrieSegmentor() {
            @Override
            public String[] segments( String szPathKey ) {
                return szPathKey.split( "\\.|\\/" );
            }

            @Override
            public String getSeparator() {
                return StringUtils.FOLDER_SEPARATOR;
            }
        });
    }

    @Override
    public String getServiceKeyword() {
        return this.mszServicePathKey;
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

    @Override
    public void registerController( String addr, MessageController controller ){
        this.mRoutingTable.put( addr, controller );
    }


    protected UMCConnection wrap( Package that ) {
        return (UMCConnection) that;
    }

    protected abstract void prepareDispatch( Package that ) throws IOException;

    protected abstract boolean sift( Package that );

    protected boolean isMyJob( Package that, String szServiceKey ) {
        return szServiceKey != null;
    }

    protected void messageDispatch( Package that ) throws IOException, ServiceException {
        boolean bDenialService = false;

        try{
            UMCConnection connection  = this.wrap( that );
            UMCMessage msg            = connection.getMessage();

            if( this.sift( that ) ) {
                connection.getTransmit().sendInformMsg(null, Status.IllegalMessage );
                return;
            }

            UMCHead head                = msg.getHead();
            Object  exHead              = head.getExtraHead();
            String szAddr               = this.mHeaderDecipher.getServicePath( exHead );
            if( szAddr == null ) {
                this.mHeaderDecipher.sendIllegalMessage( connection );
                return;
            }

            MessageController controller = this.mRoutingTable.get( szAddr );
            if( controller != null ) {
                connection.entrust( this );

                Object[] args;
                if( controller.isArgsIndexed() ) {
                    args = this.mHeaderDecipher.values( exHead, controller.getArgumentsDescriptor() ).toArray();
                }
                else {
                    List<String > keys = controller.getArgumentsKey();
                    args = this.mHeaderDecipher.evals( exHead, controller.getArgumentsDescriptor(), keys );
                }

                try{
                    Object ret = controller.invoke( args );
                    connection.getTransmit().sendMsg( this.mHeaderDecipher.assembleReturnMsg( ret, controller.getReturnDescriptor() ) );
                }
                catch ( Exception e ) {
                    this.mHeaderDecipher.sendInternalError( connection );
                }
            }
            else {
                if ( this.mMessagram != null ) {
                    this.doMessagelet( szAddr, that );
                }

                bDenialService = true;
            }
        }
        catch ( RuntimeException e ) {
            throw new ServiceInternalException( e );
        }

        if ( bDenialService ) {
            throw new DenialServiceException( "It's none of my business." );
        }
    }

    protected abstract void doMessagelet( String szMessagelet, Package that ) ;

    @Override
    public void toDispatch( Package that ) throws IOException, ServiceException {
        this.prepareDispatch( that );
        this.messageDispatch( that );
    }

}