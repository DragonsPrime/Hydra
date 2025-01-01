package com.pinecone.slime.jelly.source.ds.jdbc;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.ReflectionUtils;

public class SQLErrorCodes implements Pinenut {
    @Nullable
    private String[] databaseProductNames;
    private boolean useSqlStateForTranslation = false;
    private String[] badSqlGrammarCodes = new String[0];
    private String[] invalidResultSetAccessCodes = new String[0];
    private String[] duplicateKeyCodes = new String[0];
    private String[] dataIntegrityViolationCodes = new String[0];
    private String[] permissionDeniedCodes = new String[0];
    private String[] dataAccessResourceFailureCodes = new String[0];
    private String[] transientDataAccessResourceCodes = new String[0];
    private String[] cannotAcquireLockCodes = new String[0];
    private String[] deadlockLoserCodes = new String[0];
    private String[] cannotSerializeTransactionCodes = new String[0];
    @Nullable
    private CustomSQLErrorCodesTranslation[] customTranslations;
    @Nullable
    private SQLExceptionTranslator customSqlExceptionTranslator;

    public SQLErrorCodes() {
    }

    public void setDatabaseProductName(@Nullable String databaseProductName) {
        this.databaseProductNames = new String[]{databaseProductName};
    }

    @Nullable
    public String getDatabaseProductName() {
        return this.databaseProductNames != null && this.databaseProductNames.length > 0 ? this.databaseProductNames[0] : null;
    }

    public void setDatabaseProductNames(@Nullable String... databaseProductNames) {
        this.databaseProductNames = databaseProductNames;
    }

    @Nullable
    public String[] getDatabaseProductNames() {
        return this.databaseProductNames;
    }

    public void setUseSqlStateForTranslation(boolean useStateCodeForTranslation) {
        this.useSqlStateForTranslation = useStateCodeForTranslation;
    }

    public boolean isUseSqlStateForTranslation() {
        return this.useSqlStateForTranslation;
    }

    public void setBadSqlGrammarCodes(String... badSqlGrammarCodes) {
        this.badSqlGrammarCodes = StringUtils.sortStringArray(badSqlGrammarCodes);
    }

    public String[] getBadSqlGrammarCodes() {
        return this.badSqlGrammarCodes;
    }

    public void setInvalidResultSetAccessCodes(String... invalidResultSetAccessCodes) {
        this.invalidResultSetAccessCodes = StringUtils.sortStringArray(invalidResultSetAccessCodes);
    }

    public String[] getInvalidResultSetAccessCodes() {
        return this.invalidResultSetAccessCodes;
    }

    public String[] getDuplicateKeyCodes() {
        return this.duplicateKeyCodes;
    }

    public void setDuplicateKeyCodes(String... duplicateKeyCodes) {
        this.duplicateKeyCodes = duplicateKeyCodes;
    }

    public void setDataIntegrityViolationCodes(String... dataIntegrityViolationCodes) {
        this.dataIntegrityViolationCodes = StringUtils.sortStringArray(dataIntegrityViolationCodes);
    }

    public String[] getDataIntegrityViolationCodes() {
        return this.dataIntegrityViolationCodes;
    }

    public void setPermissionDeniedCodes(String... permissionDeniedCodes) {
        this.permissionDeniedCodes = StringUtils.sortStringArray(permissionDeniedCodes);
    }

    public String[] getPermissionDeniedCodes() {
        return this.permissionDeniedCodes;
    }

    public void setDataAccessResourceFailureCodes(String... dataAccessResourceFailureCodes) {
        this.dataAccessResourceFailureCodes = StringUtils.sortStringArray(dataAccessResourceFailureCodes);
    }

    public String[] getDataAccessResourceFailureCodes() {
        return this.dataAccessResourceFailureCodes;
    }

    public void setTransientDataAccessResourceCodes(String... transientDataAccessResourceCodes) {
        this.transientDataAccessResourceCodes = StringUtils.sortStringArray(transientDataAccessResourceCodes);
    }

    public String[] getTransientDataAccessResourceCodes() {
        return this.transientDataAccessResourceCodes;
    }

    public void setCannotAcquireLockCodes(String... cannotAcquireLockCodes) {
        this.cannotAcquireLockCodes = StringUtils.sortStringArray(cannotAcquireLockCodes);
    }

    public String[] getCannotAcquireLockCodes() {
        return this.cannotAcquireLockCodes;
    }

    public void setDeadlockLoserCodes(String... deadlockLoserCodes) {
        this.deadlockLoserCodes = StringUtils.sortStringArray(deadlockLoserCodes);
    }

    public String[] getDeadlockLoserCodes() {
        return this.deadlockLoserCodes;
    }

    public void setCannotSerializeTransactionCodes(String... cannotSerializeTransactionCodes) {
        this.cannotSerializeTransactionCodes = StringUtils.sortStringArray(cannotSerializeTransactionCodes);
    }

    public String[] getCannotSerializeTransactionCodes() {
        return this.cannotSerializeTransactionCodes;
    }

    public void setCustomTranslations(CustomSQLErrorCodesTranslation... customTranslations) {
        this.customTranslations = customTranslations;
    }

    @Nullable
    public CustomSQLErrorCodesTranslation[] getCustomTranslations() {
        return this.customTranslations;
    }

    public void setCustomSqlExceptionTranslatorClass( @Nullable Class<? extends SQLExceptionTranslator> customTranslatorClass ) {
        if (customTranslatorClass != null) {
            try {
                this.customSqlExceptionTranslator = (SQLExceptionTranslator) ReflectionUtils.accessibleConstructor(customTranslatorClass, new Class[0]).newInstance();
            }
            catch (Throwable e) {
                throw new IllegalStateException("Unable to instantiate custom translator", e);
            }
        }
        else {
            this.customSqlExceptionTranslator = null;
        }

    }

    public void setCustomSqlExceptionTranslator( @Nullable SQLExceptionTranslator customSqlExceptionTranslator ) {
        this.customSqlExceptionTranslator = customSqlExceptionTranslator;
    }

    @Nullable
    public SQLExceptionTranslator getCustomSqlExceptionTranslator() {
        return this.customSqlExceptionTranslator;
    }
}

