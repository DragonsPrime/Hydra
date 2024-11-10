package com.pinecone.slime.jelly.source.ibatis.proxy;

import java.sql.SQLException;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.apache.ibatis.exceptions.PersistenceException;
//import org.springframework.dao.DataAccessException;
//import org.springframework.dao.support.PersistenceExceptionTranslator;
//import org.springframework.jdbc.UncategorizedSQLException;


import com.pinecone.slime.jelly.source.ds.dao.DataAccessException;
import com.pinecone.slime.jelly.source.ds.dao.PersistenceExceptionTranslator;
import com.pinecone.slime.jelly.source.ds.jdbc.SQLErrorCodeSQLExceptionTranslator;
import com.pinecone.slime.jelly.source.ds.jdbc.SQLExceptionTranslator;
import com.pinecone.slime.jelly.source.ds.jdbc.UncategorizedSQLException;
import com.pinecone.slime.jelly.source.ds.transaction.TransactionException;
//import org.springframework.jdbc.support.SQLExceptionTranslator;
//import org.springframework.transaction.TransactionException;

public class MyBatisExceptionTranslator implements PersistenceExceptionTranslator {
    private final Supplier<SQLExceptionTranslator> exceptionTranslatorSupplier;
    private SQLExceptionTranslator exceptionTranslator;

    public MyBatisExceptionTranslator(DataSource dataSource, boolean exceptionTranslatorLazyInit) {
        this(() -> {
            return new SQLErrorCodeSQLExceptionTranslator(dataSource);
        }, exceptionTranslatorLazyInit);
    }

    public MyBatisExceptionTranslator(Supplier<SQLExceptionTranslator> exceptionTranslatorSupplier, boolean exceptionTranslatorLazyInit) {
        this.exceptionTranslatorSupplier = exceptionTranslatorSupplier;
        if (!exceptionTranslatorLazyInit) {
            this.initExceptionTranslator();
        }

    }

    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException e) {
        if (e instanceof PersistenceException) {
            if (((RuntimeException)e).getCause() instanceof PersistenceException) {
                e = (PersistenceException)((RuntimeException)e).getCause();
            }

            if (((RuntimeException)e).getCause() instanceof SQLException) {
                this.initExceptionTranslator();
                String task = ((RuntimeException)e).getMessage() + "\n";
                SQLException se = (SQLException)((RuntimeException)e).getCause();
                DataAccessException dae = this.exceptionTranslator.translate(task, (String)null, se);
                return (DataAccessException)(dae != null ? dae : new UncategorizedSQLException(task, (String)null, se));
            }
            else if (((RuntimeException)e).getCause() instanceof TransactionException) {
                throw (TransactionException)((RuntimeException)e).getCause();
            }
            else {
                return new MyBatisSystemException((Throwable)e);
            }
        }
        else {
            return null;
        }
    }

    private synchronized void initExceptionTranslator() {
        if (this.exceptionTranslator == null) {
            this.exceptionTranslator = (SQLExceptionTranslator)this.exceptionTranslatorSupplier.get();
        }

    }
}
