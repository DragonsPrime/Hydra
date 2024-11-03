package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.source.RemoteFrameManipulator;

public class GenericRemoteFrame extends ArchFrame implements RemoteFrame {
    private GUID                    deviceGuid;
    private RemoteFrameManipulator  frameManipulator;

    public GenericRemoteFrame() {
    }

    public GenericRemoteFrame(GUID deviceGuid) {
        this.deviceGuid = deviceGuid;
    }

    public GenericRemoteFrame( RemoteFrameManipulator remoteFrameManipulator ) {
        this.frameManipulator = remoteFrameManipulator;

    }

    public GUID getDeviceGuid() {
        return deviceGuid;
    }


    public void setDeviceGuid(GUID deviceGuid) {
        this.deviceGuid = deviceGuid;
    }

    @Override
    public void setRemoteFrameManipulator(RemoteFrameManipulator remoteFrameManipulator) {
        this.frameManipulator = remoteFrameManipulator;
    }

    public String toString() {
        return "GenericRemoteCluster{deviceGuid = " + deviceGuid + "}";
    }

    @Override
    public void save() {
        this.frameManipulator.insert(this);
    }
    @Override
    public void remove() {
        this.frameManipulator.remove( this.getSegGuid() );
    }
}
