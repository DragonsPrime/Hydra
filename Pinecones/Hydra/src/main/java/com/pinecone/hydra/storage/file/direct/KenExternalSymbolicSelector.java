package com.pinecone.hydra.storage.file.direct;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.name.path.PathResolver;
import com.pinecone.hydra.storage.file.entity.ExternalSymbolic;
import com.pinecone.hydra.storage.file.source.ExternalSymbolicManipulator;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;
import com.pinecone.hydra.system.ko.kom.MultiFolderPathSelector;
import com.pinecone.hydra.system.ko.kom.ReparseLinkSelector;
import com.pinecone.hydra.unit.imperium.ImperialTree;
import com.pinecone.hydra.unit.imperium.entity.ReparseLinkNode;

import java.util.List;

public class KenExternalSymbolicSelector extends ReparseLinkSelector implements ExternalSymbolicSelector {
    protected ExternalSymbolicManipulator mExternalSymbolicManipulator;

    public KenExternalSymbolicSelector( MultiFolderPathSelector pathSelector ) {
        super( pathSelector );
    }

    public KenExternalSymbolicSelector(
            PathResolver pathResolver, ImperialTree trieTree, GUIDNameManipulator dirMan, GUIDNameManipulator[] fileMans,
            ExternalSymbolicManipulator externalSymbolicManipulator
    ) {
        super( pathResolver, trieTree, new GUIDNameManipulator[]{ dirMan }, fileMans );
        this.mExternalSymbolicManipulator = externalSymbolicManipulator;
    }

    public KenExternalSymbolicSelector(
            PathResolver pathResolver, ImperialTree trieTree, GUIDNameManipulator[] dirMans, GUIDNameManipulator[] fileMans,
            ExternalSymbolicManipulator externalSymbolicManipulator
    ) {
        super( pathResolver, trieTree, dirMans, fileMans );
        this.mExternalSymbolicManipulator = externalSymbolicManipulator;
    }

    @Override
    public Object search( String[] parts ) {
        List<String> resolvedParts = this.pathResolver.resolvePath(parts);
        return this.dfsSearch( resolvedParts );
    }

    @Override
    public ReparseLinkNode searchLinkNode(String[] parts ) {
        Object result = this.search( parts );
        if( result instanceof ReparseLinkNode ) {
            return (ReparseLinkNode) result;
        }
        return null;
    }

    @Override
    protected Object beforeDFSTermination( String currentPart, GUID guid ) {
        Object obj = super.beforeDFSTermination( currentPart, guid );
        if ( obj == null ) {
            boolean b = this.mExternalSymbolicManipulator.isSymbolicMatchedByNameGuid( currentPart, guid );
            if ( b ) {
                return guid;
            }
        }

        return guid;
    }

    @Override
    protected Object tryTerminationBlock( String currentPart, GUID guid ) {
        Object obj = super.tryTerminationBlock( currentPart, guid );
        if ( obj == null ) {
            boolean b = this.mExternalSymbolicManipulator.isSymbolicMatchedByNameGuid( currentPart, guid );
            if ( b ) {
                return guid;
            }
        }

        return obj;
    }
}
