package com.pinecone.hydra.unit.imperium.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.imperium.LinkedType;

import java.util.List;

public interface TireOwnerManipulator extends Pinenut {
    void insertRootNode ( GUID guid, LinkedType linkedType );

    default void insertRootNode ( GUID guid ) {
        this.insertRootNode( guid, LinkedType.Owned );
    }

    void insert( GUID targetGuid, GUID parentGUID, LinkedType linkedType );

    default void insertOwnedNode( GUID targetGuid, GUID parentGUID ) {
        this.insert( targetGuid, parentGUID, LinkedType.Owned );
    }

    default void insertHardLinkedNode( GUID targetGuid, GUID parentGUID ) {
        this.insert( targetGuid, parentGUID, LinkedType.Hard );
    }



    void update( GUID targetGuid, GUID parentGUID, LinkedType linkedType );

    void updateParentGuid( GUID targetGuid, GUID parentGUID );

    void updateLinkedType( GUID targetGuid, LinkedType linkedType );



    void remove( GUID subordinateGuid, GUID ownerGuid );

    void removeBySubordinate( GUID subordinateGuid );

    void removeByOwner( GUID OwnerGuid );

    GUID getOwner( GUID subordinateGuid );

    List<GUID > getSubordinates( GUID guid );



    void setLinkedType             ( GUID sourceGuid, GUID targetGuid, LinkedType linkedType );

    default void setOwned          ( GUID sourceGuid, GUID targetGuid ) {
        this.setLinkedType( sourceGuid, targetGuid, LinkedType.Owned );
    }

    default void setHardLink       ( GUID sourceGuid, GUID targetGuid ) {
        this.setLinkedType( sourceGuid, targetGuid, LinkedType.Hard );
    }

    LinkedType getLinkedType       ( GUID childGuid,GUID parentGuid );
}
