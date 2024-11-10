package com.pinecone.slime.jelly.source.ds.jdbc;

import java.lang.reflect.Constructor;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.Arrays;
import javax.sql.DataSource;

import com.pinecone.framework.unit.SingletonSupplier;
import com.pinecone.framework.util.SupplierUtils;
import com.pinecone.slime.jelly.source.ds.dao.CannotAcquireLockException;
import com.pinecone.slime.jelly.source.ds.dao.CannotSerializeTransactionException;
import com.pinecone.slime.jelly.source.ds.dao.DataAccessException;
import com.pinecone.slime.jelly.source.ds.dao.DataAccessResourceFailureException;
import com.pinecone.slime.jelly.source.ds.dao.DataIntegrityViolationException;
import com.pinecone.slime.jelly.source.ds.dao.DeadlockLoserDataAccessException;
import com.pinecone.slime.jelly.source.ds.dao.DuplicateKeyException;
import com.pinecone.slime.jelly.source.ds.dao.PermissionDeniedDataAccessException;
import com.pinecone.slime.jelly.source.ds.dao.TransientDataAccessResourceException;
import com.pinecone.framework.system.Nullable;

public class SQLErrorCodeSQLExceptionTranslator extends AbstractFallbackSQLExceptionTranslator {
    private static final int MESSAGE_ONLY_CONSTRUCTOR = 1;
    private static final int MESSAGE_THROWABLE_CONSTRUCTOR = 2;
    private static final int MESSAGE_SQLEX_CONSTRUCTOR = 3;
    private static final int MESSAGE_SQL_THROWABLE_CONSTRUCTOR = 4;
    private static final int MESSAGE_SQL_SQLEX_CONSTRUCTOR = 5;

    @Nullable
    private SingletonSupplier<SQLErrorCodes > sqlErrorCodes;

    public SQLErrorCodeSQLExceptionTranslator() {
        this.setFallbackTranslator(new SQLExceptionSubclassTranslator());
    }

    public SQLErrorCodeSQLExceptionTranslator( DataSource dataSource) {
        this();
        this.setDataSource(dataSource);
    }

    public SQLErrorCodeSQLExceptionTranslator(String dbName) {
        this();
        this.setDatabaseProductName(dbName);
    }

    public SQLErrorCodeSQLExceptionTranslator(SQLErrorCodes sec) {
        this();
        this.sqlErrorCodes = SingletonSupplier.of(sec);
    }

    public void setDataSource(DataSource dataSource) {
        this.sqlErrorCodes = SingletonSupplier.of(() -> {
            return SQLErrorCodesFactory.getInstance().resolveErrorCodes(dataSource);
        });
        this.sqlErrorCodes.get();
    }

    public void setDatabaseProductName(String dbName) {
        this.sqlErrorCodes = SingletonSupplier.of(SQLErrorCodesFactory.getInstance().getErrorCodes(dbName));
    }

    public void setSqlErrorCodes(@Nullable SQLErrorCodes sec) {
        this.sqlErrorCodes = SingletonSupplier.ofNullable(sec);
    }

    @Nullable
    public SQLErrorCodes getSqlErrorCodes() {
        return (SQLErrorCodes) SupplierUtils.resolve(this.sqlErrorCodes);
    }

    @Nullable
    protected DataAccessException doTranslate(String task, @Nullable String sql, SQLException ex) {
        SQLException sqlEx = ex;
        if (ex instanceof BatchUpdateException && ex.getNextException() != null) {
            SQLException nestedSqlEx = ex.getNextException();
            if (nestedSqlEx.getErrorCode() > 0 || nestedSqlEx.getSQLState() != null) {
                sqlEx = nestedSqlEx;
            }
        }

        DataAccessException dae = this.customTranslate(task, sql, sqlEx);
        if (dae != null) {
            return dae;
        } else {
            SQLErrorCodes sqlErrorCodes = this.getSqlErrorCodes();
            if (sqlErrorCodes != null) {
                SQLExceptionTranslator customTranslator = sqlErrorCodes.getCustomSqlExceptionTranslator();
                if (customTranslator != null) {
                    DataAccessException customDex = customTranslator.translate(task, sql, sqlEx);
                    if (customDex != null) {
                        return customDex;
                    }
                }
            }

            String errorCode;
            if (sqlErrorCodes != null) {
                if (sqlErrorCodes.isUseSqlStateForTranslation()) {
                    errorCode = sqlEx.getSQLState();
                } else {
                    SQLException current;
                    for(current = sqlEx; current.getErrorCode() == 0 && current.getCause() instanceof SQLException; current = (SQLException)current.getCause()) {
                    }

                    errorCode = Integer.toString(current.getErrorCode());
                }

                if (errorCode != null) {
                    CustomSQLErrorCodesTranslation[] customTranslations = sqlErrorCodes.getCustomTranslations();
                    if (customTranslations != null) {
                        CustomSQLErrorCodesTranslation[] var9 = customTranslations;
                        int var10 = customTranslations.length;

                        for(int var11 = 0; var11 < var10; ++var11) {
                            CustomSQLErrorCodesTranslation customTranslation = var9[var11];
                            if (Arrays.binarySearch(customTranslation.getErrorCodes(), errorCode) >= 0 && customTranslation.getExceptionClass() != null) {
                                DataAccessException customException = this.createCustomException(task, sql, sqlEx, customTranslation.getExceptionClass());
                                if (customException != null) {
                                    this.logTranslation(task, sql, sqlEx, true);
                                    return customException;
                                }
                            }
                        }
                    }

                    if (Arrays.binarySearch(sqlErrorCodes.getBadSqlGrammarCodes(), errorCode) >= 0) {
                        this.logTranslation(task, sql, sqlEx, false);
                        return new BadSqlGrammarException(task, sql != null ? sql : "", sqlEx);
                    }

                    if (Arrays.binarySearch(sqlErrorCodes.getInvalidResultSetAccessCodes(), errorCode) >= 0) {
                        this.logTranslation(task, sql, sqlEx, false);
                        return new InvalidResultSetAccessException(task, sql != null ? sql : "", sqlEx);
                    }

                    if (Arrays.binarySearch(sqlErrorCodes.getDuplicateKeyCodes(), errorCode) >= 0) {
                        this.logTranslation(task, sql, sqlEx, false);
                        return new DuplicateKeyException(this.buildMessage(task, sql, sqlEx), sqlEx);
                    }

                    if (Arrays.binarySearch(sqlErrorCodes.getDataIntegrityViolationCodes(), errorCode) >= 0) {
                        this.logTranslation(task, sql, sqlEx, false);
                        return new DataIntegrityViolationException(this.buildMessage(task, sql, sqlEx), sqlEx);
                    }

                    if (Arrays.binarySearch(sqlErrorCodes.getPermissionDeniedCodes(), errorCode) >= 0) {
                        this.logTranslation(task, sql, sqlEx, false);
                        return new PermissionDeniedDataAccessException(this.buildMessage(task, sql, sqlEx), sqlEx);
                    }

                    if (Arrays.binarySearch(sqlErrorCodes.getDataAccessResourceFailureCodes(), errorCode) >= 0) {
                        this.logTranslation(task, sql, sqlEx, false);
                        return new DataAccessResourceFailureException(this.buildMessage(task, sql, sqlEx), sqlEx);
                    }

                    if (Arrays.binarySearch(sqlErrorCodes.getTransientDataAccessResourceCodes(), errorCode) >= 0) {
                        this.logTranslation(task, sql, sqlEx, false);
                        return new TransientDataAccessResourceException(this.buildMessage(task, sql, sqlEx), sqlEx);
                    }

                    if (Arrays.binarySearch(sqlErrorCodes.getCannotAcquireLockCodes(), errorCode) >= 0) {
                        this.logTranslation(task, sql, sqlEx, false);
                        return new CannotAcquireLockException(this.buildMessage(task, sql, sqlEx), sqlEx);
                    }

                    if (Arrays.binarySearch(sqlErrorCodes.getDeadlockLoserCodes(), errorCode) >= 0) {
                        this.logTranslation(task, sql, sqlEx, false);
                        return new DeadlockLoserDataAccessException(this.buildMessage(task, sql, sqlEx), sqlEx);
                    }

                    if (Arrays.binarySearch(sqlErrorCodes.getCannotSerializeTransactionCodes(), errorCode) >= 0) {
                        this.logTranslation(task, sql, sqlEx, false);
                        return new CannotSerializeTransactionException(this.buildMessage(task, sql, sqlEx), sqlEx);
                    }
                }
            }

            if (this.logger.isDebugEnabled()) {
                if (sqlErrorCodes != null && sqlErrorCodes.isUseSqlStateForTranslation()) {
                    errorCode = "SQL state '" + sqlEx.getSQLState() + "', error code '" + sqlEx.getErrorCode();
                } else {
                    errorCode = "Error code '" + sqlEx.getErrorCode() + "'";
                }

                this.logger.debug("Unable to translate SQLException with " + errorCode + ", will now try the fallback translator");
            }

            return null;
        }
    }

    @Nullable
    protected DataAccessException customTranslate(String task, @Nullable String sql, SQLException sqlEx) {
        return null;
    }

    @Nullable
    protected DataAccessException createCustomException(String task, @Nullable String sql, SQLException sqlEx, Class<?> exceptionClass) {
        try {
            int constructorType = 0;
            Constructor<?>[] constructors = exceptionClass.getConstructors();
            Constructor[] var7 = constructors;
            int var8 = constructors.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                Constructor<?> constructor = var7[var9];
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length == 1 && String.class == parameterTypes[0] && constructorType < 1) {
                    constructorType = 1;
                }

                if (parameterTypes.length == 2 && String.class == parameterTypes[0] && Throwable.class == parameterTypes[1] && constructorType < 2) {
                    constructorType = 2;
                }

                if (parameterTypes.length == 2 && String.class == parameterTypes[0] && SQLException.class == parameterTypes[1] && constructorType < 3) {
                    constructorType = 3;
                }

                if (parameterTypes.length == 3 && String.class == parameterTypes[0] && String.class == parameterTypes[1] && Throwable.class == parameterTypes[2] && constructorType < 4) {
                    constructorType = 4;
                }

                if (parameterTypes.length == 3 && String.class == parameterTypes[0] && String.class == parameterTypes[1] && SQLException.class == parameterTypes[2] && constructorType < 5) {
                    constructorType = 5;
                }
            }

            Constructor exceptionConstructor;
            switch(constructorType) {
                case 1:
                    Class<?>[] messageOnlyArgsClass = new Class[]{String.class};
                    Object[] messageOnlyArgs = new Object[]{task + ": " + sqlEx.getMessage()};
                    exceptionConstructor = exceptionClass.getConstructor(messageOnlyArgsClass);
                    return (DataAccessException)exceptionConstructor.newInstance(messageOnlyArgs);
                case 2:
                    Class<?>[] messageAndThrowableArgsClass = new Class[]{String.class, Throwable.class};
                    Object[] messageAndThrowableArgs = new Object[]{task + ": " + sqlEx.getMessage(), sqlEx};
                    exceptionConstructor = exceptionClass.getConstructor(messageAndThrowableArgsClass);
                    return (DataAccessException)exceptionConstructor.newInstance(messageAndThrowableArgs);
                case 3:
                    Class<?>[] messageAndSqlExArgsClass = new Class[]{String.class, SQLException.class};
                    Object[] messageAndSqlExArgs = new Object[]{task + ": " + sqlEx.getMessage(), sqlEx};
                    exceptionConstructor = exceptionClass.getConstructor(messageAndSqlExArgsClass);
                    return (DataAccessException)exceptionConstructor.newInstance(messageAndSqlExArgs);
                case 4:
                    Class<?>[] messageAndSqlAndThrowableArgsClass = new Class[]{String.class, String.class, Throwable.class};
                    Object[] messageAndSqlAndThrowableArgs = new Object[]{task, sql, sqlEx};
                    exceptionConstructor = exceptionClass.getConstructor(messageAndSqlAndThrowableArgsClass);
                    return (DataAccessException)exceptionConstructor.newInstance(messageAndSqlAndThrowableArgs);
                case 5:
                    Class<?>[] messageAndSqlAndSqlExArgsClass = new Class[]{String.class, String.class, SQLException.class};
                    Object[] messageAndSqlAndSqlExArgs = new Object[]{task, sql, sqlEx};
                    exceptionConstructor = exceptionClass.getConstructor(messageAndSqlAndSqlExArgsClass);
                    return (DataAccessException)exceptionConstructor.newInstance(messageAndSqlAndSqlExArgs);
                default:
                    if (this.logger.isWarnEnabled()) {
                        this.logger.warn("Unable to find appropriate constructor of custom exception class [" + exceptionClass.getName() + "]");
                    }

                    return null;
            }
        }
        catch ( Throwable e ) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("Unable to instantiate custom exception class [" + exceptionClass.getName() + "]", e);
            }

            return null;
        }
    }

    private void logTranslation(String task, @Nullable String sql, SQLException sqlEx, boolean custom) {
        if (this.logger.isDebugEnabled()) {
            String intro = custom ? "Custom translation of" : "Translating";
            this.logger.debug(intro + " SQLException with SQL state '" + sqlEx.getSQLState() + "', error code '" + sqlEx.getErrorCode() + "', message [" + sqlEx.getMessage() + "]" + (sql != null ? "; SQL was [" + sql + "]" : "") + " for task [" + task + "]");
        }

    }
}

