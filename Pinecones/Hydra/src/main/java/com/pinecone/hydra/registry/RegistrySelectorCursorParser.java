package com.pinecone.hydra.registry;

import com.pinecone.framework.system.ParseException;
import com.pinecone.framework.util.CursorParser;

public class RegistrySelectorCursorParser implements CursorParser {
    protected RegistryJPathSelector mParser;

    protected RegistrySelectorCursorParser( RegistryJPathSelector parser ) {
        this.mParser = parser;
    }

    @Override
    public void back() throws ParseException {
        this.mParser.back();
    }

    @Override
    public char next() throws ParseException {
        return this.mParser.next();
    }

    @Override
    public String next( int n ) throws ParseException {
        return this.mParser.next(n);
    }

    @Override
    public Object nextValue() throws ParseException {
        return this.mParser.eval();
    }

    @Override
    public Object nextValue( Object indexKey, Object parent ) throws ParseException {
        return this.nextValue();
    }
}