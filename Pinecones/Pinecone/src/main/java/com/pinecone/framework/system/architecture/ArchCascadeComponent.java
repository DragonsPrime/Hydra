package com.pinecone.framework.system.architecture;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.framework.util.name.Namespace;
import com.pinecone.framework.util.name.UniNamespace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class ArchCascadeComponent extends ArchComponent implements CascadeComponent {
    private CascadeComponent                mParent;
    private Namespace                       mName;
    private Map<String, Component >         mChildren;

    protected ArchCascadeComponent( Namespace name, CascadeComponentManager manager, CascadeComponent parent ) {
        super( manager );
        this.mName     = name;
        this.mChildren = new LinkedTreeMap<>();

        if( name == null ) {
            this.setName( this.className() );
        }
        this.setParent( parent );
    }

    @Override
    public CascadeComponent parent() {
        return this.mParent;
    }

    @Override
    public void setParent( CascadeComponent parent ) {
        this.mParent = parent;
        if( parent != null ) {
            this.mName.setParent( parent.getName() );
        }
    }

    @Override
    public Namespace getName() {
        return this.mName;
    }

    @Override
    public void setName( Namespace name ) {
        this.mName = name;
    }

    @Override
    public void setName( String name ) {
        CascadeComponent.super.setName( name );
    }

    @Override
    public Collection<Component > children() {
        return this.mChildren.values();
    }

    protected Map<String, Component > getChildren() {
        return this.mChildren;
    }

    @Override
    public CascadeComponentManager getComponentManager() {
        return (CascadeComponentManager) super.getComponentManager();
    }

    @Override
    public void addChildComponent( CascadeComponent child ) {
        child.setParent( this );
        this.referChildComponent( child );
        this.getComponentManager().addComponent( child );
    }

    @Override
    public void referChildComponent    ( Component child ) {
        this.mChildren.put( child.getFullName(), child );
    }

    @Override
    public void detachChildComponent( String fullName ) {
        this.mChildren.remove( fullName );
    }

    public void removeChildComponent  ( @Nullable Component child, String fullName ) {
        if( child == null ) {
            child = this.getChildComponentByFullName( fullName );
        }

        if( child != null ) {
            this.detachChildComponent( fullName );
            if( child instanceof CascadeComponent && this.ownedChild( (CascadeComponent)child ) ) {
                this.getComponentManager().removeComponent( child );
            }
        }
    }

    @Override
    public void removeChildComponent  ( Component child ) {
        this.removeChildComponent( child, child.getFullName() );
    }

    @Override
    public void removeChildComponent  ( String fullName ) {
        this.removeChildComponent( null, fullName );
    }

    @Override
    public void clear() {
        this.mChildren.clear();
    }

    @Override
    public void independent( String newName ) {
        if( this.mParent != null ) {
            this.mParent = null;

            this.getComponentManager().detachComponent( this );

            this.mName.setName( newName );
            this.mName.setParent( null );

            this.getComponentManager().addComponent( this );
        }
    }

    @Override
    public void purge() {
        this.purgeChildren();

        String                szFN = this.getName().getFullName();
        if ( this.mParent != null ) {
            this.mParent.removeChildComponent( szFN );
        }
        this.getComponentManager().removeComponent( szFN );
    }

    @Override
    public void purgeChildren() {
        List<Component > purgeList = new ArrayList<>( this.mChildren.values() );
        for ( Component child : purgeList ) {
            this.mChildren.remove( child.getFullName() );

            if( child instanceof CascadeComponent ) {
                if( this.ownedChild( (CascadeComponent)child ) ) { // Purge owned child,
                    this.getComponentManager().removeComponent( child.getFullName() );
                }
            }
        }
    }

    @Override
    public boolean hasOwnChild( CascadeComponent child ) {
        Component component = this.getChildComponentByFullName( child.getFullName() );
        if( component instanceof CascadeComponent && component == child ) {
            return this.ownedChild( child );
        }
        return false;
    }

    @Override
    public boolean hasReferredChild( Component child ) {
        return this.mChildren.containsKey( child.getFullName() );
    }

    @Override
    public Component getChildComponentByFullName( String fullName ) {
        return this.mChildren.get( fullName );
    }

    @Override
    public int childSize() {
        return this.mChildren.size();
    }
}
