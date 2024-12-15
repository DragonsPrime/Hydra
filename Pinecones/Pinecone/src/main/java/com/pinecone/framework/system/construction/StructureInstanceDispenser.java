package com.pinecone.framework.system.construction;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.util.lang.DynamicFactory;

public interface StructureInstanceDispenser extends InstanceDispenser {
    StructureDefinition update ( Class<?> type, StructureDefinition definition ) ;

    InstanceDispenser register( Class<?> type, StructureDefinition definition, @Nullable InstancePool<? > pool ) ;

    InstanceDispenser register( Class<?> type, StructureDefinition definition ) ;

    InstanceDispenser register( Class<?> type, Structure structure, @Nullable InstancePool<? > pool ) ;

    InstanceDispenser register( Class<?> type, Structure structure ) ;

    InstanceDispenser register( Class<?> type, @Nullable InstancePool<? > pool ) ;

    InstanceDispenser register( StructureDefinition definition ) ;

    InstanceDispenser registerByImplicitFirstFound( Class<?> type, @Nullable Structure structure, @Nullable InstancePool<? > pool ) ;

    InstanceDispenser registerByImplicitFirstFound( Class<?> type, @Nullable Structure structure ) ;

    InstanceDispenser registerByImplicitFirstFound( Class<?> type ) ;

    <T > T allotInstance( Class<T> type, @Nullable Structure instanceStructure ) ;

    StructureDefinition getStructureDefinition( Class<?> type );

    InstancePool<? > getInstancePool( Class<?> type );

    DynamicFactory getCentralFactory();
}
