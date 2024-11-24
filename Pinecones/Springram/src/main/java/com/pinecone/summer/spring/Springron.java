package com.pinecone.summer.spring;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.system.functions.Executor;
import com.pinecone.hydra.servgram.ArchServgramium;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeoutException;

public class Springron extends ArchServgramium implements Springram {
    protected String[]                 mSpringbootArgs;
    protected Thread                   mSpringPrimaryThread;
    protected SpringKernel             mSpringKernel;

    public Springron( String szName, Processum parent, String[] springbootArgs ) {
        super( szName, parent );
        this.mSpringbootArgs      = springbootArgs;
        this.mSpringKernel        = new SpringKernel();
        this.mSpringKernel.setSpringram( this );

        this.mSpringPrimaryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Springron.this.infoLifecycle( "VitalizingSubsystem", "Start" );
                Springron.this.mSpringKernel.execute( Springron.this.mSpringbootArgs );
                Springron.this.infoLifecycle( "VitalizingSubsystem", "Subsystem readied" );
                while ( Springron.this.mSpringKernel.getContext().isActive() ) {
                    try {
                        Thread.sleep( 100 );
                    }
                    catch ( InterruptedException e ) {
                        Springron.this.mSpringKernel.terminate();
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                Springron.this.infoLifecycle( "SubsystemTermination", "Subsystem terminated" );
            }
        });

        this.mSpringPrimaryThread.setName( this.getName() + "Primary" + this.mSpringPrimaryThread.getName() );
        this.setThreadAffinity( this.mSpringPrimaryThread );
    }

    public Springron( String szName, Processum parent ) {
        this( szName, parent, new String[0] );
    }

    @Override
    public Springram setPrimarySources( Class<?> primarySources ) {
        this.mSpringKernel.setPrimarySources( primarySources );
        return this;
    }

    @Override
    public void join() throws InterruptedException {
        this.mSpringPrimaryThread.join();
    }

    @Override
    public void join( long millis ) throws InterruptedException {
        this.mSpringPrimaryThread.join( millis );
    }

    @Override
    public void execute() throws Exception {
        this.mSpringPrimaryThread.start();
    }

    @Override
    public ConfigurableApplicationContext getContext() {
        return this.mSpringKernel.getContext();
    }

    @Override
    public SpringApplication getSpringApplication() {
        return this.mSpringKernel.getSpringApplication();
    }

    @Override
    public void setInitializer( Executor initializer ) {
        this.mSpringKernel.setInitializer(initializer);
    }

    @Override
    public void terminate() {
        this.mSpringKernel.terminate();

        long nStart = System.currentTimeMillis();
        try{
            while ( this.mSpringKernel.getContext().isActive() ){
                Thread.sleep( 50 );
                if( System.currentTimeMillis() - nStart > 5000 ) {
                    throw new TimeoutException( "Terminating springboot timeout." );
                }
            }

            this.mSpringPrimaryThread.join();
        }
        catch ( InterruptedException e ) {
            Thread.currentThread().interrupt();
        }
        catch ( TimeoutException e1 ) {
            throw new ProxyProvokeHandleException( e1 );
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public void apoptosis() {
        this.terminate();
    }

    @Override
    public void kill() {
        this.terminate();
        super.kill();
    }
}
