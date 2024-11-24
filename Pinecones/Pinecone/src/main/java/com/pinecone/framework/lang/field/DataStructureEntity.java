package com.pinecone.framework.lang.field;

public interface DataStructureEntity extends SegmentEntity {
    String StructureNameKey = "__NAME__";

    int getStartOffset();

    int getTextOffset();

    int getDataOffset();

    void setTextOffset( int offset );

    void setDataOffset( int offset );

    boolean isEmpty();

    int size();

    int capacity();

    void resize( int newSize );

    FieldEntity[] getFields();

    FieldEntity[] getSegments();

    void setTextField( int index, FieldEntity field ) ;

    void setDataField( int index, FieldEntity field ) ;

    void setTextField( int index, String key, Object val );

    void setDataField( int index, String key, Object val );

    void setTextField( int index, String key, Class<?> type );

    void setDataField( int index, String key, Class<?> type );

    FieldEntity getTextField( int index );

    FieldEntity getDataField( int index );

    FieldEntity findTextField( String key );

    FieldEntity findDataField( String key );
}
