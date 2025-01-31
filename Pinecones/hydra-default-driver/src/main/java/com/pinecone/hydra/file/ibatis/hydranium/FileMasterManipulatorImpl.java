package com.pinecone.hydra.file.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.file.ibatis.ExternalSymbolicMapper;
import com.pinecone.hydra.file.ibatis.FileMapper;
import com.pinecone.hydra.file.ibatis.FileMetaMapper;
import com.pinecone.hydra.file.ibatis.FileSystemAttributeMapper;
import com.pinecone.hydra.file.ibatis.FolderMapper;
import com.pinecone.hydra.file.ibatis.FolderMetaMapper;
import com.pinecone.hydra.file.ibatis.FolderVolumeMappingMapper;
import com.pinecone.hydra.file.ibatis.LocalFrameMapper;
import com.pinecone.hydra.file.ibatis.RemoteFrameMapper;
import com.pinecone.hydra.file.ibatis.SymbolicMapper;
import com.pinecone.hydra.file.ibatis.SymbolicMetaMapper;

import com.pinecone.hydra.storage.file.source.ExternalSymbolicManipulator;
import com.pinecone.hydra.storage.file.source.FileManipulator;
import com.pinecone.hydra.storage.file.source.FileMasterManipulator;
import com.pinecone.hydra.storage.file.source.FileMetaManipulator;
import com.pinecone.hydra.storage.file.source.FileSystemAttributeManipulator;
import com.pinecone.hydra.storage.file.source.FolderManipulator;
import com.pinecone.hydra.storage.file.source.FolderMetaManipulator;
import com.pinecone.hydra.storage.file.source.FolderVolumeMappingManipulator;
import com.pinecone.hydra.storage.file.source.LocalFrameManipulator;
import com.pinecone.hydra.storage.file.source.RemoteFrameManipulator;
import com.pinecone.hydra.storage.file.source.SymbolicManipulator;
import com.pinecone.hydra.storage.file.source.SymbolicMetaManipulator;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class FileMasterManipulatorImpl implements FileMasterManipulator {
    @Resource
    @Structure( type = FileSystemAttributeMapper.class )
    FileSystemAttributeManipulator fileSystemAttributeManipulator;

    @Resource
    @Structure( type = FileMapper.class )
    FileManipulator fileManipulator;

    @Resource
    @Structure( type = FileMetaMapper.class )
    FileMetaManipulator fileMetaManipulator;

    @Resource
    @Structure( type = FolderMapper.class )
    FolderManipulator folderManipulator;

    @Resource
    @Structure( type = FolderMetaMapper.class )
    FolderMetaManipulator folderMetaManipulator;

    @Resource
    @Structure( type = LocalFrameMapper.class )
    LocalFrameManipulator localFrameManipulator;

    @Resource
    @Structure( type = RemoteFrameMapper.class )
    RemoteFrameManipulator remoteFrameManipulator;

    @Resource
    @Structure( type = SymbolicMapper.class )
    SymbolicManipulator symbolicManipulator;

    @Resource
    @Structure( type = SymbolicMetaMapper.class )
    SymbolicMetaManipulator symbolicMetaManipulator;

    @Resource
    @Structure( type = ExternalSymbolicMapper.class )
    ExternalSymbolicManipulator externalSymbolicManipulator;

    @Resource( type = FileMasterTreeManipulatorImpl.class )
    KOISkeletonMasterManipulator skeletonMasterManipulator;

    @Structure( type = FolderVolumeMappingMapper.class)
    FolderVolumeMappingMapper folderVolumeRelationMapper;

    public FileMasterManipulatorImpl() {

    }

    public FileMasterManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( FileMasterManipulatorImpl.class, Map.of(), this );
        this.skeletonMasterManipulator = new FileMasterTreeManipulatorImpl( driver );
    }

    @Override
    public FileSystemAttributeManipulator getAttributeManipulator() {
        return this.fileSystemAttributeManipulator;
    }

    @Override
    public FileManipulator getFileManipulator() {
        return this.fileManipulator;
    }

    @Override
    public FileMetaManipulator getFileMetaManipulator() {
        return this.fileMetaManipulator;
    }

    @Override
    public FolderManipulator getFolderManipulator() {
        return this.folderManipulator;
    }

    @Override
    public FolderMetaManipulator getFolderMetaManipulator() {
        return this.folderMetaManipulator;
    }

    @Override
    public LocalFrameManipulator getLocalFrameManipulator() {
        return this.localFrameManipulator;
    }

    @Override
    public RemoteFrameManipulator getRemoteFrameManipulator() {
        return this.remoteFrameManipulator;
    }

    @Override
    public SymbolicManipulator getSymbolicManipulator() {
        return this.symbolicManipulator;
    }

    @Override
    public SymbolicMetaManipulator getSymbolicMetaManipulator() {
        return this.symbolicMetaManipulator;
    }

    @Override
    public ExternalSymbolicManipulator getExternalSymbolicManipulator() {
        return this.externalSymbolicManipulator;
    }

    @Override
    public FolderVolumeMappingManipulator getFolderVolumeRelationManipulator() {
        return this.folderVolumeRelationMapper;
    }

    @Override
    public KOISkeletonMasterManipulator getSkeletonMasterManipulator() {
        return this.skeletonMasterManipulator;
    }
}
