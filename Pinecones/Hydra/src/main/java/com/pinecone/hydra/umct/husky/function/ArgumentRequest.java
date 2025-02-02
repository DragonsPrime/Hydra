package com.pinecone.hydra.umct.husky.function;

import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.framework.lang.field.FieldEntity;
import com.pinecone.hydra.umct.husky.RequestPackage;

public interface ArgumentRequest extends RequestPackage {
    void from( Class<? >[] parameters );

    void from( Object[] args );

    void conform( DataStructureEntity tpl );

    DataStructureEntity getDataStructureEntity() ;

    FieldEntity[] getSegments() ;

    void setField( int index, String key, Object val ) ;

    void setField( int index, String key, Class<?> type ) ;

    void setField( int index, Object val ) ;

    FieldEntity getField( int index );

    FieldEntity findField( String key );

    ArgumentRequest instancing();
}
