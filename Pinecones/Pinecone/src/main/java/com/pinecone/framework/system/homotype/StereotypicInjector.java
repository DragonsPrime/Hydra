package com.pinecone.framework.system.homotype;

public interface StereotypicInjector extends Injector {
    Class<?> getStereotype();

    void     setStereotype( Class<?> stereotype );
}
