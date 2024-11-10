package com.pinecone.framework.util;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.unit.ConcurrentReferenceHashMap;

import java.beans.IntrospectionException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ReflectionUtils {
    private static final String CGLIB_RENAMED_METHOD_PREFIX = "CGLIB$";
    private static final Map<Class<?>, Method[]> declaredMethodsCache = new ConcurrentReferenceHashMap(256);
    public static ReflectionUtils.FieldFilter COPYABLE_FIELDS = new ReflectionUtils.FieldFilter() {
        public boolean matches(Field field) {
            return !Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers());
        }
    };
    public static ReflectionUtils.MethodFilter NON_BRIDGED_METHODS = new ReflectionUtils.MethodFilter() {
        public boolean matches(Method method) {
            return !method.isBridge();
        }
    };
    public static ReflectionUtils.MethodFilter USER_DECLARED_METHODS = new ReflectionUtils.MethodFilter() {
        public boolean matches(Method method) {
            return !method.isBridge() && method.getDeclaringClass() != Object.class;
        }
    };

    public ReflectionUtils() {
    }

    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, (Class)null);
    }

    public static Field findField(Class<?> clazz, String name, Class<?> type) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.isTrue(name != null || type != null, "Either name or type of the field must be specified");

        for(Class searchType = clazz; !Object.class.equals(searchType) && searchType != null; searchType = searchType.getSuperclass()) {
            Field[] fields = searchType.getDeclaredFields();
            Field[] t = fields;
            int len = fields.length;

            for(int i = 0; i < len; ++i) {
                Field field = t[i];
                if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
                    return field;
                }
            }
        }

        return null;
    }

    public static void setField(Field field, Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            handleReflectionException(e);
            throw new IllegalStateException("Unexpected reflection exception - " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static Object getField(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            handleReflectionException(e);
            throw new IllegalStateException("Unexpected reflection exception - " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static Method findMethod(Class<?> clazz, String name) {
        return findMethod(clazz, name);
    }

    public static Method findMethod(Class<?> clazz, String name, Class... paramTypes) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(name, "Method name must not be null");

        for(Class searchType = clazz; searchType != null; searchType = searchType.getSuperclass()) {
            Method[] methods = searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType);
            Method[] t = methods;
            int len = methods.length;

            for( int i = 0; i < len; ++i ) {
                Method method = t[i];
                if (name.equals(method.getName()) && (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                    return method;
                }
            }
        }

        return null;
    }

    public static Object invokeMethod(Method method, Object target) {
        return invokeMethod(method, target);
    }

    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception e) {
            handleReflectionException(e);
            throw new IllegalStateException("Should never get here");
        }
    }

    public static Object invokeJdbcMethod(Method method, Object target) throws SQLException {
        return invokeJdbcMethod(method, target);
    }

    public static Object invokeJdbcMethod(Method method, Object target, Object... args) throws SQLException {
        try {
            return method.invoke(target, args);
        }
        catch (IllegalAccessException e) {
            handleReflectionException(e);
        }
        catch (InvocationTargetException e1) {
            if (e1.getTargetException() instanceof SQLException) {
                throw (SQLException)e1.getTargetException();
            }

            handleInvocationTargetException(e1);
        }

        throw new IllegalStateException("Should never get here");
    }

    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        } else if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method: " + ex.getMessage());
        } else {
            if (ex instanceof InvocationTargetException) {
                handleInvocationTargetException((InvocationTargetException)ex);
            }

            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            } else {
                throw new UndeclaredThrowableException(ex);
            }
        }
    }

    public static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }

    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException)ex;
        } else if (ex instanceof Error) {
            throw (Error)ex;
        } else {
            throw new UndeclaredThrowableException(ex);
        }
    }

    public static void rethrowException(Throwable ex) throws Exception {
        if (ex instanceof Exception) {
            throw (Exception)ex;
        } else if (ex instanceof Error) {
            throw (Error)ex;
        } else {
            throw new UndeclaredThrowableException(ex);
        }
    }

    public static boolean declaresException(Method method, Class<?> exceptionType) {
        Assert.notNull(method, "Method must not be null");
        Class<?>[] declaredExceptions = method.getExceptionTypes();
        Class[] t = declaredExceptions;
        int length = declaredExceptions.length;

        for( int i = 0; i < length; ++i ) {
            Class<?> declaredException = t[i];
            if (declaredException.isAssignableFrom(exceptionType)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    public static boolean isEqualsMethod(Method method) {
        if (method != null && method.getName().equals("equals")) {
            Class<?>[] paramTypes = method.getParameterTypes();
            return paramTypes.length == 1 && paramTypes[0] == Object.class;
        } else {
            return false;
        }
    }

    public static boolean isHashCodeMethod(Method method) {
        return method != null && method.getName().equals("hashCode") && method.getParameterTypes().length == 0;
    }

    public static boolean isToStringMethod(Method method) {
        return method != null && method.getName().equals("toString") && method.getParameterTypes().length == 0;
    }

    public static boolean isObjectMethod(Method method) {
        if (method == null) {
            return false;
        }
        else {
            try {
                Object.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
                return true;
            }
            catch ( Exception e ) {
                return false;
            }
        }
    }

    public static boolean isCglibRenamedMethod(Method renamedMethod) {
        String name = renamedMethod.getName();
        if (!name.startsWith("CGLIB$")) {
            return false;
        } else {
            int i;
            for(i = name.length() - 1; i >= 0 && Character.isDigit(name.charAt(i)); --i) {
            }

            return i > "CGLIB$".length() && i < name.length() - 1 && name.charAt(i) == '$';
        }
    }

    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }

    }

    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }

    }

    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }

    }

    public static <T> Constructor<T> accessibleConstructor(Class<T> clazz, Class... parameterTypes) throws NoSuchMethodException {
        Constructor<T> ctor = clazz.getDeclaredConstructor(parameterTypes);
        ReflectionUtils.makeAccessible(ctor);
        return ctor;
    }

    public static void doWithMethods(Class<?> clazz, ReflectionUtils.MethodCallback mc) throws IllegalArgumentException {
        doWithMethods(clazz, mc, (ReflectionUtils.MethodFilter)null);
    }

    public static void doWithMethods(Class<?> clazz, ReflectionUtils.MethodCallback mc, ReflectionUtils.MethodFilter mf) throws IllegalArgumentException {
        Method[] methods = getDeclaredMethods(clazz);
        Method[] t = methods;
        int length = methods.length;

        int i;
        for( i = 0; i < length; ++i ) {
            Method method = t[i];
            if (mf == null || mf.matches(method)) {
                try {
                    mc.doWith(method);
                }
                catch (IllegalAccessException e) {
                    throw new IllegalStateException("Shouldn't be illegal to access method '" + method.getName() + "': " + e);
                }
            }
        }

        if (clazz.getSuperclass() != null) {
            doWithMethods(clazz.getSuperclass(), mc, mf);
        } else if (clazz.isInterface()) {
            Class[] interfaces = clazz.getInterfaces();
            length = interfaces.length;

            for( i = 0; i < length; ++i ) {
                Class<?> superIfc = interfaces[i];
                doWithMethods(superIfc, mc, mf);
            }
        }

    }

    public static Method[] getAllDeclaredMethods(Class<?> leafClass) throws IllegalArgumentException {
        final List<Method> methods = new ArrayList(32);
        doWithMethods(leafClass, new ReflectionUtils.MethodCallback() {
            public void doWith(Method method) {
                methods.add(method);
            }
        });
        return (Method[])methods.toArray(new Method[methods.size()]);
    }

    public static Method[] getUniqueDeclaredMethods(Class<?> leafClass) throws IllegalArgumentException {
        final List<Method> methods = new ArrayList(32);
        doWithMethods(leafClass, new ReflectionUtils.MethodCallback() {
            public void doWith(Method method) {
                boolean knownSignature = false;
                Method methodBeingOverriddenWithCovariantReturnType = null;
                Iterator iter = methods.iterator();

                while(iter.hasNext()) {
                    Method existingMethod = (Method)iter.next();
                    if (method.getName().equals(existingMethod.getName()) && Arrays.equals(method.getParameterTypes(), existingMethod.getParameterTypes())) {
                        if (existingMethod.getReturnType() != method.getReturnType() && existingMethod.getReturnType().isAssignableFrom(method.getReturnType())) {
                            methodBeingOverriddenWithCovariantReturnType = existingMethod;
                            break;
                        }

                        knownSignature = true;
                        break;
                    }
                }

                if (methodBeingOverriddenWithCovariantReturnType != null) {
                    methods.remove(methodBeingOverriddenWithCovariantReturnType);
                }

                if (!knownSignature && !ReflectionUtils.isCglibRenamedMethod(method)) {
                    methods.add(method);
                }

            }
        });
        return (Method[])methods.toArray(new Method[methods.size()]);
    }

    private static Method[] getDeclaredMethods(Class<?> clazz) {
        Method[] result = (Method[])declaredMethodsCache.get(clazz);
        if (result == null) {
            result = clazz.getDeclaredMethods();
            declaredMethodsCache.put(clazz, result);
        }

        return result;
    }

    public static void doWithFields(Class<?> clazz, ReflectionUtils.FieldCallback fc) throws IllegalArgumentException {
        doWithFields(clazz, fc, (ReflectionUtils.FieldFilter)null);
    }

    public static void doWithFields(Class<?> clazz, ReflectionUtils.FieldCallback fc, ReflectionUtils.FieldFilter ff) throws IllegalArgumentException {
        Class targetClass = clazz;

        do {
            Field[] fields = targetClass.getDeclaredFields();
            Field[] t = fields;
            int len = fields.length;

            for(int i = 0; i < len; ++i) {
                Field field = t[i];
                if (ff == null || ff.matches(field)) {
                    try {
                        fc.doWith(field);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Shouldn't be illegal to access field '" + field.getName() + "': " + e);
                    }
                }
            }

            targetClass = targetClass.getSuperclass();
        } while(targetClass != null && targetClass != Object.class);

    }

    public static void shallowCopyFieldState(final Object src, final Object dest) throws IllegalArgumentException {
        if (src == null) {
            throw new IllegalArgumentException("Source for field copy cannot be null");
        } else if (dest == null) {
            throw new IllegalArgumentException("Destination for field copy cannot be null");
        } else if (!src.getClass().isAssignableFrom(dest.getClass())) {
            throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() + "] must be same or subclass as source class [" + src.getClass().getName() + "]");
        } else {
            doWithFields(src.getClass(), new ReflectionUtils.FieldCallback() {
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    ReflectionUtils.makeAccessible(field);
                    Object srcValue = field.get(src);
                    field.set(dest, srcValue);
                }
            }, COPYABLE_FIELDS);
        }
    }

    public interface FieldFilter {
        boolean matches( Field field );
    }

    public interface FieldCallback {
        void doWith( Field field ) throws IllegalArgumentException, IllegalAccessException;
    }

    public interface MethodFilter {
        boolean matches( Method method );
    }

    public interface MethodCallback {
        void doWith( Method method ) throws IllegalArgumentException, IllegalAccessException;
    }


    /**
     *   Version: New add in Pinecone Java Ver 20240628
     */
    public static Object tryAccessibleInvoke( Method method, Object obj, Object... args ) throws IllegalArgumentException, InvocationTargetException {
        try{
            method.setAccessible( true );
            return method.invoke( obj, args );
        }
        catch ( IllegalAccessException eae ) {
            throw new ProxyProvokeHandleException( eae );
        }
    }


    /**
     *   Version: New add in Pinecone Java Ver 20241006
     */
    public static Object beanGet( Object bean, String propertyKey ) {
        try{
            java.beans.PropertyDescriptor propertyDescriptor = new java.beans.PropertyDescriptor( propertyKey, bean.getClass() );
            Method readMethod = propertyDescriptor.getReadMethod();
            if ( readMethod != null ) {
                try{
                    readMethod.setAccessible( true );
                    return readMethod.invoke( bean );
                }
                catch ( InvocationTargetException | IllegalArgumentException | IllegalAccessException e ) {
                    return null;
                }
            }
        }
        catch ( IntrospectionException e ) {
            return null;
        }
        return null;
    }

    public static void beanSet( Object bean, String propertyKey, Object val ) throws IllegalArgumentException {
        try{
            java.beans.PropertyDescriptor propertyDescriptor = new java.beans.PropertyDescriptor( propertyKey, bean.getClass() );
            Method writeMethod = propertyDescriptor.getWriteMethod();
            if ( writeMethod != null ) {
                try{
                    writeMethod.setAccessible( true );
                    writeMethod.invoke( bean, val );
                }
                catch ( InvocationTargetException | IllegalArgumentException | IllegalAccessException e ) {
                    throw new IllegalArgumentException( e );
                }
            }
        }
        catch ( IntrospectionException e ) {
            throw new IllegalArgumentException( e );
        }
    }

}
