package com.pinecone.framework.util.id;

public class Int32ID implements NumbernicID {
    protected int mId;

    public Int32ID( int id ) {
        this.mId = id;
    }

    @Override
    public Identification parse( String hexID ) {
        this.mId = Integer.parseInt( hexID, 16 );
        return this;
    }

    @Override
    public long longVal() {
        return this.mId;
    }

    @Override
    public int intVal() {
        return this.mId;
    }
}
