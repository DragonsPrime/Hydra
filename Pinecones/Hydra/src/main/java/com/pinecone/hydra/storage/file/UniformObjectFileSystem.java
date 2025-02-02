package com.pinecone.hydra.storage.file;

import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.storage.file.entity.FSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.GenericFSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.FileTreeNode;
import com.pinecone.hydra.storage.file.entity.Folder;
import com.pinecone.hydra.storage.file.entity.Frame;
import com.pinecone.hydra.storage.file.entity.GenericFileNode;
import com.pinecone.hydra.storage.file.entity.GenericFolder;
import com.pinecone.hydra.storage.file.entity.LocalFrame;
import com.pinecone.hydra.storage.file.entity.RemoteFrame;
import com.pinecone.hydra.storage.file.operator.FileSystemOperatorFactory;
import com.pinecone.hydra.storage.file.operator.GenericFileSystemOperatorFactory;
import com.pinecone.hydra.storage.file.source.FileSystemAttributeManipulator;
import com.pinecone.hydra.storage.file.source.FileManipulator;
import com.pinecone.hydra.storage.file.source.FileMasterManipulator;
import com.pinecone.hydra.storage.file.source.FileMetaManipulator;
import com.pinecone.hydra.storage.file.source.FolderManipulator;
import com.pinecone.hydra.storage.file.source.FolderMetaManipulator;
import com.pinecone.hydra.storage.file.source.FolderVolumeMappingManipulator;
import com.pinecone.hydra.storage.file.source.LocalFrameManipulator;
import com.pinecone.hydra.storage.file.source.RemoteFrameManipulator;
import com.pinecone.hydra.storage.file.source.SymbolicManipulator;
import com.pinecone.hydra.storage.file.source.SymbolicMetaManipulator;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.transmit.exporter.FileExportEntity;
import com.pinecone.hydra.storage.file.transmit.receiver.FileReceiveEntity;
import com.pinecone.hydra.system.identifier.KOPathResolver;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.hydra.system.ko.kom.ArchReparseKOMTree;
import com.pinecone.hydra.system.ko.kom.GenericReparseKOMTreeAddition;
import com.pinecone.hydra.system.ko.kom.StandardPathSelector;
import com.pinecone.hydra.unit.imperium.ImperialTreeNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
import com.pinecone.hydra.unit.imperium.operator.TreeNodeOperator;
import com.pinecone.slime.map.indexable.IndexableMapQuerier;
import com.pinecone.ulf.util.guid.GUIDs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 *  Pinecone Ursus For Java UniformObjectFileSystem
 *  Author: Ken, Harold.E (Dragon King)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  *****************************************************************************************
 *  Uniform Object File System (Ken`s OFS / KOFS)
 *  Uniform Distribute Object Storage File System
 *  Supported TB-PB-ZB Level Big Data Storage
 *
 *  *****************************************************************************************
 */
public class UniformObjectFileSystem extends ArchReparseKOMTree implements KOMFileSystem {
    protected FSNodeAllotment                         fsNodeAllotment;

    protected FileSystemAttributeManipulator          fileSystemAttributeManipulator;
    protected FileManipulator                         fileManipulator;
    protected FileMasterManipulator                   fileMasterManipulator;
    protected FileMetaManipulator                     fileMetaManipulator;
    protected FolderManipulator                       folderManipulator;
    protected FolderMetaManipulator                   folderMetaManipulator;
    protected LocalFrameManipulator                   localFrameManipulator;
    protected RemoteFrameManipulator                  remoteFrameManipulator;
    protected SymbolicManipulator                     symbolicManipulator;
    protected SymbolicMetaManipulator                 symbolicMetaManipulator;
    protected FolderVolumeMappingManipulator          folderVolumeMappingManipulator;

    protected IndexableMapQuerier<String, String >    globalPathGuidCacheQuerier;


    public UniformObjectFileSystem( Processum superiorProcess, KOIMasterManipulator masterManipulator, KOMFileSystem parent, String name, IndexableMapQuerier<String, String > globalPathGuidCacheQuerier ){
        // Phase [1] Construct system.
        super( superiorProcess, masterManipulator, KernelFileSystemConfig, parent, name );

        // Phase [2] Construct fundamentals.
        this.fileMasterManipulator         = (FileMasterManipulator) masterManipulator;
        this.pathResolver                  =  new KOPathResolver( this.kernelObjectConfig );
        this.guidAllocator                 =  GUIDs.newGuidAllocator();

        // Phase [3] Construct manipulators.
        this.operatorFactory                 =  new GenericFileSystemOperatorFactory( this, (FileMasterManipulator) masterManipulator );
        this.fileSystemAttributeManipulator  =  this.fileMasterManipulator.getAttributeManipulator();
        this.fileManipulator                 =  this.fileMasterManipulator.getFileManipulator();
        this.fileMetaManipulator             =  this.fileMasterManipulator.getFileMetaManipulator();
        this.folderManipulator               =  this.fileMasterManipulator.getFolderManipulator();
        this.folderMetaManipulator           =  this.fileMasterManipulator.getFolderMetaManipulator();
        this.localFrameManipulator           =  this.fileMasterManipulator.getLocalFrameManipulator();
        this.remoteFrameManipulator          =  this.fileMasterManipulator.getRemoteFrameManipulator();
        this.symbolicManipulator             =  this.fileMasterManipulator.getSymbolicManipulator();
        this.symbolicMetaManipulator         =  this.fileMasterManipulator.getSymbolicMetaManipulator();
        this.folderVolumeMappingManipulator  =  this.fileMasterManipulator.getFolderVolumeRelationManipulator();

        // Phase [4] Construct selectors.
        this.pathSelector                    =  new StandardPathSelector(
                this.pathResolver, this.imperialTree, this.folderManipulator, new GUIDNameManipulator[] { this.fileManipulator }
        );
        // Warning: ReparseKOMTreeAddition must be constructed only after `pathSelector` has been constructed.
        this.mReparseKOM                     =  new GenericReparseKOMTreeAddition( this );

        // Phase [5] Construct misc.
//        this.propertyTypeConverter         =  new DefaultPropertyConverter();
//        this.textValueTypeConverter        =  new DefaultTextValueConverter();
        this.fsNodeAllotment                 =  new GenericFSNodeAllotment(this.fileMasterManipulator,this);
        this.globalPathGuidCacheQuerier      =  globalPathGuidCacheQuerier;
    }

//    public GenericKOMFileSystem( Hydrarum hydrarum ) {
//        this.hydrarum = hydrarum;
//    }

    public UniformObjectFileSystem( Processum superiorProcess, KOIMasterManipulator masterManipulator, KOMFileSystem parent, String name ) {
        this( superiorProcess, masterManipulator, parent, name, null );
    }

    public UniformObjectFileSystem( Processum superiorProcess, KOIMasterManipulator masterManipulator ){
        this( superiorProcess, masterManipulator, null, KOMFileSystem.class.getSimpleName() );
    }

    public UniformObjectFileSystem( Processum superiorProcess, KOIMasterManipulator masterManipulator, IndexableMapQuerier<String, String > globalPathGuidCacheQuerier  ){
        this( superiorProcess, masterManipulator, null, KOMFileSystem.class.getSimpleName(), globalPathGuidCacheQuerier );
    }

    public UniformObjectFileSystem( KOIMappingDriver driver, KOMFileSystem parent, String name ) {
        this(
                driver.getSuperiorProcess(),
                driver.getMasterManipulator(),
                parent,
                name
        );
    }

    public UniformObjectFileSystem( KOIMappingDriver driver ) {
        this(
                driver.getSuperiorProcess(),
                driver.getMasterManipulator()
        );
    }

    public UniformObjectFileSystem( KOIMappingDriver driver, IndexableMapQuerier<String, String > globalPathGuidCacheQuerier  ) {
        this(
                driver.getSuperiorProcess(),
                driver.getMasterManipulator(),
                globalPathGuidCacheQuerier
        );
    }



    @Override
    public FileTreeNode get(GUID guid, int depth ) {
        return (FileTreeNode) super.get( guid, depth );
    }

    @Override
    public FileTreeNode get( GUID guid ) {
        return (FileTreeNode) super.get( guid );
    }

    @Override
    public void update(FileTreeNode node) {
        TreeNodeOperator operator = this.operatorFactory.getOperator(node.getMetaType());
        operator.update( node );
    }

    @Override
    public FileTreeNode getSelf( GUID guid ) {
        return (FileTreeNode) super.getSelf( guid );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<FileTreeNode > fetchRoot() {
        return (List) super.fetchRoot();
    }



    @Override
    public FileSystemConfig getConfig() {
        return (FileSystemConfig) this.kernelObjectConfig;
    }

    public FileSystemOperatorFactory getOperatorFactory() {
        return (FileSystemOperatorFactory) this.operatorFactory;
    }

    @Override
    public FileNode getFileNode(GUID guid) {
        return ( FileNode ) this.get( guid );
    }

    @Override
    public Folder getFolder(GUID guid) {
        return ( Folder ) this.get( guid );
    }

    @Override
    public void removeFileNode(GUID guid) {

    }

    @Override
    public void removeFolder(GUID guid) {

    }

    @Override
    public List<TreeNode> getAllTreeNode() {
        List<GUID> nameSpaceNodes = this.fileManipulator.dumpGuid();
        List<GUID> confNodes      = this.folderManipulator.dumpGuid();
        ArrayList<TreeNode> treeNodes = new ArrayList<>();
        for (GUID guid : nameSpaceNodes){
            TreeNode treeNode = this.get(guid);
            treeNodes.add(treeNode);
        }
        for ( GUID guid : confNodes ){
            TreeNode treeNode = this.get(guid);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    protected FileTreeNode affirmTreeNodeByPath( String path, Class<? > cnSup, Class<? > nsSup ) {
        String[] parts = this.pathResolver.segmentPathParts( path );
        String currentPath = "";
        GUID parentGuid = GUIDs.Dummy72();

        FileTreeNode node = this.queryElement( path );
        if( node != null ) {
            return node;
        }

        FileTreeNode ret = null;
        for( int i = 0; i < parts.length; ++i ){
            currentPath = currentPath + ( i > 0 ? this.getConfig().getPathNameSeparator() : "" ) + parts[ i ];
            node = this.queryElement( currentPath );
            if ( node == null){
                if ( i == parts.length - 1 && cnSup != null ){
                    FileNode fileNode = (FileNode) this.dynamicFactory.optNewInstance( cnSup, new Object[]{ this } );
                    fileNode.setName( parts[i] );
                    GUID guid = this.put( fileNode );
                    this.affirmOwnedNode( parentGuid, guid );
                    return fileNode;
                }
                else {
                    Folder folder = (Folder) this.dynamicFactory.optNewInstance( nsSup, new Object[]{ this } );
                    folder.setName(parts[i]);
                    GUID guid = this.put(folder);
                    if ( i != 0 ){
                        this.affirmOwnedNode( parentGuid, guid );
                        parentGuid = guid;
                    }
                    else {
                        parentGuid = guid;
                    }

                    ret = folder;
                }
            }
            else {
                parentGuid = node.getGuid();
            }
        }

        return ret;
    }

    @Override
    public FileNode affirmFileNode(String path) {
        FileNode fileNode = (FileNode) this.affirmTreeNodeByPath(path, GenericFileNode.class, GenericFolder.class);
        this.initVolume( path );
        return fileNode;
    }

    @Override
    public Folder affirmFolder(String path) {
        Folder folder = (Folder) this.affirmTreeNodeByPath(path, null, GenericFolder.class);
        this.initVolume( path );
        return folder;
    }

    @Override
    public void setDataAffinityGuid( GUID childGuid, GUID affinityParentGuid ) {

    }


    @Override
    public GUID queryGUIDByPath( String path ) {
        FileSystemConfig config = this.getConfig();
        if ( this.globalPathGuidCacheQuerier != null ) {
            String key = FileConstants.FILE_PATH_CACHE_KEY + path;
            String szGUID = this.globalPathGuidCacheQuerier.get( key );
            if ( StringUtils.isEmpty( szGUID ) ) {
                return GUIDs.GUID72( szGUID );
            }
        }
        GUID guid =  super.queryGUIDByPath( path ); // Into OLTP-RDB
        if ( this.globalPathGuidCacheQuerier != null ) {
            String key = FileConstants.FILE_PATH_CACHE_KEY + path;
            this.globalPathGuidCacheQuerier.insert( key, guid.toString(), config.getExpiryTime() );
        }
        return guid;
    }

    @Override
    public ElementNode queryElement(String path) {
        GUID guid = this.queryGUIDByPath( path );
        if( guid != null ) {
            return (ElementNode) this.get( guid );
        }
        return null;
    }

    @Override
    public List<TreeNode> selectByName(String name) {
        return null;
    }

    @Override
    public void moveTo(String sourcePath, String destinationPath) {
        GUID[] pair = this.assertCopyMove( sourcePath, destinationPath );
        GUID sourceGuid      = pair[ 0 ];
        GUID destinationGuid = pair[ 1 ];

        this.imperialTree.moveTo( sourceGuid, destinationGuid );
        this.imperialTree.removeCachePath( sourceGuid );
    }

    @Override
    public void move(String sourcePath, String destinationPath) {
        GUID sourceGuid         = this.assertPath( sourcePath, "source" );

        List<String > sourParts = this.pathResolver.resolvePathParts( sourcePath );
        List<String > destParts = this.pathResolver.resolvePathParts( destinationPath );

        String szLastDestTarget = destParts.get( destParts.size() - 1 );
        sourcePath      = sourcePath.trim();
        destinationPath = destinationPath.trim();

        //   Case1: Move "game/terraria/npc"   => "game/minecraft/npc", which has the same dest name.
        // Case1-1: Move "game/terraria/npc/"  => "game/minecraft/npc/"
        // Case1-2: Move "game/terraria/npc/." => "game/minecraft/npc/."
        if(
                sourParts.get( sourParts.size() - 1 ).equals( szLastDestTarget ) || szLastDestTarget.equals( "." ) ||
                        ( sourcePath.endsWith( this.getConfig().getPathNameSeparator() ) && destinationPath.endsWith( this.getConfig().getPathNameSeparator() ) )
        ) {
            destParts.remove( destParts.size() - 1 );
            String szParentPath = this.pathResolver.assemblePath( destParts );
            destParts.add( szLastDestTarget );

            // Move to, which has the same name or explicit current dir `.`.
            this.moveTo( sourcePath, szParentPath );
        }
        // Case 2: "game/terraria/npc" => "game/minecraft/character/" || "game/minecraft/character/."
        //    game/terraria/npc => game/minecraft/character/npc
        else if ( !sourcePath.endsWith( this.getConfig().getPathNameSeparator() ) && (
                destinationPath.endsWith( this.getConfig().getPathNameSeparator() ) || destinationPath.endsWith( "." ) )
        ) {
            Folder target = this.affirmFolder( destinationPath );
            this.imperialTree.moveTo( sourceGuid, target.getGuid() );
        }
        // Case3: Move "game/terraria/npc" => "game/minecraft/character", move all children therein.
        //    game/terraria/npc/f1 => game/minecraft/character/f1
        //    game/terraria/npc/f2 => game/minecraft/character/f2
        //    etc.
        else {
            //  Case3-1: Is config or other none namespace node.
            //           Move "game/terraria/file" => "game/minecraft/dir".
            //  Case3-2: "game/terraria/npc/" => "game/minecraft/character"
            // Eq.Case2: Move "game/terraria/npc" => "game/minecraft/character",
            if( !this.folderManipulator.isFolder( sourceGuid ) ) {
                Folder target = this.affirmFolder( destinationPath );
                this.imperialTree.moveTo( sourceGuid, target.getGuid() );
            }
            else {
                List<TreeNode > children = this.getChildren( sourceGuid );
                if( !children.isEmpty() ) {
                    Folder target = this.affirmFolder( destinationPath );
                    for( TreeNode node : children ) {
                        this.imperialTree.moveTo( node.getGuid(), target.getGuid() );
                    }
                }
            }

            this.imperialTree.removeTreeNodeOnly( sourceGuid );
        }

        this.imperialTree.removeCachePath( sourceGuid );
    }

    @Override
    public void copyTo(String sourcePath, String destinationPath) {
    }

    @Override
    public void copy(String sourcePath, String destinationPath) {

    }

    @Override
    public Object querySelectorJ( String szSelector ) {
        return null;
    }


    @Override
    public TreeMap<Long, Frame> getFrameByFileGuid( GUID guid ) {
        TreeMap< Long, Frame > frameMap = new TreeMap<>();
        List<RemoteFrame> remoteFrames = this.remoteFrameManipulator.getRemoteFrameByFileGuid(guid);
        for( RemoteFrame remoteFrame : remoteFrames ){
            if( remoteFrame.getDeviceGuid().equals(GUIDs.GUID72("0000000-000000-0000-00")) ){
                LocalFrame localFrame = this.localFrameManipulator.getLocalFrameByGuid(remoteFrame.getSegGuid());
                frameMap.put( localFrame.getSegId(),localFrame );
            }
            else {
                //todo 远程获取逻辑
            }
        }

        return frameMap;
    }

    @Override
    public FSNodeAllotment getFSNodeAllotment() {
        return this.fsNodeAllotment;
    }

    @Override
    public Object querySelector(String szSelector) {
        return null;
    }

    @Override
    public List querySelectorAll(String szSelector) {
        return null;
    }

    @Override
    public void copyFileNodeTo(GUID sourceGuid, GUID destinationGuid) {

    }

    @Override
    public Frame getLastFrame(GUID guid) {
        RemoteFrame remoteFrame = this.remoteFrameManipulator.getLastFrame(guid);
        if ( remoteFrame.getDeviceGuid().equals( GUIDs.GUID72("0000000-000000-0000-00") ) ){
            return this.localFrameManipulator.getLocalFrameByGuid(remoteFrame.getSegGuid());
        }
        else {
            //todo 远端获取方法
        }
        return null;
    }

    @Override
    public void copyFolderTo(GUID sourceGuid, GUID destinationGuid) {

    }

    @Override
    public void upload( FileNode file, String destDirPath ) {
        if ( file.getIsUploadSuccessful() ){
            //this.upload0(file,destDirPath,0);
        }
        else {
            TreeMap<Long, Frame> frames = file.getFrames();
            Map.Entry<Long, Frame> longFrameEntry = frames.lastEntry();
            long segId = longFrameEntry.getValue().getSegId();
            //this.upload0(file, destDirPath, segId);
        }
    }


    private String getNodeName(ImperialTreeNode node ){
        UOI type = node.getType();
        TreeNode newInstance = (TreeNode)type.newInstance();
        TreeNodeOperator operator = this.getOperatorFactory().getOperator(newInstance.getMetaType());
        TreeNode treeNode = operator.get(node.getGuid());
        return treeNode.getName();
    }

    private boolean allNonNull( List<?> list ) {
        return list.stream().noneMatch( Objects::isNull );
    }

    protected GUID[] assertCopyMove ( String sourcePath, String destinationPath ) throws IllegalArgumentException {
        GUID sourceGuid      = this.queryGUIDByPath( sourcePath );
        if( sourceGuid == null ) {
            throw new IllegalArgumentException( "Undefined source '" + sourcePath + "'" );
        }

        GUID destinationGuid = this.queryGUIDByPath( destinationPath );
        if( !this.folderManipulator.isFolder( destinationGuid ) ){
            throw new IllegalArgumentException( "Illegal destination '" + destinationPath + "', should be namespace." );
        }

        if( destinationGuid == null ) {
            throw new IllegalArgumentException( "Undefined destination '" + destinationPath + "'" );
        }

        if( sourceGuid == destinationGuid ) {
            throw new IllegalArgumentException( "Cyclic path detected '" + sourcePath + "'" );
        }

        return new GUID[] { sourceGuid, destinationGuid };
    }

    @Override
    public void receive( FileReceiveEntity entity) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        entity.receive();
    }

    @Override
    public void receive( FileReceiveEntity entity, Number offset, Number endSize) throws IOException {
        entity.receive(offset, endSize );
    }

    @Override
    public void export( FileExportEntity entity ) throws SQLException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        entity.export();
    }

    @Override
    public void export( FileExportEntity entity, Number offset, Number endSize ) {

    }

    @Override
    public void setFolderVolumeMapping(GUID folderGuid, GUID volumeGuid) {
        this.folderVolumeMappingManipulator.insert( folderGuid, volumeGuid );
    }

    @Override
    public GUID getMappingVolume(GUID folderGuid) {
        return this.folderVolumeMappingManipulator.getVolumeGuid( folderGuid );
    }

    @Override
    public GUID getMappingVolume(String path) {
        String[] parts = this.pathResolver.segmentPathParts( path );
        GUID currentVolumeGuid = null;
        String currentPath = "";
        for( int i = 0; i < parts.length - 1; i++ ){
            currentPath = currentPath + ( i > 0 ? this.getConfig().getPathNameSeparator() : "" ) + parts[ i ];
            ElementNode elementNode = this.queryElement(currentPath);
            Folder folder = this.getFolder(elementNode.getGuid());
            GUID relationVolume = folder.getRelationVolume();
            if ( relationVolume != null ){
                currentVolumeGuid = relationVolume;
            }
        }
        return currentVolumeGuid;
    }

    private void initVolume(String path ){
        String[] parts = this.pathResolver.segmentPathParts( path );
        Folder root = this.getFolder(this.queryGUIDByPath(parts[0]));
        if( root.getRelationVolume() == null ){
            root.applyVolume( GUIDs.GUID72( this.getConfig().getDefaultVolume() ) );
        }
    }
}
