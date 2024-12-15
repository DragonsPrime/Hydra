package com.wolf.construction;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import com.pinecone.framework.util.Debug;

@Aspect
@Component
public class CanisAspect {
    @Before("execution(* com.wolf.construction..*(..))")
    public void beforeMethod() {
        Debug.whitef( "We are canes." );
    }
}
