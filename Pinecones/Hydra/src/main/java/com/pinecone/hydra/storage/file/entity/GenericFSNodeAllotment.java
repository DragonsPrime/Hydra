package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.source.FileMasterManipulator;
import com.pinecone.ulf.util.id.GuidAllocator;

public class GenericFSNodeAllotment implements FSNodeAllotment {
    private FileMasterManipulator       fileMasterManipulator;
    private KOMFileSystem               fileSystem;
    private GuidAllocator               guidAllocator;

    public GenericFSNodeAllotment(FileMasterManipulator fileMasterManipulator, KOMFileSystem fileSystem){
        this.fileMasterManipulator = fileMasterManipulator;
        this.fileSystem = fileSystem;
        this.guidAllocator   = fileSystem.getGuidAllocator();
    }
    @Override
    public Folder newFolder(){
        GenericFolder folder = new GenericFolder(fileSystem, fileMasterManipulator.getFolderManipulator());
        folder.setGuid( guidAllocator.nextGUID72() );
        return folder;
    }



    @Override
    public Folder newFolder(String name) {
        GenericFolder folder = new GenericFolder(fileSystem, fileMasterManipulator.getFolderManipulator());
        folder.setName( name );
        folder.setGuid( guidAllocator.nextGUID72() );
        return folder;
    }

    @Override
    public FileNode newFileNode(){
        GenericFileNode fileNode = new GenericFileNode(fileSystem, fileMasterManipulator.getFileManipulator());
        fileNode.setGuid( guidAllocator.nextGUID72() );
        return fileNode;
    }
    @Override
    public FileNode newFileNode(String name, long definitionSize, boolean crc32Xor, boolean integrityCheckEnable, boolean disableCluster) {
        GenericFileNode fileNode = new GenericFileNode(fileSystem, fileMasterManipulator.getFileManipulator());
        fileNode.setGuid( guidAllocator.nextGUID72() );
        fileNode.setName( name );
        //fileNode.setCrc32Xor( crc32Xor );
        fileNode.setDefinitionSize( definitionSize );
        fileNode.setIntegrityCheckEnable( integrityCheckEnable );
        fileNode.setDisableCluster( disableCluster );
        return fileNode;
    }

    @Override
    public FileNode newFileNode(String name, long definitionSize) {
        GenericFileNode fileNode = new GenericFileNode(fileSystem, fileMasterManipulator.getFileManipulator());
        fileNode.setName( name );
        fileNode.setDefinitionSize( definitionSize );
        fileNode.setGuid( guidAllocator.nextGUID72() );
        return fileNode;
    }

    @Override
    public FileNode newFileNode(String name, boolean crc32Xor, boolean integrityCheckEnable, boolean disableCluster) {
        GenericFileNode fileNode = new GenericFileNode(fileSystem, fileMasterManipulator.getFileManipulator());
        fileNode.setName( name );
        //fileNode.setCrc32Xor( crc32Xor );
        fileNode.setDisableCluster( disableCluster );
        fileNode.setIntegrityCheckEnable( integrityCheckEnable );
        return fileNode;
    }



    @Override
    public LocalFrame newLocalFrame(){
        GenericLocalFrame frame = new GenericLocalFrame(fileMasterManipulator.getLocalFrameManipulator());
        frame.setSegGuid( guidAllocator.nextGUID72() );
        return frame;
    }
    @Override
    public LocalFrame newLocalFrame(GUID fileGuid, int segId, String sourceName, String crc32, long size, long fileStartOffset) {
        GenericLocalFrame frame = new GenericLocalFrame(fileMasterManipulator.getLocalFrameManipulator());
        frame.setSegGuid( guidAllocator.nextGUID72() );
        frame.setSegId( segId );
        frame.setSourceName( sourceName );
        frame.setCrc32( crc32 );
        frame.setSize( size );
        frame.setFileGuid( fileGuid );
        return frame;
    }

    @Override
    public LocalFrame newLocalFrame(GUID fileGuid, int segId, String sourceName) {
        GenericLocalFrame frame = new GenericLocalFrame(fileMasterManipulator.getLocalFrameManipulator());
        frame.setFileGuid( fileGuid );
        frame.setSegId( segId );
        frame.setSourceName( sourceName );
        frame.setSegGuid( guidAllocator.nextGUID72() );
        return frame;
    }



    @Override
    public RemoteFrame newRemoteFrame(){
        GenericRemoteFrame frame = new GenericRemoteFrame(fileMasterManipulator.getRemoteFrameManipulator());
        frame.setSegGuid( guidAllocator.nextGUID72() );
        return frame;
    }
    @Override
    public RemoteFrame newRemoteFrame(GUID fileGuid, int segId, String crc32, long size) {
        GenericRemoteFrame frame = new GenericRemoteFrame(fileMasterManipulator.getRemoteFrameManipulator());
        frame.setSegGuid( guidAllocator.nextGUID72() );
        frame.setFileGuid( fileGuid );
        frame.setSegId( segId );
        frame.setCrc32( crc32 );
        frame.setSize( size );
        return frame;
    }

    @Override
    public RemoteFrame newRemoteFrame(GUID fileGuid, int segId) {
        GenericRemoteFrame frame = new GenericRemoteFrame(fileMasterManipulator.getRemoteFrameManipulator());
        frame.setFileGuid( fileGuid );
        frame.setSegGuid( guidAllocator.nextGUID72() );
        frame.setSegId( segId );
        return frame;
    }

    @Override
    public Symbolic newSymbolic() {
        return new GenericSymbolic(this.fileMasterManipulator.getSymbolicManipulator());
    }

    @Override
    public SymbolicMeta newSymbolicMeta() {
        return new GenericSymbolicMeta(this.fileMasterManipulator.getSymbolicMetaManipulator());
    }


    @Override
    public Strip newStrip() {

        GenericStrip strip = new GenericStrip(this.fileMasterManipulator.getStripManipulator());
        strip.setStripGuid( this.guidAllocator.nextGUID72() );
        return strip;
    }
}
