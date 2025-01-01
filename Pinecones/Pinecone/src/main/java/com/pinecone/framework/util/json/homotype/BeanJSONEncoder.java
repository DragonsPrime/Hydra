package com.pinecone.framework.util.json.homotype;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import com.pinecone.framework.system.prototype.Pinenut;

public interface BeanJSONEncoder extends Pinenut {
    BeanJSONEncoder BasicEncoder = new GenericBeanJSONEncoder();

    String encode( Object bean );

    String encode( Object bean, Set<String > exceptedKeys );

    void encode( Object bean, Writer writer, int nIndentFactor ) throws IOException;

    default void encode( Object bean, Writer writer ) throws IOException {
        this.encode( bean, writer, 0 );
    }

    void valueJsonify( Object val, Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException;

    String valueJsonify( Object val );
}
