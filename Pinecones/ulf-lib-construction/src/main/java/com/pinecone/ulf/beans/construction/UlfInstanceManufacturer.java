package com.pinecone.ulf.beans.construction;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.pinecone.framework.system.construction.InstanceManufacturer;

public interface UlfInstanceManufacturer extends InstanceManufacturer {
    AnnotationConfigApplicationContext getApplicationContext();
}
