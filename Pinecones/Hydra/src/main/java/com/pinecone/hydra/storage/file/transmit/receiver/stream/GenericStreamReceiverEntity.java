package com.pinecone.hydra.storage.file.transmit.receiver.stream;

import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.transmit.receiver.ArchReceiveEntity;

import java.io.IOException;
import java.io.InputStream;

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
        this.streamReceiver.receive(this);
    }
}
