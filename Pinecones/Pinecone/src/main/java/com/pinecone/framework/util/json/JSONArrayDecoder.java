package com.pinecone.framework.util.json;

import java.lang.reflect.Type;

public abstract class JSONArrayDecoder implements JSONDecoder {
    protected abstract void add( Object self, Object parent, Object indexKey, Object val );

    @Override
    public void decode( Object self, Object parent, Object indexKey, ArchCursorParser x, Type genericTypes ) {
        if ( x.nextClean() != '[' ) {
            throw x.syntaxError("A JSONArray text must start with '['");
        }
        else if ( x.nextClean() != ']' ) {
            x.back();

            int i = 0;
            while( true ) {
                if ( x.nextClean() == ',' ) {
                    x.back();

                    this.add( self, parent, indexKey, JSON.NULL );
                }
                else {
                    x.back();
                    try {
                        Object[] args = null;
                        if( genericTypes != null ) {
                            args = new Object[]{ genericTypes };
                        }
                        this.add( self, parent, indexKey, x.nextValue( i, self, args ) );
                        ++i;
                    }
                    catch ( JSONParserRedirectException e ) {
                        x.handleRedirectException( e );
                    }
                }

                switch( x.nextClean() ) {
                    case ',': {
                        if (x.nextClean() == ']') {
                            return;
                        }

                        x.back();
                        break;
                    }
                    case ']': {
                        return;
                    }
                    default: {
                        throw x.syntaxError("Expected a ',' or ']'");
                    }
                }
            }
        }
    }
}
