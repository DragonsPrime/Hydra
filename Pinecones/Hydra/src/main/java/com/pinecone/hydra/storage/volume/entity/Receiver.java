package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.RandomAccessChanface;
import com.pinecone.hydra.storage.StorageIOResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface Receiver extends Pinenut {
    StorageIOResponse receive(Chanface chanface) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException;
    StorageIOResponse receive(Chanface chanface,Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException;
    StorageIOResponse receive(RandomAccessChanface randomAccessChanface) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException;
    StorageIOResponse receive(RandomAccessChanface randomAccessChanface,Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
