package com.pinecone.hydra.system.ko.kom;

import java.util.List;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.name.path.PathResolver;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;
import com.pinecone.hydra.unit.imperium.ImperialTree;

public class SimplePathSelector extends StandardPathSelector {
    public SimplePathSelector(PathResolver pathResolver, ImperialTree trieTree, GUIDNameManipulator dirMan, GUIDNameManipulator[] fileMans ) {
        super( pathResolver, trieTree, dirMan, fileMans );
    }

    @Override
    protected List<GUID> searchLinks ( GUID guid, String partName ) {
        return null;
    }

    @Override
    protected List<GUID > searchLinksFirstCase ( String partName ) {
        return null;
    }

    @Override
    protected void fetchAllOriginalGuidsRootCase( List<GUID > guids, String partName ) {

    }
}
