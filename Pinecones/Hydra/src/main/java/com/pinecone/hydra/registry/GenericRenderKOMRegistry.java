package com.pinecone.hydra.registry;

import com.pinecone.framework.system.executum.Processum;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.template.UTRAlmondProvider;
import com.pinecone.framework.util.template.UniformTemplateRenderer;
import com.pinecone.hydra.registry.render.RenderConfigNode;
import com.pinecone.hydra.registry.render.RenderRegistryTreeNode;
import com.pinecone.hydra.registry.render.RenderTextValue;
import com.pinecone.hydra.system.ko.driver.KOIMasterManipulator;

public class GenericRenderKOMRegistry extends GenericKOMRegistry implements RenderDistributeRegistry {
    protected UniformTemplateRenderer       mUniformTemplateRenderer;
    private UniformTemplateRenderer         renderer;

    public GenericRenderKOMRegistry( Processum superiorProcess, KOIMasterManipulator masterManipulator ){
        super( superiorProcess, masterManipulator );
        this.renderer = new UTRAlmondProvider();
    }


    @Override
    public RenderRegistryTreeNode getSelf( GUID guid ) {
        return (RenderRegistryTreeNode) this.getOperatorByGuid( guid ).getSelf( guid );
    }

    @Override
    public RenderConfigNode getConfigNode( GUID guid ) {
        return (RenderConfigNode) super.getConfigNode(guid);
    }

    @Override
    public RenderTextValue getTextValue( GUID guid ) {
        return (RenderTextValue) this.registryTextFileManipulator.getTextValue(guid);
    }

    @Override
    public UniformTemplateRenderer getRenderer() {
        return this.renderer;
    }
}
