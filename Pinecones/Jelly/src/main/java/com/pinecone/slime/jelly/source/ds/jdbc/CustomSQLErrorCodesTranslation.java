package com.pinecone.slime.jelly.source.ds.jdbc;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.util.StringUtils;
import com.pinecone.slime.jelly.source.ds.dao.DataAccessException;

public class CustomSQLErrorCodesTranslation {
    private String[] errorCodes = new String[0];
    @Nullable
    private Class<?> exceptionClass;

    public CustomSQLErrorCodesTranslation() {
    }

    public void setErrorCodes(String... errorCodes) {
        this.errorCodes = StringUtils.sortStringArray(errorCodes);
    }

    public String[] getErrorCodes() {
        return this.errorCodes;
    }

    public void setExceptionClass(@Nullable Class<?> exceptionClass) {
        if ( exceptionClass != null && !DataAccessException.class.isAssignableFrom(exceptionClass) ) {
            throw new IllegalArgumentException("Invalid exception class [" + exceptionClass + "]: needs to be a subclass of [org.springframework.dao.DataAccessException]");
        }
        else {
            this.exceptionClass = exceptionClass;
        }
    }

    @Nullable
    public Class<?> getExceptionClass() {
        return this.exceptionClass;
    }
}
