package com.pinecone.framework.util.json;

import com.pinecone.framework.system.ErrorStrings;
import com.pinecone.framework.system.prototype.FamilyContext;
import com.pinecone.framework.system.prototype.OverridableFamily;
import com.pinecone.framework.unit.MultiScopeMap;
import com.pinecone.framework.unit.MultiScopeMaptron;
import com.pinecone.framework.util.template.TemplateParser;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *  Pinecone For Java JPlusCursorParser [ Bean Nuts Almond Dragon, JSON+ For Pinecone Java ]
 *  Copyright © 2008 - 2024 Bean Nuts Foundation ( Dragon King ) All rights reserved. [Harold.E / JH.W]
 *  *****************************************************************************************
 *  Author: undefined
 *  Last Modified Date: 2024-02-17
 *  *****************************************************************************************
 *  JSON Plus is an enhanced JSON5 Edition
 *  {
 *      "parent": { k1: 123 },
 *      "next": {
 *         key: #include "path",
 *        key2: #"${this.key}",
 *        key3: #"${root.parent.k1}", // key3: #"${parent.k1}",
 *        key4: "normal-string", // key4: normal-string, "key4": normal-string, 'key4': normal-string
 *        key5: #extends root.parent,
 *      }
 *  }
 *  Support: JSON, JSON5, JSON Plus
 *  *****************************************************************************************
 */
public class JPlusCursorParser extends JSONCursorParser {
    protected FamilyContext mScopeContext;

    public JPlusCursorParser( Reader reader, FamilyContext scopeContext ) {
        super(reader);
        this.mScopeContext      = scopeContext;
    }

    public JPlusCursorParser( InputStream inputStream, FamilyContext scopeContext ) throws JSONParseException {
        this( (Reader)(new InputStreamReader(inputStream)), scopeContext );
    }

    public JPlusCursorParser( String s, FamilyContext scopeContext ) {
        this( (Reader)(new StringReader(s)), scopeContext );
    }

    public FamilyContext getScopeContext() {
        return this.mScopeContext;
    }

    public void setScopeContext( JPlusContext context ) {
        this.mScopeContext = context;
    }

    @SuppressWarnings("unchecked")
    protected Map<Object, Object > construct_reinterpret_scope_domain() {
        FamilyContext context = this.getScopeContext();
        List<MultiScopeMap<Object, Object >> dummy_root = new ArrayList<>();
        dummy_root.add( new MultiScopeMaptron<>( (Map<Object, Object >)context.root() ) );
        List<Object > globalScopes = context.getGlobalScopes();
        if( globalScopes != null ) {
            for ( Object scope : globalScopes ) {
                dummy_root.add( new MultiScopeMaptron<>( (Map<Object, Object >)scope ) );
            }
        }

        Object dyThisScope  = context.thisScope();
        Object dySuperScope = context.parent();
        Object dyRootScope  = context.root();

        Map<Object, Object > thisScope;
        if( dyThisScope instanceof Map<?, ? > ){
            thisScope = (Map<Object, Object >) dyThisScope;
        }
        else if( dyThisScope instanceof JSONArray ){
            thisScope = (Map)( (JSONArray) dyThisScope ).toJSONObject();
        }
        else {
            thisScope = new TreeMap<>();
        }

        MultiScopeMaptron<Object, Object > scope = new MultiScopeMaptron<>( null, dummy_root );

        if( context instanceof OverridableFamily && ( (OverridableFamily)context ).isOverriddenAffinity() ) {
            Object $this = scope.get( "this" );
            if( $this != null ) {
                dyThisScope = $this;
            }

            Object $super = scope.get( "super" );
            if( $super != null ) {
                dySuperScope = $super;
            }

            Object $root = scope.get( "__root__" );
            if( $super != null ) {
                dyRootScope = $root;
            }
        }


        scope.setThisScope( thisScope );
        scope.elevate( new TreeMap<>() );
        scope.put( "this"      , dyThisScope     );
        scope.put( "super"     , dySuperScope    );
        scope.put( "__root__"  , dyRootScope     );
        scope.put( "__scope__" , (Object) scope  );

        return scope;
    }

    protected Object reinterpret_eval_object( StringBuilder token ) {
        Map<Object, Object > scope = this.construct_reinterpret_scope_domain();

        TemplateParser tp = new TemplateParser( new StringReader(token.toString()), scope );
        return tp.evalValue();
    }

    protected void reinterpret_eval_token( StringBuilder token ) {
        Map<Object, Object > scope = this.construct_reinterpret_scope_domain();

        TemplateParser tp = new TemplateParser( new StringReader(token.toString()), scope );
        token.setLength(0);
        token.append( tp.eval() );
    }

    /**
     * override_object_with_parent
     * @param dyThisScope
     * @param parent
     * @return a boolean, which indicates it is a qualified K-V-Based object.
     */
    @SuppressWarnings("unchecked")
    protected boolean reinterpret_override_object_with_parent( Object dyThisScope, Object parent ) {
        if( parent instanceof Map ) {
            if( dyThisScope instanceof Map ) {
                ( (Map)dyThisScope ).putAll( (Map)parent );
            }
            else if( dyThisScope instanceof List ) {
                ( (List)dyThisScope ).addAll( ( (Map)parent ).values() );
            }
        }
        else if( parent instanceof List ) {
            List l = (List)parent;
            if( dyThisScope instanceof Map ) {
                int i = 0;
                for ( Object item : l ) {
                    ( (Map)dyThisScope ).put( String.valueOf(i), item );
                    ++i;
                }
            }
            else if( dyThisScope instanceof List ) {
                ( (List)dyThisScope ).addAll( l );
            }
        }
        else {
            return false;
        }

        return true;
    }

    protected Object reinterpret_include_path_from_context_paths( StringBuilder path ) {
        try{
            // Notice, currently context should be <b>Parent</b> NOT 'this'!
            // Under object context, the parser is sequentially parse from sibling to sibling.
            return ( new JPlusCursorParser( new FileReader(path.toString()), this.getScopeContext() ) ).nextValue( null, this.getScopeContext().parent() );
        }
        catch ( IOException e ){
            Path[] parentPaths = this.getScopeContext().getParentPaths();
            Object ret = null;
            for ( int i = 0; i < parentPaths.length; ++i ) {
                try{
                    ret = ( new JPlusCursorParser( new FileReader( parentPaths[i].resolve(path.toString()).toFile() ), this.getScopeContext() ) ).nextValue( null, this.getScopeContext().parent() );
                }
                catch ( IOException e1 ) {
                    ret = null;
                }
            }

            if( ret == null ) {
                throw this.syntaxError( ErrorStrings.E_IRREDEEMABLE_NO_PATH_CONTEXT_MATCHED + "What-> '" + path + "'" );
            }

            return ret;
        }
    }

    protected boolean select_reinterpret_token( StringBuilder token, Object[] ret ) {
        String szToken = token.toString();
        if( szToken.isEmpty() ) {
            return false;
        }

        char c = szToken.charAt(0);
        switch ( c ) {
            case 'i':{
                if( szToken.equals( "include" ) ) {
                    c = this.next();
                    token = this.devour_follow_string( c );
                    ret[0] = this.reinterpret_include_path_from_context_paths( token );
                    return true;
                }
                else {
                    return false;
                }
            }
            case 'e':{
                if( szToken.equals( "extends" ) ) {
                    c = this.next();
                    token = this.devour_follow_string( c );
                    Object parent = this.reinterpret_eval_object( token );
                    Object dyThisScope = this.getScopeContext().thisScope();

                    if( !this.reinterpret_override_object_with_parent( dyThisScope, parent ) ) {
                        throw this.syntaxError( "Overridden object should be be K-V-Based object." );
                    }

                    throw new JSONParserRedirectException( 1 ); // Redirect key-parse sequence and skip.
                }
                else {
                    return false;
                }
            }
            case 'r':{
                if( szToken.equals( "ref" ) ) {
                    c = this.next();
                    token = this.devour_follow_string( c );  // #ref T, T->STRING
                    ret[0] = this.reinterpret_eval_object( token );
                    return true;
                }
                else {
                    return false;
                }
            }
            default: {
                this.reinterpret_eval_token( token );
                ret[0] = token;
                return false;
            }
        }
    }


    protected StringBuilder devour_follow_string( char currentChar ) {
        switch ( currentChar ) {
            case '\'':
            case '\"':{
                return this.nextString( currentChar );
            }
        }

        StringBuilder sb;
        for ( sb = new StringBuilder(); currentChar >= ' ' && ",:]}/\\\"\'[{;=#& ".indexOf(currentChar) < 0; currentChar = this.next() ) {
            sb.append(currentChar);
        }

        if( currentChar != ' ' ) {
            this.back();
        }
        return sb;
    }

    @Override
    protected Object eval_next_string_token( StringBuilder sb, char currentChar ) {
        switch ( currentChar ) {
            case '#':{
                currentChar = this.next();
                sb = this.devour_follow_string( currentChar );

                Object[] ret = new Object[1];
                boolean bIsReferObject = this.select_reinterpret_token( sb, ret );

                currentChar = this.next(); // [xxx\'] <- at '\'', to get follow, before eval_next_string_token invoked this.back();
                if( bIsReferObject ){
                    return ret[0];
                }
                break;
            }
            case '&':{
                currentChar = this.next();
                sb = this.devour_follow_string( currentChar );
                return this.reinterpret_eval_object( sb );
            }
            default:{
                break;
            }
        }

        return super.eval_next_string_token( sb, currentChar );
    }

    protected void apply_inner_patriarch( Object parent, Object neo ) {
        if( this.getScopeContext().parent() == null && this.getScopeContext().root() == null ) {
            this.getScopeContext().setRoot  ( neo    );
        }

        this.getScopeContext().setThisScope ( neo    );
        this.getScopeContext().setParent    ( parent );
    }

    @Override
    public Object nextValue( Object indexKey, Object parent ) throws JSONParseException {
        char c = this.nextClean();
        switch(c) {
            case '"':
            case '\'': {
                return this.nextString(c).toString();
            }
            case '[': {
                this.back();
                Object lastThis = this.getScopeContext().thisScope();
                JSONArray p     = new JSONArraytron( );

                this.apply_inner_patriarch( parent, p );
                p.jsonDecode( this );
                this.getScopeContext().setThisScope( lastThis );
                return p;
            }
            case '{': {
                this.back();
                JSONObject p    = new JSONMaptron( );
                Object lastThis = this.getScopeContext().thisScope();

                this.apply_inner_patriarch( parent, p );
                p.jsonDecode( this );
                this.getScopeContext().setThisScope( lastThis );
                return p;
            }
            default: {
                StringBuilder sb = this.eval_next_string( c );
                return this.eval_next_string_token(sb, c);
            }
        }
    }

    @Override
    public void handleRedirectException( JSONParserRedirectException e ) {
        if( e.getContext() != null ) {
            Object[] context = (Object[])e.getContext();
            String key = (String) context[0];
            Object val = context[1];

            if( key != null ){
                throw this.syntaxError( "Macro function '#extends' can't be value." );
            }
        }
    }
}
