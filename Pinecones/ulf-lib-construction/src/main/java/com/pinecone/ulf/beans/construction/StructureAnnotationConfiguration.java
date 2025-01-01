package com.pinecone.ulf.beans.construction;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StructureAnnotationConfiguration {
    @Bean
    public StructureAnnotationProcessor structureAnnotationProcessor( ConfigurableListableBeanFactory beanFactory ) {
        return new StructureAnnotationProcessor( beanFactory );
    }
}
