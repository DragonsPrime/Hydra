package com.pinecone.hydra.umct;
import com.pinecone.hydra.umc.msg.UMCMethod;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AddressMapping {
    String name() default "";

    String[] value() default {};

    boolean relative() default true; // Only for methods.

    UMCMethod[] method() default {};
}