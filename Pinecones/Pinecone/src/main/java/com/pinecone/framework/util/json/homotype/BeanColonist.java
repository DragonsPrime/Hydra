package com.pinecone.framework.util.json.homotype;

import java.util.Set;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;

public interface BeanColonist extends Pinenut {
    BeanColonist WrappedColonist = new WrappedBeanColonist();
    BeanColonist DirectColonist  = new DirectBeanColonist();

    void populate( Object bean, JSONObject target, boolean bRecursive ) ;

    default void populate( Object bean, JSONObject target ) {
        this.populate( bean, target, true );
    }

    default JSONObject populate( Object bean, boolean bRecursive ) {
        JSONObject jo = new JSONMaptron();
        this.populate( bean, jo, bRecursive );
        return jo;
    }

    default JSONObject populate( Object bean ) {
        return this.populate( bean, true );
    }


    default void populate( Object bean, JSONObject target, boolean bRecursive, Set<String > exceptedKeys ) {
        this.populate( bean, target, bRecursive );

        target.removeAll( exceptedKeys );
    }

    default void populate( Object bean, JSONObject target, Set<String > exceptedKeys ) {
        this.populate( bean, target, true, exceptedKeys );
    }

    default JSONObject populate( Object bean, boolean bRecursive, Set<String > exceptedKeys ) {
        JSONObject jo = new JSONMaptron();
        this.populate( bean, jo, bRecursive, exceptedKeys );
        return jo;
    }

    default JSONObject populate( Object bean, Set<String > exceptedKeys ) {
        return this.populate( bean, true, exceptedKeys );
    }
}
