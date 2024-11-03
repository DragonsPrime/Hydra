package com.pinecone.hydra.service.kom.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.entity.GenericServiceElement;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;

import java.util.List;

public interface ServiceNodeManipulator extends GUIDNameManipulator {
    //ServiceNode的CRUD
    void insert(GenericServiceElement serviceNode);
    void remove(GUID UUID);
    GenericServiceElement getServiceNode(GUID UUID);
    void update(GenericServiceElement serviceNode);
    List<GenericServiceElement> fetchServiceNodeByName(String name);

    @Override
    List<GUID> getGuidsByName(String name);

    @Override
    List<GUID> getGuidsByNameID(String name, GUID guid);
}
