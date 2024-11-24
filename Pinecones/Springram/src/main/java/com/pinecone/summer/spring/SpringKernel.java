package com.pinecone.summer.spring;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.config.JSONConfig;
import com.pinecone.framework.util.config.PatriarchalConfig;
import com.pinecone.summer.spring.util.ConfigUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

import com.pinecone.framework.system.functions.Executor;

@SpringBootApplication
public class SpringKernel implements Pinenut {
    private ConfigurableApplicationContext mContext;
    private SpringApplication              mSpringApplication;
    private Springram                      mSpringram;
    private Class<? >                      mPrimarySources = SpringKernel.class;
    private Executor                       mInitializer;


    void setSpringram( Springram springram ) {
        this.mSpringram = springram;
    }

    public void setPrimarySources( Class<?> primarySources ) {
        this.mPrimarySources = primarySources;
    }

    public void execute( String... args ) {
        this.mSpringApplication = new SpringApplication( this.mPrimarySources );

        PatriarchalConfig jo = this.mSpringram.getConfig();
        if( jo instanceof JSONConfig ) {
            Map<String, Object > confs = ConfigUtils.recursionMapToPropertiesMap( (JSONConfig)jo );
            if( !confs.containsKey( "spring.config.location" ) ){
                confs.put( "spring.config.location", "" );
            }
//            this.mSpringApplication.setDefaultProperties(Map.of(
//                    //"spring.config.location", "./system/setup/application.yaml"
//                    "spring.config.location", "",
//                    "server.port", "3912"
//            ));

            this.mSpringApplication.setDefaultProperties( confs );
        } // Otherwise, using default config `application.yaml`.

        if( this.mInitializer != null ) {
            try{
                this.mInitializer.execute();
            }
            catch ( Exception ignore ) {
                // Ignore
            }
        }
        this.mContext = this.mSpringApplication.run( args );
    }

    public void terminate() {
        if ( this.mContext != null ) {
            this.mContext.close();
        }
    }

    public ConfigurableApplicationContext getContext() {
        return this.mContext;
    }

    public SpringApplication getSpringApplication() {
        return this.mSpringApplication;
    }

    public void setInitializer( Executor initializer ) {
        this.mInitializer = initializer;
    }
}

