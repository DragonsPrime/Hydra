package com.pinecone.hydra.storage.volume.entity.local;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.hydra.storage.file.FileSystemConfig;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FSNodeAllotment;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.RemoteFrame;
import com.pinecone.hydra.storage.volume.VolumeConfig;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ArchLogicVolume;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity;
import com.pinecone.hydra.storage.volume.source.SpannedVolumeManipulator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.List;

public class TitanLocalSpannedVolume extends ArchLogicVolume implements LocalSpannedVolume {
    private SpannedVolumeManipulator spannedVolumeManipulator;

    public TitanLocalSpannedVolume(VolumeTree volumeTree, SpannedVolumeManipulator spannedVolumeManipulator) {
        super(volumeTree);
        this.spannedVolumeManipulator = spannedVolumeManipulator;
    }
    public TitanLocalSpannedVolume( VolumeTree volumeTree ){
        super( volumeTree );
    }

    public TitanLocalSpannedVolume(){
    }
    public void setSpannedVolumeManipulator( SpannedVolumeManipulator spannedVolumeManipulator ){
        this.spannedVolumeManipulator = spannedVolumeManipulator;
    }
    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public void channelExport(KOMFileSystem fileSystem, FileNode file) throws IOException {
        List<LogicVolume> volumes = this.getChildren();
        for( LogicVolume volume : volumes ){

        }
    }

    @Override
    public void streamExport(KOMFileSystem fileSystem, FileNode file) throws IOException {

    }



    @Override
    public void extendLogicalVolume(GUID physicalGuid) {

    }

    @Override
    public List<GUID> lsblk() {
        return null;
    }

    @Override
    public void streamReceive(KOMFileSystem fileSystem, FileNode file, InputStream inputStream) {

    }
    @Override
    public void channelReceive(KOMFileSystem fileSystem, FileNode file, FileChannel channel) throws IOException {
        //首先判断卷的剩余容量是否足够保存此文件
        if( checkCapacity( file.getDefinitionSize(),this.volumeCapacity ) ){
            List<LogicVolume> volumes = this.getChildren();
            //开始分配
            if( volumes.size() < 2 ){
                //只有一个子卷无需分配
                LogicVolume volume = volumes.get(0);
                volume.channelReceive( fileSystem, file, channel );
            }else {
                long currentOffset = 0;
                long totalFreeSpace = this.getFreeSpace( this );
                long remainingSize = file.getDefinitionSize();
                for( LogicVolume volume : volumes ){
                    long freeSpace = this.getFreeSpace(volume);
                    if ( freeSpace == 0 ){
                        continue;
                    }

                    long dataToSave = (remainingSize * freeSpace) /totalFreeSpace ;

                    if ( dataToSave > freeSpace ) {
                        dataToSave = freeSpace;
                    }

                    if ( currentOffset + dataToSave > file.getDefinitionSize() ) {
                        dataToSave = file.getDefinitionSize() - currentOffset;
                    }
                    volume.channelReceive( fileSystem,file,channel,currentOffset,dataToSave );
                    currentOffset += dataToSave;
                    remainingSize -= dataToSave;
                    totalFreeSpace -= freeSpace;
                    if( currentOffset > file.getDefinitionSize() ){
                        break;
                    }
                }
            }
            fileSystem.put( file );
        }
    }

    @Override
    public void channelReceive(KOMFileSystem fileSystem, FileNode file, FileChannel channel, long start, long offset) throws IOException {

    }

    @Override
    public void channelReceive(KOMFileSystem fileSystem, FileNode file, FileChannel channel, String raidRule) {
        if( this.checkCapacity( file.getDefinitionSize(), this.volumeCapacity ) ){
            FSNodeAllotment fsNodeAllotment = fileSystem.getFSNodeAllotment();
            List<LogicVolume> volumes = this.getChildren();
            int threadNum = volumes.size();

            long remainingSize = file.getDefinitionSize();
            RemoteFrame remoteFrame = fsNodeAllotment.newRemoteFrame();
        }


    }

    @Override
    public void setVolumeTree(VolumeTree volumeTree) {
        this.volumeTree = volumeTree;
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    private boolean checkCapacity(long fileSize, VolumeCapacity capacity){
        long remainingCapacity = capacity.getDefinitionCapacity() - capacity.getUsedSize();
        return remainingCapacity > fileSize;
    }

    private long getFreeSpace( LogicVolume volume ){
        return volume.getVolumeCapacity().getDefinitionCapacity() - volume.getVolumeCapacity().getUsedSize();
    }
}
