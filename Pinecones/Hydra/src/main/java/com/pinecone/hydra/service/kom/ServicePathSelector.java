package com.pinecone.hydra.service.kom;

import com.pinecone.framework.util.name.path.PathResolver;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;
import com.pinecone.hydra.system.ko.kom.StandardPathSelector;
import com.pinecone.hydra.unit.udtt.DistributedTrieTree;

public class ServicePathSelector extends StandardPathSelector {
    public ServicePathSelector(PathResolver pathResolver, DistributedTrieTree trieTree, GUIDNameManipulator dirMan, GUIDNameManipulator[] fileMans ) {
        super( pathResolver, trieTree, dirMan, fileMans );
    }
}
