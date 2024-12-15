package com.wolf.construction;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pinecone.framework.util.Debug;

@Component
@Scope("singleton")
public class HuskyService {
    @Resource
    private FoxBlade foxBlade;

    public void run() {
        Debug.trace( "Husky is running!" );
    }

    public void tryFoxBlade() {
        this.foxBlade.trying();
    }
}
