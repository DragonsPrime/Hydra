package com.pinecone.hydra.volume.ibatis.hydranium;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.hydra.storage.volume.source.MirroredVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.MountPointManipulator;
import com.pinecone.hydra.storage.volume.source.PhysicalVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.SimpleVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.SpannedVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.SqliteVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.StripedVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.VolumeAllocateManipulator;
import com.pinecone.hydra.storage.volume.source.VolumeCapacityManipulator;
import com.pinecone.hydra.storage.volume.source.VolumeMasterManipulator;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOISkeletonMasterManipulator;
import com.pinecone.hydra.volume.ibatis.MirroredVolumeMapper;
import com.pinecone.hydra.volume.ibatis.MountPointMapper;
import com.pinecone.hydra.volume.ibatis.PhysicalVolumeMapper;
import com.pinecone.hydra.volume.ibatis.SimpleVolumeMapper;
import com.pinecone.hydra.volume.ibatis.SpannedVolumeMapper;
import com.pinecone.hydra.volume.ibatis.SqliteVolumeMapper;
import com.pinecone.hydra.volume.ibatis.StripedVolumeMapper;
import com.pinecone.hydra.volume.ibatis.VolumeAllocateMapper;
import com.pinecone.hydra.volume.ibatis.VolumeCapacityMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
@Component
public class VolumeMasterManipulatorImpl implements VolumeMasterManipulator {
    @Resource
    @Structure( type = VolumeMasterTreeManipulatorImpl.class )
    KOISkeletonMasterManipulator skeletonMasterManipulator;

    @Resource
    @Structure( type = MirroredVolumeMapper.class )
    MirroredVolumeManipulator mirroredVolumeManipulator;

    @Resource
    @Structure( type = MountPointMapper.class )
    MountPointManipulator     mountPointManipulator;

    @Resource
    @Structure( type = SimpleVolumeMapper.class )
    SimpleVolumeManipulator   simpleVolumeManipulator;

    @Resource
    @Structure( type = SpannedVolumeMapper.class )
    SpannedVolumeManipulator  spannedVolumeManipulator;

    @Resource
    @Structure( type = StripedVolumeMapper.class )
    StripedVolumeManipulator  stripedVolumeManipulator;

    @Resource
    @Structure( type = VolumeCapacityMapper.class )
    VolumeCapacityManipulator volumeCapacityManipulator;

    @Resource
    @Structure( type = PhysicalVolumeMapper.class )
    PhysicalVolumeManipulator physicalVolumeManipulator;

    @Resource
    @Structure( type = VolumeAllocateMapper.class )
    VolumeAllocateManipulator volumeAllocateManipulator;

    @Resource
    @Structure( type = SqliteVolumeMapper.class )
    SqliteVolumeManipulator   sqliteVolumeManipulator;

    public VolumeMasterManipulatorImpl() {

    }

    public VolumeMasterManipulatorImpl( KOIMappingDriver driver ) {
        driver.autoConstruct( VolumeMasterManipulatorImpl.class, Map.of(), this );
        this.skeletonMasterManipulator = new VolumeMasterTreeManipulatorImpl( driver );
    }
    @Override
    public KOISkeletonMasterManipulator getSkeletonMasterManipulator() {
        return this.skeletonMasterManipulator;
    }

    @Override
    public MirroredVolumeManipulator getMirroredVolumeManipulator() {
        return this.mirroredVolumeManipulator;
    }

    @Override
    public MountPointManipulator getMountPointManipulator() {
        return this.mountPointManipulator;
    }

    @Override
    public SimpleVolumeManipulator getSimpleVolumeManipulator() {
        return this.simpleVolumeManipulator;
    }

    @Override
    public SpannedVolumeManipulator getSpannedVolumeManipulator() {
        return this.spannedVolumeManipulator;
    }

    @Override
    public StripedVolumeManipulator getStripedVolumeManipulator() {
        return this.stripedVolumeManipulator;
    }

    @Override
    public VolumeCapacityManipulator getVolumeCapacityManipulator() {
        return this.volumeCapacityManipulator;
    }

    @Override
    public PhysicalVolumeManipulator getPhysicalVolumeManipulator() {
        return this.physicalVolumeManipulator;
    }

    @Override
    public VolumeAllocateManipulator getVolumeAllocateManipulator() {
        return this.volumeAllocateManipulator;
    }

    @Override
    public SqliteVolumeManipulator getSqliteVolumeManipulator() {
        return this.sqliteVolumeManipulator;
    }
}
