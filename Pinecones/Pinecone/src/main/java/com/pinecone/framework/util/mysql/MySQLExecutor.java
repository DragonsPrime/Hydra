package com.pinecone.framework.util.mysql;

import com.pinecone.framework.util.rdb.ArchRDBExecutor;
import com.pinecone.framework.util.rdb.RDBHost;

public class MySQLExecutor extends ArchRDBExecutor {
    public MySQLExecutor( RDBHost rdbHost ) {
        super( rdbHost );
    }
}
