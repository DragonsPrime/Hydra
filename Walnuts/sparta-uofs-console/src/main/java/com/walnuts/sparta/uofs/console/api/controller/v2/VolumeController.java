package com.walnuts.sparta.uofs.console.api.controller.v2;

import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping( "/api/v2/uofs/volume" )
public class VolumeController {
    @Resource
    private UniformVolumeManager primaryVolume;
}
