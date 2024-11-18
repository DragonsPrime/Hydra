package com.sauron.radium.system;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.system.construction.UnifyStructureInjector;
import com.pinecone.framework.system.homotype.StereotypicInjector;
import com.pinecone.framework.unit.MultiScopeMap;
import com.pinecone.framework.unit.MultiScopeMaptron;
import com.pinecone.framework.util.config.JSONConfig;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.homotype.AnnotatedObjectInjector;
import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.config.MapConfigReinterpreter;
import com.pinecone.hydra.config.ScopedMapConfigReinterpreter;
import com.pinecone.hydra.system.ArchSystemCascadeComponent;
import com.pinecone.hydra.system.HyComponent;
import com.pinecone.hydra.system.Hydrarum;

import java.util.Map;

public class ConfigScope extends ArchSystemCascadeComponent implements Saunut, HyComponent {
    public static final String KeyGlobal = "Global" + ConfigScope.class.getSimpleName();

    protected JSONConfig                      mProtoConfig;
    protected MultiScopeMap<String, Object >  mConfigScope;
    protected MapConfigReinterpreter          mConfigReinterpreter;

    public ConfigScope( Namespace name, Hydrarum system, HyComponent parent, JSONConfig config ) {
        super( name, system, system.getComponentManager(), parent );

        this.mConfigScope = new MultiScopeMaptron<>( new JSONMaptron() );
        this.mProtoConfig = config;

        this.reinterpret_conf_default();

        this.mConfigReinterpreter = new ScopedMapConfigReinterpreter( this.getScopeMap() );
    }

    public ConfigScope( Hydrarum system, HyComponent parent, JSONConfig config ) {
        this( (Namespace) null, system, parent, config );
    }

    public ConfigScope( Hydrarum system, JSONConfig config ) {
        this( system, null,config );
    }

    public ConfigScope( String name, Hydrarum system, HyComponent parent, JSONConfig config ) {
        this( system, parent, config );

        this.setTargetingName( name );
    }

    public ConfigScope( String name, Hydrarum system, JSONConfig config ) {
        this( name, system, null, config );
    }

    protected void reinterpret_conf_default() {
        this.mConfigScope.setName( "GlobalConfigScope" );
        for ( Map.Entry<String,Object > kv: this.getProtoConfig().entrySet() ) {
            this.mConfigScope.put( kv.getKey(), kv.getValue() );
        }
    }

    public JSONConfig getProtoConfig() {
        return this.mProtoConfig;
    }

    public MultiScopeMap<String, Object > getScopeMap() {
        return this.mConfigScope;
    }

    public MapConfigReinterpreter getMapConfigReinterpreter() {
        return this.mConfigReinterpreter;
    }

    public MapConfigReinterpreter newMapConfigReinterpreter() {
        return new ScopedMapConfigReinterpreter( this.getScopeMap() );
    }


    public StereotypicInjector autoInject( Class<?> stereotype, Object config, Object instance ) {
        AnnotatedObjectInjector injector = new AnnotatedObjectInjector( stereotype );
        try{
            injector.inject( config, instance );
            return injector;
        }
        catch ( Exception e ){
            throw new ProxyProvokeHandleException( e );
        }
    }

    public StereotypicInjector autoInject( Class<?> stereotype, Map config, Object instance ) {
        AnnotatedObjectInjector injector = new AnnotatedObjectInjector( stereotype );
        try{
            injector.inject( config, instance );
        }
        catch ( Exception e ){
            throw new ProxyProvokeHandleException( e );
        }
        return injector;
    }

    public StereotypicInjector autoConstruct( Class<?> stereotype, Object config, Object instance ) {
        UnifyStructureInjector injector = new UnifyStructureInjector( stereotype, ( (RadiumSystem)this.getSystem()).getDispenserCenter().getInstanceDispenser() );
        try{
            injector.inject( config, instance );
            return injector;
        }
        catch ( Exception e ){
            throw new ProxyProvokeHandleException( e );
        }
    }

    public StereotypicInjector autoConstruct( Class<?> stereotype, Map config, Object instance ) {
        UnifyStructureInjector injector = new UnifyStructureInjector( stereotype, ( (RadiumSystem)this.getSystem()).getDispenserCenter().getInstanceDispenser() );
        try{
            injector.inject( config, instance );
        }
        catch ( Exception e ){
            throw new ProxyProvokeHandleException( e );
        }
        return injector;
    }

}
