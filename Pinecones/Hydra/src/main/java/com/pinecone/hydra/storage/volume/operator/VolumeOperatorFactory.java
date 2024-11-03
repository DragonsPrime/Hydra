package com.pinecone.hydra.storage.volume.operator;

import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.LocalSimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalSpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.LocalStripedVolume;
import com.pinecone.hydra.storage.volume.source.VolumeMasterManipulator;
import com.pinecone.hydra.unit.udtt.operator.OperatorFactory;
import com.pinecone.hydra.unit.udtt.operator.TreeNodeOperator;

public interface VolumeOperatorFactory extends OperatorFactory {
    String DefaultSimpleVolume         = LocalSimpleVolume.class.getSimpleName();
    String DefaultStripedVolume        = LocalStripedVolume.class.getSimpleName();
    String DefaultSpannedVolume        = LocalSpannedVolume.class.getSimpleName();

    void register( String typeName, TreeNodeOperator functionalNodeOperation );

    void registerMetaType( Class<?> clazz, String metaType );

    void registerMetaType( String classFullName, String metaType );

    String getMetaType( String classFullName );

    VolumeOperator getOperator(String typeName );

    VolumeManager getVolumeManager();

    VolumeMasterManipulator getMasterManipulator();
}
