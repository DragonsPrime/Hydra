package com.pinecone.ulf.beans.construction;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.pinecone.framework.system.construction.ReuseCycle;
import com.pinecone.framework.system.construction.Structure;
import com.pinecone.framework.util.ReflectionUtils;

public class StructureAnnotationProcessor implements InstantiationAwareBeanPostProcessor {

    private final ConfigurableListableBeanFactory beanFactory;

    public StructureAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();

        ReflectionUtils.doWithFields(clazz, field -> {
            if ( field.isAnnotationPresent(Structure.class) ) {
                handleStructureField(field, bean);
            }
        });

        ReflectionUtils.doWithMethods(clazz, method -> {
            if ( method.isAnnotationPresent(Structure.class) ) {
                handleStructureMethod(method, bean);
            }
        });

        return true;
    }

    private void handleStructureField(Field field, Object bean) throws IllegalAccessException {
        Structure structure = field.getAnnotation(Structure.class);
        Object dependency = resolveDependency(structure, field.getType());
        field.setAccessible(true);
        field.set(bean, dependency);
    }

    private void handleStructureMethod(Method method, Object bean) {
        Structure structure = method.getAnnotation(Structure.class);
        Object dependency = resolveDependency(structure, method.getParameterTypes()[0]);
        ReflectionUtils.invokeMethod(method, bean, dependency);
    }

    private Object resolveDependency(Structure structure, Class<?> type) {
        String beanName = structure.name();
        Object dependency;

        if ( !beanName.isEmpty() ) {
            dependency = this.beanFactory.getBean(beanName);
        }
        else if ( structure.cycle() == ReuseCycle.Singleton || structure.cycle() == ReuseCycle.PreSingleton ) {
            dependency = this.beanFactory.getBean(type);
        }
        else if ( structure.cycle() == ReuseCycle.Disposable || structure.cycle() == ReuseCycle.Recyclable ) {
            dependency = this.beanFactory.createBean(type);
        }
        else {
            throw new UnsupportedOperationException( "Unsupported reuse cycle: " + structure.cycle() );
        }

        return dependency;
    }
}