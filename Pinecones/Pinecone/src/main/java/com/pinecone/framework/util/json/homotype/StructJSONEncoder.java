package com.pinecone.framework.util.json.homotype;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import com.pinecone.framework.system.prototype.Pinenut;

public interface StructJSONEncoder extends Pinenut {
    StructJSONEncoder BasicEncoder = new GenericStructJSONEncoder();

    String encode( Object struct );

    String encode( Object struct, Set<String > exceptedKeys, boolean bAllFields );

    default String encode( Object struct, Set<String > exceptedKeys ) {
        return this.encode( struct, exceptedKeys, false );
    }

    default String encode( Object struct, boolean bAllFields ) {
        return this.encode( struct, null, bAllFields );
    }

    void encode( Object struct, Writer writer, int nIndentFactor ) throws IOException;

    default void encode( Object struct, Writer writer ) throws IOException {
        this.encode( struct, writer, 0 );
    }

    void valueJsonify( Object val, Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException;

    String valueJsonify( Object val );
}

