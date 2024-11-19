package com.pinecone.hydra.storage.file.transmit.exporter.stream;

import com.pinecone.framework.util.Bytes;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.Frame;
import com.pinecone.hydra.storage.file.entity.LocalFrame;
import com.pinecone.hydra.storage.file.transmit.exporter.ArchExporter;
import com.pinecone.hydra.storage.file.transmit.exporter.ExporterEntity;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.TreeMap;
import java.util.zip.CRC32;

public class GenericStreamExporter extends ArchExporter implements StreamExporter {
    @Override
    public void export(ExporterEntity entity, UniformVolumeManager volumeManager) throws IOException {
        StreamExporterEntity exporter = entity.evinceStreamExporterEntity();
        try (OutputStream outputStream = exporter.getOutputStream()) {
            FileNode file = exporter.getFile();

            // 获取文件所有的簇
            TreeMap<Long, Frame> framesMap = file.getFrames();
            for (long i = 0; i < framesMap.size(); i++) {
                long parityCheck = 0;
                long checksum = 0;
                LocalFrame frame = framesMap.get(i).evinceLocalFrame();
                File frameFile = new File(frame.getSourceName());

                // 使用 try-with-resources 确保资源被释放
                try (InputStream frameInputStream = Files.newInputStream(frameFile.toPath())) {
                    byte[] buffer = new byte[(int) frame.getSize()];
                    int bytesRead = 0;
                    int totalBytesRead = 0;

                    // 循环读取，确保读取完整帧数据
                    while (totalBytesRead < buffer.length && (bytesRead = frameInputStream.read(buffer, totalBytesRead, buffer.length - totalBytesRead)) != -1) {
                        totalBytesRead += bytesRead;
                    }
                    if (totalBytesRead != buffer.length) {
                        throw new IOException("无法读取完整的帧数据");
                    }

                    CRC32 crc = new CRC32();
                    for (byte b : buffer) {
                        parityCheck += Bytes.calculateParity(b);
                        checksum += b & 0xFF;
                        crc.update(b);
                    }

                    if (!frame.getCrc32().equals(Long.toHexString(crc.getValue()))) {
                        throw new IOException("文件簇已损坏，终止上传");
                    }

                    // 写入输出流
                    outputStream.write(buffer);
                }
            }
            // 确保所有数据都被写出
            outputStream.flush();
        }
    }


    @Override
    public void resumablExport(ExporterEntity entity) {

    }
}
