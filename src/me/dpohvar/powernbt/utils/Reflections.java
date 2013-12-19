package me.dpohvar.powernbt.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: DPOH-VAR
 * Date: 12.09.13
 * Time: 15:12
 */
public class Reflections {

    private static String preClassB = "org.bukkit.craftbukkit";
    private static String preClassM = "net.minecraft.server";
    private static boolean forge = false;

    static {
        if(Bukkit.getServer()!=null){
            if(Bukkit.getVersion().contains("MCPC")||Bukkit.getVersion().contains("Forge")) forge = true;
            Server server = Bukkit.getServer();
            Class<?> bukkitServerClass = server.getClass();
            String[] pas = bukkitServerClass.getName().split("\\.");
            if (pas.length == 5) {
                String verB = pas[3];
                preClassB += "."+verB;
            }
            try {
                Method getHandle = bukkitServerClass.getDeclaredMethod("getHandle");
                Object handle = getHandle.invoke(server);
                Class handleServerClass = handle.getClass();
                pas = handleServerClass.getName().split("\\.");
                if (pas.length == 5) {
                    String verM = pas[3];
                    preClassM += "."+verM;
                }
            } catch (Exception ignored) {
            }
        } else {
            preClassB = "org.bukkit.craftbukkit.v1_6_R2";
            preClassM = "net.minecraft.server.v1_6_R2";
        }
    }

    /**
     * @return true if server has forge classes
     */
    public static boolean isForge(){
        return forge;
    }

    /**
     * Get class for name.
     * Replace {nms} to net.minecraft.server.V*.
     * Replace {cb} to org.bukkit.craftbukkit.V*.
     * @param bukkitClass class path if server is bukkit
     * @param forgeClass class path if server is forge or mcpc
     * @return class
     */
    public static Class getClass(String bukkitClass,String forgeClass){
        try {
            if(forge){
                if((forgeClass)==null) return null;
                forgeClass = forgeClass.replace("{cb}",preClassB);
                forgeClass = forgeClass.replace("{nms}",preClassM);
                return Class.forName(forgeClass);
            } else{
                if((bukkitClass)==null) return null;
                bukkitClass = bukkitClass.replace("{cb}",preClassB);
                bukkitClass = bukkitClass.replace("{nms}",preClassM);
                return Class.forName(bukkitClass);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class not found",e);
        }
    }

    /**
     * Get class for name.
     * Replace {nms} to net.minecraft.server.V*.
     * Replace {cb} to org.bukkit.craftbukkit.V*.
     * @param bukkitClass class name
     * @return class
     */
    public static Class getClass(String bukkitClass){
        try {
            bukkitClass = bukkitClass.replace("{cb}",preClassB);
            bukkitClass = bukkitClass.replace("{nms}",preClassM);
            return Class.forName(bukkitClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class not found",e);
        }
    }

    /**
     * get first field of sourceClass by type
     * set accessible = true;
     * @param sourceClass source class
     * @param fieldClass type of field
     * @return field
     * @throws RuntimeException if field not found
     */
    public static Field getField(Class sourceClass,Class fieldClass){
        Field field = null;
        for(Field f:sourceClass.getFields()){
            if(f.getType().equals(fieldClass)){
                field=f;
                break;
            }
        }
        if(field==null) for(Field f:sourceClass.getDeclaredFields()){
            if(f.getType().equals(fieldClass)){
                field=f;
                break;
            }
        }
        if(field == null) throw new RuntimeException(
                "no field of type "+fieldClass.getName()+ " in class " +sourceClass.getName()
        );
        field.setAccessible(true);
        return field;
    }

    /**
     * try to get field by name
     * if field not found, try to find field by suffix
     * @param sourceClass class
     * @param name name of field or suffix
     * @return field
     * @throws RuntimeException if field not found
     */
    public static Field getField(Class sourceClass,String name){
        Field field = null;
        try{
            field = sourceClass.getField(name);
        } catch (NoSuchFieldException e) {
            for(Field f:sourceClass.getFields()){
                if(f.getName().endsWith(name)){
                    field = f;
                    break;
                }
            }
            if(field == null) for(Field f:sourceClass.getDeclaredFields()){
                if(f.getName().endsWith(name)){
                    field = f;
                    break;
                }
            }
        }
        if(field == null) throw new RuntimeException(
                "no field \""+field+ "\" in class" +sourceClass.getName()
        );
        field.setAccessible(true);
        return field;

    }

    /**
     * get field value of object o
     * @param o object
     * @param fieldClass type of field
     * @return value of field
     * @throws RuntimeException of an error
     */
    public static Object getFieldValue(Object o,Class fieldClass){
        try {
            return getField(o.getClass(),fieldClass).get(o);
        } catch (Throwable e) {
            throw new RuntimeException("can't get field",e);
        }
    }

    /**
     * Get value of field
     * @param field field
     * @param o object
     * @return value
     */
    public static <T> T getFieldValue(Field field,Object o){
        try {
            return (T) field.get(o);
        } catch (Throwable e) {
            throw new RuntimeException("can't get field",e);
        }
    }

    /**
     * get field value of object o
     * @param o object
     * @param fieldClass type of field
     * @param val value of field
     * @throws RuntimeException of an error
     */
    public static void setFieldValue(Object o,Class fieldClass,Object val){
        try {
            getField(o.getClass(),fieldClass).set(o,val);
        } catch (Throwable e) {
            throw new RuntimeException("field not found",e);
        }
    }

    /**
     * get field value of object o
     * @param field field
     * @param o object
     * @param val value of field
     * @throws RuntimeException of an error
     */
    public static void setFieldValue(Field field,Object o,Object val){
        try {
            field.set(o, val);
        } catch (Throwable e) {
            throw new RuntimeException("field not found",e);
        }
    }

    /**
     * get first method of sourceClass by return type
     * @param sourceClass source class
     * @param returnClass return type
     * @return method
     * @throws RuntimeException if method not found
     */
    public static Method getMethod(Class sourceClass,Class returnClass){
        if (returnClass == null) returnClass = void.class;
        Method method = null;
        for(Method m:sourceClass.getMethods()){
            if(m.getReturnType().equals(returnClass)){
                method=m;
                break;
            }
        }
        if(method==null) for(Method m:sourceClass.getDeclaredMethods()){
            if(m.getReturnType().equals(returnClass)){
                method=m;
                break;
            }
        }
        if(method == null) throw new RuntimeException(
                "no method with return type "+returnClass.getName()+ " in class" +sourceClass.getName()
        );
        method.setAccessible(true);
        return method;
    }

    /**
     * get first method of sourceClass by return type and arguments types
     * @param sourceClass source class
     * @param returnClass return type
     * @param argumentsTypes arguments types
     * @return method
     * @throws RuntimeException if method not found
     */
    public static Method getMethodByTypes(Class sourceClass,Class returnClass, Class... argumentsTypes){
        if (returnClass == null) returnClass = void.class;
        Method method = null;
        List<Method> methods = new ArrayList<Method>();
        methods.addAll(Arrays.asList(sourceClass.getMethods()));
        methods.addAll(Arrays.asList(sourceClass.getDeclaredMethods()));
        check: for(Method m:methods){
            if(!m.getReturnType().equals(returnClass)) continue;
            Class[] params = m.getParameterTypes();
            if(params.length != argumentsTypes.length) continue;
            int i=0;
            for(Class c:argumentsTypes) {
                if (!c.equals(params[i++])) continue check;
            }
            method = m;
            break;
        }
        if(method == null) throw new RuntimeException(
                "no method with return type "+returnClass.getName()+ " in class" +sourceClass.getName()
        );
        method.setAccessible(true);
        return method;
    }

    /**
     * get first constructor of sourceClass
     * @param sourceClass source class
     * @return constructor
     * @throws RuntimeException if constructor not found
     */
    public static Constructor getConstructor(Class sourceClass){
        Constructor constructor = null;
        Constructor[] constructors = sourceClass.getConstructors();
        if(constructors!=null && constructors.length>0) {
            constructor = constructors[0];
        } else {
            constructors = sourceClass.getDeclaredConstructors();
            if(constructors!=null && constructors.length>0) {
                constructor = constructors[0];
            } else {
                constructor = sourceClass.getEnclosingConstructor();
            }
        }
        if(constructor == null) throw new RuntimeException(
                "no constructor of class" +sourceClass.getName()
        );
        constructor.setAccessible(true);
        return constructor;
    }

    /**
     * get first constructor of sourceClass and arguments types
     * @param sourceClass source class
     * @param argumentsTypes arguments types
     * @return constructor
     * @throws RuntimeException if constructor not found
     */
    public static Constructor getConstructorByTypes(Class sourceClass, Class... argumentsTypes){
        Constructor constructor = null;
        List<Constructor> constructors = new ArrayList<Constructor>();
        constructors.addAll(Arrays.asList(sourceClass.getConstructors()));
        constructors.addAll(Arrays.asList(sourceClass.getDeclaredConstructors()));
        constructors.add(sourceClass.getEnclosingConstructor());
        check: for(Constructor con:constructors){
            if(con==null) continue;
            Class[] params = con.getParameterTypes();
            if(params.length != argumentsTypes.length) continue;
            int i=0;
            for(Class c:argumentsTypes) {
                if (!c.equals(params[i++])) continue check;
            }
            constructor = con;
            break;
        }
        if(constructor == null) throw new RuntimeException(
                "no special constructor in class" +sourceClass.getName()
        );
        constructor.setAccessible(true);
        return constructor;
    }

    /**
     * Create new object with constructor
     * @param constructor constructor
     * @param args arguments
     * @return new object
     */
    public static Object create(Constructor constructor,Object... args){
        try{
            return constructor.newInstance(args);
        } catch (Throwable e) {
            throw new RuntimeException("constructor error",e);
        }
    }

    /**
     * Invoke method for object
     * @param method applied method
     * @param obj object
     * @param params method parameters
     * @return method result
     */
    public static Object invoke(Method method,Object obj,Object... params){
        try {
            return method.invoke(obj,params);
        } catch (Throwable e) {
            throw new RuntimeException("invoke error",e);
        }
    }

    public static <T> T clone(T object){
        Class c = object.getClass();
        try {
            Method clone = c.getDeclaredMethod("clone");
            return (T) clone.invoke(object);
        } catch (Exception e) {
            throw new RuntimeException("clone error",e);
        }
    }



}
