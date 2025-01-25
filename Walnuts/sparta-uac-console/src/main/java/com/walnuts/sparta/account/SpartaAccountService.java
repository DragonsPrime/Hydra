package com.walnuts.sparta.account;

import com.pinecone.framework.system.executum.Processum;
import com.pinecone.hydra.servgram.Servgram;
import com.pinecone.hydra.system.component.Slf4jTraceable;
import com.pinecone.summer.spring.Springron;

import java.io.IOException;
import java.nio.file.Path;

public class SpartaAccountService extends Springron implements Slf4jTraceable {
    public SpartaAccountService( String szName, Processum parent, String[] springbootArgs ) {
        super( szName, parent, springbootArgs );
        this.mSpringKernel.setPrimarySources( SpartaBoot.class );
    }

    public SpartaAccountService( String szName, Processum parent ) {
        this( szName, parent, new String[0] );
    }

    @Override
    protected void loadConfig() {
        this.mServgramList     = this.getAttachedOrchestrator().getSectionConfig().getChild( Servgram.ConfigServgramsKey );
        Object dyServgramConf  = this.mServgramList.get( this.gramName() );
        if( dyServgramConf instanceof String ) {
            try{
                this.mServgramConf = this.mServgramList.getChildFromPath( Path.of((String) dyServgramConf) );
            }
            catch ( IOException ignore ) {
                this.getLogger().info( "[Notice] Spring will use the default config `application.yaml`." );
            }
        }
        else {
            this.mServgramConf = this.mServgramList.getChild( this.gramName() );
        }
    }
}
