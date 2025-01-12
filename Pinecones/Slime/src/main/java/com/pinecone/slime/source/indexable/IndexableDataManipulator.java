package com.pinecone.slime.source.indexable;

import com.pinecone.framework.system.prototype.Pinenut;

import java.util.Collection;
import java.util.List;

public interface IndexableDataManipulator<K, V > extends Pinenut {

    // Counting: szScopeKey
    long counts                ( IndexableTargetScopeMeta meta, String szScopeKey );

    // Counting: Namespace::key
    long countsByNS            ( IndexableTargetScopeMeta meta, String szNamespace, Object key );

    // Counting: Namespace
    long countsNS              ( IndexableTargetScopeMeta meta, String szNamespace );

    List query                 ( IndexableTargetScopeMeta meta, String szStatement );

    List<V > queryVal          ( IndexableTargetScopeMeta meta, String szStatement );

    Object selectAllByNS       ( IndexableTargetScopeMeta meta, String szNamespace, Object key );

    List<V > selectsByNS       ( IndexableTargetScopeMeta meta, String szNamespace, Object key );

    V    selectByNS            ( IndexableTargetScopeMeta meta, String szNamespace, Object key );

    V    selectByKey           ( IndexableTargetScopeMeta meta, Object key );

    void insertByNS            ( IndexableTargetScopeMeta meta, String szNamespace, K key, V entity );

    void insert                ( IndexableTargetScopeMeta meta, K key, V entity );

    void insert                ( IndexableTargetScopeMeta meta, K key, V entity, long expireMill );

    void updateByNS            ( IndexableTargetScopeMeta meta, String szNamespace, K key, V entity );

    void update                ( IndexableTargetScopeMeta meta, K key, V entity );

    void deleteByNS            ( IndexableTargetScopeMeta meta, String szNamespace, Object key );

    void deleteByKey           ( IndexableTargetScopeMeta meta, Object key );

    void purge                 ( IndexableTargetScopeMeta meta );

    void purgeByNS             ( IndexableTargetScopeMeta meta, String szNamespace );

    void commit                ();

}
