package com.pinecone.hydra.umct.stereotype;

import java.lang.reflect.Method;

public final class IfaceUtils {
    public static String getIfaceMethodName( Method method ){
        String ifaceName = method.getName();

        Iface annotation = method.getAnnotation(Iface.class);
        if ( annotation != null && !annotation.name().isEmpty() ) {
            ifaceName = annotation.name();
        }

        return ifaceName;
    }
}
