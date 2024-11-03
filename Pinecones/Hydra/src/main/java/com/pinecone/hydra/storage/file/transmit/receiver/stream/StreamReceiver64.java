package com.pinecone.hydra.storage.file.transmit.receiver.stream;

import com.pinecone.framework.util.Bytes;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.FileSystemConfig;
import com.pinecone.hydra.storage.file.FrameSegmentNaming;
import com.pinecone.hydra.storage.file.KOFSFrameSegmentNaming;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.LocalFrame;
import com.pinecone.hydra.storage.file.entity.RemoteFrame;
import com.pinecone.hydra.storage.file.transmit.receiver.ArchReceiver;
import com.pinecone.hydra.storage.file.transmit.receiver.ReceiveEntity;
import com.pinecone.ulf.util.id.GUIDs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.CRC32;

public class StreamReceiver64 extends ArchReceiver implements StreamReceiver {
    protected KOMFileSystem mKOMFileSystem;
    protected FrameSegmentNaming mFrameSegmentNaming;

    public StreamReceiver64(KOMFileSystem komFileSystem ) {
        this.mKOMFileSystem      = komFileSystem;
        this.mFrameSegmentNaming = new KOFSFrameSegmentNaming();
    }

    @Override
    public void receive(ReceiveEntity entity) throws IOException {
        StreamReceiverEntity streamReceiverEntity = entity.evinceStreamReceiverEntity();
        String destDirPath = streamReceiverEntity.getDestDirPath();
        FileNode file = streamReceiverEntity.getFile();
        KOMFileSystem fileSystem = streamReceiverEntity.getFileSystem();

        long chunkSize = this.mKOMFileSystem.getConfig().getFrameSize().longValue(); // 每片10MB
        int parityCheck = 0;
        long checksum = 0;
        long crc32Xor = 0;
        FSNodeAllotment allotment = fileSystem.getFSNodeAllotment();
        long bytesRead = 0;
        long segId = 0;

        // 使用 InputStream 读取文件数据流
        try (InputStream inputStream = streamReceiverEntity.getInputStream()) {
            byte[] buffer = new byte[(int) chunkSize];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                CRC32 crc = new CRC32();
                for (int i = 0; i < read; i++) {
                    byte b = buffer[i];
                    parityCheck += Bytes.calculateParity(b);
                    checksum += b & 0xFF;
                    crc.update(b);
                }
                if (segId == 0) {
                    crc32Xor = crc.getValue();
                }
                else {
                    crc32Xor ^= crc.getValue();
                }

                // 文件片段命名
                String sourceName = this.mFrameSegmentNaming.naming(file.getName(), segId, Long.toHexString(crc.getValue()));
                Path chunkFile = Paths.get(destDirPath, sourceName);

                // 创建本地和远程分片信息
                LocalFrame localFrame = allotment.newLocalFrame(file.getGuid(), (int) segId, chunkFile.toString(), Long.toHexString(crc.getValue()), read, 0);
                RemoteFrame remoteFrame = allotment.newRemoteFrame(file.getGuid(), (int) segId, Long.toHexString(crc.getValue()), read);
                remoteFrame.setDeviceGuid( this.mKOMFileSystem.getConfig().getLocalhostGUID() );
                remoteFrame.setSegGuid(localFrame.getSegGuid());

                // 使用 OutputStream 将数据写入文件片段
                try (OutputStream chunkOutputStream = new FileOutputStream(chunkFile.toFile())) {
                    chunkOutputStream.write(buffer, 0, read);
                    localFrame.setFileStartOffset(read);
                }

                segId++;
                bytesRead += read;
                localFrame.save();
                remoteFrame.save();
            }
        }

        // 更新文件元数据
        file.setPhysicalSize(bytesRead);
        file.setLogicSize(bytesRead);
        file.setChecksum(checksum);
        file.setParityCheck(parityCheck);
        file.setCrc32Xor(Long.toHexString(crc32Xor));
        fileSystem.put(file);
    }

    @Override
    public void receive(ReceiveEntity entity, Number offset, Number endSize) throws IOException {

    }

    @Override
    public void receive(ReceiveEntity entity, GUID frameGuid, int threadId, int threadNum) throws IOException {

    }

    @Override
    public void resumableReceive(ReceiveEntity entity) throws IOException {

    }
}
