package com.pinecone.hydra.system.ko.kom;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.DistributedTrieTree;
import com.pinecone.hydra.unit.udtt.entity.ReparseLinkNode;

public class GenericReparseKOMTreeAddition implements ReparseKOMTreeAddition {
    protected ArchKOMTree           mKOMTree;
    protected DistributedTrieTree   mDistributedTrieTree;
    protected ReparsePointSelector  mReparsePointSelector;

    public GenericReparseKOMTreeAddition( ArchKOMTree tree, ReparsePointSelector reparsePointSelector ) {
        this.mKOMTree              = tree;
        this.mDistributedTrieTree  = tree.getMasterTrieTree();
        this.mReparsePointSelector = reparsePointSelector ;
    }

    public GenericReparseKOMTreeAddition( ArchKOMTree tree ) {
        this.mKOMTree              = tree;
        this.mDistributedTrieTree  = tree.getMasterTrieTree();
        this.mReparsePointSelector = new ReparseLinkSelector( (MultiFolderPathSelector) this.mKOMTree.pathSelector ) ;
    }

    @Override
    public ReparseLinkNode queryReparseLinkByNS(String path, String szBadSep, String szTargetSep ) {
        if( szTargetSep != null ) {
            path = path.replace( szBadSep, szTargetSep );
        }

        String[] parts = this.mKOMTree.pathResolver.segmentPathParts( path );
        return this.mReparsePointSelector.searchLinkNode( parts );
    }

    @Override
    public ReparseLinkNode queryReparseLink(String path) {
        return this.queryReparseLinkByNS( path, null, null );
    }

    @Override
    public void affirmOwnedNode( GUID parentGuid, GUID childGuid ) {
        this.mDistributedTrieTree.affirmOwnedNode( childGuid, parentGuid );
    }

    @Override
    public void newHardLink( GUID sourceGuid, GUID targetGuid ) {
        this.mDistributedTrieTree.newHardLink( sourceGuid, targetGuid );
    }

    @Override
    public void newLinkTag( GUID originalGuid, GUID dirGuid, String tagName ) {
        this.mDistributedTrieTree.newLinkTag( originalGuid, dirGuid, tagName, this.mKOMTree );
    }

    @Override
    public void updateLinkTag( GUID tagGuid, String tagName ) {
        this.mDistributedTrieTree.updateLinkTagName( tagGuid, tagName );
    }

    @Override
    public void removeReparseLink( GUID guid ) {
        this.mDistributedTrieTree.removeReparseLink( guid );
    }

    @Override
    public void newLinkTag(String originalPath, String dirPath, String tagName) {
        GUID originalGuid           = this.mKOMTree.queryGUIDByPath( originalPath );
        GUID dirGuid                = this.mKOMTree.queryGUIDByPath( dirPath );

        if( this.mDistributedTrieTree.getOriginalGuid( tagName, dirGuid ) == null ) {
            this.mDistributedTrieTree.newLinkTag( originalGuid, dirGuid, tagName, this.mKOMTree );
        }
    }

    /** ReparseLinkNode or GUID **/
    @Override
    public Object queryEntityHandleByNS( String path, String szBadSep, String szTargetSep ) {
        if( szTargetSep != null ) {
            path = path.replace( szBadSep, szTargetSep );
        }

        String[] parts = this.mKOMTree.pathResolver.segmentPathParts( path );
        return this.mReparsePointSelector.search( parts );
    }

    @Override
    public void remove( String path ) {
        Object handle = this.mKOMTree.queryEntityHandle( path );
        if( handle instanceof GUID ) {
            this.mKOMTree.remove( (GUID) handle );
        }
        else if( handle instanceof ReparseLinkNode ) {
            ReparseLinkNode linkNode = (ReparseLinkNode) handle;
            this.removeReparseLink( linkNode.getTagGuid() );
        }
    }
}
