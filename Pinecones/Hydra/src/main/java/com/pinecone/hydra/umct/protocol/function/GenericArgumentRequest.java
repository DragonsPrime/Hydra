package com.pinecone.hydra.umct.protocol.function;

import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.framework.lang.field.FieldEntity;
import com.pinecone.framework.lang.field.GenericFieldEntity;
import com.pinecone.framework.lang.field.GenericStructure;
import com.pinecone.hydra.umct.protocol.ArchRequestPackage;

public class GenericArgumentRequest extends ArchRequestPackage implements ArgumentRequest {
    protected DataStructureEntity mDataStructureEntity;

    protected GenericArgumentRequest( String szInterceptedPath ) {
        super( szInterceptedPath );
    }

    public GenericArgumentRequest( DataStructureEntity dataStructureEntity, String szInterceptedPath ) {
        super( szInterceptedPath );
        this.mDataStructureEntity = dataStructureEntity;
    }

    public GenericArgumentRequest( String szInterceptedPath, Class<? >[] parameters ) {
        super( szInterceptedPath );
        this.from( parameters );
    }

    public GenericArgumentRequest( String szInterceptedPath, Object[] args ) {
        super( szInterceptedPath );
        this.from( args );
    }

    public GenericArgumentRequest( String szInterceptedPath, DataStructureEntity tpl ) {
        super( szInterceptedPath );
        this.conform( tpl );
    }

    public GenericArgumentRequest( FieldEntity[] segments ) {
        this( new GenericStructure( segments, 0, 1 ) );
    }

    public GenericArgumentRequest( DataStructureEntity entity ) {
        super( (String) entity.getSegments()[ 0 ].getValue() );
        this.mDataStructureEntity = entity;
    }

    @Override
    public void from( Class<? >[] parameters ) {
        this.mDataStructureEntity = MethodTemplates.from( this.mDataStructureEntity, this.mszInterceptedPath, parameters );
    }

    @Override
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

    @Override
    public void conform( DataStructureEntity tpl ) {
        this.mDataStructureEntity = MethodTemplates.conform( tpl, this.mszInterceptedPath );
    }

    @Override
    public DataStructureEntity getDataStructureEntity() {
        return this.mDataStructureEntity;
    }

    @Override
    public FieldEntity[] getSegments() {
        return this.mDataStructureEntity.getSegments();
    }


    @Override
    public void setField( int index, String key, Object val ) {
        this.mDataStructureEntity.setDataField( index, key, val );
    }

    @Override
    public void setField( int index, String key, Class<?> type ) {
        this.mDataStructureEntity.setDataField( index, key, type );
    }

    @Override
    public void setField( int index, Object val ) {
        FieldEntity field = this.getField( index );
        if( field != null ) {
            field.setValue( val );
        }
        else {
            this.setField( index, Integer.toString( index ), val );
        }
    }

    @Override
    public FieldEntity getField( int index ) {
        return this.mDataStructureEntity.getDataField( index );
    }

    @Override
    public FieldEntity findField( String key ) {
        return this.mDataStructureEntity.findDataField( key );
    }

    @Override
    public String toJSONString() {
        return this.mDataStructureEntity.toJSONString();
    }

    @Override
    public ArgumentRequest instancing() {
        FieldEntity[] proto = this.getSegments();

        FieldEntity[] ins = new FieldEntity[ proto.length ];
        for ( int i = 1; i < proto.length; ++i ) {
            FieldEntity entity = proto[ i ];
            ins[ i ] = new GenericFieldEntity( entity.getName(), null, entity.getType() );
        }
        ins[ 0 ] = proto[ 0 ];

        return new GenericArgumentRequest( ins );
    }
}
