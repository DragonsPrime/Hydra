package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.system.regimentation.CascadeNodus;
import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.lang.DynamicFactory;
import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.umc.msg.ExtraEncode;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;
import com.pinecone.hydra.umc.msg.extra.GenericExtraHeadCoder;
import com.pinecone.hydra.umc.msg.handler.ErrorMessageAudit;
import com.pinecone.hydra.umc.msg.handler.GenericErrorMessageAudit;
import com.pinecone.hydra.umct.UMCTExpressHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;


public abstract class WolfMCNode extends WolfNettyServgram implements UlfMessageNode {
    protected ExtraHeadCoder        mExtraHeadCoder     ;
    protected final ReentrantLock   mMajorIOLock        = new ReentrantLock();
    protected ErrorMessageAudit     mErrorMessageAudit  ;
    protected UlfMessageNode        mParentNode         ;
    protected Namespace             mNodeNamespace      ;
    protected long                  mnMessageNodeId     ;

    public WolfMCNode( long nodeId, String szName, Processum parentProcess, UlfMessageNode parent, Map<String, Object> joConf, @Nullable ExtraHeadCoder extraHeadCoder ) {
        super( szName, parentProcess, joConf );

        this.mExtraHeadCoder    = extraHeadCoder;
        this.mErrorMessageAudit = new GenericErrorMessageAudit( this );
        this.mParentNode        = parent;
        this.mnMessageNodeId    = nodeId;
        this.setTargetingName( szName );
    }

    public WolfMCNode( long nodeId, String szName, Hydrarum system, Map<String, Object> joConf, @Nullable ExtraHeadCoder extraHeadCoder ) {
        this( nodeId, szName, system, null, joConf, extraHeadCoder );
    }

    @Override
    public CascadeNodus parent() {
        return this.mParentNode;
    }

    @Override
    public Namespace getTargetingName() {
        return this.mNodeNamespace;
    }

    @Override
    public void setTargetingName( Namespace name ) {
        this.mNodeNamespace = name;
    }

    @Override
    public ExtraHeadCoder getExtraHeadCoder() {
        return this.mExtraHeadCoder;
    }

    @Override
    public long getMessageNodeId() {
        return this.mnMessageNodeId;
    }

    public ReentrantLock getMajorIOLock() {
        return this.mMajorIOLock;
    }

    public WolfMCNode apply( Map<String, Object> joConf ) {
        this.setConfig( joConf );

        try{
            if( this.mExtraHeadCoder == null ) {
                String szExtraHeadCoder   = (String) joConf.get( "ExtraHeadCoder" );
                if( StringUtils.isEmpty( szExtraHeadCoder ) ) {
                    this.mExtraHeadCoder  = new GenericExtraHeadCoder() ;
                }
                else {
                    this.mExtraHeadCoder  = (ExtraHeadCoder) DynamicFactory.DefaultFactory.loadInstance( szExtraHeadCoder, null, null );
                }

                String szDefaultExtraEncode = (String) joConf.get( "DefaultExtraEncode" );
                if( StringUtils.isEmpty( szDefaultExtraEncode ) ) {
                    this.mExtraHeadCoder.setDefaultEncode( ExtraEncode.JSONString );
                }
                else {
                    this.mExtraHeadCoder.setDefaultEncode( ExtraEncode.valueOf( szDefaultExtraEncode ) );
                }
            }
        }
        catch ( ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            throw new ProxyProvokeHandleException( e );
        }

        return this;
    }

    public abstract WolfMCNode apply( UlfAsyncMsgHandleAdapter fnRecipientMsgHandler );

    public WolfMCNode apply( UMCTExpressHandler handler ){
        this.apply( UlfAsyncMsgHandleAdapter.wrap( handler ) );
        return this;
    }

    @Override
    public ErrorMessageAudit getErrorMessageAudit() {
        return this.mErrorMessageAudit;
    }

    @Override
    public void setErrorMessageAudit( ErrorMessageAudit audit ) {
        this.mErrorMessageAudit = audit;
    }
}
