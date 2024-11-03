package com.pinecone.hydra.storage.file.transmit.exporter.channel;

import com.pinecone.framework.util.Bytes;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.Frame;
import com.pinecone.hydra.storage.file.entity.LocalFrame;
import com.pinecone.hydra.storage.file.transmit.exporter.ArchExporter;
import com.pinecone.hydra.storage.file.transmit.exporter.ExporterEntity;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.TreeMap;
import java.util.zip.CRC32;

public class GenericChannelExporter extends ArchExporter implements ChannelExporter{
    @Override
    public void export(ExporterEntity entity) throws IOException {
        ChannelExporterEntity exporter = entity.evinceChannelExporterEntity();
        FileChannel fileChannel = exporter.getChannel();
        FileNode file = exporter.getFile();
        KOMFileSystem fileSystem = exporter.getFileSystem();

        // 获取文件所有的簇
        TreeMap<Long, Frame> framesMap = file.getFrames();
        for (long i = 0; i < framesMap.size(); i++) {
            long parityCheck = 0;
            long checksum = 0;
            LocalFrame frame = framesMap.get(i).evinceLocalFrame();
            File frameFile = new File(frame.getSourceName());

            // 使用 try-with-resources 确保资源被释放
            try (FileChannel frameChannel = FileChannel.open(frameFile.toPath(), StandardOpenOption.READ)) {
                ByteBuffer buffer = ByteBuffer.allocate((int) frame.getSize());
                frameChannel.read(buffer);
                buffer.flip();
                CRC32 crc = new CRC32();
                while ( buffer.hasRemaining() ) {
                    byte b = buffer.get();
                    parityCheck += Bytes.calculateParity( b );
                    checksum += b & 0xFF;
                    crc.update(b);
                }
                if ( !frame.getCrc32().equals( Long.toHexString(crc.getValue()) ) ){
                    throw new IOException("文件簇已损坏，终止上传");
                }
                buffer.rewind();
                fileChannel.write(buffer);
                buffer.clear();
            }
        }
    }

    @Override
    public void resumablExport(ExporterEntity entity) {

    }

}
