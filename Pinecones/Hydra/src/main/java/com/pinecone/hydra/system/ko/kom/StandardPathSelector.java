package com.pinecone.hydra.system.ko.kom;

import java.util.List;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.name.path.PathResolver;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;
import com.pinecone.hydra.unit.imperium.ImperialTree;

public class StandardPathSelector extends MultiFolderPathSelector implements PathSelector {
    public StandardPathSelector(PathResolver pathResolver, ImperialTree trieTree, GUIDNameManipulator dirMan, GUIDNameManipulator[] fileMans ) {
        super( pathResolver, trieTree, new GUIDNameManipulator[]{ dirMan }, fileMans );
    }

    public GUIDNameManipulator getDirManipulator() {
        return this.dirManipulators[ 0 ];
    }

    @Override
    protected List<GUID > searchDirAndLinks ( GUID guid, String partName ) {
        List<GUID > guids = this.dirManipulators[ 0 ].getGuidsByNameID( partName, guid );
        if( guids != null && !guids.isEmpty() ) {
            return guids;
        }

        GUID linkGuid = this.imperialTree.getOriginalGuidByNodeGuid( partName, guid );
        if( linkGuid != null ) {
            return List.of( linkGuid );
        }
        return null;
    }

    @Override
    protected List<GUID > searchDirAndLinksFirstCase ( String partName ) {
        List<GUID > guids = this.dirManipulators[ 0 ].getGuidsByName( partName );
        if( guids != null && !guids.isEmpty() ) {
            return guids;
        }

        return this.imperialTree.fetchOriginalGuidRoot( partName );
    }

    @Override
    protected List<GUID > fetchDirsAllGuids(String partName ) {
        List<GUID > guids = this.dirManipulators[ 0 ].getGuidsByName( partName );
        guids.removeIf( guid -> !this.imperialTree.isRoot( guid ) );
        return guids;
    }
}
