package com.pinecone.hydra.umct;

import com.pinecone.framework.unit.trie.TrieMap;
import com.pinecone.framework.unit.trie.TrieSegmentor;
import com.pinecone.framework.unit.trie.UniTrieMaptron;

import com.pinecone.framework.util.StringUtils;
import com.pinecone.hydra.express.Package;
import com.pinecone.hydra.umc.msg.Status;
import com.pinecone.hydra.umc.msg.UMCHead;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.wolfmc.UlfInformMessage;
import com.pinecone.hydra.umct.decipher.HeaderDecipher;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ArchMsgDeliver implements MessageDeliver {
    protected String                                      mszName;
    protected MessageExpress                              mExpress;
    protected MessageJunction                             mJunction;
    protected TrieMap<String, MessageHandler>             mRoutingTable;
    protected HeaderDecipher                              mHeaderDecipher;
    protected String                                      mszServicePathKey;

    public ArchMsgDeliver( String szName, MessageExpress express, HeaderDecipher headerDecipher, String szServicePathKey ) {
        this.mszName           = szName;
        this.mExpress          = express;
        this.mJunction         = this.mExpress.getJunction();
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
    public MessageExpress  getExpress() {
        return this.mExpress;
    }

    public MessageJunction getJunction(){
        return this.mJunction;
    }

    @Override
    public Map<String, MessageHandler> getRoutingTable() {
        return this.mRoutingTable;
    }

    @Override
    public void registerHandler( String addr, MessageHandler controller ){
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

    protected UMCMessage processResponse( UMCMessage request, UMCMessage response ) {
        MessageExpress me = this.getExpress();
        try{
            UMCTExpress ue = (UMCTExpress) me;
            return ue.processResponse( request, response );
        }
        catch ( ClassCastException e ) {
            return response;
        }
    }

    protected void messageDispatch( Package that ) throws IOException, ServiceException {
        boolean bDenialService = false;

        try{
            UMCConnection connection  = this.wrap( that );
            UMCMessage request        = connection.getMessage();

            if ( request.getHead().getStatus() != Status.OK ) {
                throw new ServiceInternalException( "Error response." );
            }

            if( this.sift( that ) ) {
                connection.getTransmit().sendInformMsg( null, Status.IllegalMessage );
                return;
            }

            UMCHead head                = request.getHead();
            Object  exHead              = head.getExtraHead();
            String szAddr               = this.mHeaderDecipher.getServicePath( exHead );
            if( szAddr == null ) {
                this.mHeaderDecipher.sendIllegalMessage( connection );
                return;
            }

            MessageHandler controller = this.mRoutingTable.get( szAddr );
            if( controller != null ) {
                connection.entrust( this );

                Object[] args;
                if( controller.isArgsIndexed() ) {
                    args = this.mHeaderDecipher.values( exHead, controller.getArgumentsDescriptor(), controller.getArgumentTemplate() ).toArray();
                }
                else {
                    List<String > keys = controller.getArgumentsKey();
                    args = this.mHeaderDecipher.evals( exHead, controller.getArgumentsDescriptor(), keys, controller.getArgumentTemplate() );
                }

                try{
                    Object ret = controller.invoke( args );
                    UMCMessage response = this.mHeaderDecipher.assembleReturnMsg( ret, controller.getReturnDescriptor() );
                    connection.getTransmit().sendMsg( this.processResponse( request, response ) );
                }
                catch ( Exception e ) {
                    this.mHeaderDecipher.sendInternalError( connection );
                }
            }
            else {
                if ( this.mJunction != null ) {
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