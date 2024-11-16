package com.pinecone.hydra.umct;

import com.pinecone.framework.system.ProvokeHandleException;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.lang.DynamicFactory;
import com.pinecone.framework.util.lang.GenericDynamicFactory;

import java.util.Map;

public class IntegratedMessagram extends ArchMessagram implements Messagram {
    protected String                          mszExpressFactory;
    protected Map<String, Object >            mExpressesConf;
    protected DynamicFactory                  mExpressFactory;

    public IntegratedMessagram(String szName, Processum parent, Map<String, Object > config ) {
        super( szName, parent, config );

        this.prepareConfig();
        this.prepareExpresses();
    }

    @SuppressWarnings( "unchecked" )
    protected void prepareConfig() {
        this.mszExpressFactory = (String) this.getProtoConfig().get( "ExpressFactory" );
        this.mExpressesConf    = (Map) this.getProtoConfig().get( "Expresses" );
    }

    protected void prepareExpresses() {
        if( StringUtils.isEmpty( this.mszExpressFactory ) ) {
            this.mExpressFactory = new GenericDynamicFactory( this.getSystem().getTaskManager().getClassLoader() );
        }
        else {
            this.mExpressFactory = (DynamicFactory) DynamicFactory.DefaultFactory.optLoadInstance(
                    this.mszExpressFactory, null, new Object[] { this.getSystem().getTaskManager().getClassLoader() }
            );
        }

        try{
            if( this.mExpressesConf != null ) {
                for( Map.Entry<String, Object > kv : this.mExpressesConf.entrySet() ) {
                    Object v = kv.getValue();
                    Map map = (Map) v;

                    String szEngine = (String) map.get( "Engine" );
                    Object node = this.mExpressFactory.loadInstance( szEngine, null, new Object[] { kv.getKey(), this } );
                    if( node instanceof MessageExpress ){
                        MessageExpress express = (MessageExpress) node;
                        this.mExpresses.put( express.getName(), express );
                    }
                    else {
                        throw new IllegalArgumentException( "Illegal message express engine, should be `MessageExpress`: " + szEngine );
                    }
                }
            }
        }
        catch ( Exception e ) {
            throw new ProvokeHandleException( e );
        }
    }

    @Override
    public Map<String, Object > getExpressesConfig() {
        return this.mExpressesConf;
    }

    @Override
    public String getLetsNamespace() {
        return "";
    }
}
