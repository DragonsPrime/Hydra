package com.pinecone.slime.jelly.source.ds.jdbc;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.unit.ConcurrentReferenceHashMap;
import com.pinecone.framework.util.Assert;
import com.pinecone.framework.util.PatternMatchUtils;

public class SQLErrorCodesFactory implements Pinenut {
    private static final Log logger = LogFactory.getLog(SQLErrorCodesFactory.class);
    private static final SQLErrorCodesFactory instance = new SQLErrorCodesFactory();
    private final Map<String, SQLErrorCodes > errorCodesMap;
    private final Map<DataSource, SQLErrorCodes > dataSourceCache = new ConcurrentReferenceHashMap<>(16);

    public static SQLErrorCodesFactory getInstance() {
        return instance;
    }

    protected SQLErrorCodesFactory() {
        Map errorCodes = Collections.emptyMap();
        /*try {
            DefaultListableBeanFactory lbf = new DefaultListableBeanFactory();
            lbf.setBeanClassLoader(this.getClass().getClassLoader());
            XmlBeanDefinitionReader bdr = new XmlBeanDefinitionReader(lbf);
            Resource resource = this.loadResource("org/springframework/jdbc/support/sql-error-codes.xml");
            if (resource != null && resource.exists()) {
                bdr.loadBeanDefinitions(resource);
            } else {
                logger.info("Default sql-error-codes.xml not found (should be included in spring-jdbc jar)");
            }

            resource = this.loadResource("sql-error-codes.xml");
            if (resource != null && resource.exists()) {
                bdr.loadBeanDefinitions(resource);
                logger.debug("Found custom sql-error-codes.xml file at the root of the classpath");
            }

            errorCodes = lbf.getBeansOfType(SQLErrorCodes.class, true, false);
            if (logger.isTraceEnabled()) {
                logger.trace("SQLErrorCodes loaded: " + errorCodes.keySet());
            }
        }
        catch (BeansException var5) {
            logger.warn("Error loading SQL error codes from config file", var5);
            errorCodes = Collections.emptyMap();
        }*/

        this.errorCodesMap = errorCodes;
    }

    public SQLErrorCodes getErrorCodes(String databaseName) {
        Assert.notNull(databaseName, "Database product name must not be null");
        SQLErrorCodes sec = (SQLErrorCodes)this.errorCodesMap.get(databaseName);
        if (sec == null) {
            Iterator iter = this.errorCodesMap.values().iterator();

            while(iter.hasNext()) {
                SQLErrorCodes candidate = (SQLErrorCodes)iter.next();
                if ( PatternMatchUtils.simpleMatch(candidate.getDatabaseProductNames(), databaseName) ) {
                    sec = candidate;
                    break;
                }
            }
        }

        if (sec != null) {
            //this.checkCustomTranslatorRegistry(databaseName, sec);
            if (logger.isDebugEnabled()) {
                logger.debug("SQL error codes for '" + databaseName + "' found");
            }

            return sec;
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("SQL error codes for '" + databaseName + "' not found");
            }

            return new SQLErrorCodes();
        }
    }

    public SQLErrorCodes getErrorCodes(DataSource dataSource) {
        SQLErrorCodes sec = this.resolveErrorCodes(dataSource);
        return sec != null ? sec : new SQLErrorCodes();
    }

    @Nullable
    public SQLErrorCodes resolveErrorCodes( DataSource dataSource ) {
        Assert.notNull(dataSource, "DataSource must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Looking up default SQLErrorCodes for DataSource [" + this.identify(dataSource) + "]");
        }

        SQLErrorCodes sec = (SQLErrorCodes)this.dataSourceCache.get(dataSource);
        if (sec == null) {
            synchronized(this.dataSourceCache) {
                sec = (SQLErrorCodes)this.dataSourceCache.get(dataSource);
                if (sec == null) {
                    // TODO, implement JDBC support.
//                    try {
//                        String name = (String)JdbcUtils.extractDatabaseMetaData(dataSource, DatabaseMetaData::getDatabaseProductName);
//                        if ( StringUtils.hasLength( name ) ) {
//                            return this.registerDatabase(dataSource, name);
//                        }
//                    }
//                    catch ( MetaDataAccessException e ) {
//                        logger.warn("Error while extracting database name", e);
//                    }

                    return null;
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("SQLErrorCodes found in cache for DataSource [" + this.identify(dataSource) + "]");
        }

        return sec;
    }

    public SQLErrorCodes registerDatabase(DataSource dataSource, String databaseName) {
        SQLErrorCodes sec = this.getErrorCodes(databaseName);
        if (logger.isDebugEnabled()) {
            logger.debug("Caching SQL error codes for DataSource [" + this.identify(dataSource) + "]: database product name is '" + databaseName + "'");
        }

        this.dataSourceCache.put(dataSource, sec);
        return sec;
    }

    @Nullable
    public SQLErrorCodes unregisterDatabase(DataSource dataSource) {
        return (SQLErrorCodes)this.dataSourceCache.remove(dataSource);
    }

    private String identify(DataSource dataSource) {
        return dataSource.getClass().getName() + '@' + Integer.toHexString(dataSource.hashCode());
    }
}