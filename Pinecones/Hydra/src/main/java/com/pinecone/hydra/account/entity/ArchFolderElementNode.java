package com.pinecone.hydra.account.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.AccountManager;

import java.util.ArrayList;
import java.util.List;

public class ArchFolderElementNode extends ArchElementNode implements FolderElement{

    public ArchFolderElementNode(){
        super();
    }
    public ArchFolderElementNode(AccountManager accountManager){
        super(accountManager);
    }

    @Override
    public List<ElementNode> fetchChildren() {
        ArrayList<ElementNode> elementNodes = new ArrayList<>();
        List<GUID> guids = this.fetchChildrenGuids();
        for( GUID elementGuid : guids ){
            ElementNode node = (ElementNode)this.accountManager.get(elementGuid);
            elementNodes.add( node );
        }
        return elementNodes;
    }

    @Override
    public List<GUID> fetchChildrenGuids() {
        return this.accountManager.fetchChildrenGuids(this.getGuid());
    }

    @Override
    public void addChild(ElementNode child) {
        GUID childId;
        boolean bContainsChild = this.containsChild( child.getName() );
        if( bContainsChild ) {
            return;
        }
        else {
            childId = this.accountManager.put( child );
        }


        this.accountManager.addChildren( this.guid, childId );
    }

    @Override
    public boolean containsChild(String childName) {
        return this.accountManager.containsChild( this.guid, childName );
    }
}
