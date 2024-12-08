package com.pinecone.hydra.umct;

import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.hydra.servgram.ArchServgramium;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.framework.system.executum.Processum;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public abstract class ArchMessagram extends ArchServgramium implements Messagram {
    public static final String DefaultServiceKey = "__NAME__";

    protected Map<String, MessageExpress > mExpresses;
    protected Map<String, Object >         mProtoConfig;


    public ArchMessagram( String szName, Processum parent, Map<String, Object > config ) {
        super( szName, parent );

        this.mExpresses   = new LinkedTreeMap<>();
        this.mProtoConfig = config;
    }

    @Override
    public Map<String, Object > getProtoConfig() {
        return this.mProtoConfig;
    }

    @Override
    public Messagram addExpress( MessageExpress express ) {
        this.mExpresses.put( express.getName(), express );
        return this;
    }

    @Override
    public MessageExpress getExpressByName( String name ) {
        return this.mExpresses.get( name );
    }

    @Override
    public Messagram removeExpress( String name ) {
        this.mExpresses.remove( name );
        return this;
    }

    public abstract String getLetsNamespace() ;

    protected Messagelet contriveByClassName( String szClassName, UMCConnection UMCConnection) {
        Messagelet obj = null;
        try {
            Class<?> pVoid = Class.forName( szClassName );
            try{
                Constructor<?> constructor = pVoid.getConstructor( UMCConnection.class, ArchMessagram.class );
                obj = (Messagelet) constructor.newInstance(UMCConnection, this );
            }
            catch (NoSuchMethodException | InvocationTargetException e1){
                e1.printStackTrace();
            }
        }
        catch ( ClassNotFoundException | IllegalAccessException | InstantiationException e ){
            return null;
        }

        return obj;
    }

    public Messagelet contriveByScheme( String szSchemeName, UMCConnection UMCConnection) throws IllegalArgumentException {
        String szClassName = this.getLetsNamespace() + szSchemeName ;
        Messagelet obj = this.contriveByClassName( szClassName, UMCConnection);
        if( obj == null ){
            throw new IllegalArgumentException( "[Messagelet] Fantasy scheme with no crew member." );
        }
        return obj;
    }

    @Override
    public Hydrarum getSystem() {
        return (Hydrarum) super.getSystem();
    }

    @Override
    public void execute() {
        this.infoLifecycle( "toSeek", "Can do !" );
    }
}
