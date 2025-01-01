package com.pinecone.summer.spring;

import com.pinecone.framework.system.functions.Executor;
import com.pinecone.hydra.servgram.Servgram;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public interface Springram extends Servgram {
    void execute() throws Exception;

    void join() throws InterruptedException;

    void join( long millis ) throws InterruptedException;

    ConfigurableApplicationContext getContext();

    Springram setPrimarySources( Class<?> primarySources ) ;

    SpringApplication getSpringApplication();

    void setInitializer( Executor initializer );
}
