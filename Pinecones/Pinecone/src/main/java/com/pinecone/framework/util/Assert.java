package com.pinecone.framework.util;

import java.util.Collection;
import java.util.Map;

public abstract class Assert {
    public Assert() {
    }

    public static void isTrue(boolean expression, String message) {
        if ( !expression ) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue( boolean expression ) {
        Assert.isTrue( expression, "[Assertion failed] - this expression must be true" );
    }

    public static void isNull( Object object, String message ) {
        if ( object != null ) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNull( Object object ) {
        Assert.isNull(object, "[Assertion failed] - the object argument must be null");
    }

    public static void notNull( Object object, String message ) {
        if ( object == null ) {
            throw new IllegalArgumentException( message );
        }
    }

    public static void notNull( Object object ) {
        Assert.notNull( object, "[Assertion failed] - this argument is required; it must not be null" );
    }

    public static void hasLength( String text, String message ) {
        if ( !StringUtils.hasLength(text) ) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void hasLength( String text ) {
        Assert.hasLength( text, "[Assertion failed] - this String argument must have length; it must not be null or empty" );
    }

    public static void hasText( String text, String message ) {
        if ( !StringUtils.hasText(text) ) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void hasText( String text ) {
        Assert.hasText(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
    }

    public static void doesNotContain( String textToSearch, String substring, String message ) {
        if ( StringUtils.hasLength(textToSearch) && StringUtils.hasLength(substring) && textToSearch.contains(substring) ) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void doesNotContain( String textToSearch, String substring ) {
        Assert.doesNotContain(textToSearch, substring, "[Assertion failed] - this String argument must not contain the substring [" + substring + "]");
    }

    public static void notEmpty( Object[] array, String message ) {
        if ( ObjectUtils.isEmpty(array) ) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty( Object[] array ) {
        Assert.notEmpty( array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element" );
    }

    public static void noNullElements( Object[] array, String message ) {
        if ( array != null ) {
            int len = array.length;

            for( int i = 0; i < len; ++i ) {
                Object element = array[i];
                if ( element == null ) {
                    throw new IllegalArgumentException(message);
                }
            }
        }

    }

    public static void noNullElements( Object[] array ) {
        Assert.noNullElements(array, "[Assertion failed] - this array must not contain any null elements");
    }

    public static void notEmpty( Collection<?> collection, String message ) {
        if ( CollectionUtils.isEmpty(collection) ) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty( Collection<?> collection ) {
        Assert.notEmpty(collection, "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
    }

    public static void notEmpty( Map<?, ?> map, String message ) {
        if ( CollectionUtils.isEmpty(map) ) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty( Map<?, ?> map ) {
        Assert.notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
    }

    public static void isInstanceOf( Class<?> clazz, Object obj ) {
        isInstanceOf(clazz, obj, "");
    }

    public static void isInstanceOf( Class<?> type, Object obj, String message ) {
        Assert.notNull(type, "Type to check against must not be null");
        if ( !type.isInstance(obj) ) {
            throw new IllegalArgumentException((StringUtils.hasLength(message) ? message + " " : "") + "Object of class [" + (obj != null ? obj.getClass().getName() : "null") + "] must be an instance of " + type);
        }
    }

    public static void isAssignable( Class<?> superType, Class<?> subType ) {
        Assert.isAssignable( superType, subType, "" );
    }

    public static void isAssignable( Class<?> superType, Class<?> subType, String message ) {
        Assert.notNull(superType, "Type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(message + subType + " is not assignable to " + superType);
        }
    }

    public static void state( boolean expression, String message ) {
        if ( !expression ) {
            throw new IllegalStateException(message);
        }
    }

    public static void state( boolean expression ) {
        Assert.state( expression, "[Assertion failed] - this state invariant must be true" );
    }

    public static void provokeIrrationally( Throwable bad ) {
        throw new IllegalArgumentException( bad );
    }
}
