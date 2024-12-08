package com.pinecone.hydra.account;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.identifier.KOPathResolver;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.hydra.system.ko.kom.ArchKOMTree;
import com.pinecone.hydra.system.ko.kom.SimplePathSelector;
import com.pinecone.hydra.unit.udtt.DistributedTreeNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;
import com.pinecone.hydra.unit.udtt.operator.TreeNodeOperator;
import com.pinecone.hydra.account.operator.GenericAccountOperatorFactory;
import com.pinecone.hydra.account.source.DomainNodeManipulator;
import com.pinecone.hydra.account.source.GroupNodeManipulator;
import com.pinecone.hydra.account.source.UserMasterManipulator;
import com.pinecone.hydra.account.source.UserNodeManipulator;
import com.pinecone.ulf.util.id.GUIDs;

import java.util.List;
import java.util.Objects;

public class UniformAccountManager extends ArchKOMTree implements AccountManager {
    protected Hydrarum                  hydrarum;

    protected UserMasterManipulator     userMasterManipulator;

    protected GroupNodeManipulator      groupNodeManipulator;

    protected UserNodeManipulator       userNodeManipulator;

    protected DomainNodeManipulator     domainNodeManipulator;

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

        this.pathSelector                  =   new SimplePathSelector(
                this.pathResolver, this.distributedTrieTree, this.domainNodeManipulator, new GUIDNameManipulator[] { this.groupNodeManipulator, this.userNodeManipulator }
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



    protected String getNS(GUID guid, String szSeparator ){
        String path = this.distributedTrieTree.getCachePath(guid);
        if ( path != null ) {
            return path;
        }

        DistributedTreeNode node = this.distributedTrieTree.getNode(guid);
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

    private String getNodeName(DistributedTreeNode node ){
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
