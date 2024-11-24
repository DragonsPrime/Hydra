package com.pinecone.hydra.system.ko.kom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.lang.DynamicFactory;
import com.pinecone.framework.util.lang.GenericDynamicFactory;
import com.pinecone.framework.util.name.Namespace;
import com.pinecone.framework.util.name.UniNamespace;
import com.pinecone.framework.util.name.path.PathResolver;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.ko.CascadeInstrument;
import com.pinecone.hydra.system.ko.KernelObjectConfig;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.hydra.unit.udtt.ArchTrieObjectModel;
import com.pinecone.hydra.unit.udtt.DistributedTreeNode;
import com.pinecone.hydra.unit.udtt.DistributedTrieTree;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.entity.EntityNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;
import com.pinecone.hydra.unit.udtt.operator.OperatorFactory;
import com.pinecone.hydra.unit.udtt.operator.TreeNodeOperator;
import com.pinecone.ulf.util.id.GuidAllocator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ArchKOMTree extends ArchTrieObjectModel implements KOMInstrument {
    protected Namespace             mThisNamespace;
    protected KOMInstrument         mParentInstrument;

    protected Hydrarum              hydrarum;

    protected GuidAllocator         guidAllocator;
    protected OperatorFactory       operatorFactory;

    protected PathResolver          pathResolver;
    protected PathSelector          pathSelector;

    protected DynamicFactory        dynamicFactory;

    public ArchKOMTree (
            Hydrarum hydrarum, KOIMasterManipulator masterManipulator,
            OperatorFactory operatorFactory, KernelObjectConfig kernelObjectConfig, PathSelector pathSelector,
            KOMInstrument parent, String name
    ){
        this( hydrarum, masterManipulator, kernelObjectConfig, parent, name );

        this.pathSelector              =  pathSelector;
        this.operatorFactory           =  operatorFactory;
    }

    public ArchKOMTree (
            Hydrarum hydrarum, KOIMasterManipulator masterManipulator, KernelObjectConfig kernelObjectConfig,
            KOMInstrument parent, String name
    ){
        super( masterManipulator, kernelObjectConfig );
        this.hydrarum                      = hydrarum;
        this.dynamicFactory                = new GenericDynamicFactory( hydrarum.getTaskManager().getClassLoader() );
        this.mParentInstrument             = parent;
        this.setTargetingName( name );
    }

    //************************************** CascadeInstrument **************************************
    @Override
    public KOMInstrument parent() {
        return this.mParentInstrument;
    }

    @Override
    public void setParent( CascadeInstrument parent ) {
        this.mParentInstrument = (KOMInstrument) parent;
    }

    @Override
    public Namespace getTargetingName() {
        return this.mThisNamespace;
    }

    @Override
    public void setTargetingName( Namespace name ) {
        this.mThisNamespace = name;
    }

    //************************************** CascadeInstrument End **************************************

    @Override
    public GUID put( TreeNode treeNode ) {
        TreeNodeOperator operator = this.operatorFactory.getOperator( treeNode.getMetaType() );
        return operator.insert( treeNode );
    }

    @Override
    public boolean contains( GUID nodeGuid ) {
        return this.distributedTrieTree.contains( nodeGuid );
    }

    @Override
    public TreeNode get( GUID guid, int depth ) {
        return this.getOperatorByGuid( guid ).get( guid, depth );
    }

    @Override
    public TreeNode getSelf( GUID guid ) {
        return this.getOperatorByGuid( guid ).getSelf( guid );
    }

    protected String getNS( GUID guid, String szSeparator ) {
        String path = this.distributedTrieTree.getCachePath(guid);
        if ( path != null ) {
            return path;
        }

        DistributedTreeNode node = this.distributedTrieTree.getNode(guid);
        GUID owner = this.distributedTrieTree.getOwner(guid);
        if ( owner == null ){
            String assemblePath = this.getNodeName(node);
            while ( !node.getParentGUIDs().isEmpty() && this.allNonNull( node.getParentGUIDs() ) ){
                List<GUID> parentGuids = node.getParentGUIDs();
                for( int i = 0; i < parentGuids.size(); ++i ){
                    if ( parentGuids.get(i) != null ){
                        node = this.distributedTrieTree.getNode( parentGuids.get(i) );
                        break;
                    }
                }
                String nodeName = this.getNodeName(node);
                assemblePath = nodeName + szSeparator + assemblePath;
            }
            this.distributedTrieTree.insertCachePath( guid, assemblePath );
            return assemblePath;
        }
        else{
            String assemblePath = this.getNodeName( node );
            while ( !node.getParentGUIDs().isEmpty() && this.allNonNull( node.getParentGUIDs() ) ){
                node = this.distributedTrieTree.getNode( owner );
                String nodeName = this.getNodeName( node );
                assemblePath = nodeName + szSeparator + assemblePath;
                owner = this.distributedTrieTree.getOwner( node.getGuid() );
            }
            this.distributedTrieTree.insertCachePath( guid, assemblePath );
            return assemblePath;
        }
    }

    @Override
    public String getPath( GUID guid ) {
        return this.getNS( guid, this.kernelObjectConfig.getPathNameSeparator() );
    }

    @Override
    public String getFullName( GUID guid ) {
        return this.getNS( guid, this.kernelObjectConfig.getFullNameSeparator() );
    }

    protected TreeNodeOperator getOperatorByGuid( GUID guid ) {
        DistributedTreeNode node = this.distributedTrieTree.getNode( guid );
        if ( node == null ){
            return null;
        }
        TreeNode newInstance = (TreeNode)node.getType().newInstance( new Class<? >[]{this.getClass()}, this );
        return this.operatorFactory.getOperator( newInstance.getMetaType() );
    }

    @Override
    public TreeNode get( GUID guid ) {
        TreeNodeOperator operator = this.getOperatorByGuid( guid );
        if( operator == null ) {
            return null;
        }
        return operator.get( guid );
    }

    /** Final Solution 20240929: 无法获取类型 */
    @Override
    public GUID queryGUIDByNS( String path, String szBadSep, String szTargetSep ) {
        if( szTargetSep != null ) {
            path = path.replace( szBadSep, szTargetSep );
        }

        String[] parts = this.pathResolver.segmentPathParts( path );
        List<String > resolvedParts = this.pathResolver.resolvePath( parts );
        path = this.pathResolver.assemblePath( resolvedParts );

        GUID guid = this.distributedTrieTree.queryGUIDByPath( path );
        if ( guid != null ){
            return guid;
        }


        guid = this.pathSelector.searchGUID( resolvedParts );
        if( guid != null ){
            this.distributedTrieTree.insertCachePath( guid, path );
        }
        return guid;
    }

    @Override
    public GUID queryGUIDByPath( String path ) {
        return this.queryGUIDByNS( path, null, null );
    }

    @Override
    public void remove( GUID guid ) {
        GUIDDistributedTrieNode node = this.distributedTrieTree.getNode( guid );
        TreeNode newInstance = (TreeNode)node.getType().newInstance();
        TreeNodeOperator operator = this.operatorFactory.getOperator( newInstance.getMetaType() );
        operator.purge( guid );
    }

    @Override
    public abstract Object queryEntityHandleByNS( String path, String szBadSep, String szTargetSep ) ;

    public Object queryEntityHandle( String path ) {
        return this.queryEntityHandleByNS( path, null, null );
    }

    @Override
    public void remove( String path ) {
        Object handle = this.queryEntityHandle( path );
        if( handle instanceof GUID ) {
            this.remove( (GUID) handle );
        }
    }

    @Override
    public List<TreeNode > getChildren( GUID guid ) {
        List<GUIDDistributedTrieNode > childNodes = this.distributedTrieTree.getChildren( guid );
        ArrayList<TreeNode > nodes = new ArrayList<>();
        for( GUIDDistributedTrieNode node : childNodes ){
            TreeNode treeNode =  this.get(node.getGuid());
            nodes.add( treeNode );
        }
        return nodes;
    }

    @Override
    public List<GUID > fetchChildrenGuids( GUID guid ) {
        return this.distributedTrieTree.fetchChildrenGuids( guid );
    }

    public EntityNode queryNodeByNS(String path, String szBadSep, String szTargetSep ) {
        Object ret = this.queryEntityHandleByNS( path, szBadSep, szTargetSep );
        if( ret instanceof EntityNode ) {
            return (EntityNode) ret;
        }
        else if( ret instanceof GUID ) {
            return this.get( (GUID) ret );
        }

        return null;
    }

    @Override
    public List<? extends TreeNode > fetchRoot() {
        List<GUID> guids = this.distributedTrieTree.fetchRoot();
        ArrayList<TreeNode> treeNodes = new ArrayList<>();
        for( GUID guid : guids ){
            TreeNode treeNode = this.get(guid);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    @Override
    public void rename( GUID guid, String name ) {
        GUIDDistributedTrieNode node = this.distributedTrieTree.getNode(guid);
        TreeNode newInstance = (TreeNode)node.getType().newInstance();
        TreeNodeOperator operator = this.operatorFactory.getOperator( newInstance.getMetaType() );
        operator.updateName( guid, name );

        this.distributedTrieTree.removeCachePath( guid );
    }

    @Override
    public EntityNode queryNode( String path ) {
        return this.queryNodeByNS( path, null, null );
    }



    @Override
    public GUID queryGUIDByFN( String fullName ) {
        return this.queryGUIDByNS(
                fullName, this.kernelObjectConfig.getFullNameSeparator(), this.kernelObjectConfig.getPathNameSeparator()
        );
    }

    private String getNodeName( DistributedTreeNode node ){
        UOI type = node.getType();
        TreeNode newInstance = (TreeNode)type.newInstance();
        TreeNodeOperator operator = this.operatorFactory.getOperator(newInstance.getMetaType());
        TreeNode treeNode = operator.get(node.getGuid());
        return treeNode.getName();
    }


    private boolean allNonNull( List<?> list ) {
        return list.stream().noneMatch( Objects::isNull );
    }

    @Override
    public GuidAllocator getGuidAllocator() {
        return this.guidAllocator;
    }

    @Override
    public DistributedTrieTree getMasterTrieTree() {
        return this.distributedTrieTree;
    }
}
