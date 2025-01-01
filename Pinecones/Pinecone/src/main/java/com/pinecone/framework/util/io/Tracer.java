package com.pinecone.framework.util.io;

import com.pinecone.framework.system.prototype.Pinenut;

import java.io.PrintStream;

public interface Tracer extends Pinenut {
    PrintStream getOut();

    PrintStream getErr();



    Tracer echo( Object data, Object...objects ) ;

    Tracer cerr( Object data, Object...objects ) ;


    Tracer log( Object that );

    Tracer log( Object Anything, Object...objects );


    Tracer info( Object that );

    Tracer info( Object Anything, Object...objects );


    Tracer warn ( Object Anything, Object...objects );

    Tracer warn ( Object that );


    Tracer error ( Object Anything, Object...objects );

    Tracer error ( Object that );


    Tracer trace() ;

    Tracer trace( Object Anything, Object...objects ) ;


    Tracer colorf( int colorCode, Object that );

    Tracer colorf( int colorCode, Object Anything, Object...objects );
}
