package com.pinecone.hydra.registry.operator;

import com.pinecone.hydra.registry.KOMRegistry;
import com.pinecone.hydra.registry.entity.GenericNamespace;
import com.pinecone.hydra.registry.entity.GenericProperties;
import com.pinecone.hydra.registry.entity.GenericTextFile;
import com.pinecone.hydra.registry.source.RegistryMasterManipulator;
import com.pinecone.hydra.unit.imperium.operator.TreeNodeOperator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class GenericRegistryOperatorFactory implements RegistryOperatorFactory {
    protected RegistryMasterManipulator        registryMasterManipulator;

    protected KOMRegistry registry;

    protected Map<String, TreeNodeOperator>    registerer = new HashMap<>();

    protected Map<String, String >             metaTypeMap = new TreeMap<>();

    protected void registerDefaultMetaType( Class<?> genericType ) {
        this.metaTypeMap.put( genericType.getName(), genericType.getSimpleName().replace("Generic","") );
    }

    protected void registerDefaultMetaTypes() {
        this.registerDefaultMetaType( GenericNamespace.class );
        this.registerDefaultMetaType( GenericProperties.class );
        this.registerDefaultMetaType( GenericTextFile.class );
    }

    public GenericRegistryOperatorFactory(KOMRegistry registry, RegistryMasterManipulator registryMasterManipulator ){
        this.registry = registry;
        this.registryMasterManipulator = registryMasterManipulator;

        this.registerer.put(
                RegistryOperatorFactory.DefaultNamespaceNodeKey,
                new NamespaceNodeOperator( this )
        );

        this.registerer.put(RegistryOperatorFactory.DefaultPropertyConfigNodeKey,
                new PropertiesOperator(this)
        );

        this.registerer.put(RegistryOperatorFactory.DefaultTextConfigNode,
                new TextValueNodeOperator(this)
        );

        this.registerDefaultMetaTypes();
    }

    @Override
    public void register( String typeName, TreeNodeOperator functionalNodeOperation ) {
        this.registerer.put( typeName, functionalNodeOperation );
    }

    @Override
    public void registerMetaType( Class<?> clazz, String metaType ){
        this.registerMetaType( clazz.getName(), metaType );
    }

    @Override
    public void registerMetaType( String classFullName, String metaType ){
        this.metaTypeMap.put( classFullName, metaType );
    }

    @Override
    public String getMetaType( String classFullName ) {
        return this.metaTypeMap.get( classFullName );
    }

    @Override
    public RegistryNodeOperator getOperator( String typeName ) {
        //Debug.trace( this.registerer.toString() );
        return (RegistryNodeOperator)this.registerer.get( typeName );
    }

    @Override
    public KOMRegistry getRegistry() {
        return this.registry;
    }

    @Override
    public RegistryMasterManipulator getMasterManipulator() {
        return this.registryMasterManipulator;
    }
}
