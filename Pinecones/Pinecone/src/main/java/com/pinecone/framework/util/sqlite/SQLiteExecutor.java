package com.pinecone.framework.util.sqlite;

import com.pinecone.framework.util.rdb.ArchRDBExecutor;
import com.pinecone.framework.util.rdb.RDBHost;

public class SQLiteExecutor extends ArchRDBExecutor {
    public SQLiteExecutor( RDBHost rdbHost ) {
        super( rdbHost );
    }
}