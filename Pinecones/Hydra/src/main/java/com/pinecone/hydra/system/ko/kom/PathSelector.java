package com.pinecone.hydra.system.ko.kom;

import java.util.List;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.util.id.GUID;

public interface PathSelector extends KOMSelector {
    GUID searchGUID( String[] parts );

    GUID searchGUID( String[] parts, @Nullable String[] lpResolvedPath );

    GUID searchGUID( List<String > resolvedParts );


    GUID searchGUID( GUID parentID, String[] parts );

    GUID searchGUID( GUID parentID, String[] parts, @Nullable String[] lpResolvedPath );

    GUID searchGUID( GUID parentID, List<String > resolvedParts );
}
