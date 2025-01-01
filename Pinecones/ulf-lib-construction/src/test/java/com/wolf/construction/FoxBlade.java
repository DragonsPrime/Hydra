package com.wolf.construction;

import javax.annotation.Resource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pinecone.framework.util.Debug;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
//@Scope(BeanDefinition.SCOPE_SINGLETON)
public class FoxBlade {
    @Resource
    private FoxService foxService;

    public void attack() {
        Debug.bluef( this.foxService.getName() + " the fox-paladin who is using fox-blade(" + this.hashCode() + ") to attack." );
    }

    public void trying() {
        Debug.redf( "This fox-blade(" + this.hashCode() + ") is for fox only." );
    }
}
