package com.pinecone.hydra.storage.file.transmit.receiver.channel;

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
import com.pinecone.hydra.storage.file.entity.Strip;
import com.pinecone.hydra.storage.file.transmit.receiver.ArchReceiver;
import com.pinecone.hydra.storage.file.transmit.receiver.ReceiveEntity;
import com.pinecone.hydra.storage.volume.VolumeConfig;
import com.pinecone.ulf.util.id.GUIDs;
import com.pinecone.ulf.util.id.GuidAllocator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.CRC32;

public class ChannelReceiver64 extends ArchReceiver implements ChannelReceiver{
    protected KOMFileSystem           mKOMFileSystem;
    protected FrameSegmentNaming mFrameSegmentNaming;

    public ChannelReceiver64(KOMFileSystem komFileSystem ) {
        this.mKOMFileSystem      = komFileSystem;
        this.mFrameSegmentNaming = new KOFSFrameSegmentNaming();
    }


    @Override
    public void receive( ReceiveEntity entity, Number offset, Number endSize ) throws IOException {
        ChannelReceiverEntity channelReceiverEntity = entity.evinceChannelReceiverEntity();
        FileChannel fileChannel = channelReceiverEntity.getChannel();
        String destDirPath = channelReceiverEntity.getDestDirPath();
        FileNode file = channelReceiverEntity.getFile();
        KOMFileSystem fileSystem = channelReceiverEntity.getFileSystem();

        long frameSize = this.mKOMFileSystem.getConfig().getFrameSize().longValue();
        int parityCheck = 0;
        long checksum = 0;
        long crc32Xor = 0;
        ByteBuffer buffer = ByteBuffer.allocate((int) frameSize);
        FSNodeAllotment allotment = fileSystem.getFSNodeAllotment();
        GuidAllocator guidAllocator = fileSystem.getGuidAllocator();
        long bytesRead = 0;
        long segId = offset.longValue() / frameSize;
        if( offset.longValue() % frameSize != 0 ){
            segId++;
        }
        long remainingBytes = endSize.longValue();    // 剩余需要读取的字节数

        // 将文件指针移动到 offset 位置
        fileChannel.position(offset.longValue());

        while (remainingBytes > 0) {
            buffer.clear();
            int bytesToRead = (int) Math.min(frameSize, remainingBytes);  // 计算本次读取的字节数
            buffer.limit(bytesToRead);

            int read = fileChannel.read(buffer);
            if (read == -1) {
                break; // 文件结束，跳出循环
            }

            buffer.flip();

            CRC32 crc = new CRC32();
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                parityCheck += Bytes.calculateParity(b);
                checksum += b & 0xFF;
                crc.update(b);
            }

            if (segId == 0) {
                crc32Xor = crc.getValue();
            } else {
                crc32Xor = crc32Xor ^ crc.getValue();
            }

            String sourceName = this.mFrameSegmentNaming.naming(file.getName(), segId, Long.toHexString(crc.getValue()));
            Path chunkFile = Paths.get(destDirPath, sourceName);
            LocalFrame localFrame = allotment.newLocalFrame(file.getGuid(), (int) segId, chunkFile.toString(), Long.toHexString(crc.getValue()), read, 0);
            RemoteFrame remoteFrame = allotment.newRemoteFrame(file.getGuid(), (int) segId, Long.toHexString(crc.getValue()), read);
            remoteFrame.setDeviceGuid( this.mKOMFileSystem.getConfig().getLocalhostGUID() );
            remoteFrame.setSegGuid(localFrame.getSegGuid());

            try (FileChannel chunkChannel = FileChannel.open(chunkFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                buffer.rewind();
                int write = chunkChannel.write(buffer);
                localFrame.setFileStartOffset(write);
            }

            segId++;
            bytesRead += read;
            remainingBytes -= read;  // 更新剩余需要读取的字节数

            localFrame.save();
            remoteFrame.save();
        }
    }

    @Override
    public void receive( ReceiveEntity entity ) throws IOException {
        ChannelReceiverEntity channelReceiverEntity = entity.evinceChannelReceiverEntity();
        FileChannel fileChannel = channelReceiverEntity.getChannel();
        String destDirPath = channelReceiverEntity.getDestDirPath();
        FileNode file = channelReceiverEntity.getFile();
        KOMFileSystem fileSystem = channelReceiverEntity.getFileSystem();
        long frameSize = this.mKOMFileSystem.getConfig().getFrameSize().longValue();;
        int parityCheck = 0;
        long checksum = 0;
        long crc32Xor = 0;
        ByteBuffer buffer = ByteBuffer.allocate((int) frameSize);
        FSNodeAllotment allotment = fileSystem.getFSNodeAllotment();
        GuidAllocator guidAllocator = fileSystem.getGuidAllocator();
        long bytesRead = 0;
        long segId = 0;

        while (true) {
            buffer.clear();
            int read = 0;

                read = fileChannel.read(buffer);
                if (read == -1) {
                    break;
                }
                buffer.flip();

                CRC32 crc = new CRC32();
                while ( buffer.hasRemaining() ) {
                    byte b = buffer.get();
                    parityCheck += Bytes.calculateParity( b );
                    checksum += b & 0xFF;
                    crc.update(b);
                }
                if( segId == 0 ){
                    crc32Xor = crc.getValue();
                }
                else {
                    crc32Xor = crc32Xor^crc.getValue();
                }
                String sourceName = this.mFrameSegmentNaming.naming( file.getName(),segId,Long.toHexString(crc.getValue()) );
                Path chunkFile = Paths.get(destDirPath, sourceName);
                LocalFrame localFrame = allotment.newLocalFrame( file.getGuid(),(int) segId,chunkFile.toString(),Long.toHexString(crc.getValue()),read,0 );
                RemoteFrame remoteFrame = allotment.newRemoteFrame( file.getGuid(),(int)segId,Long.toHexString(crc.getValue()), read);
                remoteFrame.setDeviceGuid(this.mKOMFileSystem.getConfig().getLocalhostGUID());
                remoteFrame.setSegGuid( localFrame.getSegGuid() );
                try ( FileChannel chunkChannel = FileChannel.open(chunkFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE) ) {
                    buffer.rewind();
                    int write = chunkChannel.write(buffer);
                    localFrame.setFileStartOffset( write );
                }

                segId++;
                bytesRead += read;
                localFrame.save();
                remoteFrame.save();
        }
        file.setPhysicalSize( bytesRead );
        file.setLogicSize( bytesRead );
        file.setChecksum( checksum );
        file.setParityCheck( parityCheck );
        file.setCrc32Xor( Long.toHexString(crc32Xor) );
        fileSystem.put( file );
    }

    @Override
    public void receive( ReceiveEntity entity, GUID frameGuid, int threadId, int threadNum ) throws IOException {
        ChannelReceiverEntity channelReceiverEntity = entity.evinceChannelReceiverEntity();
        FileChannel fileChannel = channelReceiverEntity.getChannel();
        String destDirPath = channelReceiverEntity.getDestDirPath();
        FileNode file = channelReceiverEntity.getFile();
        KOMFileSystem fileSystem = channelReceiverEntity.getFileSystem();

        long stripSize = FileSystemConfig.stripSize1;
        long offset = FileSystemConfig.stripSize1 * threadId;
        long stripId = threadId;
        ByteBuffer buffer = ByteBuffer.allocate((int) stripSize);
        FSNodeAllotment allotment = fileSystem.getFSNodeAllotment();
        long bytesRead = 0;

        String sourceName = this.mFrameSegmentNaming.naming( file.getName(),frameGuid, threadId );
        Path stripFile = Paths.get(destDirPath, sourceName);
        while( true ){
            buffer.clear();
            int read = 0;
            fileChannel.position( offset );
            read = fileChannel.read( buffer );
            if( read == -1 ){
                break;
            }

            buffer.flip();
            Strip strip = allotment.newStrip();
            strip.setSegGuid( frameGuid );
            strip.setSize( read );
            strip.setDefinitionSize( read );
            strip.setSourceName( stripFile.toString() );
            strip.setStripId( stripId );

            try ( FileChannel chunkChannel = FileChannel.open(stripFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE) ) {
                buffer.rewind();
                chunkChannel.position(chunkChannel.size());
                chunkChannel.write(buffer);
            }
            strip.setFileStartOffset( offset );
            offset += stripSize * threadNum;
            stripId += threadNum;
            strip.save();
        }

    }

    @Override
    public void resumableReceive(ReceiveEntity entity ) throws IOException {
        ChannelReceiverEntity channelReceiverEntity = entity.evinceChannelReceiverEntity();
        FileChannel fileChannel = channelReceiverEntity.getChannel();
        String destDirPath = channelReceiverEntity.getDestDirPath();
        FileNode file = channelReceiverEntity.getFile();
        KOMFileSystem fileSystem = channelReceiverEntity.getFileSystem();
        FSNodeAllotment creator = fileSystem.getFSNodeAllotment();
        GuidAllocator guidAllocator = fileSystem.getGuidAllocator();

        long chunkSize = 10 * 1024 * 1024;
        //todo 如果簇不在本地？
        LocalFrame lastFrame = fileSystem.getLastFrame( file.getGuid() ).evinceLocalFrame();
        long segId = lastFrame.getSegId();
        long bytesRead = segId * chunkSize; // 从最后一个已传输的分片计算出起始位置

        // 校验最后的frame是否存在数据错误
        if (lastFrame.getSize() > 0 && isFrameCorrupted(lastFrame, fileChannel,chunkSize)) {
            // 补全并传输损坏的分片
            bytesRead = resumeIncompleteFrame(lastFrame, fileChannel, chunkSize);
        }

        // 从剩余部分继续传输文件
        transferRemaining(fileChannel, bytesRead, segId, creator, guidAllocator, destDirPath, file, chunkSize);
    }

    // 校验frame是否存在数据损坏
    private boolean isFrameCorrupted( LocalFrame frame, FileChannel fileChannel, long chunkSize ) throws IOException {
        FileChannel chunkFileChannel = FileChannel.open(Paths.get(frame.getSourceName()), StandardOpenOption.READ);
            long position = 0;
            long frameSize = frame.getSize();
            ByteBuffer buffer = ByteBuffer.allocate((int) frameSize);
            ByteBuffer sourceBuffer = ByteBuffer.allocate((int) frameSize);

            // 逐步比较frame中的数据和文件中的数据
            while (position < frameSize) {
                buffer.clear();
                int readFromFrame = chunkFileChannel.read(buffer, position);
                buffer.flip();

                sourceBuffer.clear();
                fileChannel.read(sourceBuffer, frame.getSegId() * chunkSize + position);
                sourceBuffer.flip();

                if (!buffer.equals(sourceBuffer)) {
                    return true; // 如果frame与源文件的数据不一致，说明数据损坏
                }
                position += readFromFrame;
            }
        return false;
    }

    // 补全损坏的分片
    private long resumeIncompleteFrame( LocalFrame frame, FileChannel fileChannel, long chunkSize ) {
        long bytesRead = frame.getSegId() * chunkSize + frame.getSize();
        long remainingSize = chunkSize - frame.getSize();
        ByteBuffer buffer = ByteBuffer.allocate((int) remainingSize);

        try {
            fileChannel.read(buffer, bytesRead);
            buffer.flip();

            try (FileChannel chunkFileChannel = FileChannel.open(Paths.get(frame.getSourceName()), StandardOpenOption.WRITE, StandardOpenOption.APPEND)) {
                chunkFileChannel.write(buffer); // 写入frame剩余部分的数据
            }

            bytesRead += buffer.remaining();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return bytesRead;
    }

    // 处理文件剩余部分的传输
    private void transferRemaining( FileChannel fileChannel, long bytesRead, long segId, FSNodeAllotment allotment, GuidAllocator guidAllocator, String destDirPath, FileNode file, long chunkSize ) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate((int) chunkSize);

        while (true) {
            buffer.clear();
            int read = 0;

                read = fileChannel.read(buffer, bytesRead);
                if (read == -1) {
                    break;  // 文件读取完毕
                }
                buffer.flip();

                // 计算CRC32
                CRC32 crc = new CRC32();
                while (buffer.hasRemaining()) {
                    crc.update(buffer.get());
                }

                // 创建新的frame并写入分片文件
                String sourceName = this.mFrameSegmentNaming.naming( file.getName(),segId,Long.toHexString(crc.getValue()) );
                Path chunkFile = Paths.get(destDirPath, sourceName);
                LocalFrame localFrame = allotment.newLocalFrame( file.getGuid(),(int) segId,chunkFile.toString(),Long.toHexString(crc.getValue()),read,0 );
                localFrame.save();
                try (FileChannel chunkChannel = FileChannel.open(chunkFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    buffer.rewind();
                    chunkChannel.write(buffer);
                }

                segId++;
                bytesRead += read;

        }
    }


}
