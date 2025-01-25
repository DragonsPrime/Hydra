package com.pinecone.framework.util.json;

import com.pinecone.framework.system.prototype.Pinenut;

public interface JSONString extends Pinenut {
    String toJSONString();

    static JSONString wrapRaw( String that ) {
        return new JSONString() {
            @Override
            public String toJSONString() {
                return that;
            }

            @Override
            public String toString() {
                return that;
            }
        };
    }
}