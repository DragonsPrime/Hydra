package com.pinecone.hydra.system.ko.runtime;

import java.util.List;

import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.lang.DynamicFactory;
import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.ko.CascadeInstrument;
import com.pinecone.hydra.system.ko.KernelObjectConfig;
import com.pinecone.hydra.system.ko.kom.KOMInstrument;
import com.pinecone.hydra.unit.imperium.ImperialTree;
import com.pinecone.hydra.unit.imperium.entity.EntityNode;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;
import com.pinecone.framework.util.id.GuidAllocator;

public abstract class ArchRuntimeKOMTree implements RuntimeInstrument {
    protected Namespace             mThisNamespace;
    protected KOMInstrument         mParentInstrument;

    protected Hydrarum              hydrarum;
    protected Processum             superiorProcess;

    protected GuidAllocator         guidAllocator;

    protected DynamicFactory        dynamicFactory;

    //************************************** CascadeInstrument **************************************
    @Override
    public KOMInstrument parent() {
        return this.mParentInstrument;
    }

    @Override
    public Processum getSuperiorProcess() {
        return this.superiorProcess;
    }

    @Override
    public void setParent( CascadeInstrument parent ) {
        this.mParentInstrument = (KOMInstrument) parent;
    }

    @Override
    public Namespace getTargetingName() {
        return this.mThisNamespace;
    }

    @Override
    public void setTargetingName( Namespace name ) {
        this.mThisNamespace = name;
    }

    //************************************** CascadeInstrument End **************************************


    @Override
    public GuidAllocator getGuidAllocator() {
        return this.guidAllocator;
    }













    @Override
    public KernelObjectConfig getConfig() {
        return null;
    }

    @Override
    public String getPath( GUID guid ) {
        return null;
    }

    @Override
    public String getFullName( GUID guid ) {
        return null;
    }

    @Override
    public GUID queryGUIDByPath( String path ) {
        return null;
    }

    @Override
    public GUID queryGUIDByFN( String fullName ) {
        return null;
    }

    @Override
    public boolean contains( GUID nodeGuid ) {
        return false;
    }

    @Override
    public GUID put( TreeNode treeNode ) {
        return null;
    }

    @Override
    public TreeNode get( GUID guid ) {
        return null;
    }

    @Override
    public TreeNode get( GUID guid, int depth ) {
        return null;
    }

    @Override
    public TreeNode getSelf( GUID guid ) {
        return null;
    }

    @Override
    public void remove( GUID guid ) {

    }

    @Override
    public void remove( String path ) {

    }

    @Override
    public void rename( GUID guid, String name ) {

    }

    @Override
    public List<TreeNode> getChildren( GUID guid ) {
        return null;
    }

    @Override
    public List<GUID> fetchChildrenGuids( GUID guid ) {
        return null;
    }

    @Override
    public List<? extends TreeNode> fetchRoot() {
        return null;
    }

    @Override
    public Object queryEntityHandleByNS( String path, String szBadSep, String szTargetSep ) {
        return null;
    }

    @Override
    public EntityNode queryNode( String path ) {
        return null;
    }

    @Override
    public GUID queryGUIDByNS( String path, String szBadSep, String szTargetSep ) {
        return null;
    }

    @Override
    public ImperialTree getMasterTrieTree() {
        return null;
    }
}
