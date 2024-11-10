package com.pinecone.slime.jelly.source.ds.dao;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.system.prototype.Pinenut;

@FunctionalInterface
public interface PersistenceExceptionTranslator extends Pinenut {
    @Nullable
    DataAccessException translateExceptionIfPossible(RuntimeException e );
}