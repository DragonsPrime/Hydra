package com.pinecone.hydra.service.kom.operator;

import com.pinecone.hydra.service.kom.ServicesInstrument;
import com.pinecone.hydra.service.kom.entity.CommonMeta;
import com.pinecone.hydra.service.kom.entity.ElementNode;
import com.pinecone.hydra.service.kom.source.CommonDataManipulator;
import com.pinecone.hydra.service.kom.source.ServiceMasterManipulator;
import com.pinecone.hydra.unit.imperium.ImperialTree;

public abstract class ArchElementOperator implements ElementOperator {
    protected ServicesInstrument            servicesInstrument;
    protected ImperialTree                  imperialTree;
    protected CommonDataManipulator         commonDataManipulator;
    protected ServiceMasterManipulator      serviceMasterManipulator;
    protected ElementOperatorFactory        factory;

    public ArchElementOperator( ElementOperatorFactory factory ){
        this( factory.getServiceMasterManipulator(),factory.getServicesTree() );
        this.factory = factory;
    }
    public ArchElementOperator( ServiceMasterManipulator masterManipulator, ServicesInstrument servicesInstrument ){
        this.imperialTree = servicesInstrument.getMasterTrieTree();
        this.servicesInstrument       = servicesInstrument;
        this.commonDataManipulator    = masterManipulator.getCommonDataManipulator();
        this.serviceMasterManipulator = masterManipulator;
        //this.factory = new GenericServiceOperatorFactory(servicesTree,masterManipulator);
    }

    public ElementOperatorFactory getOperatorFactory() {
        return this.factory;
    }

    protected void applyCommonMeta( ElementNode ele, CommonMeta commonMeta ){
        if( commonMeta != null ) {
            ele.setGuid             ( commonMeta.getGuid()             );
            ele.setScenario         ( commonMeta.getScenario()         );
            ele.setPrimaryImplLang  ( commonMeta.getPrimaryImplLang()  );
            ele.setExtraInformation ( commonMeta.getExtraInformation() );
            ele.setLevel            ( commonMeta.getLevel()            );
            ele.setDescription      ( commonMeta.getDescription()      );
        }
    }
}
