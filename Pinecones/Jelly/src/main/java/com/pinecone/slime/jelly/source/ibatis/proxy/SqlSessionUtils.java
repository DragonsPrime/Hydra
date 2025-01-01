package com.pinecone.slime.jelly.source.ibatis.proxy;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
//import org.mybatis.spring.SqlSessionHolder;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
//import org.springframework.dao.DataAccessException;
//import org.springframework.dao.TransientDataAccessResourceException;
//import org.springframework.dao.support.PersistenceExceptionTranslator;
//import org.springframework.transaction.support.TransactionSynchronizationAdapter;
//import org.springframework.transaction.support.TransactionSynchronizationManager;
//import org.springframework.util.Assert;

import com.pinecone.framework.util.Assert;
import com.pinecone.slime.jelly.source.ds.dao.DataAccessException;
import com.pinecone.slime.jelly.source.ds.dao.PersistenceExceptionTranslator;
import com.pinecone.slime.jelly.source.ds.jdbc.TransientDataAccessResourceException;
import com.pinecone.slime.jelly.source.ds.transaction.TransactionSynchronizationAdapter;
import com.pinecone.slime.jelly.source.ds.transaction.TransactionSynchronizationManager;

public final class SqlSessionUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionUtils.class);
    private static final String NO_EXECUTOR_TYPE_SPECIFIED = "No ExecutorType specified";
    private static final String NO_SQL_SESSION_FACTORY_SPECIFIED = "No SqlSessionFactory specified";
    private static final String NO_SQL_SESSION_SPECIFIED = "No SqlSession specified";

    private SqlSessionUtils() {
    }

    public static SqlSession getSqlSession(SqlSessionFactory sessionFactory) {
        ExecutorType executorType = sessionFactory.getConfiguration().getDefaultExecutorType();
        return getSqlSession(sessionFactory, executorType, (PersistenceExceptionTranslator)null);
    }

    public static SqlSession getSqlSession(SqlSessionFactory sessionFactory, ExecutorType executorType, PersistenceExceptionTranslator exceptionTranslator) {
        Assert.notNull(sessionFactory, "No SqlSessionFactory specified");
        Assert.notNull(executorType, "No ExecutorType specified");
        SqlSessionHolder holder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
        SqlSession session = sessionHolder(executorType, holder);
        if (session != null) {
            return session;
        }
        else {
            LOGGER.debug(() -> {
                return "Creating a new SqlSession";
            });
            session = sessionFactory.openSession(executorType);
            registerSessionHolder(sessionFactory, executorType, exceptionTranslator, session);
            return session;
        }
    }

    private static void registerSessionHolder(SqlSessionFactory sessionFactory, ExecutorType executorType, PersistenceExceptionTranslator exceptionTranslator, SqlSession session) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            Environment environment = sessionFactory.getConfiguration().getEnvironment();
            if (environment.getTransactionFactory() instanceof SpringManagedTransactionFactory) {
                LOGGER.debug(() -> {
                    return "Registering transaction synchronization for SqlSession [" + session + "]";
                });
                SqlSessionHolder holder = new SqlSessionHolder(session, executorType, exceptionTranslator);
                TransactionSynchronizationManager.bindResource(sessionFactory, holder);
                TransactionSynchronizationManager.registerSynchronization(new SqlSessionUtils.SqlSessionSynchronization(holder, sessionFactory));
                holder.setSynchronizedWithTransaction(true);
                holder.requested();
            }
            else {
                if (TransactionSynchronizationManager.getResource(environment.getDataSource()) != null) {
                    throw new TransientDataAccessResourceException("SqlSessionFactory must be using a SpringManagedTransactionFactory in order to use Hydra transaction synchronization");
                }

                LOGGER.debug(() -> {
                    return "SqlSession [" + session + "] was not registered for synchronization because DataSource is not transactional";
                });
            }
        }
        else {
            LOGGER.debug(() -> {
                return "SqlSession [" + session + "] was not registered for synchronization because synchronization is not active";
            });
        }

    }

    private static SqlSession sessionHolder(ExecutorType executorType, SqlSessionHolder holder) {
        SqlSession session = null;
        if ( holder != null && holder.isSynchronizedWithTransaction() ) {
            if (holder.getExecutorType() != executorType) {
                throw new TransientDataAccessResourceException("Cannot change the ExecutorType when there is an existing transaction");
            }

            holder.requested();
            LOGGER.debug(() -> {
                return "Fetched SqlSession [" + holder.getSqlSession() + "] from current transaction";
            });
            session = holder.getSqlSession();
        }

        return session;
    }

    public static void closeSqlSession(SqlSession session, SqlSessionFactory sessionFactory) {
        Assert.notNull(session, "No SqlSession specified");
        Assert.notNull(sessionFactory, "No SqlSessionFactory specified");
        SqlSessionHolder holder = (SqlSessionHolder)TransactionSynchronizationManager.getResource(sessionFactory);
        if (holder != null && holder.getSqlSession() == session) {
            LOGGER.debug(() -> {
                return "Releasing transactional SqlSession [" + session + "]";
            });
            holder.released();
        } else {
            LOGGER.debug(() -> {
                return "Closing non transactional SqlSession [" + session + "]";
            });
            session.close();
        }

    }

    public static boolean isSqlSessionTransactional(SqlSession session, SqlSessionFactory sessionFactory) {
        Assert.notNull(session, "No SqlSession specified");
        Assert.notNull(sessionFactory, "No SqlSessionFactory specified");
        SqlSessionHolder holder = (SqlSessionHolder)TransactionSynchronizationManager.getResource(sessionFactory);
        return holder != null && holder.getSqlSession() == session;
    }

    private static final class SqlSessionSynchronization extends TransactionSynchronizationAdapter {
        private final SqlSessionHolder holder;
        private final SqlSessionFactory sessionFactory;
        private boolean holderActive = true;

        public SqlSessionSynchronization(SqlSessionHolder holder, SqlSessionFactory sessionFactory) {
            Assert.notNull(holder, "Parameter 'holder' must be not null");
            Assert.notNull(sessionFactory, "Parameter 'sessionFactory' must be not null");
            this.holder = holder;
            this.sessionFactory = sessionFactory;
        }

        public int getOrder() {
            return 999;
        }

        public void suspend() {
            if (this.holderActive) {
                SqlSessionUtils.LOGGER.debug(() -> {
                    return "Transaction synchronization suspending SqlSession [" + this.holder.getSqlSession() + "]";
                });
                TransactionSynchronizationManager.unbindResource(this.sessionFactory);
            }

        }

        public void resume() {
            if (this.holderActive) {
                SqlSessionUtils.LOGGER.debug(() -> {
                    return "Transaction synchronization resuming SqlSession [" + this.holder.getSqlSession() + "]";
                });
                TransactionSynchronizationManager.bindResource(this.sessionFactory, this.holder);
            }

        }

        public void beforeCommit(boolean readOnly) {
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                try {
                    SqlSessionUtils.LOGGER.debug(() -> {
                        return "Transaction synchronization committing SqlSession [" + this.holder.getSqlSession() + "]";
                    });
                    this.holder.getSqlSession().commit();
                } catch (PersistenceException var4) {
                    if (this.holder.getPersistenceExceptionTranslator() != null) {
                        DataAccessException translated = this.holder.getPersistenceExceptionTranslator().translateExceptionIfPossible(var4);
                        if (translated != null) {
                            throw translated;
                        }
                    }

                    throw var4;
                }
            }

        }

        public void beforeCompletion() {
            if (!this.holder.isOpen()) {
                SqlSessionUtils.LOGGER.debug(() -> {
                    return "Transaction synchronization deregistering SqlSession [" + this.holder.getSqlSession() + "]";
                });
                TransactionSynchronizationManager.unbindResource(this.sessionFactory);
                this.holderActive = false;
                SqlSessionUtils.LOGGER.debug(() -> {
                    return "Transaction synchronization closing SqlSession [" + this.holder.getSqlSession() + "]";
                });
                this.holder.getSqlSession().close();
            }

        }

        public void afterCompletion(int status) {
            if (this.holderActive) {
                SqlSessionUtils.LOGGER.debug(() -> {
                    return "Transaction synchronization deregistering SqlSession [" + this.holder.getSqlSession() + "]";
                });
                TransactionSynchronizationManager.unbindResourceIfPossible(this.sessionFactory);
                this.holderActive = false;
                SqlSessionUtils.LOGGER.debug(() -> {
                    return "Transaction synchronization closing SqlSession [" + this.holder.getSqlSession() + "]";
                });
                this.holder.getSqlSession().close();
            }

            this.holder.reset();
        }
    }
}

