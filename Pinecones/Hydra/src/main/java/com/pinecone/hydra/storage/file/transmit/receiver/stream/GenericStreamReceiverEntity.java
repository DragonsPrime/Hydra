package com.pinecone.hydra.storage.file.transmit.receiver.stream;

import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.transmit.receiver.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class GenericStreamReceiverEntity extends ArchReceiveEntity implements StreamReceiverEntity {
    private InputStream     inputStream;
    private StreamReceiver streamReceiver;
    public GenericStreamReceiverEntity(KOMFileSystem fileSystem, String destDirPath, FileNode file, InputStream inputStream) {
        super(fileSystem, destDirPath, file);
        this.inputStream = inputStream;
        this.streamReceiver = new StreamReceiver64( fileSystem );
    }

    @Override
    public InputStream getInputStream() {
        return this.inputStream;
    }

    @Override
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void receive() throws IOException {
        //this.streamReceiver.receive(this);
    }

    @Override
    public void receive(LogicVolume volume) throws IOException, SQLException {

    }

    @Override
    public void receive(Number offset, Number endSize) throws IOException {

    }
}
