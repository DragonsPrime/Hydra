package com.pinecone.framework.util.json;

import com.pinecone.framework.util.CursorParser;
import com.pinecone.framework.util.GeneralStrings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

/**
 *  Pinecone For Java JSONCursorParser [ Bean Nuts Almond JSON For Pinecone Java ]
 *  Copyright © 2008 - 2028 Bean Nuts Foundation ( DR.Undefined ) All rights reserved. [Harold.E / WJH]
 *  Tip:
 *  *****************************************************************************************
 *  JSON util Version Signature: Ver. 3.4 [Build 20240531] [Pinecone Ver.3.4]
 *  Author: undefined
 *  Last Modified Date: 2024-05-31
 *  *****************************************************************************************
 *  Principle : Bottom-up parsing implementation with recursive descendent method.
 *  [ Due to Java doesn't had fucking pointer, using cursor method can be better ]
 *  Reference[1]: https://www.json.org/json-en.html
 *  Reference[2]: https://spec.json5.org/
 *  Syntax: T-> "", T->[0-9] T-> '', T->[a-zA-Z]..., etc
 *          T-> { T : T }, T-> [ T ], etc
 *  Support: JSON, JSON5
 *  *****************************************************************************************
 *  Notice: This is high tolerance JSON parser, It does not fully comply with the JSON standard for error situations
 *  *****************************************************************************************
 */
public abstract class ArchCursorParser implements CursorParser {
    protected long     mnCharacter;
    protected boolean  mbIsEOF;
    protected int      mnParseAt ;
    protected int      mnLineAt;
    protected char     mcPrevious;
    protected Reader   mReader;
    protected boolean  mbUsePrevious;

    public ArchCursorParser( Reader reader ) {
        this.mReader = (Reader)(reader.markSupported() ? reader : new BufferedReader(reader));
        this.mbIsEOF = false;
        this.mbUsePrevious = false;
        this.mcPrevious = 0;
        this.mnParseAt = 0;
        this.mnCharacter = 1L;
        this.mnLineAt = 1;
    }

    public ArchCursorParser( InputStream inputStream ) throws JSONParseException {
        this((Reader)(new InputStreamReader(inputStream)));
    }

    public ArchCursorParser( String s ) {
        this((Reader)(new StringReader(s)));
    }

    public void lineBack() {
        if ( !this.mbUsePrevious && this.mnParseAt > 0L ) {
            --this.mnParseAt;
            --this.mnLineAt;
            if( this.mnCharacter != 0 ) {
                --this.mnCharacter;
            }
            this.mbUsePrevious = true;
            this.mbIsEOF = false;
        }
    }

    @Override
    public void back() throws JSONParseException {
        if (!this.mbUsePrevious && this.mnParseAt > 0L) {
            --this.mnParseAt;
            --this.mnCharacter;
            this.mbUsePrevious = true;
            this.mbIsEOF = false;
        }
        else {
            throw new JSONParseException("Stepping back two steps is not supported");
        }
    }

    public static int dehexchar(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        } else if (c >= 'A' && c <= 'F') {
            return c - 55;
        } else {
            return c >= 'a' && c <= 'f' ? c - 87 : -1;
        }
    }

    public boolean isEndLine() {
        return this.mbIsEOF && !this.mbUsePrevious;
    }

    public boolean more() throws JSONParseException {
        this.next();
        if ( this.isEndLine() ) {
            return false;
        }
        else {
            this.back();
            return true;
        }
    }

    @Override
    public char next() throws JSONParseException {
        int c;
        if (this.mbUsePrevious) {
            this.mbUsePrevious = false;
            c = this.mcPrevious;
        }
        else {
            try {
                c = this.mReader.read();
            }
            catch ( IOException e ) {
                throw new JSONParseException(e);
            }

            if (c <= 0) {
                this.mbIsEOF = true;
                c = 0;
            }
        }

        ++this.mnParseAt;
        if ( this.mcPrevious == '\r' ) {
            ++this.mnLineAt;
            this.mnCharacter = (long)(c == 10 ? 0 : 1);
        }
        else if ( c == '\n' ) {
            ++this.mnLineAt;
            this.mnCharacter = 0L;
        }
        else {
            ++this.mnCharacter;
        }

        this.mcPrevious = (char)c;
        return this.mcPrevious;
    }

    public char next( char c ) throws JSONParseException {
        char n = this.next();
        if (n != c) {
            throw this.syntaxError("Error parser json string with expected '" + c + "' and instead saw '" + n + "'");
        } else {
            return n;
        }
    }

    @Override
    public String next( int n ) throws JSONParseException {
        if (n == 0) {
            return "";
        }
        else {
            char[] chars = new char[n];

            for( int pos = 0; pos < n; ++pos ) {
                chars[pos] = this.next();
                if ( this.isEndLine() ) {
                    throw this.syntaxError("Error parser json string with substring bounds error.");
                }
            }

            return new String(chars);
        }
    }



    public boolean skipComment( char cCurrentChar ){
        if( cCurrentChar == '/' ){
            char nextC = this.next();
            if( nextC == '*' ){
                while( true ) {
                    char c = this.next();
                    if( c == '*' ){
                        c = this.next();

                        while ( c == '*' ) {
                            c = this.next();
                        }

                        if( c == '/' ){
                            return true;
                        }
                    }
                }
            }
            else if( nextC == '/' ){
                while( true ) {
                    char c = this.next();
                    if( c == '\n' ){
                        this.lineBack();
                        return true;
                    }
                    else if( c == '\r' ){
                        c = this.next();
                        if( c == '\n' ){
                            this.lineBack();
                            return true;
                        }
                        this.back();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public char nextClean() throws JSONParseException {
        char c;
        do {
            c = this.next();
            if ( this.skipComment( c ) ){
                c = this.next();
            }
        }
        while( c != 0 && c <= ' ' );

        return c;
    }

    public StringBuilder nextString( char quote ) throws JSONParseException {
        StringBuilder sb = new StringBuilder();

        while( true ) {
            char c = this.next();
            if( this.isEndLine() ) {
                return sb;
            }

            switch(c) {
                case '\u0000': {
                    sb.append( '\0' );
                    continue;
                }
                case '\n': {
                    sb.append( '\n' );
                    continue;
                }
                case '\r': {
                    sb.append( '\r' );
                    continue;
                    //throw this.syntaxError("Error parser json string with unterminated string."); //What fucking ever, who care.
                }
                case '\\': {
                    c = this.next();
                    if( GeneralStrings.transferCharParse( c, this, sb ) ){
                        continue;
                    }
                }
                default: {
                    if ( c == quote ) {
                        return sb;
                    }

                    sb.append(c);
                }
            }
        }
    }

    public String nextTo( char delimiter ) throws JSONParseException {
        StringBuffer sb = new StringBuffer();

        while(true) {
            char c = this.next();
            if (c == delimiter || c == 0 || c == '\n' || c == '\r') {
                if (c != 0) {
                    this.back();
                }

                return sb.toString().trim();
            }

            sb.append(c);
        }
    }

    public String nextTo( String delimiters ) throws JSONParseException {
        StringBuffer sb = new StringBuffer();

        while(true) {
            char c = this.next();
            if (delimiters.indexOf(c) >= 0 || c == 0 || c == '\n' || c == '\r') {
                if (c != 0) {
                    this.back();
                }

                return sb.toString().trim();
            }

            sb.append(c);
        }
    }

    protected StringBuilder eval_next_string( char currentChat ) {
        StringBuilder sb;
        for ( sb = new StringBuilder(); currentChat >= ' ' && ",:]}/\\\"[{;=#&".indexOf(currentChat) < 0; currentChat = this.next() ) {
            sb.append(currentChat);
        }
        return sb;
    }

    protected Object eval_next_string_token( StringBuilder sb, char currentChat ) {
        this.back();
        String string = sb.toString().trim();
        if ( string.isEmpty() ) {
            throw this.syntaxError("Error parser json string missing value.");
        }
        else {
            return JSONUtils.stringToValue( string );
        }
    }

    @Override
    public Object nextValue( Object indexKey, Object parent ) throws JSONParseException {
        char c = this.nextClean();
        switch(c) {
            case '"':
            case '\'': {
                return this.nextString(c).toString();
            }
            case '[': {
                this.back();
                return this.newJSONArray( indexKey, this, parent );
            }
            case '{': {
                this.back();
                return this.newJSONObject( indexKey, this, parent );
            }
            default: {
                StringBuilder sb = this.eval_next_string( c );
                return this.eval_next_string_token(sb, c);
            }
        }
    }

    @Override
    public Object nextValue() throws JSONParseException {
        return this.nextValue( null, null );
    }

    public char skipTo( char to ) throws JSONParseException {
        char c;
        try {
            int startIndex      = this.mnParseAt;
            int startLine       = this.mnLineAt;
            long startCharacter = this.mnCharacter;
            this.mReader.mark(1000000);

            do {
                c = this.next();
                if (c == 0) {
                    this.mReader.reset();
                    this.mnParseAt = startIndex;
                    this.mnCharacter = startCharacter;
                    this.mnLineAt = startLine;
                    return c;
                }
            } while(c != to);
        }
        catch ( IOException e ) {
            throw new JSONParseException( e );
        }

        this.back();
        return c;
    }

    public JSONParseException syntaxError( String message ) {
        return new JSONParseException( message + this.toString(), (int)this.mnParseAt );
    }

    @Override
    public String toString() {
        return " at " + this.mnParseAt + " [character " + this.mnCharacter + " line " + this.mnLineAt + "]";
    }

    public void handleRedirectException( JSONParserRedirectException e ) {

    }



    protected abstract Object newJSONArray( Object indexKey, ArchCursorParser parser, Object parent );

    protected abstract Object newJSONObject( Object indexKey, ArchCursorParser parser, Object parent );
}
