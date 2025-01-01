package com.pinecone.hydra.storage.volume.entity;

import java.sql.SQLException;

public interface SimpleVolume extends LogicVolume{
    @Override
    default SimpleVolume evinceSimpleVolume() {
        return this;
    }

     void assembleSQLiteExecutor() throws SQLException;
}
