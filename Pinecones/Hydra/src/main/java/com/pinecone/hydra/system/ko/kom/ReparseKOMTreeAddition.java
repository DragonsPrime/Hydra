package com.pinecone.hydra.system.ko.kom;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.entity.ReparseLinkNode;

public interface ReparseKOMTreeAddition extends Pinenut {
    ReparseLinkNode queryReparseLinkByNS( String path, String szBadSep, String szTargetSep ) ;

    ReparseLinkNode queryReparseLink( String path );

    void affirmOwnedNode( GUID parentGuid, GUID childGuid ) ;

    void newHardLink( GUID sourceGuid, GUID targetGuid ) ;

    void newLinkTag( GUID originalGuid, GUID dirGuid, String tagName ) ;

    void updateLinkTag( GUID tagGuid, String tagName ) ;

    void removeReparseLink( GUID guid ) ;

    void newLinkTag( String originalPath, String dirPath, String tagName ) ;

    void remove( String path );

    Object queryEntityHandleByNS( String path, String szBadSep, String szTargetSep );
}
