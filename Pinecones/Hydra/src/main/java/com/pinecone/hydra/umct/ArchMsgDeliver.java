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
import com.pinecone.hydra.umct.util.HeaderEvaluator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public abstract class ArchMsgDeliver implements MessageDeliver {
    protected String                                      mszName;
    protected Hydrarum                                    mSystem;
    protected MessageExpress                              mExpress;
    protected ArchMessagram                               mMessagram;
    protected TrieMap<String, MessageController >         mRoutingTable;
    protected HeaderEvaluator                             mHeaderEvaluator;

    public ArchMsgDeliver( String szName, MessageExpress express, HeaderEvaluator headerEvaluator ) {
        this.mszName          = szName;
        this.mExpress         = express;
        this.mSystem          = this.mExpress.getSystem();
        this.mMessagram       = this.mExpress.getMessagram();
        this.mHeaderEvaluator = headerEvaluator;
        this.mRoutingTable    = new UniTrieMaptron<>(HashMap::new, new TrieSegmentor() {
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
            connection.getTransmit().sendInformMsg(null, Status.IllegalMessage );
            return;
        }

        UMCHead head                = msg.getHead();
        Object  exHead              = head.getExtraHead();
        String szAddr               = this.mHeaderEvaluator.evalString( exHead, this.getServiceKeyword() );
        if( szAddr == null ) {
            connection.getTransmit().sendInformMsg(null, Status.IllegalMessage );
            return;
        }

        MessageController controller = this.mRoutingTable.get( szAddr );
        if( controller != null ) {
            connection.entrust( this );

            Object[] args;
            if( controller.isArgsIndexed() ) {
                args = this.mHeaderEvaluator.values( exHead ).toArray();
            }
            else {
                List<String > keys = controller.getArgumentsKey();
                args = this.mHeaderEvaluator.evals( exHead, keys );
            }

            try{
                controller.invoke( args );
            }
            catch ( Exception e ) {
                connection.getTransmit().sendInformMsg(null, Status.InternalError );
            }
        }
        else {
            throw new DenialServiceException( "It's none of my business." );
        }
    }

    protected abstract void doMessagelet( String szMessagelet, Package that ) ;

    @Override
    public void toDispatch( Package that ) throws IOException {
        this.prepareDispatch( that );
        this.messageDispatch( that );
    }

}