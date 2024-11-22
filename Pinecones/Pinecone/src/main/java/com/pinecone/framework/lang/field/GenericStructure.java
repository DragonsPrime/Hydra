package com.pinecone.framework.lang.field;

import java.util.Arrays;

import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.json.JSON;

public class GenericStructure implements DataStructureEntity {
    protected FieldEntity[] mSegments;

    protected int mnTextOffset;

    protected int mnDataOffset;

    public GenericStructure( String szName, int nTextOffset, int nDataOffset ,int nElements ) {
        if ( nDataOffset < 1 || nTextOffset >= nDataOffset ) {
            throw new IllegalArgumentException( "DataOffset must be greater than 1." );
        }

        this.mSegments       = new FieldEntity[ nDataOffset - nTextOffset + nElements ];
        this.mSegments[ 0 ]  = new GenericFieldEntity( DataStructureEntity.StructureNameKey, szName, String.class );
        this.mnTextOffset    = nTextOffset;
        this.mnDataOffset   = nDataOffset;
    }

    public GenericStructure( String szName ,int nElements ) {
        this( szName, 0, 1 , nElements );
    }

    @Override
    public String getName() {
        return (String) this.mSegments[ 0 ].getValue();
    }

    @Override
    public String getSimpleName() {
        String sz = this.getName();
        String[] debris = sz.split( "\\.|\\/" );
        if( debris.length > 1 ) {
            return debris [ 1 ];
        }
        return sz;
    }

    @Override
    public int getStartOffset() {
        return 0;
    }

    @Override
    public int getTextOffset() {
        return this.mnTextOffset;
    }

    @Override
    public int getDataOffset() {
        return this.mnDataOffset;
    }

    @Override
    public void setTextOffset( int offset ) {
        if( offset < 0 ) {
            return;
        }

        if ( offset > this.mnTextOffset ) {
            this.resize( offset + this.mSegments.length );
            System.arraycopy( this.mSegments, this.mnTextOffset, this.mSegments, offset, this.mnDataOffset - this.mnTextOffset );
        }

        this.mnTextOffset = offset;
    }

    @Override
    public void setDataOffset( int offset ) {
        if( offset <= 1 ) {
            return;
        }

        if ( offset > this.mnDataOffset ) {
            this.resize( this.size() + offset - this.mnDataOffset  );
            System.arraycopy( this.mSegments, this.mnDataOffset, this.mSegments, offset, this.mSegments.length - offset );
            for ( int i = this.mnDataOffset ; i < offset; ++i ) {
                this.mSegments[ i ] = null;
            }
        }

        this.mnDataOffset = offset;
    }

    @Override
    public boolean isEmpty() {
        return this.mnDataOffset <= 0;
    }

    @Override
    public int size() {
        return this.mSegments.length - this.mnDataOffset;
    }

    @Override
    public int capacity() {
        return this.mSegments.length;
    }

    @Override
    public void resize( int newSize ) {
        if ( newSize + this.mnDataOffset <= this.mSegments.length ) {
            throw new IllegalArgumentException( "New size must be greater than current size." );
        }

        FieldEntity[] newSegments = new FieldEntity[ newSize + this.mnDataOffset ];
        System.arraycopy( this.mSegments, 0, newSegments, 0, this.mSegments.length );
        this.mSegments = newSegments;
    }

    @Override
    public FieldEntity[] getFields() {
        return Arrays.copyOfRange( this.mSegments, this.mnDataOffset, this.mSegments.length );
    }

    @Override
    public FieldEntity[] getSegments() {
        return this.mSegments;
    }

    @Override
    public void setTextField( int index, FieldEntity field ) {
        if ( index < this.mnTextOffset || index >= this.mnDataOffset ) {
            throw new IndexOutOfBoundsException( "Text segment index out of bounds." );
        }
        this.mSegments[ this.mnTextOffset + index ] = field;
    }

    @Override
    public void setDataField( int index, FieldEntity field ) {
        int dataEnd   = this.mSegments.length;
        if ( index >= dataEnd - this.mnDataOffset ) {
            throw new IndexOutOfBoundsException( "Data segment index out of bounds." );
        }
        this.mSegments[ this.mnDataOffset + index ] = field;
    }

    @Override
    public void setTextField( int index, String key, Object val ) {
        FieldEntity legacy = this.getTextField( index );
        FieldEntity neo    = null;
        if( legacy != null ) {
            if( legacy.getName().equals( key ) ) {
                legacy.setValue( val );
                return;
            }
        }
        neo = new GenericFieldEntity( key, val );
        this.setTextField( index, neo );
    }

    @Override
    public void setDataField( int index, String key, Object val ) {
        FieldEntity legacy = this.getDataField( index );
        FieldEntity neo    = null;
        if( legacy != null ) {
            if( key.equals( legacy.getName() ) ) {
                legacy.setValue( val );
                return;
            }
        }
        neo = new GenericFieldEntity( key, val );
        this.setDataField( index, neo );
    }

    @Override
    public FieldEntity getDataField( int index ) {
        return this.mSegments[ this.mnDataOffset + index ];
    }

    @Override
    public FieldEntity getTextField( int index ) {
        return this.mSegments[ this.mnTextOffset + index ];
    }

    @Override
    public FieldEntity findTextField( String key ) {
        for ( int i = this.mnTextOffset; i < this.mnDataOffset; ++i ) {
            FieldEntity entity = this.mSegments[ i ];
            if( entity != null && entity.getName().equals( key ) ) {
                return entity;
            }
        }

        return null;
    }

    @Override
    public FieldEntity findDataField( String key ) {
        for ( int i = this.mnDataOffset; i < this.mSegments.length; ++i ) {
            FieldEntity entity = this.mSegments[ i ];
            if( entity != null && entity.getName().equals( key ) ) {
                return entity;
            }
        }

        return null;
    }

    @Override
    public String toJSONString() {
        StringBuilder sb = new StringBuilder();
        sb.append( '{' );

        for( int i = this.mnDataOffset; i < this.mSegments.length; ++i ) {
            FieldEntity entity = this.mSegments[ i ];
            if( entity != null ) {
                sb.append( StringUtils.jsonQuote( entity.getName() ) );
                sb.append( ':' );
                sb.append( JSON.stringify( entity.getValue() ) );
                sb.append( ',' );
            }
        }

        if( sb.charAt( sb.length() - 1 ) == ',' ) {
            sb.deleteCharAt( sb.length() - 1 );
        }

        sb.append( '}' );
        return sb.toString();
    }
}
