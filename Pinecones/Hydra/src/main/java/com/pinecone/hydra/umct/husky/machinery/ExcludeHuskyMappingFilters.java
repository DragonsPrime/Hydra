package com.pinecone.hydra.umct.husky.machinery;

import java.io.IOException;
import java.util.List;

import com.pinecone.framework.util.lang.TypeFilter;
import com.pinecone.hydra.umct.husky.compiler.ClassDigest;
import com.pinecone.hydra.umct.mapping.MappingDigest;
import com.pinecone.ulf.util.lang.HierarchyClassInspector;

import javassist.NotFoundException;

public class ExcludeHuskyMappingFilters implements TypeFilter {
    protected HierarchyClassInspector mClassInspector;
    protected PMCTContextMachinery mPMCTContextMachinery;

    public ExcludeHuskyMappingFilters( HierarchyClassInspector inspector, PMCTContextMachinery marshal ) {
        this.mClassInspector = inspector;
        this.mPMCTContextMachinery = marshal;
    }

    @Override
    public boolean match( String szClassName, Object pool ) throws IOException {
        boolean isIface = this.scanIface( szClassName, pool );
        if ( isIface ) {
            return false;
        }

        boolean isController = this.scanController( szClassName, pool );
        return !isController;
    }

    protected boolean scanIface( String szClassName, Object pool ) throws IOException {
        ClassDigest classDigest = this.mPMCTContextMachinery.getInterfacialCompiler().compile( szClassName, false );
        if ( classDigest != null ) {
            this.mPMCTContextMachinery.addClassDigest( classDigest );
            return true;
        }
        return false;
    }

    protected boolean scanController( String szClassName, Object pool ) throws IOException {
        try{
            List<MappingDigest > mappingDigests = this.mPMCTContextMachinery.getControllerInspector().characterize( szClassName );
            if ( mappingDigests != null && !mappingDigests.isEmpty() ) {
                this.mPMCTContextMachinery.addAll( mappingDigests );
                return true;
            }
        }
        catch ( NotFoundException e ) {
            return false;
        }

        return false;
    }

}