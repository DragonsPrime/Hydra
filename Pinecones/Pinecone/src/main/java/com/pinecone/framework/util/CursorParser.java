package com.pinecone.framework.util;

import com.pinecone.framework.system.ParseException;
import com.pinecone.framework.system.prototype.Pinenut;

public interface CursorParser extends Pinenut {
    void back() throws ParseException;

    char next() throws ParseException;

    String next( int n ) throws ParseException;

    Object nextValue() throws ParseException ;

    Object nextValue( Object indexKey, Object parent ) throws ParseException ;
}
