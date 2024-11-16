package com.pinecone.framework.util.io;

import com.pinecone.framework.system.prototype.PinenutTraits;
import com.pinecone.framework.util.json.JSON;

import java.io.PrintStream;

public class Tracerson implements Tracer {
    public static final String COLOR_STRING_UTF_END = "\u001B[0m";

    protected String mszInfoColor    = "\u001B[34m";

    protected String mszWarnColor    = "\u001B[33m";

    protected String mszTraceColor   = "\u001B[36m";

    protected String mszElementSpilt = " ";

    protected PrintStream out = System.out;

    protected PrintStream err = System.err;


    public Tracerson() {

    }

    public Tracerson( PrintStream out, PrintStream err ) {
        this.out = out;
        this.err = err;
    }

    protected String stringify( Object data ){
        try {
            return PinenutTraits.invokeToJSONString( data );
        }
        catch ( Exception e1 ){
            return JSON.stringify( data );
        }
    }

    @Override
    public PrintStream getOut() {
        return this.out;
    }

    @Override
    public PrintStream getErr() {
        return this.err;
    }


    @Override
    public Tracerson echo( Object data, Object...objects ) {
        this.out.print( data );
        for ( Object row : objects ) {
            this.out.print( row );
        }
        return this;
    }

    @Override
    public Tracerson cerr( Object data, Object...objects ) {
        this.err.print( data );
        for ( Object row : objects ) {
            this.err.print( row );
        }
        return this;
    }



    protected void printlnColorfulEnd() {
        if( this.out.equals( System.out ) ) {
            this.out.println( Tracerson.COLOR_STRING_UTF_END );
        }
        else {
            this.out.println();
        }
    }

    protected void printlnStringify( Object Anything, Object...objects ) {
        this.out.print( this.stringify( Anything ) );
        for ( Object row : objects ) {
            this.out.print( this.mszElementSpilt );
            this.out.print( this.stringify( row ) );
        }
    }

    protected void printlnColorful( String szColor, Object Anything, Object...objects ) {
        this.out.print( szColor );
        this.printlnStringify( Anything, objects );
        this.printlnColorfulEnd();
    }


    @Override
    public Tracerson log( Object that ){
        this.out.println( this.stringify( that ) );
        return this;
    }

    @Override
    public Tracerson log( Object Anything, Object...objects ){
        this.printlnStringify( Anything, objects );
        this.out.println();
        return this;
    }


    protected String queryInfoColor(){
        if( this.out.equals( System.out ) ) {
            return this.mszInfoColor;
        }
        else {
            return "[INFO] ";
        }
    }

    @Override
    public Tracerson info( Object that ){
        this.out.print( this.queryInfoColor() );
        this.out.print( this.stringify( that ) );
        this.printlnColorfulEnd();
        return this;
    }

    @Override
    public Tracerson info( Object Anything, Object...objects ){
        this.printlnColorful( this.queryInfoColor(), Anything, objects );
        return this;
    }


    protected String queryWarnColor(){
        if( this.out.equals( System.out ) ) {
            return this.mszWarnColor;
        }
        else {
            return "[WARN] ";
        }
    }

    @Override
    public Tracerson warn ( Object that ){
        this.out.print( this.queryWarnColor() );
        this.out.print( this.stringify( that ) );
        this.printlnColorfulEnd();
        return this;
    }

    @Override
    public Tracerson warn ( Object Anything, Object...objects ){
        this.printlnColorful( this.queryWarnColor(), Anything, objects );
        return this;
    }


    @Override
    public Tracerson error ( Object that ){
        this.err.println( this.stringify( that ) );
        return this;
    }

    @Override
    public Tracerson error ( Object Anything, Object...objects ){
        this.err.print( this.stringify( Anything ) );
        for ( Object row : objects ) {
            this.err.print( this.mszElementSpilt );
            this.err.print( this.stringify( row ) );
        }
        return this;
    }


    protected String queryTraceColor(){
        if( this.out.equals( System.out ) ) {
            return this.mszTraceColor;
        }
        else {
            return "[TRACE] ";
        }
    }

    protected void printTraceInfo( StackTraceElement[] elements ){
        this.out.println( this.getClass().getName() + ": Call Trace Info:");
        if( elements != null ){
            for( int i = 0; i < elements.length; i++ ){
                if( i == 0 && elements[0].getClassName().equals( "java.lang.Thread" ) ){
                    continue;
                }
                this.out.println( "\tat " + elements[i] );
            }
        }
    }

    @Override
    public Tracerson trace() {
        this.out.print( this.queryTraceColor() );
        this.printTraceInfo( Thread.currentThread().getStackTrace() );
        this.printlnColorfulEnd();
        return this;
    }

    @Override
    public Tracerson trace( Object Anything, Object...objects ) {
        this.out.print( this.queryTraceColor() );
        this.log( Anything, objects );
        this.printTraceInfo( Thread.currentThread().getStackTrace() );
        this.printlnColorfulEnd();
        return this;
    }

    @Override
    public Tracer colorf( int colorCode, Object that ){
        this.out.print( "\u001B[" + colorCode + "m" );
        this.out.print( this.stringify( that ) );
        this.printlnColorfulEnd();
        return this;
    }

    @Override
    public Tracer colorf( int colorCode, Object Anything, Object...objects ){
        this.printlnColorful( "\u001B[" + colorCode + "m", Anything, objects );
        return this;
    }

}
