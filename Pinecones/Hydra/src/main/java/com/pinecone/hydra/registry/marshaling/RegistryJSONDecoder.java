package com.pinecone.hydra.registry.marshaling;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.registry.KOMRegistry;
import com.pinecone.hydra.registry.entity.ElementNode;
import com.pinecone.hydra.registry.entity.GenericNamespace;
import com.pinecone.hydra.registry.entity.GenericProperties;
import com.pinecone.hydra.registry.entity.GenericTextFile;
import com.pinecone.hydra.registry.entity.GenericTextValue;
import com.pinecone.hydra.registry.entity.Namespace;
import com.pinecone.hydra.registry.entity.Properties;
import com.pinecone.hydra.registry.entity.RegistryTreeNode;
import com.pinecone.hydra.registry.entity.TextFile;
import com.pinecone.hydra.registry.entity.TextValueTypes;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

public class RegistryJSONDecoder implements RegistryDecoder {
    protected KOMRegistry registry;

    public RegistryJSONDecoder( KOMRegistry registry ) {
        this.registry = registry;
    }

    protected boolean isPropertiesFormat( Map jo ) {
        boolean b = false;
        for( Object o : jo.entrySet() ) {
            Map.Entry kv = (Map.Entry) o;
            if( kv.getValue() instanceof Map ) {
                b = true;
            }
            if( kv.getValue() instanceof List ) {
                b = true;
            }
        }
        return b;
    }

    protected boolean isPropertiesFormat( List jo ) {
        boolean b = false;
        for( Object o : jo ) {
            if( o instanceof Map ) {
                b = true;
            }
            if( o instanceof List ) {
                b = true;
            }
        }
        return b;
    }

    @Override
    public ElementNode decode( String szName, Object o, GUID parentGuid ) {
        if ( o instanceof Map ) {
            return (ElementNode) this.registry.get( this.decodeJSONObject( szName, (Map) o, parentGuid ).getGuid() );
        }
        else if ( o instanceof List ) {
            return (ElementNode) this.registry.get( this.decodeJSONArray(szName, (List) o, parentGuid).getGuid() );
        }

        // Handling text file as a leaf node
        TextFile file = new GenericTextFile(this.registry);
        file.setName( szName );
        this.registry.put( file );
        file.put( new GenericTextValue( file.getGuid(), o.toString(), TextValueTypes.queryType(o) ) );
        this.registry.affirmOwnedNode( parentGuid, file.getGuid() );
        return file;
    }

    protected Namespace newNamespace( String szName ) {
        Namespace ns = new GenericNamespace( this.registry );
        ns.setName( szName );

        return ns;
    }

    protected Object[]   affirmNSExisted( String szName, GUID parentGuid ) {
        Namespace ns = null;

        if( parentGuid == null ) {
            ElementNode rootE = this.registry.queryElement( szName );
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
            ElementNode parentNode = (ElementNode)this.registry.get( parentGuid );
            if( parentNode instanceof Namespace ) {
                Collection<RegistryTreeNode> destChildren = parentNode.evinceNamespace().getChildren().values();
                for( TreeNode node : destChildren ) {
                    if( szName.equals( node.getName() ) ) {
                        if( node instanceof Namespace ) {
                            ns = (Namespace) node;
                            break;
                        }
                        else {
                            throw new IllegalArgumentException(
                                    String.format( "<Registry> Existed child-destination [%s] should be namespace.", szName )
                            );
                        }
                    }
                }
            }
        }


        GUID currentGuid;
        if( ns == null ) {
            ns = this.newNamespace( szName );
            currentGuid  = this.registry.put( ns );
            this.registry.affirmOwnedNode( parentGuid, currentGuid );
        }
        else {
            currentGuid = ns.getGuid();
        }
        return new Object[] { ns, currentGuid };
    }

    protected Object[]   affirmPrExisted( String szName, GUID parentGuid ) {
        Properties pr = null;

        if( parentGuid == null ) {
            ElementNode rootE = this.registry.queryElement( szName );
            if( rootE != null ) {
                if( rootE.evinceProperties() == null ) {
                    throw new IllegalArgumentException(
                            String.format( "Existed child-destination [%s] should be properties.", szName )
                    );
                }

                pr = rootE.evinceProperties();
            }
        }
        else {
            ElementNode parentNode = (ElementNode)this.registry.get( parentGuid );
            if( parentNode instanceof Namespace ) {
                Collection<RegistryTreeNode> destChildren = parentNode.evinceNamespace().getChildren().values();
                for( TreeNode node : destChildren ) {
                    if( szName.equals( node.getName() ) ) {
                        if( node instanceof Properties ) {
                            pr = (Properties) node;
                            break;
                        }
                        else {
                            throw new IllegalArgumentException(
                                    String.format( "Existed child-destination [%s] should be properties.", szName )
                            );
                        }
                    }
                }
            }
        }



        Properties neo ;
        if( pr == null ) {
            neo = new GenericProperties( this.registry );
            neo.setName( szName );
        }
        else {
            neo = pr;
        }
        return new Object[] { pr, neo };
    }

    protected ElementNode decodeJSONObject( String szName, Map jo, GUID parentGuid ) {
        boolean isNamespace = this.isPropertiesFormat(jo);
        ElementNode elementNode;
        GUID currentGuid;

        if ( isNamespace ) {
            Object[] pair = this.affirmNSExisted( szName, parentGuid );
            Namespace     ns = (Namespace) pair[ 0 ];
            currentGuid      = (GUID)      pair[ 1 ];

            for ( Object o : jo.entrySet() ) {
                Map.Entry kv = (Map.Entry) o;
                this.decode( kv.getKey().toString(), kv.getValue(), currentGuid );
            }

            elementNode = ns;
        }
        else {
            Object[] pair = this.affirmPrExisted( szName, parentGuid );
            Properties   prX = (Properties) pair[ 0 ];
            Properties   pro = (Properties) pair[ 1 ];

            for ( Object o : jo.entrySet() ) {
                Map.Entry kv = (Map.Entry) o;
                pro.put( kv.getKey().toString(), kv.getValue() );
            }

            if( prX == null ) {
                currentGuid = this.registry.put( pro );
                this.registry.affirmOwnedNode( parentGuid, currentGuid );
            }
            elementNode = pro;
        }

        return elementNode;
    }

    protected ElementNode decodeJSONArray( String szName, List ja, GUID parentGuid ) {
        boolean isNamespace = this.isPropertiesFormat(ja);
        ElementNode elementNode;
        GUID currentGuid;

        if ( isNamespace ) {
            Object[] pair = this.affirmNSExisted( szName, parentGuid );
            Namespace     ns = (Namespace) pair[ 0 ];
            currentGuid = (GUID)      pair[ 1 ];

            int i = 0;
            for ( Object o : ja ) {
                this.decode( Integer.toString(i), o, currentGuid );
                ++i;
            }

            elementNode = ns;
        }
        else {
            Object[] pair = this.affirmPrExisted( szName, parentGuid );
            Properties   prX = (Properties) pair[ 0 ];
            Properties   pro = (Properties) pair[ 1 ];

            int i = 0;
            for ( Object o : ja ) {
                pro.put( Integer.toString(i), o );
                ++i;
            }
            if( prX == null ) {
                currentGuid = this.registry.put( pro );
                this.registry.affirmOwnedNode( parentGuid, currentGuid );
            }
            elementNode = pro;
        }

        return elementNode;
    }
}
