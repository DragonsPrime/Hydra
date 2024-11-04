package com.pinecone.hydra.storage.volume;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.MountPoint;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.TitanVolumeAllotment;
import com.pinecone.hydra.storage.volume.entity.VolumeAllotment;
import com.pinecone.hydra.storage.volume.entity.VolumeCapacity64;
import com.pinecone.hydra.storage.volume.operator.TitanVolumeOperatorFactory;
import com.pinecone.hydra.storage.volume.source.LogicVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.MirroredVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.MountPointManipulator;
import com.pinecone.hydra.storage.volume.source.PhysicalVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.SimpleVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.SpannedVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.SQLiteVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.StripedVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.VolumeAllocateManipulator;
import com.pinecone.hydra.storage.volume.source.VolumeCapacityManipulator;
import com.pinecone.hydra.storage.volume.source.VolumeMasterManipulator;
import com.pinecone.hydra.system.Hydrarum;
import com.pinecone.hydra.system.identifier.KOPathResolver;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;
import com.pinecone.hydra.system.ko.kom.ArchKOMTree;
import com.pinecone.hydra.system.ko.kom.SimplePathSelector;
import com.pinecone.hydra.unit.udtt.DistributedTreeNode;
import com.pinecone.hydra.unit.udtt.DistributedTrieTree;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.entity.EntityNode;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;
import com.pinecone.hydra.unit.udtt.operator.TreeNodeOperator;
import com.pinecone.ulf.util.id.GUIDs;
import com.pinecone.ulf.util.id.GuidAllocator;

import java.util.List;
import java.util.Objects;

public class UniformVolumeManager extends ArchKOMTree implements VolumeManager {
    protected VolumeAllotment                   volumeAllotment;
    protected MirroredVolumeManipulator         mirroredVolumeManipulator;
    protected MountPointManipulator             mountPointManipulator;
    protected PhysicalVolumeManipulator         physicalVolumeManipulator;
    protected SimpleVolumeManipulator           simpleVolumeManipulator;
    protected SpannedVolumeManipulator          spannedVolumeManipulator;
    protected StripedVolumeManipulator          stripedVolumeManipulator;
    protected VolumeCapacityManipulator         volumeCapacityManipulator;
    protected VolumeMasterManipulator           volumeMasterManipulator;
    protected VolumeAllocateManipulator         volumeAllocateManipulator;
    protected SQLiteVolumeManipulator           sqliteVolumeManipulator;
    protected LogicVolumeManipulator            primeLogicVolumeManipulator;


    public UniformVolumeManager( Hydrarum hydrarum, KOIMasterManipulator masterManipulator, VolumeManager parent, String name ) {
        super( hydrarum, masterManipulator, VolumeManager.KernelVolumeConfig, parent, name );

        this.volumeMasterManipulator       =   ( VolumeMasterManipulator ) masterManipulator;
        this.pathResolver                  =   new KOPathResolver( this.kernelObjectConfig );
        this.guidAllocator                 =   GUIDs.newGuidAllocator();

        this.operatorFactory               =   new TitanVolumeOperatorFactory( this, this.volumeMasterManipulator );
        this.volumeAllotment               =   new TitanVolumeAllotment( this,this.volumeMasterManipulator );
        this.mirroredVolumeManipulator     =   this.volumeMasterManipulator.getMirroredVolumeManipulator();
        this.mountPointManipulator         =   this.volumeMasterManipulator.getMountPointManipulator();
        this.physicalVolumeManipulator     =   this.volumeMasterManipulator.getPhysicalVolumeManipulator();
        this.simpleVolumeManipulator       =   this.volumeMasterManipulator.getSimpleVolumeManipulator();
        this.spannedVolumeManipulator      =   this.volumeMasterManipulator.getSpannedVolumeManipulator();
        this.stripedVolumeManipulator      =   this.volumeMasterManipulator.getStripedVolumeManipulator();
        this.volumeCapacityManipulator     =   this.volumeMasterManipulator.getVolumeCapacityManipulator();
        this.volumeAllocateManipulator     =   this.volumeMasterManipulator.getVolumeAllocateManipulator();
        this.sqliteVolumeManipulator       =   this.volumeMasterManipulator.getSQLiteVolumeManipulator();
        this.primeLogicVolumeManipulator   =   this.volumeMasterManipulator.getPrimeLogicVolumeManipulator();

        this.pathSelector                  =   new SimplePathSelector(
                this.pathResolver, this.distributedTrieTree, this.primeLogicVolumeManipulator, new GUIDNameManipulator[] {}
        );
    }

    public UniformVolumeManager( Hydrarum hydrarum, KOIMasterManipulator masterManipulator ) {
        this( hydrarum, masterManipulator, null, VolumeManager.class.getSimpleName() );
    }

    public UniformVolumeManager( KOIMappingDriver driver, VolumeManager parent, String name ){
        this( driver.getSystem(), driver.getMasterManipulator(), parent, name );
    }

    public UniformVolumeManager( KOIMappingDriver driver ) {
        this( driver.getSystem(), driver.getMasterManipulator() );
    }

    @Override
    public GuidAllocator getGuidAllocator() {
        return this.guidAllocator;
    }

    @Override
    public DistributedTrieTree getMasterTrieTree() {
        return this.distributedTrieTree;
    }

    @Override
    public VolumeConfig getConfig() {
        return (VolumeConfig) this.kernelObjectConfig;
    }

    public VolumeAllotment getVolumeAllotment(){
        return this.volumeAllotment;
    }

    protected String getNS( GUID guid, String szSeparator ){
        String path = this.distributedTrieTree.getCachePath(guid);
        if ( path != null ) {
            return path;
        }

        DistributedTreeNode node = this.distributedTrieTree.getNode(guid);
        String assemblePath = this.getNodeName(node);
        while ( !node.getParentGUIDs().isEmpty() && this.allNonNull( node.getParentGUIDs() ) ){
            List<GUID> parentGuids = node.getParentGUIDs();
            for( int i = 0; i < parentGuids.size(); ++i ){
                if ( parentGuids.get(i) != null ){
                    node = this.distributedTrieTree.getNode( parentGuids.get(i) );
                    break;
                }
            }
            String nodeName = this.getNodeName(node);
            assemblePath = nodeName + szSeparator + assemblePath;
        }
        this.distributedTrieTree.insertCachePath( guid, assemblePath );
        return assemblePath;
    }

    @Override
    public String getPath( GUID guid ) {
        return this.getNS( guid, this.kernelObjectConfig.getPathNameSeparator() );
    }

    @Override
    public String getFullName( GUID guid ) {
        return this.getNS( guid, this.kernelObjectConfig.getFullNameSeparator() );
    }

    @Override
    public GUID queryGUIDByFN( String fullName ) {
        return null;
    }

    @Override
    public GUID put( TreeNode treeNode ) {
        TreeNodeOperator operator = this.operatorFactory.getOperator( this.getVolumeMetaType( treeNode ) );
        return operator.insert( treeNode );
    }

    protected TreeNodeOperator getOperatorByGuid( GUID guid ) {
        DistributedTreeNode node = this.distributedTrieTree.getNode( guid );
        TreeNode newInstance = (TreeNode)node.getType().newInstance( new Class<? >[]{this.getClass()}, this );
        return this.operatorFactory.getOperator( this.getVolumeMetaType( newInstance ) );
    }

    @Override
    public LogicVolume get( GUID guid ) {
        return (LogicVolume) this.getOperatorByGuid( guid ).get( guid );
    }

    @Override
    public TreeNode get(GUID guid, int depth) {
        return null;
    }

    @Override
    public TreeNode getSelf(GUID guid) {
        return null;
    }

    @Override
    public void remove(GUID guid) {
        GUIDDistributedTrieNode node = this.distributedTrieTree.getNode( guid );
        TreeNode newInstance = (TreeNode)node.getType().newInstance();
        TreeNodeOperator operator = this.operatorFactory.getOperator( this.getVolumeMetaType( newInstance ) );
        operator.purge( guid );
    }

    @Override
    public void remove(String path) {

    }

    @Override
    public PhysicalVolume getPhysicalVolume(GUID guid) {
        PhysicalVolume physicalVolume = this.physicalVolumeManipulator.getPhysicalVolume(guid);
        MountPoint mountPoint = this.mountPointManipulator.getMountPointByVolumeGuid(guid);
        VolumeCapacity64 volumeCapacity = this.volumeCapacityManipulator.getVolumeCapacity(guid);
        physicalVolume.setMountPoint( mountPoint );
        physicalVolume.setVolumeCapacity( volumeCapacity );
        return physicalVolume;
    }

    @Override
    public List<TreeNode> getChildren(GUID guid) {
        return super.getChildren( guid );
    }

    @Override
    public Object queryEntityHandleByNS(String path, String szBadSep, String szTargetSep) {
        return null;
    }

    @Override
    public EntityNode queryNode(String path) {
        return null;
    }

    @Override
    public List<? extends TreeNode> fetchRoot() {
        return null;
    }

    @Override
    public void rename(GUID guid, String name) {

    }

    private boolean allNonNull( List<?> list ) {
        return list.stream().noneMatch( Objects::isNull );
    }

    @Override
    public GUID insertPhysicalVolume(PhysicalVolume physicalVolume) {
        GUID guid = physicalVolume.getGuid();
        VolumeCapacity64 volumeCapacity = physicalVolume.getVolumeCapacity();
        if( volumeCapacity.getVolumeGuid() == null ){
            volumeCapacity.setVolumeGuid( guid );
        }

        MountPoint mountPoint = physicalVolume.getMountPoint();
        if( mountPoint.getVolumeGuid() == null ){
            mountPoint.setVolumeGuid( guid );
        }


        this.physicalVolumeManipulator.insert( physicalVolume );
        this.volumeCapacityManipulator.insert( volumeCapacity );
        this.mountPointManipulator.insert( mountPoint );
        return guid;
    }

    @Override
    public void purgePhysicalVolume(GUID guid) {
        this.physicalVolumeManipulator.remove( guid );
        this.volumeCapacityManipulator.remove( guid );
        this.mountPointManipulator.removeByVolumeGuid( guid );
    }

    @Override
    public void insertAllocate(GUID objectGuid, GUID childVolumeGuid, GUID parentVolumeGuid) {
        this.volumeAllocateManipulator.insert( objectGuid, childVolumeGuid, parentVolumeGuid);
    }

    @Override
    public PhysicalVolume getSmallestCapacityPhysicalVolume() {
        PhysicalVolume smallestCapacityPhysicalVolume = this.physicalVolumeManipulator.getSmallestCapacityPhysicalVolume();
        return this.getPhysicalVolume( smallestCapacityPhysicalVolume.getGuid() );
    }


    @Override
    public VolumeMasterManipulator getMasterManipulator() {
        return this.volumeMasterManipulator;
    }

    private String getNodeName(DistributedTreeNode node ){
        UOI type = node.getType();
        TreeNode newInstance = (TreeNode)type.newInstance();
        TreeNodeOperator operator = this.operatorFactory.getOperator(this.getVolumeMetaType( newInstance ));
        TreeNode treeNode = operator.get(node.getGuid());
        return treeNode.getName();
    }

    private String getVolumeMetaType( TreeNode treeNode ){
        return treeNode.className().replace("Titan","");
    }
}
