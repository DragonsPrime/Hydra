package com.pinecone.hydra.storage.file.direct;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.name.path.PathResolver;
import com.pinecone.hydra.storage.file.FileSystemConfig;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.cache.DefaultCacheConstants;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.ExternalSymbolic;
import com.pinecone.hydra.storage.file.source.ExternalSymbolicManipulator;
import com.pinecone.hydra.storage.file.source.FileManipulator;
import com.pinecone.hydra.storage.file.source.FileMasterManipulator;
import com.pinecone.hydra.storage.file.source.FolderManipulator;
import com.pinecone.hydra.storage.io.TitanFileChannelChanface;
import com.pinecone.hydra.storage.io.TitanInputStreamChanface;
import com.pinecone.hydra.system.identifier.KOPathResolver;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;
import com.pinecone.hydra.system.ko.kom.PathSelector;
import com.pinecone.hydra.unit.imperium.ImperialTree;
import com.pinecone.hydra.unit.imperium.source.TreeMasterManipulator;
import com.pinecone.ulf.util.guid.GUIDs;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class KenDirectFileSystemAccess implements DirectFileSystemAccess {
    protected KOMFileSystem                 fileSystem;

    protected PathResolver                  pathResolver;

    protected PathSelector                  pathSelector;

    protected FileMasterManipulator         fileMasterManipulator;

    protected FolderManipulator             folderManipulator;

    protected FileManipulator               fileManipulator;

    protected ExternalSymbolicManipulator   externalSymbolicManipulator;

    protected ImperialTree                  imperialTree;


    public KenDirectFileSystemAccess(KOMFileSystem fileSystem){
        this.fileSystem                     = fileSystem;
        this.pathResolver                   = new KOPathResolver( fileSystem.getConfig() );
        this.fileMasterManipulator          = this.fileSystem.getFileMasterManipulator();
        this.fileManipulator                = this.fileMasterManipulator.getFileManipulator();
        this.folderManipulator              = this.fileMasterManipulator.getFolderManipulator();
        this.externalSymbolicManipulator    = this.fileMasterManipulator.getExternalSymbolicManipulator();;
        this.imperialTree                   = fileSystem.getMasterTrieTree();

        this.pathSelector = new KenExternalSymbolicSelector(
                this.pathResolver, this.fileSystem.getMasterTrieTree(),this.folderManipulator, new GUIDNameManipulator[] { this.fileManipulator },
                this.externalSymbolicManipulator
        );
    }
    @Override
    public void remove(String path) {

    }

    @Override
    public ElementNode queryElement(String path) {
        GUID guid = this.queryGUIDByPath(path);
        if(guid == null){
            return null;
        }
        ExternalSymbolic externalSymbolic = this.externalSymbolicManipulator.getSymbolicByGuid(guid);
        Debug.trace(externalSymbolic);
        return null;
    }

    @Override
    public void insertExternalSymbolic(ExternalSymbolic externalSymbolic) {
        this.externalSymbolicManipulator.insert( externalSymbolic );
    }

    private GUID queryGUIDByPath(String path ) {
        return this.queryGUIDByNS( path, null, null );
    }

    private GUID queryGUIDByNS( String path, String szBadSep, String szTargetSep ) {
        if( szTargetSep != null ) {
            path = path.replace( szBadSep, szTargetSep );
        }

        String[] parts = this.pathResolver.segmentPathParts( path );
        List<String > resolvedParts = this.pathResolver.resolvePath( parts );
        path = this.pathResolver.assemblePath( resolvedParts );

        GUID guid = this.imperialTree.queryGUIDByPath( path );
        if ( guid != null ){
            return guid;
        }


        guid = this.pathSelector.searchGUID( resolvedParts );
        if( guid != null ){
            this.imperialTree.insertCachePath( guid, path );
        }
        return guid;
    }
}
