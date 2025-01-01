package com.pinecone.framework.util.json;

import java.lang.reflect.Type;

public abstract class JSONObjectDecoder implements JSONDecoder {
    protected abstract void set( Object self, String key, Object val );

    @Override
    public void decode( Object self, Object parent, Object indexKey, ArchCursorParser x, Type genericTypes ) {
        if ( x.nextClean() != '{' ) {
            throw x.syntaxError("A JSONObject text must begin with '{'");
        }
        else {
            while( true ) {
                char c = x.nextClean();
                switch(c) {
                    case '\u0000': {
                        throw x.syntaxError( "A JSONObject text must end with '}'" );
                    }
                    case '}': {
                        return;
                    }
                    default: {
                        x.back();

                        String key = null;
                        Object val = null;
                        try {
                            key = x.nextValue( null, self, null ).toString();
                            c = x.nextClean();
                            if ( c != ':' && c != '=' ) {
                                throw x.syntaxError( "Expected a ':', '=' after a key" );
                            }

                            Object[] args = null;
                            if( genericTypes != null ) {
                                args = new Object[]{ genericTypes };
                            }
                            val = x.nextValue( key, self, args );
                            this.set( self, key, val );
                        }
                        catch ( JSONParserRedirectException e ) {
                            e.setContext( new Object[]{ key, val } );
                            x.handleRedirectException( e );
                        }

                        switch ( x.nextClean() ) {
                            case ',':
                            case ';': {
                                if ( x.nextClean() == '}' ) {
                                    return;
                                }

                                x.back();
                                break;
                            }
                            case '}':{
                                return;
                            }
                            default: {
                                throw x.syntaxError( "Expected a ',' or '}'" );
                            }
                        }
                    }
                }
            }
        }
    }
}
