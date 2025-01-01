package com.wolf.construction;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.pinecone.framework.system.construction.Structure;
import com.pinecone.framework.util.Debug;
import com.pinecone.ulf.beans.aop.UlfurEnableAspectProxy;
import com.pinecone.ulf.beans.construction.StructureAnnotationConfiguration;
import com.pinecone.ulf.beans.construction.UlfInstanceManufacturer;
import com.pinecone.ulf.beans.construction.UlfurInstanceManufacturer;

@Component
public class CanesService {
    @Resource
    //@Structure
    private FoxService foxService;

    @Resource
    private HuskyService huskyService;

    public void test() {
        Debug.trace( "Husky and fox are Canidae." );

        this.foxService.digging();
        this.huskyService.run();

        this.foxService.attack( "Kevin" );

        this.huskyService.tryFoxBlade();
    }

    public static void main( String[] args ) throws Exception {
        UlfInstanceManufacturer manufacturer = new UlfurInstanceManufacturer( StructureAnnotationConfiguration.class, UlfurEnableAspectProxy.class );

        manufacturer.registers( List.of( FoxService.class, HuskyService.class, CanesService.class, FoxBlade.class, CanisAspect.class ) );

        CanesService canes = manufacturer.allotInstance( CanesService.class );
        canes.test();
    }
}
