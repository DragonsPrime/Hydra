package com.pinecone.hydra.service.kom.marshaling;

import java.util.Collection;
import java.util.Map;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.ServicesInstrument;
import com.pinecone.hydra.service.kom.entity.ApplicationElement;
import com.pinecone.hydra.service.kom.entity.ElementNode;
import com.pinecone.hydra.service.kom.entity.FolderElement;
import com.pinecone.hydra.service.kom.entity.GenericApplicationElement;
import com.pinecone.hydra.service.kom.entity.GenericNamespace;
import com.pinecone.hydra.service.kom.entity.GenericServiceElement;
import com.pinecone.hydra.service.kom.entity.Namespace;
import com.pinecone.hydra.service.kom.entity.ServiceElement;

public class ServicesJSONDecoder implements ServicesInstrumentDecoder {
    protected ServicesInstrument instrument;

    public ServicesJSONDecoder( ServicesInstrument instrument ) {
        this.instrument = instrument;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public ElementNode    decode( String szName, Object o, GUID parentGuid ) {
        if ( o instanceof Map ) {
            return (ElementNode) this.instrument.get( this.decodeJSONObject( szName, (Map<String, Object>) o, parentGuid ).getGuid() );
        }

        throw new IllegalArgumentException( "Elements of `ServersInstrument` should all be object." );
    }

    protected Namespace   newNamespace( String szName, Map<String, Object > jo ) {
        Namespace ns = new GenericNamespace( jo, this.instrument );
        ns.setName( szName );

        return ns;
    }

    protected Object[]    affirmNSExisted( String szName, GUID parentGuid, Map<String, Object > jo ) {
        Namespace ns = null;

        if( parentGuid == null ) {
            ElementNode rootE = this.instrument.queryElement( szName );
            if( rootE != null ) {
                if( rootE.evinceNamespace() == null ) {
                    throw new IllegalArgumentException(
                            String.format( "Existed child-destination [%s] should be namespace.", szName )
                    );
                }

                ns = rootE.evinceNamespace();
            }
        }
        else {
            ElementNode parentNode = (ElementNode)this.instrument.get( parentGuid );
            if( parentNode instanceof Namespace ) {
                Collection<ElementNode> destChildren = parentNode.evinceNamespace().fetchChildren();
                for( ElementNode node : destChildren ) {
                    if( szName.equals( node.getName() ) ) {
                        if( node instanceof Namespace ) {
                            ns = (Namespace) node;
                            break;
                        }
                        else {
                            throw new IllegalArgumentException(
                                    String.format( "<ServicesInstrument> Existed child-destination [%s] should be namespace.", szName )
                            );
                        }
                    }
                }
            }
        }


        GUID currentGuid;
        if( ns == null ) {
            ns = this.newNamespace( szName, jo );
            currentGuid  = this.instrument.put( ns );
            this.instrument.affirmOwnedNode( parentGuid, currentGuid );
        }
        else {
            currentGuid = ns.getGuid();
        }
        return new Object[] { ns, currentGuid };
    }

    protected Object[]    affirmAppExisted( String szName, GUID parentGuid, Map<String, Object > jo ) {
        ApplicationElement app = null;

        if( parentGuid == null ) {
            ElementNode rootE = this.instrument.queryElement( szName );
            if( rootE != null ) {
                if( rootE.evinceApplicationElement() == null ) {
                    throw new IllegalArgumentException(
                            String.format( "Existed child-destination [%s] should be `ApplicationElement`.", szName )
                    );
                }

                app = rootE.evinceApplicationElement();
            }
        }
        else {
            ElementNode parentNode = (ElementNode)this.instrument.get( parentGuid );
            if( parentNode instanceof Namespace ) {
                Collection<ElementNode> destChildren = parentNode.evinceNamespace().fetchChildren();
                for( ElementNode node : destChildren ) {
                    if( szName.equals( node.getName() ) ) {
                        if( node instanceof ApplicationElement ) {
                            app = (ApplicationElement) node;
                            break;
                        }
                        else {
                            throw new IllegalArgumentException(
                                    String.format( "Existed child-destination [%s] should be `ApplicationElement`.", szName )
                            );
                        }
                    }
                }
            }
        }



        ApplicationElement neo ;
        if( app == null ) {
            neo = new GenericApplicationElement( jo, this.instrument );
            neo.setName( szName );
        }
        else {
            neo = app;
        }
        return new Object[] { app, neo };
    }

    protected Object[]    affirmSerExisted( String szName, GUID parentGuid, Map<String, Object > jo ) {
        ServiceElement ser = null;

        if( parentGuid == null ) {
            ElementNode rootE = this.instrument.queryElement( szName );
            if( rootE != null ) {
                if( rootE.evinceServiceElement() == null ) {
                    throw new IllegalArgumentException(
                            String.format( "Existed child-destination [%s] should be `ServiceElement`.", szName )
                    );
                }

                ser = rootE.evinceServiceElement();
            }
        }
        else {
            ElementNode parentNode = (ElementNode)this.instrument.get( parentGuid );
            Collection<ElementNode> destChildren;
            if( parentNode instanceof FolderElement ) {
                destChildren = ( (FolderElement) parentNode ).fetchChildren();
                for( ElementNode node : destChildren ) {
                    if( szName.equals( node.getName() ) ) {
                        if( node instanceof ServiceElement ) {
                            ser = (ServiceElement) node;
                            break;
                        }
                        else {
                            throw new IllegalArgumentException(
                                    String.format( "Existed child-destination [%s] should be `ServiceElement`.", szName )
                            );
                        }
                    }
                }
            }
            else {
                throw new IllegalStateException(
                        String.format( "Parent of `ServiceElement` [%s] should be `FolderElement`.", szName )
                );
            }
        }



        ServiceElement neo ;
        if( ser == null ) {
            neo = new GenericServiceElement( jo, this.instrument );
            neo.setName( szName );
        }
        else {
            neo = ser;
        }
        return new Object[] { ser, neo };
    }

    protected Object[]    decodeExternalElements( String szMetaType, String szName, GUID parentGuid, Map<String, Object > jo ) throws IllegalArgumentException {
        throw new IllegalArgumentException( "Unknown metaType '" + szMetaType + "'." );
    }

    protected void        decodeChildren ( Map jo, GUID currentGuid ) {
        for ( Object o : jo.entrySet() ) {
            Map.Entry kv = (Map.Entry) o;
            Object   val = kv.getValue();
            if( val instanceof Map ) {
                this.decode( kv.getKey().toString(), val, currentGuid );
            }
        }
    }

    protected ElementNode decodeJSONObject( String szName, Map<String, Object > jo, GUID parentGuid ) {
        String szMetaType = (String) jo.get( "metaType" );
        boolean isNamespace = szMetaType == null || szMetaType.equals( Namespace.class.getSimpleName() );
        ElementNode elementNode;
        GUID currentGuid;

        if ( isNamespace ) {
            Object[] pair = this.affirmNSExisted( szName, parentGuid, jo );
            Namespace     ns = (Namespace) pair[ 0 ];
            currentGuid      = (GUID)      pair[ 1 ];

            this.decodeChildren( jo, currentGuid );

            elementNode = ns;
        }
        else {
            Object[] pair;
            boolean bIsFolderElement = false;
            if( szMetaType.equals( ApplicationElement.class.getSimpleName() ) ) {
                pair = this.affirmAppExisted( szName, parentGuid, jo );
                bIsFolderElement = true;
            }
            else if( szMetaType.equals( ServiceElement.class.getSimpleName() ) ) {
                pair = this.affirmSerExisted( szName, parentGuid, jo );
            }
            else {
                try{
                    pair = this.decodeExternalElements( szMetaType, szName, parentGuid, jo );
                }
                catch ( RuntimeException e ) {
                    throw new IllegalArgumentException( e );
                }
            }

            ElementNode          arc = (ElementNode) pair[ 0 ];
            ElementNode          neo = (ElementNode) pair[ 1 ];

            if( arc == null ) {
                currentGuid = this.instrument.put( neo );
                this.instrument.affirmOwnedNode( parentGuid, currentGuid );
            }
            else {
                currentGuid = arc.getGuid();
                this.instrument.update( neo );
            }

            if( bIsFolderElement ) {
                Object services = jo.get( "services" );
                if( services instanceof Map ) {
                    Map joSer = (Map) services;
                    this.decodeChildren( joSer, currentGuid );
                }
            }

            elementNode = neo;
        }

        return elementNode;
    }

}
