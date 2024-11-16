package com.pinecone.framework.system.architecture;

import com.pinecone.framework.system.regimentation.CascadeNodus;
import com.pinecone.framework.util.name.Namespace;

import java.util.Collection;

public interface CascadeComponent extends Component, CascadeNodus {
    @Override
    CascadeComponent parent();

    void setParent( CascadeComponent parent );

    @Override
    default boolean isRoot() {
        return this.parent() == null;
    }

    @Override
    default CascadeComponent root() {
        return (CascadeComponent) CascadeNodus.super.root();
    }

    Collection<Component > children();

    @Override
    Namespace getTargetingName();

    @Override
    void setTargetingName( Namespace name );

    @Override
    default void setTargetingName( String name ) {
        CascadeNodus.super.setTargetingName( name );
    }

    @Override
    default String getSimpleName() {
        return this.getTargetingName().getSimpleName();
    }

    @Override
    default String getFullName() {
        return this.getTargetingName().getFullName();
    }

    CascadeComponentManager getComponentManager();

    void addChildComponent      ( CascadeComponent child ) ;


    void detachChildComponent   ( String fullName );

    void referChildComponent    ( Component child ) ;

    void removeChildComponent   ( Component child );

    void removeChildComponent   ( String fullName ) ;

    default boolean ownedChild  ( CascadeComponent child ) {
        return child.parent() == this;
    }

    boolean hasOwnChild         ( CascadeComponent child ) ;

    boolean hasReferredChild    ( Component child ) ;

    Component getChildComponentByFullName( String fullName ) ;


    // Only clear all children reference.
    void clear() ;

    // if this has parent, mark it as null, and elevated to a root node.
    void independent( String newName );

    // Purge itself and its own children
    void purge();

    // Purge its own children
    void purgeChildren();

    int childSize() ;
}
