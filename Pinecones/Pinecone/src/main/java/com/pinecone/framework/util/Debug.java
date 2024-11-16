package com.pinecone.framework.util;

import com.pinecone.framework.system.InstantKillError;
import com.pinecone.framework.util.io.Tracer;
import com.pinecone.framework.util.io.Tracerson;
import com.pinecone.framework.util.json.JSON;

public class Debug {
    private final static Tracer console = new Tracerson();

    public static Tracer console() {
        return Debug.console;
    }

    public static Tracer probe(){
        System.err.println("\n\rFuck is here !\n\r");
        return Debug.console;
    }

    public static Tracer fmt( int nIndentFactor, Object Anything, Object...objects ){
        Debug.console.getOut().print( JSON.stringify( Anything, nIndentFactor ) );
        for ( Object row : objects ) {
            Debug.console.getOut().print( JSON.stringify( row, nIndentFactor ) );
        }
        return Debug.console;
    }

    public static Tracer fmp( int nIndentFactor, Object Anything, Object...objects ){
        Debug.console.getOut().print( JSON.stringify( JSON.parse( JSON.stringify( Anything ) ), nIndentFactor ) );
        for ( Object row : objects ) {
            Debug.console.getOut().print( JSON.stringify( JSON.parse( JSON.stringify( row ) ), nIndentFactor ) );
        }
        return Debug.console;
    }

    public static Tracer trace( Object Anything, Object...objects ){
        return Debug.console.log( Anything, objects );
    }

    public synchronized static Tracer traceSyn( Object Anything, Object...objects ){
        return Debug.console.log( Anything, objects );
    }

    public static Tracer info ( Object Anything, Object...objects ){
        return Debug.console.info( Anything, objects );
    }

    public synchronized static Tracer infoSyn( Object Anything, Object...objects ){
        return Debug.console.info( Anything, objects );
    }

    public static Tracer warn ( Object Anything, Object...objects ){
        return Debug.console.warn( Anything, objects );
    }

    public synchronized static Tracer warnSyn( Object Anything, Object...objects ){
        return Debug.console.warn( Anything, objects );
    }

    public static Tracer colorf( int colorCode, Object Anything, Object...objects ){
        return Debug.console.colorf( colorCode, Anything, objects );
    }

    public static Tracer purplef( Object Anything, Object...objects ){
        return Debug.console.colorf( 35, Anything, objects );
    }

    public synchronized static Tracer purplefs( Object Anything, Object...objects ){
        return Debug.purplef( Anything, objects );
    }

    public static Tracer greenf( Object Anything, Object...objects ){
        return Debug.console.colorf( 32, Anything, objects );
    }

    public synchronized static Tracer greenfs( Object Anything, Object...objects ){
        return Debug.greenf( Anything, objects );
    }

    public static Tracer whitef( Object Anything, Object...objects ){
        return Debug.console.colorf( 30, Anything, objects );
    }

    public synchronized static Tracer whitefs( Object Anything, Object...objects ){
        return Debug.whitef( Anything, objects );
    }


    public static Tracer hhf(){
        Debug.console.getOut().println();
        return Debug.console;
    }


    public static Tracer echo( Object data, Object...objects ) {
        return Debug.console.echo( data, objects );
    }

    public static Tracer cerr( Object data, Object...objects ) {
        return Debug.console.cerr( data, objects );
    }

    public synchronized static Tracer cerrSyn( Object data, Object...objects ) {
        return Debug.console.cerr( data, objects );
    }


    public static void sleep( long millis ) {
        try {
            Thread.sleep( millis );
        }
        catch ( InterruptedException e ) {
            Debug.cerr( e.getMessage() );
        }
    }

    public static void stop() {
        throw new InstantKillError( "Invoked at [ Debug::stop() ]" );
    }

    public static void exit() {
        System.exit( -666 );
    }

}
