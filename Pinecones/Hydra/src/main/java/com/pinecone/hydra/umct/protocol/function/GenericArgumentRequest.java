package com.pinecone.hydra.umct.protocol.function;

import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.framework.lang.field.FieldEntity;
import com.pinecone.framework.lang.field.GenericStructure;
import com.pinecone.hydra.umct.protocol.ArchRequestPackage;

public class GenericArgumentRequest extends ArchRequestPackage implements ArgumentRequest {
    protected DataStructureEntity mDataStructureEntity;

    public GenericArgumentRequest( String szInterceptedPath, Class<? >[] parameters ) {
        super( szInterceptedPath );
        this.from( parameters );
    }

    public GenericArgumentRequest( String szInterceptedPath, Object[] args ) {
        super( szInterceptedPath );
        this.from( args );
    }

    public void from( Class<? >[] parameters ) {
        if( this.mDataStructureEntity == null || parameters.length != this.mDataStructureEntity.size() ) {
            this.mDataStructureEntity = new GenericStructure( this.mszInterceptedPath, parameters.length );
        }

        int i = 0;
        for( Class<? > parameter : parameters ) {
            this.mDataStructureEntity.setDataField( i,
                    parameter.getName().replace( ".", "_" ) + "_" + i,
                    parameter
            );
            ++i;
        }
    }

    public void from( Object[] args ) {
        if( this.mDataStructureEntity == null || args.length != this.mDataStructureEntity.size() ) {
            this.mDataStructureEntity = new GenericStructure( this.mszInterceptedPath, args.length );
        }

        for ( int i = 0; i < args.length; ++i ) {
            this.mDataStructureEntity.setDataField( i,
                    args[ i ].getClass().getName().replace( ".", "_" ) + "_" + i,
                    args[ i ]
            );
        }
    }

    public DataStructureEntity getDataStructureEntity() {
        return this.mDataStructureEntity;
    }

    public FieldEntity[] getSegment() {
        return this.mDataStructureEntity.getSegments();
    }


    public void setField( int index, String key, Object val ) {
        this.mDataStructureEntity.setDataField( index, key, val );
    }

    public void setField( int index, String key, Class<?> type ) {
        this.mDataStructureEntity.setDataField( index, key, type );
    }

    public FieldEntity getField( int index ) {
        return this.mDataStructureEntity.getDataField( index );
    }

    public FieldEntity findField( String key ) {
        return this.mDataStructureEntity.findDataField( key );
    }

    @Override
    public String toJSONString() {
        return this.mDataStructureEntity.toJSONString();
    }
}
