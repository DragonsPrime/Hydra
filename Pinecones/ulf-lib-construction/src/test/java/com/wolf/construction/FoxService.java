package com.wolf.construction;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pinecone.framework.util.Debug;

@Component
@Scope("singleton")
public class FoxService {
    @Resource
    private FoxBlade foxBlade;

    public void digging() {
        Debug.trace( "Fox is digging!" );
    }

    public String getName() {
        return "Donovan";
    }

    public void attack( String target ) {
        Debug.redf( "Preparing attack." );
        this.foxBlade.attack();
        Debug.greenf( "And " + target + " is dead." );
    }
}