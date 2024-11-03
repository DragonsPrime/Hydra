package com.pinecone.hydra.system.ko.kom;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.identifier.KOPathResolver;
import com.pinecone.hydra.system.ko.KernelObjectConfig;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.hydra.unit.udtt.entity.ReparseLinkNode;
import com.pinecone.hydra.unit.udtt.operator.OperatorFactory;

public abstract class ArchReparseKOMTree extends ArchKOMTree implements ReparseKOMTree {
    protected ReparseKOMTreeAddition mReparseKOM;

    public ArchReparseKOMTree(
            Hydrarum hydrarum, KOIMasterManipulator masterManipulator , OperatorFactory operatorFactory, KernelObjectConfig kernelObjectConfig, PathSelector pathSelector,
            KOMInstrument parent, String name
    ){
        this( hydrarum, masterManipulator, kernelObjectConfig, parent, name );
        this.pathResolver                  =  new KOPathResolver( kernelObjectConfig );
        this.pathSelector                  =  pathSelector;
        this.operatorFactory               =  operatorFactory;
        this.mReparseKOM                   =  new GenericReparseKOMTreeAddition( this );
    }

    public ArchReparseKOMTree (
            Hydrarum hydrarum, KOIMasterManipulator masterManipulator ,KernelObjectConfig kernelObjectConfig, KOMInstrument parent, String name
    ){
        super( hydrarum, masterManipulator, kernelObjectConfig, parent, name );
    }

    @Override
    public ReparseLinkNode queryReparseLinkByNS( String path, String szBadSep, String szTargetSep ) {
        return this.mReparseKOM.queryReparseLinkByNS( path, szBadSep, szTargetSep );
    }

    @Override
    public ReparseLinkNode queryReparseLink( String path ) {
        return this.queryReparseLinkByNS( path, null, null );
    }

    @Override
    public void affirmOwnedNode( GUID parentGuid, GUID childGuid ) {
        this.mReparseKOM.affirmOwnedNode( parentGuid, childGuid );
    }

    @Override
    public void newHardLink( GUID sourceGuid, GUID targetGuid ) {
        this.mReparseKOM.newHardLink( sourceGuid, targetGuid );
    }

    @Override
    public void newLinkTag( GUID originalGuid, GUID dirGuid, String tagName ) {
        this.mReparseKOM.newLinkTag( originalGuid, dirGuid, tagName);
    }

    @Override
    public void updateLinkTag( GUID tagGuid, String tagName ) {
        this.mReparseKOM.updateLinkTag( tagGuid, tagName );
    }

    @Override
    public void removeReparseLink( GUID guid ) {
        this.mReparseKOM.removeReparseLink( guid );
    }

    @Override
    public void newLinkTag( String originalPath, String dirPath, String tagName ) {
        this.mReparseKOM.newLinkTag( originalPath, dirPath, tagName );
    }

    /** ReparseLinkNode or GUID **/
    @Override
    public Object queryEntityHandleByNS( String path, String szBadSep, String szTargetSep ) {
        return this.mReparseKOM.queryEntityHandleByNS( path, szBadSep, szTargetSep );
    }

    @Override
    public void remove( String path ) {
        this.mReparseKOM.remove( path );
    }
}
