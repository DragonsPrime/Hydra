package com.pinecone.framework.util.json;

import com.pinecone.framework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public abstract class JSONUtils {
    public static void prospectNumberQualify(Object o) throws JSONException {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double)o).isInfinite() || ((Double)o).isNaN()) {
                    throw new JSONException("JSON does not allow non-finite numbers.");
                }
            }
            else if (o instanceof Float && (((Float)o).isInfinite() || ((Float)o).isNaN())) {
                throw new JSONException("JSON does not allow non-finite numbers.");
            }
        }
    }

    private static String numberLikeStringTransfer( String string ){
        if ( string.indexOf(46) > 0 && string.indexOf(101) < 0 && string.indexOf(69) < 0 ) {
            while(string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }

            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }

        return string;
    }

    public static String numberToString( Number number ) throws JSONException {
        if (number == null) {
            throw new JSONException("Null pointer");
        }
        else {
            JSONUtils.prospectNumberQualify(number);
            String string = number.toString();
            return JSONUtils.numberLikeStringTransfer( string );
        }
    }

    public static String doubleToString( double d ) {
        if (!Double.isInfinite(d) && !Double.isNaN(d)) {
            String string = Double.toString(d);
            return JSONUtils.numberLikeStringTransfer( string );
        }
        else {
            return "null";
        }
    }

    public static String valueToString( Object value ) throws JSONException {
        if ( value != null && !value.equals((Object)null) ) {
            if ( value instanceof JSONString ) {
                String object;
                try {
                    object = ((JSONString)value).toJSONString();
                }
                catch ( Exception e ) {
                    throw new JSONException(e);
                }

                return object;
            }
            else if ( value instanceof Number ) {
                return JSONUtils.numberToString((Number)value);
            }
            else if (!(value instanceof Boolean) && !(value instanceof JSONObject) && !(value instanceof JSONArray)) {
                if (value instanceof Map) {
                    return (new JSONMaptron((Map)value)).toString();
                }
                else if (value instanceof Collection) {
                    return (new JSONArraytron((Collection)value)).toString();
                }
                else {
                    return value.getClass().isArray() ? (new JSONArraytron(value)).toString() : StringUtils.jsonQuote(value.toString());
                }
            }
            else {
                return value.toString();
            }
        }
        else {
            return "null";
        }
    }

    public static String noneStartZeroInteger( String szNum ) {
        if( szNum.startsWith( "0" ) ) {
            int i;
            for ( i = 0; i < szNum.length(); i++ ) {
                if( i == szNum.length() - 1 && szNum.charAt(i) == '0' ){ // 0000001, 0nX
                    break;
                }
                if( szNum.charAt(i) != '0' ) {
                    break;
                }
            }
            return szNum.substring( i );
        }
        return szNum;
    }

    public static Object stringToValue( String string ) {
        if ( string.equals("") ) {
            return string;
        }
        else if (string.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        else if (string.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        else if ( string.equalsIgnoreCase("null") || string.equalsIgnoreCase("undefined") ) {
            return JSON.NULL;
        }
        else {
            char b = string.charAt(0);
            if ( b >= '0' && b <= '9' || b == '-' ) {
                try {
                    if ( string.indexOf( '.' ) <= -1 && string.indexOf( 'e' ) <= -1 && string.indexOf( 'E' ) <= -1 ) {
                        String szToken = JSONUtils.noneStartZeroInteger( string );
                        if( szToken.length() < 18 ) {
                            Long n = Long.parseLong( szToken );
                            if ( szToken.equals( n.toString() ) ) {
                                if ( n == (long)n.intValue() ) {
                                    return n.intValue();
                                }

                                return n;
                            }
                        }
                        else {
                            return new BigInteger( szToken );
                        }
                    }
                    else {
                        if( string.length() < 18 ) {
                            Double d = Double.valueOf( string );
                            if ( !d.isInfinite() && !d.isNaN() ) {
                                return d;
                            }
                        }
                        else {
                            return new BigDecimal( string );
                        }
                    }
                }
                catch ( Exception e ) {
                    //e.printStackTrace();
                }
            }

            return string;
        }
    }

    public static Object wrapValue( Object value, boolean bWrapBean ) {
        try {
            if ( value == null ) {
                return JSON.NULL;
            }
            else if (!(value instanceof JSONObject) && !(value instanceof JSONArray) && !JSON.NULL.equals(value) && !(value instanceof JSONString) && !(value instanceof Byte) && !(value instanceof Character) && !(value instanceof Short) && !(value instanceof Integer) && !(value instanceof Long) && !(value instanceof Boolean) && !(value instanceof Float) && !(value instanceof Double) && !(value instanceof String)) {
                if (value instanceof Collection) {
                    return new JSONArraytron((Collection)value);
                }
                else if ( value.getClass().isArray() ) {
                    return new JSONArraytron(value);
                }
                else if ( value instanceof Map ) {
                    return new JSONMaptron((Map)value);
                }
                else {
                    if( bWrapBean ) {
                        Package objectPackage = value.getClass().getPackage();
                        String objectPackageName = objectPackage != null ? objectPackage.getName() : "";
                        return !objectPackageName.startsWith("java.") && !objectPackageName.startsWith("javax.") && value.getClass().getClassLoader() != null ? new JSONMaptron(value) : value.toString();
                    }
                    else {
                        return null;
                    }
                }
            }
            else {
                return value;
            }
        }
        catch ( Exception e ) {
            return null;
        }
    }

    public static Object wrapValue( Object value ) {
        return JSONUtils.wrapValue( value, true );
    }

    public static String[] getOwnPropertyNames ( JSONObject that ) {
        int nSize = that.size();
        if ( nSize == 0 ) {
            return null;
        }
        else {
            Iterator iterator = that.keys();
            String[] names = new String[nSize];

            for( int i = 0; iterator.hasNext(); ++i ) {
                names[i] = (String)iterator.next();
            }

            return names;
        }
    }

    public static Object cloneElement ( Object that ) {
        if( that instanceof JSONArray ) {
            return   ( ( JSONArray ) that ).clone();
        }
        else if( that instanceof JSONObject ) {
            return   ( ( JSONObject ) that ).clone();
        }
        return that;
    }

    public static int asInt32Key( Object key ) {
        if( key instanceof Integer ) {
            return (int) key;
        }
        else if( key instanceof Float || key instanceof Double || key instanceof BigDecimal ) {
            throw new JSONException( "Array does not allow float as key." );
        }
        else if( key instanceof Number ) {
            return ((Number) key).intValue();
        }
        else if( key instanceof String ) {
            return Integer.parseInt( (String) key );
        }

        throw new JSONException( "Key of Array should be integer or integer-fmt-string." );
    }

    public static int toInt32Key( Object key ) {
        if( key instanceof Integer ) {
            return (int) key;
        }
        else if( key instanceof Number ) {
            return ((Number) key).intValue();
        }
        else if( key instanceof String ) {
            return Integer.parseInt( (String) key );
        }

        return Integer.parseInt( key.toString() );
    }

    public static String asStringKey( Object key ) {
        if( key instanceof String ) {
            return (String) key;
        }

        return key.toString();
    }
}
