package com.pinecone.hydra.system.ko.kom;

import java.util.List;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.name.path.PathResolver;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;
import com.pinecone.hydra.unit.imperium.ImperialTree;
import com.pinecone.hydra.unit.imperium.entity.ReparseLinkNode;

public class ReparseLinkSelector extends MultiFolderPathSelector implements ReparsePointSelector {
    public ReparseLinkSelector( MultiFolderPathSelector pathSelector ) {
        super( pathSelector.pathResolver, pathSelector.imperialTree, pathSelector.dirManipulators, pathSelector.fileManipulators );
    }

    public ReparseLinkSelector(PathResolver pathResolver, ImperialTree trieTree, GUIDNameManipulator dirMan, GUIDNameManipulator[] fileMans ) {
        super( pathResolver, trieTree, new GUIDNameManipulator[]{ dirMan }, fileMans );
    }

    public ReparseLinkSelector(PathResolver pathResolver, ImperialTree trieTree, GUIDNameManipulator[] dirMans, GUIDNameManipulator[] fileMans ) {
        super( pathResolver, trieTree, dirMans, fileMans );
    }

    @Override
    public Object search( String[] parts ) {
        List<String> resolvedParts = this.pathResolver.resolvePath(parts);
        return this.dfsSearch( resolvedParts );
    }

    @Override
    public ReparseLinkNode searchLinkNode( String[] parts ) {
        Object result = this.search( parts );
        if( result instanceof ReparseLinkNode ) {
            return (ReparseLinkNode) result;
        }
        return null;
    }

    @Override
    protected Object beforeDFSTermination( String currentPart, GUID guid ) {
        ReparseLinkNode reparseLinkNode = this.imperialTree.getReparseLinkNodeByNodeGuid( currentPart, guid );
        if ( reparseLinkNode != null ) {
            return reparseLinkNode;
        }
        return guid;
    }
}
