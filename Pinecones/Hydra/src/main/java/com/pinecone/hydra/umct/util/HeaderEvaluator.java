package com.pinecone.hydra.umct.util;

import java.util.Collection;
import java.util.List;

import com.pinecone.framework.system.prototype.Pinenut;

public interface HeaderEvaluator extends Pinenut {
    Object eval( Object that, String key );

    default String evalString( Object that, String key ) {
        return this.eval( that, key ).toString();
    }

    default Number evalNumber( Object that, String key ) {
        Object o = this.eval( that, key );
        if( o instanceof Number ) {
            return (Number) o;
        }
        return null;
    }

    Collection<Object > values( Object that );

    Object[] evals( Object that, List<String > keys );


}
