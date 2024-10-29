package com.pinecone.hydra.storage.volume.entity.local.physical.receive;

import com.pinecone.framework.util.Bytes;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.StorageNaming;
import com.pinecone.hydra.storage.TitanMiddleStorageObject;
import com.pinecone.hydra.storage.TitanStorageNaming;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;
import com.pinecone.ulf.util.id.GuidAllocator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.CRC32;

public class TitanDirectChannelReceive64 implements DirectChannelReceive64{
    private StorageNaming       storageNaming;
    public  TitanDirectChannelReceive64(){
        this.storageNaming = new TitanStorageNaming();
    }
    @Override
    public MiddleStorageObject receive(DirectChannelReceiveEntity entity) throws IOException {
        return receiveWithOffsetAndSize(entity, 0, entity.getReceiveStorageObject().getSize().intValue());
    }

    @Override
    public MiddleStorageObject receive(DirectChannelReceiveEntity entity, Number offset, Number endSize) throws IOException {
        return receiveWithOffsetAndSize(entity, offset.longValue(), endSize.intValue());
    }

    private MiddleStorageObject receiveWithOffsetAndSize(DirectChannelReceiveEntity entity, long offset, int size) throws IOException {
        ReceiveStorageObject receiveStorageObject = entity.getReceiveStorageObject();
        String destDirPath = entity.getDestDirPath();
        VolumeTree volumeTree = entity.getVolumeTree();
        GuidAllocator guidAllocator = volumeTree.getGuidAllocator();
        FileChannel channel = entity.getChannel();

        int parityCheck = 0;
        long checksum = 0;
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);

        GUID guid = guidAllocator.nextGUID72();
        TitanMiddleStorageObject titanMiddleStorageObject = new TitanMiddleStorageObject();
        titanMiddleStorageObject.setObjectGuid(guid);

        channel.position(offset);
        buffer.clear();
        channel.read(buffer);
        buffer.flip();
        CRC32 crc = new CRC32();

        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            parityCheck += Bytes.calculateParity(b);
            checksum += b & 0xFF;
            crc.update(b);
        }
        Debug.trace( receiveStorageObject.getName() );
        String sourceName = this.storageNaming.naming(receiveStorageObject.getName(), Long.toHexString(crc.getValue()));
        Path path = Paths.get(destDirPath, sourceName);

        try (FileChannel chunkChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            buffer.rewind();
            chunkChannel.write(buffer);
        }

        titanMiddleStorageObject.setChecksum(checksum);
        titanMiddleStorageObject.setCrc32(Long.toHexString(crc.getValue()));
        titanMiddleStorageObject.setParityCheck(parityCheck);
        titanMiddleStorageObject.setSourceName(path.toString());

        return titanMiddleStorageObject;
    }

}
