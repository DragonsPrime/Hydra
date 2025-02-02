package com.pinecone.framework.util.lang;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.lang.iterator.JarEntryIterator;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

public class JarPackageCollectorAdapter implements PathNamespaceCollectum {
    @Override
    public boolean matched( String szProtocol ) {
        return szProtocol.toLowerCase().equals( NamespaceCollector.KEY_JAR_PROTOCOL );
    }

    @Override
    public void collect         ( String szResourcePath, String szPackageName, List<String > packageNames, boolean bCollectChildren ) {
        this.collect0( szResourcePath, szPackageName, packageNames, bCollectChildren );
    }

    @Override
    public String collectFirst  ( String szResourcePath, String szNSName ) {
        return this.collect0( szResourcePath, szNSName, null, false );
    }

    public String collect0 ( String szResourcePath, String szNSName, List<String > packageNames, boolean bCollectChildren ) {
        try {
            JarEntryIterator iterator        = new JarEntryIterator( szResourcePath );
            Enumeration<JarEntry> entries    = iterator.entries ();
            String packagePath               = iterator.getPackagePath();
            String classesScopePath          = iterator.getClassesScopePath();

            while ( entries.hasMoreElements () ) {
                JarEntry jarEntry = entries.nextElement ();
                String entryName  = jarEntry.getName ();
                if( jarEntry.isDirectory() ) {
                    if( classesScopePath != null && entryName.startsWith( classesScopePath ) ) {
                        entryName = entryName.replace( classesScopePath, "" );
                    }

                    if ( bCollectChildren && packageNames != null ) { // [@Harold Notice] No need for recursion, for JAR files, this flag is usually processed in a tiled manner
                        if ( entryName.startsWith ( packagePath ) && !entryName.equals( packagePath + NamespaceCollector.RESOURCE_NAME_SEPARATOR ) ) {
                            entryName = entryName.replace ( NamespaceCollector.RESOURCE_NAME_SEPARATOR, NamespaceCollector.JAVA_PKG_CLASS_SEPARATOR );
                            entryName = entryName.substring ( 0, entryName.lastIndexOf ( NamespaceCollector.JAVA_PKG_CLASS_SEPARATOR ) );
                            packageNames.add ( entryName );
                        }
                    }
                    else {
                        //Debug.trace( jarEntry.getName(),packagePath  );

                        if ( entryName.startsWith ( packagePath ) ) {
                            String childSegment = entryName.substring ( packagePath.length() );
                            if( StringUtils.countOccurrencesOf( childSegment, NamespaceCollector.RESOURCE_NAME_SEPARATOR, 3 ) > 2 ) {
                                continue;
                            }
                            if( entryName.equals( packagePath + NamespaceCollector.RESOURCE_NAME_SEPARATOR ) ) { // Self path
                                continue;
                            }

                            entryName = entryName.replace ( NamespaceCollector.RESOURCE_NAME_SEPARATOR, NamespaceCollector.JAVA_PKG_CLASS_SEPARATOR );
                            entryName = entryName.substring ( 0, entryName.lastIndexOf ( NamespaceCollector.JAVA_PKG_CLASS_SEPARATOR ) );

                            if( packageNames == null ) {
                                return entryName;
                            }
                            else {
                                packageNames.add ( entryName );
                            }
                        }
                    }
                }
            }
        }
        catch ( IOException e ) {
            throw new ProxyProvokeHandleException( e );
        }

        return null;
    }

}