package com.sauron.radium.system;

import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.json.homotype.JSONGet;
import com.pinecone.framework.util.lang.DynamicFactory;
import com.pinecone.framework.util.lang.GenericDynamicFactory;
import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.system.ArchSystemCascadeComponent;
import com.pinecone.hydra.system.HyComponent;
import com.pinecone.framework.util.config.JSONConfig;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.template.TemplateParser;
import com.pinecone.hydra.system.Hydrarum;
import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FilesCache;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.FileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;


public class StorageSystem extends ArchSystemCascadeComponent implements Saunut, HyComponent {
    protected JSONObject                         mjoProtoConfig;

    @JSONGet( "PathScope.Reinterpret" )
    protected JSONObject                         mjoToReinterpret;

    protected JSONObject                         mjoReinterpretedScope;

    @JSONGet( "Protocols" )
    protected JSONObject                         mProtocols;

    protected DynamicFactory                     mFSProvidesFactory;

    @JSONGet( "CacheStrategy" )
    protected String                             mszCacheStrategy;

    @JSONGet( "FilesCache" )
    protected String                             mszFilesCache;

    private final StandardFileSystemManager      mFileSystemManager;

    protected Logger                             mLogger;

    public StorageSystem( Namespace name, Hydrarum system, HyComponent parent ) {
        super( name, system, system.getComponentManager(), parent );
        this.mFileSystemManager = new StandardFileSystemManager();
        if( system instanceof RadiumSystem ) {
            this.mLogger            = ((RadiumSystem) system).getTracerScope().newLogger( this.className() );
        }
        else {
            this.mLogger            = LoggerFactory.getLogger( this.className() + "Logger" );
        }


        this.loadConfig();
        this.reinterpret();
        this.prepareFileSystem();
    }

    public StorageSystem( Hydrarum system, HyComponent parent ) {
        this( null, system, parent );
    }

    public StorageSystem( Hydrarum system ) {
        this( system, null );
    }

    protected void loadConfig() {
        JSONConfig sys    = (JSONConfig) this.getSystem().getSystemConfig();
        Object jPathScope = sys.opt( this.className() );

        if( jPathScope instanceof String ) {
            try {
                this.mjoProtoConfig = sys.fromFile( this.getSystem().getWorkingPath().resolve( (String) jPathScope ).toFile() );
            }
            catch ( IOException e ) {
                this.getSystem().handleKillException( e );
            }
        }
        else {
            this.mjoProtoConfig = (JSONObject) jPathScope;
        }

        sys.put( this.className(), this.mjoProtoConfig );
        this.getSystem().getPrimaryConfigScope().autoInject( StorageSystem.class, this.mjoProtoConfig, this );
    }

    protected void reinterpret() {
        this.mjoReinterpretedScope = (JSONObject) this.getSystem().getGlobalConfigScope().thisScope();

        for ( Map.Entry<String,Object > kv: this.mjoToReinterpret.entrySet() ) {
            if( this.mjoReinterpretedScope.hasOwnProperty( kv.getKey() ) ) {
                throw new IllegalArgumentException( "Illegal system config, duplicated config key." );
            }

            if( kv.getValue() instanceof String ) {
                String szRaw = (String) kv.getValue();
                TemplateParser parser = new TemplateParser( szRaw, this.mjoReinterpretedScope );
                this.mjoReinterpretedScope.put( kv.getKey(), parser.eval() );
            }
            else {
                throw new IllegalArgumentException( "Illegal system config, reinterpret key can not be object." );
            }
        }
    }

    protected void prepareFileSystemProvides() {
        for( Object o : this.mProtocols.entrySet() ) {
            Map.Entry kv = (Map.Entry) o;

            JSONObject info  = (JSONObject) kv.getValue();
            String szProvide = info.optString( "Provide" );

            boolean bDone = true;
            if( !StringUtils.isEmpty( szProvide ) ) { // Empty for defaults, e.g. `file:///`
                Object provide = this.mFSProvidesFactory.optLoadInstance( szProvide, null, null );
                if( provide instanceof FileProvider ) {
                    try{
                        this.mFileSystemManager.addProvider( kv.getKey().toString(), (FileProvider)provide );
                    }
                    catch ( FileSystemException e ) {
                        this.mLogger.warn( "[AddFileSystemProviderCompromised] [FileSystemException] <What ->" + e.getMessage() + ">" );
                        bDone = false;
                    }
                }
                else {
                    this.mLogger.warn( "[BadAddFileSystemProvider] [Illegal provider or null] <" + kv.getKey() + "::`" + szProvide + "`>" );
                    bDone = false;
                }
            }

            if( bDone ) {
                szProvide = StringUtils.isEmpty( szProvide ) ? "Default" : szProvide;
                this.mLogger.info( "[AddFileSystemProvider] (" + kv.getKey() + "::`" + szProvide + "`) <Done>" );
            }
        }
    }

    protected void prepareFileSystemCache() {
        CacheStrategy strategy = StringUtils.isEmpty( this.mszCacheStrategy ) ? CacheStrategy.ON_CALL : CacheStrategy.valueOf( this.mszCacheStrategy );
        try{
            this.mFileSystemManager.setCacheStrategy( strategy );
        }
        catch ( FileSystemException e ) {
            this.mLogger.warn( "[SetCacheStrategy] [Compromised] <What ->" + e.getMessage() + ">" );
        }

        if( !StringUtils.isEmpty( this.mszFilesCache ) ) {
            Object cache = this.mFSProvidesFactory.optLoadInstance( this.mszFilesCache, null, null );
            if( cache instanceof FilesCache ) {
                try{
                    this.mFileSystemManager.setFilesCache( (FilesCache) cache );
                }
                catch ( FileSystemException e ) {
                    this.mLogger.warn( "[SetFilesCacheCompromised] [FileSystemException] <What ->" + e.getMessage() + ">" );
                }
            }
            else {
                this.mLogger.warn( "[SetFilesCacheCompromised] [Illegal FilesCache or null] <`" + this.mszFilesCache + "`>" );
            }
        }
    }

    protected void prepareFileSystem() {
        this.mFSProvidesFactory = new GenericDynamicFactory (
                this.getSystem().getTaskManager().getClassLoader()
        );

        this.prepareFileSystemProvides();
        this.prepareFileSystemCache();
    }

    public JSONObject getProtoConfig() {
        return this.mjoProtoConfig;
    }

    public JSONObject getReinterpretedScope() {
        return this.mjoReinterpretedScope;
    }

    public JSONObject getToReinterpret() {
        return this.mjoToReinterpret;
    }

    @Override
    public RadiumSystem getSystem() {
        return ( RadiumSystem ) super.getSystem();
    }

    public StandardFileSystemManager getFileSystemManager() {
        return this.mFileSystemManager;
    }

}
