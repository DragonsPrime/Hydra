package com.pinecone.hydra.registry.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.JSON;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.registry.KOMRegistry;
import com.pinecone.hydra.unit.imperium.entity.TreeNode;

import java.util.List;

public class GenericTextFile extends ArchConfigNode implements TextFile {
    protected TextValue             mTextValue;

    public GenericTextFile() {
    }

    public GenericTextFile( KOMRegistry registry ) {
        this.registry = registry;
    }

    @Override
    public void setTextValue( TextValue textValue ) {
        this.mTextValue = textValue;
    }

    @Override
    public void put( TextValue textValue ) {
        if( this.mTextValue == null ) {
            this.registry.putTextValue( textValue.getGuid(), textValue.getValue(), textValue.getType() );
        }
        else {
            this.update( textValue );
            this.mTextValue = textValue;
        }
    }

    @Override
    public Object decode() {
        TextValue value = this.get();
        return this.registry.getTextValueTypeConverter().converter( value.getValue(), value.getType() );
    }

    @Override
    public Object toJSON() {
        TextValue value = this.get();
        String type = this.registry.getTextValueTypeConverter().queryRecognizedType( value.getType() );
        Object ret  = this.decode();
        if( type == null || !this.registry.getTextValueTypeConverter().isJSON( ret ) ) {
            JSONObject reparse = new JSONMaptron();
            reparse.put( "type", value.getType() );
            reparse.put( "value", value.getValue() );
            return reparse;
        }
        return ret;
    }

    @Override
    public void remove( GUID guid ) {
        this.registry.removeTextValue(guid);
    }

    @Override
    public void update( TextValue textValue ) {
        this.registry.updateTextValue( textValue, this.guid );
    }


    @Override
    public void update( String text, String format ) {
        TextValue textValue = GenericTextValue.newUpdateTextValue( this.guid, text, format );
        this.update( textValue );
    }

    @Override
    public void put( String text, String format ) {
        if( this.mTextValue == null ) {
            this.registry.putTextValue( this.guid, text, format );
        }
        else {
            this.update( text, format );
        }
    }

    @Override
    public TextValue get() {
        return this.mTextValue;
    }



    public KOMRegistry parentRegistry() {
        return this.registry;
    }


    public void setRegistry(KOMRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void copyTo( String path ) {
        this.copyTo( this.registry.affirmTextConfig( path ).getGuid() );
    }

    @Override
    public void copyTo( GUID destinationGuid ) {
        TextFile thisCopy = null;
        RegistryTreeNode tn = this.registry.get( destinationGuid );
        if( tn.evinceTextFile() == null ) {
            List<TreeNode> destChildren = this.registry.getChildren( destinationGuid );
            for( TreeNode node : destChildren ) {
                if( this.getName().equals( node.getName() ) ) {
                    if( node instanceof TextFile) {
                        thisCopy = (TextFile) node;
                        break;
                    }
                    else {
                        throw new IllegalArgumentException(
                                String.format( "Existed child-destination [%s] should be text config.", this.getName() )
                        );
                    }
                }
            }
        }
        else {
            thisCopy = (TextFile) tn;
        }


        // Child-Destination non-exist.
        if( thisCopy == null ) {
            thisCopy = new GenericTextFile( this.registry );

            this.putNewCopy( thisCopy, destinationGuid );
        }

        this.copyMetaTo( thisCopy.getGuid() );
        this.copyValueTo( thisCopy.getGuid() );
    }

    @Override
    public void copyValueTo( GUID destinationGuid ) {
        if ( destinationGuid != null ){
            this.registry.copyTextValueTo( this.guid, destinationGuid );
        }
    }

    @Override
    public String toJSONString() {
        return JSON.stringify( this.toJSON() );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
