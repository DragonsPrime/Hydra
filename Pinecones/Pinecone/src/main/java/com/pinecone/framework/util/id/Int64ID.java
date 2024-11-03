package com.pinecone.framework.util.id;

public class Int64ID implements NumbernicID {
    protected long mId;

    public Int64ID( long id ) {
        this.mId = id;
    }

    @Override
    public Identification parse( String hexID ) {
        this.mId = Long.parseLong( hexID, 16 );
        return this;
    }

    @Override
    public long longVal() {
        return this.mId;
    }

    @Override
    public int intVal() {
        return (int) this.mId;
    }
}