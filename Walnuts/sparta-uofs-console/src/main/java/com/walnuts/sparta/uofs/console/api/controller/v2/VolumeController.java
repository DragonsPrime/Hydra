package com.walnuts.sparta.uofs.console.api.controller.v2;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.MountPoint;
import com.pinecone.hydra.storage.volume.entity.VolumeAllotment;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.entity.local.LocalPhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalSimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalSpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalStripedVolume;
import com.pinecone.ulf.util.id.GUIDs;
import com.walnuts.sparta.uofs.console.api.response.BasicResultResponse;
import com.walnuts.sparta.uofs.console.domain.dto.PhysicalVolumeDTO;
import com.walnuts.sparta.uofs.console.domain.dto.LogicVolumeDTO;
import com.walnuts.sparta.uofs.console.domain.dto.StorageExpansionDTO;
import com.walnuts.sparta.uofs.console.infrastructure.UOFSConsoleContents;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping( "/api/v2/uofs/volume" )
public class VolumeController {
    @Resource
    private UniformVolumeManager primaryVolume;

    /**
     * 创建物理卷
     * @param dto 创建物理卷需要的参数
     * @return 返回操作结果
     */
    @PostMapping("/create/physicalVolume")
    public BasicResultResponse<String> createPhysicalVolume(@RequestBody PhysicalVolumeDTO dto){
        VolumeAllotment volumeAllotment = this.primaryVolume.getVolumeAllotment();
        LocalPhysicalVolume physicalVolume = volumeAllotment.newLocalPhysicalVolume();

        physicalVolume.setName( dto.getName() );
        physicalVolume.setType( UOFSConsoleContents.VOLUME_TYPE_PHYSICAL );
        physicalVolume.setExtConfig(dto.getExtConfig() );

        VolumeCapacity64 volumeCapacity = volumeAllotment.newVolumeCapacity();
        volumeCapacity.setDefinitionCapacity( dto.getDefinitionCapacity() );

        MountPoint mountPoint = volumeAllotment.newMountPoint();
        mountPoint.setMountPoint(dto.getMountPoint() );

        physicalVolume.setMountPoint( mountPoint );
        physicalVolume.setVolumeCapacity( volumeCapacity );

        this.primaryVolume.insertPhysicalVolume( physicalVolume );

        return BasicResultResponse.success();
    }

    /**
     * 创建简单卷
     * @param dto 创建简单卷所需的参数
     * @return 返回操作结果
     * @throws SQLException sqlite异常
     */
    @PostMapping("/create/simpleVolume")
    public BasicResultResponse<String> createSimpleVolume(@RequestBody LogicVolumeDTO dto) throws SQLException {
        VolumeAllotment volumeAllotment = this.primaryVolume.getVolumeAllotment();
        LocalSimpleVolume simpleVolume = volumeAllotment.newLocalSimpleVolume();

        simpleVolume.setType(UOFSConsoleContents.VOLUME_TYPE_SIMPLE);
        simpleVolume.setName(dto.getName() );
        simpleVolume.setExtConfig(dto.getExtConfig() );

        VolumeCapacity64 volumeCapacity = volumeAllotment.newVolumeCapacity();
        volumeCapacity.setDefinitionCapacity( dto.getDefinitionCapacity() );

        simpleVolume.setVolumeCapacity( volumeCapacity );
        simpleVolume.build();

        return BasicResultResponse.success();
    }

    /**
     * 创建跨区卷
     * @param dto 跨区卷所需的参数
     * @return 返回操作结果
     * @throws SQLException sqlite异常
     */
    @PostMapping("/create/spannedVolume")
    public BasicResultResponse<String> createSpannedVolume(@RequestBody LogicVolumeDTO dto) throws SQLException {
        VolumeAllotment volumeAllotment = this.primaryVolume.getVolumeAllotment();
        LocalSpannedVolume spannedVolume = volumeAllotment.newLocalSpannedVolume();

        spannedVolume.setType(UOFSConsoleContents.VOLUME_TYPE_SPANNED);
        spannedVolume.setName(dto.getName() );
        spannedVolume.setExtConfig(dto.getExtConfig());

        VolumeCapacity64 volumeCapacity = volumeAllotment.newVolumeCapacity();
        volumeCapacity.setDefinitionCapacity( dto.getDefinitionCapacity() );

        spannedVolume.setVolumeCapacity( volumeCapacity );
        spannedVolume.build();

        return BasicResultResponse.success();
    }

    /**
     * 创建条带卷
     * @param dto 创建条带卷所需的参数
     * @return 返回操作结果
     * @throws SQLException sqlite异常
     */
    @PostMapping("/create/stripedVolume")
    public BasicResultResponse<String> createStripedVolume(@RequestBody LogicVolumeDTO dto) throws SQLException {
        VolumeAllotment volumeAllotment = this.primaryVolume.getVolumeAllotment();
        LocalStripedVolume stripedVolume = volumeAllotment.newLocalStripedVolume();

        stripedVolume.setType( UOFSConsoleContents.VOLUME_TYPE_STRIPED );
        stripedVolume.setName(dto.getName() );
        stripedVolume.setExtConfig(dto.getExtConfig() );

        VolumeCapacity64 volumeCapacity = volumeAllotment.newVolumeCapacity();
        volumeCapacity.setDefinitionCapacity( dto.getDefinitionCapacity() );

        stripedVolume.setVolumeCapacity( volumeCapacity );
        stripedVolume.build();

        return BasicResultResponse.success();
    }

    /**
     * 逻辑卷扩容
     * @param dto 扩容所需参数
     * @return 返回操作结果
     */
    @PostMapping("/storageExpansion")
    public BasicResultResponse<String> storageExpansion(@RequestBody StorageExpansionDTO dto){
        GUID logicGuid = GUIDs.GUID72( dto.getLogicGuid() );
        GUID physicalGuid = GUIDs.GUID72( dto.getPhysicalGuid() );

        LogicVolume logicVolume = this.primaryVolume.get(logicGuid);

        logicVolume.storageExpansion( physicalGuid );
        return BasicResultResponse.success();
    }

    /**
     * 获取子卷
     * @param volumeGuid 卷guid
     * @return 返回子集情况
     */
    @GetMapping("/getChildren")
    public String getChildren(@RequestParam("volumeGuid") String volumeGuid){
        LogicVolume logicVolume = this.primaryVolume.get(GUIDs.GUID72(volumeGuid));
        if(logicVolume == null){
            return "物理卷不存在子集";
        }

        List<LogicVolume> volumes = logicVolume.queryChildren();
        ArrayList<LogicVolume> arrayList = new ArrayList<>(volumes);

        return BasicResultResponse.success(arrayList).toJSONString();
    }

}
