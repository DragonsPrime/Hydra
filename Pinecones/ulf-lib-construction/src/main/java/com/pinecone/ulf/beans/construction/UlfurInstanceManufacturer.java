package com.pinecone.ulf.beans.construction;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import com.pinecone.framework.system.construction.InstanceManufacturer;
import com.pinecone.framework.util.Assert;

public class UlfurInstanceManufacturer implements UlfInstanceManufacturer {
    private final AnnotationConfigApplicationContext context;

    public UlfurInstanceManufacturer() {
        this.context = new AnnotationConfigApplicationContext();
    }

    public UlfurInstanceManufacturer( Class<?>... componentClasses ) {
        this.context = new AnnotationConfigApplicationContext();

        for( Class<?> cc : componentClasses ) {
            this.onlyRegister( cc );
        }
    }


    @Override
    public InstanceManufacturer registerInstancing( Class<?> type, Object instance ) {
        ConfigurableListableBeanFactory beanFactory = this.context.getBeanFactory();
        beanFactory.registerSingleton( type.getName(), this.allotInstance( type ) );
        this.context.refresh();
        return this;
    }

    public void onlyRegister( Class<?> type ) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) this.context.getBeanFactory();

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass( type );

        Scope scope = type.getAnnotation( Scope.class );
        if ( scope != null ) {
            beanDefinition.setScope( scope.value() );
        }
        else {
            beanDefinition.setScope( BeanDefinition.SCOPE_SINGLETON );
        }

        beanFactory.registerBeanDefinition( type.getName(), beanDefinition );
    }

    @Override
    public InstanceManufacturer register( Class<?> type ) {
        this.onlyRegister( type );
        this.refresh();

        return this;
    }

    @Override
    public InstanceManufacturer registers( List<Class<?>> types ) {
        for ( Class<?> type : types ) {
            this.onlyRegister( type );
        }
        this.refresh();

        return this;
    }

    @Override
    public boolean hasRegistered( Class<?> type ) {
        return this.context.containsBeanDefinition(type.getName()) || this.context.containsBean(type.getName());
    }

    @Override
    public List<Class<?>> fetchRegistered() {
        List<Class<? > > registeredClasses = new ArrayList<>();
        String[] beanNames = this.context.getBeanFactory().getBeanDefinitionNames();
        for ( String beanName : beanNames ) {
            BeanDefinition beanDefinition = this.context.getBeanFactory().getBeanDefinition(beanName);
            try {
                Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
                registeredClasses.add(beanClass);
            }
            catch ( ClassNotFoundException e ) {
                Assert.provokeIrrationally( e );
            }
        }
        return registeredClasses;
    }

    @Override
    public String[] fetchRegisteredNames() {
        return this.context.getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public void free( Object instance ) {
        String[] beanNames = this.context.getBeanNamesForType( instance.getClass() );
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) this.context.getBeanFactory();

        for ( String beanName : beanNames ) {
            beanFactory.destroySingleton( beanName );
        }
    }

    @Override
    public void free( Class<?> type, Object instance ) {
        String beanName = type.getName();
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) this.context.getBeanFactory();

        if ( beanFactory.containsSingleton(beanName) ) {
            beanFactory.destroySingleton(beanName);
        }
    }


    @Override
    public Object autowire( Object that ) {
        this.context.getAutowireCapableBeanFactory().autowireBean( that );
        return that;
    }

    @Override
    public Object allotInstance( String type ) {
        return this.context.getBean( type );
    }

    @Override
    public <T> T allotInstance( Class<T> type ) {
        return this.context.getBean( type );
    }

    @Override
    public void close() {
        this.context.close();
    }

    @Override
    public void refresh() {
        this.context.refresh();
    }

    @Override
    public AnnotationConfigApplicationContext getApplicationContext() {
        return this.context;
    }
}
