package com.pinecone.hydra.storage.volume;

import com.pinecone.hydra.storage.volume.entity.ExporterEntity;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.local.simple.TitanLocalSimpleVolume;

import com.pinecone.hydra.storage.volume.entity.local.simple.export.TitanSimpleExportEntity64;

import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.TitanSimpleReceiveEntity64;
import com.pinecone.hydra.storage.volume.entity.local.spanned.TitanLocalSpannedVolume;

import com.pinecone.hydra.storage.volume.entity.local.spanned.export.TitanSpannedExportEntity64;

import com.pinecone.hydra.storage.volume.entity.local.spanned.receive.TitanSpannedReceiveEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanLocalStripedVolume;

import com.pinecone.hydra.storage.volume.entity.local.striped.export.TitanStripedExportEntity64;

import com.pinecone.hydra.storage.volume.entity.local.striped.receive.TitanStripedReceiveEntity64;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class UnifiedTransmitConstructor implements IUnifiedTransmitConstructor{

    private Map< Class< ? extends LogicVolume >, Class< ? extends ReceiveEntity > > receiveMap = new HashMap<>();

    private Map< Class< ? extends LogicVolume >, Class< ? extends ExporterEntity > > exportMap = new HashMap<>();

    public UnifiedTransmitConstructor(){
        this.receiveMap.put( TitanLocalSimpleVolume.class, TitanSimpleReceiveEntity64.class );
        this.receiveMap.put( TitanLocalSpannedVolume.class, TitanSpannedReceiveEntity64.class );
        this.receiveMap.put( TitanLocalStripedVolume.class, TitanStripedReceiveEntity64.class );

        this.exportMap.put( TitanLocalSimpleVolume.class, TitanSimpleExportEntity64.class );
        this.exportMap.put( TitanLocalSpannedVolume.class, TitanSpannedExportEntity64.class );
        this.exportMap.put( TitanLocalStripedVolume.class, TitanStripedExportEntity64.class );
    }

    @Override
    public ReceiveEntity getReceiveEntity(Class<? extends LogicVolume> volumeClass, Object... params) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends ReceiveEntity> receiveEntityClass = receiveMap.get(volumeClass);
        if( receiveEntityClass == null ){
            throw new IllegalArgumentException(" No find class ");
        }

        Constructor<? extends ReceiveEntity> receiveConstructor = this.findReceiveConstructor(receiveEntityClass, params);

        return receiveConstructor.newInstance( params );
    }

    @Override
    public ExporterEntity getExportEntity(Class<? extends LogicVolume> volumeClass, Object... params) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends ExporterEntity> exportEntityClass = exportMap.get(volumeClass);
        if( exportEntityClass == null ){
            throw new IllegalArgumentException(" No find class ");
        }

        Constructor<? extends ExporterEntity> exportConstructor = this.findExportConstructor(exportEntityClass, params);
        return exportConstructor.newInstance( params );
    }

    private  Constructor<? extends ReceiveEntity> findReceiveConstructor(Class<? extends ReceiveEntity> clazz, Object... params) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == params.length) {
                boolean matches = true;
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                for (int i = 0; i < params.length; i++) {
                    if (!parameterTypes[i].isInstance(params[i])) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    return (Constructor<? extends ReceiveEntity>) constructor;
                }
            }
        }
        return null;
    }

    private  Constructor<? extends ExporterEntity> findExportConstructor(Class<? extends ExporterEntity> clazz, Object... params) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == params.length) {
                boolean matches = true;
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                for (int i = 0; i < params.length; i++) {
                    if (!parameterTypes[i].isInstance(params[i])) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    return (Constructor<? extends ExporterEntity>) constructor;
                }
            }
        }
        return null;
    }
}
