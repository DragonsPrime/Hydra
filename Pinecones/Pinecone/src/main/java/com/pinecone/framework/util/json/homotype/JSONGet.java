package com.pinecone.framework.util.json.homotype;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JSONGet {
    String value() default "";
}


