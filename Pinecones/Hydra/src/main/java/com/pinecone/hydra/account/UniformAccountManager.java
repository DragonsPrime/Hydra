package com.pinecone.hydra.account;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.account.entity.Account;
import com.pinecone.hydra.account.entity.Domain;
import com.pinecone.hydra.account.entity.ElementNode;
import com.pinecone.hydra.account.entity.GenericAccount;
import com.pinecone.hydra.account.entity.GenericDomain;
import com.pinecone.hydra.account.entity.GenericGroup;
import com.pinecone.hydra.account.entity.Group;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.identifier.KOPathResolver;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.hydra.system.ko.kom.ArchKOMTree;
import com.pinecone.hydra.system.ko.kom.MultiFolderPathSelector;
import com.pinecone.hydra.unit.imperium.ImperialTreeNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
import com.pinecone.hydra.unit.imperium.operator.TreeNodeOperator;
import com.pinecone.hydra.account.operator.GenericAccountOperatorFactory;
import com.pinecone.hydra.account.source.DomainNodeManipulator;
import com.pinecone.hydra.account.source.GroupNodeManipulator;
import com.pinecone.hydra.account.source.UserMasterManipulator;
import com.pinecone.hydra.account.source.UserNodeManipulator;
import com.pinecone.ulf.util.id.GUIDs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UniformAccountManager extends ArchKOMTree implements AccountManager {
    protected Hydrarum                          hydrarum;

    protected UserMasterManipulator             userMasterManipulator;

    protected GroupNodeManipulator              groupNodeManipulator;

    protected UserNodeManipulator               userNodeManipulator;

    protected DomainNodeManipulator             domainNodeManipulator;

    protected List<GUIDNameManipulator >        folderManipulators;

    protected List<GUIDNameManipulator >        fileManipulators;

    public UniformAccountManager(Hydrarum hydrarum, KOIMasterManipulator masterManipulator, AccountManager parent, String name) {
        super(hydrarum, masterManipulator, KernelAccountConfig, parent, name);
        this.hydrarum              = hydrarum;
        this.userMasterManipulator = (UserMasterManipulator) masterManipulator;
        this.pathResolver          = new KOPathResolver( this.kernelObjectConfig );
        this.guidAllocator         = GUIDs.newGuidAllocator();

        this.operatorFactory        = new GenericAccountOperatorFactory( this, this.userMasterManipulator );
        this.groupNodeManipulator   = this.userMasterManipulator.getGroupNodeManipulator();
        this.userNodeManipulator    = this.userMasterManipulator.getUserNodeManipulator();
        this.domainNodeManipulator  = this.userMasterManipulator.getDomainNodeManipulator();

        this.folderManipulators = new ArrayList<>(List.of(this.domainNodeManipulator, this.groupNodeManipulator));
        this.fileManipulators   = new ArrayList<>(List.of(this.userNodeManipulator));

        this.pathSelector                = new MultiFolderPathSelector(
                this.pathResolver, this.imperialTree, this.folderManipulators.toArray( new GUIDNameManipulator[]{} ), this.fileManipulators.toArray( new GUIDNameManipulator[]{} )
        );
    }

    public UniformAccountManager(Hydrarum hydrarum, KOIMasterManipulator masterManipulator ) {
        this( hydrarum, masterManipulator, null, AccountManager.class.getSimpleName() );
    }

    public UniformAccountManager(KOIMappingDriver driver, AccountManager parent, String name ){
        this( driver.getSystem(), driver.getMasterManipulator(), parent, name );
    }

    public UniformAccountManager(KOIMappingDriver driver ) {
        this( driver.getSystem(), driver.getMasterManipulator() );
    }

    @Override
    public Object queryEntityHandleByNS(String path, String szBadSep, String szTargetSep) {
        return null;
    }

    @Override
    public String getPath( GUID guid ) {
        return this.getNS( guid, this.kernelObjectConfig.getPathNameSeparator() );
    }

    @Override
    public String getFullName( GUID guid ) {
        return this.getNS( guid, this.kernelObjectConfig.getFullNameSeparator() );
    }

    @Override
    public ElementNode queryElement(String path) {
        GUID guid = this.queryGUIDByPath(path);
        if( guid != null ) {
            return (ElementNode) this.get( guid );
        }

        return null;
    }

    protected ElementNode affirmTreeNodeByPath(String path, Class<? > cnSup, Class<? > nsSup ) {
        String[] parts = this.pathResolver.segmentPathParts( path );
        String currentPath = "";
        GUID parentGuid = GUIDs.Dummy72();

        ElementNode node = this.queryElement(path);
        if ( node != null ){
            return node;
        }

        ElementNode ret = null;
        for( int i = 0; i < parts.length; ++i ){
            currentPath = currentPath + ( i > 0 ? this.getConfig().getPathNameSeparator() : "" ) + parts[ i ];
            node = this.queryElement( currentPath );
            if ( node == null){
                if ( i == parts.length - 1 && cnSup != null ){
                    Account account = (Account) this.dynamicFactory.optNewInstance( cnSup, new Object[]{ this } );
                    account.setName( parts[i] );
                    GUID guid = this.put( account );
                    return account;
                }
                else {
                    ElementNode element = (ElementNode) this.dynamicFactory.optNewInstance( nsSup, new Object[]{ this } );
                    element.setName( parts[i] );
                    GUID guid = this.put( element );
                    if ( i != 0 ){
                        this.treeMasterManipulator.getTrieTreeManipulator().addChild( guid, parentGuid );
                        parentGuid = guid;
                    }
                    else {
                        parentGuid = guid;
                    }

                    ret = element;
                }
            }
            else {
                parentGuid = node.getGuid();
            }
        }

        return ret;
    }
    @Override
    public Account affirmAccount(String path) {
        return (Account) this.affirmTreeNodeByPath( path, GenericAccount.class, GenericDomain.class );
    }

    @Override
    public Group affirmGroup(String path) {
        return (Group) this.affirmTreeNodeByPath( path, GenericGroup.class, GenericDomain.class);
    }

    @Override
    public Domain affirmDomain(String path) {
        return (Domain) this.affirmTreeNodeByPath( path, null, GenericDomain.class );
    }

    @Override
    public void addChildren( GUID parentGuid, GUID childrenGuid ) {
        this.treeMasterManipulator.getTrieTreeManipulator().addChild( childrenGuid, parentGuid );
    }

    @Override
    public boolean containsChild(GUID parentGuid, String childName) {
        for( GUIDNameManipulator manipulator : this.fileManipulators ) {
            if( this.containsChild( manipulator, parentGuid, childName ) ) {
                return true;
            }
        }

        for( GUIDNameManipulator manipulator : this.folderManipulators ) {
            if( this.containsChild( manipulator, parentGuid, childName ) ) {
                return true;
            }
        }
        return false;
    }

    protected boolean containsChild( GUIDNameManipulator manipulator, GUID parentGuid, String childName ) {
        List<GUID > guids = manipulator.getGuidsByName( childName );
        for( GUID guid : guids ) {
            List<GUID > ps = this.imperialTree.fetchParentGuids( guid );
            if( ps.contains( parentGuid ) ){
                return true;
            }
        }
        return false;
    }

    protected String getNS(GUID guid, String szSeparator ){
        String path = this.imperialTree.getCachePath(guid);
        if ( path != null ) {
            return path;
        }

        ImperialTreeNode node = this.imperialTree.getNode(guid);
        String assemblePath = this.getNodeName(node);
        while ( !node.getParentGUIDs().isEmpty() && this.allNonNull( node.getParentGUIDs() ) ){
            List<GUID> parentGuids = node.getParentGUIDs();
            for( int i = 0; i < parentGuids.size(); ++i ){
                if ( parentGuids.get(i) != null ){
                    node = this.imperialTree.getNode( parentGuids.get(i) );
                    break;
                }
            }
            String nodeName = this.getNodeName(node);
            assemblePath = nodeName + szSeparator + assemblePath;
        }
        this.imperialTree.insertCachePath( guid, assemblePath );
        return assemblePath;
    }

    private String getNodeName(ImperialTreeNode node ){
        UOI type = node.getType();
        TreeNode newInstance = (TreeNode)type.newInstance();
        TreeNodeOperator operator = this.operatorFactory.getOperator( newInstance.getMetaType() );
        TreeNode treeNode = operator.get(node.getGuid());
        return treeNode.getName();
    }

    private boolean allNonNull( List<?> list ) {
        return list.stream().noneMatch( Objects::isNull );
    }
}
