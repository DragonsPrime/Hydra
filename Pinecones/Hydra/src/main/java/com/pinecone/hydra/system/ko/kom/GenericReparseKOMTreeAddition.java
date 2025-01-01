package com.pinecone.hydra.system.ko.kom;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.imperium.ImperialTree;
import com.pinecone.hydra.unit.imperium.entity.ReparseLinkNode;

public class GenericReparseKOMTreeAddition implements ReparseKOMTreeAddition {
    protected ArchKOMTree           mKOMTree;
    protected ImperialTree mImperialTree;
    protected ReparsePointSelector  mReparsePointSelector;

    public GenericReparseKOMTreeAddition( ArchKOMTree tree, ReparsePointSelector reparsePointSelector ) {
        this.mKOMTree              = tree;
        this.mImperialTree = tree.getMasterTrieTree();
        this.mReparsePointSelector = reparsePointSelector ;
    }

    public GenericReparseKOMTreeAddition( ArchKOMTree tree ) {
        this.mKOMTree              = tree;
        this.mImperialTree = tree.getMasterTrieTree();
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
        this.mImperialTree.affirmOwnedNode( childGuid, parentGuid );
    }

    @Override
    public void newHardLink( GUID sourceGuid, GUID targetGuid ) {
        this.mImperialTree.newHardLink( sourceGuid, targetGuid );
    }

    @Override
    public void newLinkTag( GUID originalGuid, GUID dirGuid, String tagName ) {
        this.mImperialTree.newLinkTag( originalGuid, dirGuid, tagName, this.mKOMTree );
    }

    @Override
    public void updateLinkTag( GUID tagGuid, String tagName ) {
        this.mImperialTree.updateLinkTagName( tagGuid, tagName );
    }

    @Override
    public void removeReparseLink( GUID guid ) {
        this.mImperialTree.removeReparseLink( guid );
    }

    @Override
    public void newLinkTag(String originalPath, String dirPath, String tagName) {
        GUID originalGuid           = this.mKOMTree.queryGUIDByPath( originalPath );
        GUID dirGuid                = this.mKOMTree.queryGUIDByPath( dirPath );

        if( this.mImperialTree.getOriginalGuid( tagName, dirGuid ) == null ) {
            this.mImperialTree.newLinkTag( originalGuid, dirGuid, tagName, this.mKOMTree );
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
