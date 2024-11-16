package com.pinecone.hydra.umct.lets;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MsgService {
    String value() default "";
}