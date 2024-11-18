package com.sauron.radium.ally.messengers;

import com.pinecone.framework.system.ProvokeHandleException;
import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.config.JSONConfig;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.homotype.JSONGet;
import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.umct.MessageExpress;
import com.pinecone.hydra.umct.Messagram;
import com.pinecone.hydra.servgram.Servgram;
import com.pinecone.hydra.system.ArchSystemAutoAssembleComponent;
import com.pinecone.hydra.system.HyComponent;
import com.pinecone.hydra.system.HyHierarchy;
import com.pinecone.hydra.system.component.LogStatuses;
import com.pinecone.hydra.umc.msg.MessageNode;
import com.pinecone.hydra.umc.wolfmc.UlfAsyncMsgHandleAdapter;
import com.pinecone.hydra.umc.wolfmc.WolfMCNode;
import com.pinecone.hydra.umc.wolfmc.server.WolfMCServer;
import com.sauron.radium.system.MiddlewareManager;
import com.sauron.radium.system.RadiumSystem;
import com.sauron.radium.system.Saunut;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public class MessagersManager extends ArchSystemAutoAssembleComponent implements Saunut, HyComponent {
    @JSONGet( "Messagers" )
    protected JSONConfig                           mjoMessagersConf        ;

    @JSONGet( "Messagers.Configs" )
    protected JSONConfig                           mjoComponentConf        ;

    @JSONGet( "Messagers.Messagers" )
    protected JSONConfig                           mjoMessagers            ;


    @JSONGet( "Messagers.Configs.Enable" )
    protected boolean                              mbEnable                ;

    protected Map<String, Pinenut >                mMessagerComponent      ;



    public MessagersManager( Namespace name, HyComponent parent ) {
        super( name, parent.getSystem(), parent.getSystem().getComponentManager(), parent );

        MiddlewareManager parentManager = (MiddlewareManager) parent;

        this.getSystem().getPrimaryConfigScope().autoInject( MessagersManager.class, parentManager.getMiddlewareConfig() , this );

        this.mMessagerComponent = new LinkedTreeMap<>();
        this.prepareInstanceMessagers();

        this.infoLifecycleInitializationDone();
    }

    public MessagersManager( HyComponent parent ) {
        this( null, parent );
    }


    protected void prepareInstanceProcessum( Object node ) {
        if( node instanceof Processum ) {
            this.getSystem().getTaskManager().add( (Processum)node );
        }
    }

    protected void executeInnerServgram( Object node, JSONObject conf ) {
        HyHierarchy hierarchy = this.getSystem().getServiceArch();

        if( node instanceof Servgram ) {
            boolean bIsRecipient = conf.optBoolean( "IsRecipient" );

            if( bIsRecipient ) {
                String[] as = this.getSystem().getStartupCommandMap().get( "TestWolfMCClient" );
                if( as != null && as.length > 0 && as[0].equals( "true" ) && node instanceof WolfMCServer ){
                    return;
                }
            }

            if( !bIsRecipient ) {
                if( node instanceof MessageNode ) {
                    boolean bAutoStartInMasterMode = conf.optBoolean( "AutoStartInMasterMode" );
                    if( hierarchy.isDominantClass() && !bAutoStartInMasterMode ) {
                        return;
                    }
                }
            }

            try{
                ((Servgram) node).execute();
            }
            catch ( Exception e ) {
                throw new ProxyProvokeHandleException( e );
            }
        }
    }

    protected void prepareInstanceMessagers() {
        for( Object o : this.mjoMessagers.entrySet() ) {
            Map.Entry kv   = (Map.Entry) o;

            Object ov = kv.getValue();
            if( ov instanceof String ) {
                try{
                    ov = this.mjoMessagers.fromPath( Path.of( (String) ov ) );
                }
                catch ( IOException e ) {
                    throw new ProxyProvokeHandleException( e );
                }
            }

            JSONObject val = (JSONObject) ov;
            this.mObjectOverrider.override( val, this.mjoComponentConf, false );

            try{
                String szEngine = val.optString( "Engine" );
                String szInsNam = (String) kv.getKey();

                boolean bEnable = val.optBoolean( "Enable" );
                if( bEnable ) {
                    Object node = this.mUniformFactory.loadInstance( szEngine, null, new Object[] { szInsNam, this.getSystem(), val } );
                    if( node instanceof MessageNode ){
                        this.mMessagerComponent.put( szInsNam, (MessageNode)node );
                        this.prepareMessagersMsgHandler( szInsNam, (MessageNode)node, val );
                    }
                    else if( node instanceof Messagram ){
                        this.mMessagerComponent.put( szInsNam, (Messagram)node );
                    }
                    else {
                        throw new IllegalArgumentException( "Illegal message node engine, should be `MessageNode/Messagram`: " + szEngine );
                    }

                    this.prepareInstanceProcessum( node );
                    this.executeInnerServgram( node, val );
                }
            }
            catch ( Exception e ) {
                throw new ProvokeHandleException( e );
            }
        }

        //Debug.fmt( 2, this.mjoMessagersConf );
    }

    protected MessageExpress getMessageHandlerByName( String name ) {
        for( Map.Entry<String, Pinenut > kv: this.mMessagerComponent.entrySet() ) {
            Pinenut p = kv.getValue();
            if( p instanceof Messagram ) {
                Messagram messagram = (Messagram) p;
                MessageExpress me = messagram.getExpressByName( name );
                if( me != null ) {
                    return me;
                }
            }
        }
        return null;
    }

    protected void prepareMessagersMsgHandler( String szInsNam, MessageNode node, JSONObject conf ) {
        String szMessageHandler = conf.optString( "MessageHandler" );
        if( !StringUtils.isEmpty(szMessageHandler) ) {
            MessageExpress me;
            if( szMessageHandler.contains( "." ) ) {
                try {
                    Object o = this.mUniformFactory.loadInstance( szMessageHandler, null, null );
                    if( o instanceof MessageExpress ){
                        me = (MessageExpress) o;
                    }
                    else {
                        throw new IllegalArgumentException( "Illegal message handler, should be `MessageExpress`: " + szMessageHandler );
                    }
                }
                catch ( Exception e ) {
                    throw new ProvokeHandleException( e );
                }
            }
            else {
                me = this.getMessageHandlerByName( szMessageHandler );
            }

            if( me == null ) {
                throw new IllegalArgumentException( "Illegal message handler, can`t found: " + szMessageHandler );
            }

            if( node instanceof WolfMCNode ) {
                ((WolfMCNode) node).apply( (UlfAsyncMsgHandleAdapter)me );
                this.infoCriticalOperation(
                        "SetMessageExpress(`" + szMessageHandler + "`) ==> (`" + szInsNam + "`)", LogStatuses.StatusDone
                );
            }
        }
    }

    @Override
    public RadiumSystem getSystem() {
        return ( RadiumSystem ) super.getSystem();
    }

    public boolean isEnable() {
        return this.mbEnable;
    }

    public JSONObject getMessagers() {
        return this.mjoMessagers;
    }

    @SuppressWarnings( "unchecked" )
    public Collection<String > messagersNames() {
        return (Collection)this.getMessagers().values();
    }

    public Pinenut getComponentByName (String szName ) {
        return this.mMessagerComponent.get( szName );
    }

    public MessageNode getMessageNodeByName ( String szName ) {
        Pinenut p = this.getComponentByName( szName );
        if( p instanceof MessageNode ) {
            return (MessageNode) p;
        }
        return null;
    }

    public Messagram getMessagramByName ( String szName ) {
        Pinenut p = this.getComponentByName( szName );
        if( p instanceof Messagram ) {
            return (Messagram) p;
        }
        return null;
    }

    public Pinenut terminate( String szName ) {
        Pinenut node = this.getComponentByName( szName );
        if( node != null ) {
            if( node instanceof Servgram ) {
                ((Servgram) node).terminate();
            }
            else if( node instanceof Processum ) {
                ((Processum) node).apoptosis();
            }

            this.mMessagerComponent.remove( szName );
        }
        return node;
    }

    public int nodesSize() {
        return this.mMessagerComponent.size();
    }
}
