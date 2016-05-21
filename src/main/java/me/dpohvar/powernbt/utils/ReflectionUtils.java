package me.dpohvar.powernbt.utils;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;

/**
 * @author DPOH-VAR
 * @version 1.2
 */
@SuppressWarnings("UnusedDeclaration")
public class ReflectionUtils {

    /** boolean value, TRUE if server uses forge or MCPC+ */
    private static boolean forge = false;
    /** class loader, needed for MCPC+ */
    private static ClassLoader classLoader = Bukkit.getServer().getClass().getClassLoader();
    /** classLoader in class names */
    private static HashMap<String,String> replacements = new HashMap<String,String>();

    /** check server version and class names */
    static {
        replacements.put("cb","org.bukkit.craftbukkit");
        replacements.put("nm","net.minecraft");
        replacements.put("nms","net.minecraft.server");
        if(Bukkit.getServer()!=null) {
            String version = Bukkit.getVersion();
            if (version.contains("MCPC")) forge = true;
            else if (version.contains("Forge")) forge = true;
            else if (version.contains("Cauldron")) forge = true;
            else {
                try {
                    classLoader.loadClass("net.minecraft.nbt.NBTBase");
                    forge = true;
                } catch (ClassNotFoundException ignored) {}
            }
            Server server = Bukkit.getServer();
            Class<?> bukkitServerClass = server.getClass();
            String[] pas = bukkitServerClass.getName().split("\\.");
            if (pas.length == 5) {
                replacements.put("cb","org.bukkit.craftbukkit."+pas[3]);
            }
            try {
                Method getHandle = bukkitServerClass.getDeclaredMethod("getHandle");
                Object handle = getHandle.invoke(server);
                Class handleServerClass = handle.getClass();
                pas = handleServerClass.getName().split("\\.");
                if (pas.length == 5) {
                    replacements.put("nms","net.minecraft.server."+pas[3]);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void addReplacements(Map<String,String> r){
        replacements.putAll(r);
    }
    public static void addReplacement(String key, String value){
        replacements.put(key, value);
    }

    static Yaml yaml = new Yaml();
    public static void addReplacementsYaml(Reader reader){
        Object map = yaml.load(reader);
        if (map instanceof Map) {
            Set keys = ((Map) map).keySet();
            for(Object key: keys) {
                replacements.put(""+key, ""+((Map) map).get(key));
            }
        }
    }

    public static void addReplacementsYaml(InputStream is){
        InputStreamReader reader = new InputStreamReader(is);
        addReplacementsYaml(reader);
    }

    public static void addReplacementsYaml(File file){
        try {
            FileReader reader = new FileReader(file);
            addReplacementsYaml(reader);
        } catch (FileNotFoundException e) {
            Bukkit.getLogger().log(Level.WARNING, "file "+file+" not found", e);
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
     * Replace {nm} to net.minecraft
     * @param pattern possible class paths, split by ","
     * @return RefClass object
     * @throws RuntimeException if no class found
     */
    @SuppressWarnings("unchecked")
    public static RefClass getRefClass(String pattern){
        String[] vars;
        if (pattern.contains(" ")||pattern.contains(",")) {
            vars = pattern.split(" |,");
        } else {
            vars = new String[1];
            vars[0] = pattern;
        }
        for(String name: vars) if (!name.isEmpty()) try{
            Class clazz = classByName(name);
            if (clazz==null) return null;
            else return new RefClass(clazz);
        } catch (ClassNotFoundException ignored) {
        }
        throw new RuntimeException("no class found: "+pattern);
    }

    private static HashMap<String,Class> classPatterns = new HashMap<String, Class>(){{
        put("null",null);
        put("*",null);
        put("void",void.class);
        put("boolean",boolean.class);
        put("byte",byte.class);
        put("short",short.class);
        put("int",int.class);
        put("long",long.class);
        put("float",float.class);
        put("double",double.class);
        put("boolean[]",boolean[].class);
        put("byte[]",byte[].class);
        put("char[]",char[].class);
        put("short[]",short[].class);
        put("int[]",int[].class);
        put("long[]",long[].class);
        put("float[]",float[].class);
        put("double[]",double[].class);
    }};

    private static Class classByName(String pattern) throws ClassNotFoundException {
        if (classPatterns.containsKey(pattern)) return classPatterns.get(pattern);
        for(Map.Entry<String,String> e: replacements.entrySet()) {
            pattern = pattern.replace("{"+e.getKey()+"}", e.getValue());
        }
        if (pattern.endsWith("[]")) pattern = "[L"+pattern.substring(0,pattern.length()-2)+";";
        return classLoader.loadClass(pattern);
    }

    /**
     * get RefClass object by real class
     * @param clazz class
     * @param <T> type of inner class
     * @return RefClass based on passed class
     */
    public static <T> RefClass<T> getRefClass(Class<T> clazz) {
        return new RefClass<T>(clazz);
    }

    /**
     * RefClass - utility to simplify work with reflections.
     * @param <T> type of inner class
     */
    public static class RefClass<T> {
        private final Class<T> clazz;

        /**
         * get passed class
         * @return class
         */
        public Class<T> getRealClass() {
            return clazz;
        }

        private RefClass(Class<T> clazz) {
            this.clazz = clazz;
        }

        /**
         * see {@link Class#isInstance(Object)}
         * @param object the object to check
         * @return true if object is an instance of this class
         */
        public boolean isInstance(Object object){
            return clazz.isInstance(object);
        }

        /**
         * get existing method by name and types
         * @param name name
         * @param types method parameters. can be Class or RefClass
         * @return RefMethod object
         * @throws RuntimeException if method not found
         */
        public RefMethod getMethod(String name, Object... types) {
            try {
                Class[] classes = new Class[types.length];
                int i=0; for (Object e: types) {
                    if (e instanceof Class) classes[i++] = (Class)e;
                    else if (e instanceof RefClass) classes[i++] = ((RefClass) e).getRealClass();
                    else if (e instanceof String) classes[i++] = getRefClass((String)e).getRealClass();
                    else classes[i++] = e.getClass();
                }
                try {
                    return new RefMethod(clazz.getMethod(name, classes));
                } catch (NoSuchMethodException ignored) {
                    return new RefMethod(clazz.getDeclaredMethod(name, classes));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * get existing constructor by types
         * @param types parameters. can be Class, RefClass or String
         * @return RefMethod object
         * @throws RuntimeException if constructor not found
         */
        public RefConstructor<T> getConstructor(Object... types) {
            try {
                Class[] classes = new Class[types.length];
                int i=0; for (Object e: types) {
                    if (e instanceof Class) classes[i++] = (Class)e;
                    else if (e instanceof RefClass) classes[i++] = ((RefClass) e).getRealClass();
                    else if (e instanceof String) classes[i++] = getRefClass((String)e).getRealClass();
                    else throw new IllegalArgumentException(e+" is not a Class or RefClass");
                }
                try {
                    return new RefConstructor<T>(clazz.getConstructor(classes));
                } catch (NoSuchMethodException ignored) {
                    return new RefConstructor<T>(clazz.getDeclaredConstructor(classes));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * find method by type parameters
         * @param types parameters. can be Class or RefClass
         * @return RefMethod object
         * @throws RuntimeException if method not found
         */
        public RefMethod findMethodByParams(Object... types) {
            Class[] classes = new Class[types.length];
            int t=0; for (Object e: types) {
                if (e instanceof Class) classes[t++] = (Class)e;
                else if (e instanceof RefClass) classes[t++] = ((RefClass) e).getRealClass();
                else if (e instanceof String) classes[t++] = getRefClass((String)e).getRealClass();
                else throw new IllegalArgumentException(e+" not a Class or RefClass");
            }
            List<Method> methods = new ArrayList<Method>();
            Collections.addAll(methods, clazz.getMethods());
            Collections.addAll(methods, clazz.getDeclaredMethods());
            findMethod: for (Method m: methods) {
                Class<?>[] methodTypes = m.getParameterTypes();
                if (methodTypes.length != classes.length) continue;
                for (int i=0; i<classes.length; i++) {
                    if (!classes[i].equals(methodTypes[i])) continue findMethod;
                }
                return new RefMethod(m);
            }
            throw new RuntimeException("no such method");
        }

        /**
         * find method by conditions
         * @param condition conditions to method
         * @return RefMethod object
         * @throws RuntimeException if method not found
         */
        public RefMethod findMethod(MethodCondition... condition) {
            for(MethodCondition c: condition) try{
                if (c == null) return null;
                return c.find(this);
            } catch (Exception ignored){
            }
            throw new RuntimeException("no such method");
        }

        /**
         * find method by name
         * @param pattern possible names of method, split by ","
         * @return RefMethod object
         * @throws RuntimeException if method not found
         */
        public RefMethod findMethodByName(String pattern) {
            String[] vars;
            if (pattern.contains(" ")||pattern.contains(",")) {
                vars = pattern.split(" |,");
            } else {
                vars = new String[1];
                vars[0] = pattern;
            }
            List<Method> methods = new ArrayList<Method>();
            Collections.addAll(methods, clazz.getMethods());
            Collections.addAll(methods, clazz.getDeclaredMethods());
            for (Method m: methods) {
                for (String name: vars) {
                    if (m.getName().equals(name)) {
                        return new RefMethod(m);
                    }
                }
            }
            throw new RuntimeException("no such method");
        }

        /**
         * find method by return value
         * @param types type of returned value
         * @throws RuntimeException if method not found
         * @return RefMethod
         */
        public <Z> RefMethod<Z> findMethodByReturnType(RefClass<Z> ...types) {
            Class<Z>[] classes = new Class[types.length];
            for (int i=0; i<types.length; i++) classes[i] = types[i].clazz;
            return findMethodByReturnType(classes);
        }

        /**
         * find method by return value
         * @param patterns type of returned value, see {@link #getRefClass(String)}
         * @throws RuntimeException if method not found
         * @return RefMethod
         */
        public RefMethod findMethodByReturnType(String ...patterns) {
            for (String pattern: patterns) try {
                return findMethodByReturnType(getRefClass(pattern));
            } catch (RuntimeException ignored) {}
            throw new RuntimeException("no such method");
        }

        /**
         * find method by return value
         * @param types type of returned value
         * @return RefMethod
         * @throws RuntimeException if method not found
         */
        @SuppressWarnings("unchecked")
        public <Z> RefMethod<Z> findMethodByReturnType(Class<Z> ...types) {
            for (Class<Z> type : types) {
                if (type==null) type = (Class<Z>) void.class;
                List<Method> methods = new ArrayList<Method>();
                Collections.addAll(methods, clazz.getMethods());
                Collections.addAll(methods, clazz.getDeclaredMethods());
                for (Method m: methods) {
                    if (type.equals(m.getReturnType())) {
                        return new RefMethod(m);
                    }
                }
            }
            throw new RuntimeException("no such method");

        }

        /**
         * find constructor by number of arguments
         * @param number number of arguments
         * @return RefConstructor
         * @throws RuntimeException if constructor not found
         */
        @SuppressWarnings("unchecked")
        public RefConstructor<T> findConstructor(int number) {
            List<Constructor> constructors = new ArrayList<Constructor>();
            Collections.addAll(constructors, clazz.getConstructors());
            Collections.addAll(constructors, clazz.getDeclaredConstructors());
            for (Constructor m: constructors) {
                if (m.getParameterTypes().length == number) return new RefConstructor(m);
            }
            throw new RuntimeException("no such constructor");
        }

        /**
         * get field by name
         * @param name field name
         * @return RefField
         * @throws RuntimeException if field not found
         */
        public RefField getField(String name) {
            try {
                try {
                    return new RefField(clazz.getField(name));
                } catch (NoSuchFieldException ignored) {
                    return new RefField(clazz.getDeclaredField(name));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * find field by type
         * @param type field type
         * @return RefField
         * @throws RuntimeException if field not found
         */
        public <P> RefField<P> findField(RefClass<P> type) {
            return findField(type.clazz);
        }

        /**
         * find field by type
         * @param pattern field type, see {@link #getRefClass(String)}
         * @return RefField
         * @throws RuntimeException if field not found
         */
        public RefField findField(String pattern) {
            return findField(getRefClass(pattern));
        }

        /**
         * find field by type
         * @param type field type
         * @return RefField
         * @throws RuntimeException if field not found
         */
        @SuppressWarnings("unchecked")
        public <P> RefField<P> findField(Class<P> type) {
            if (type==null) type = (Class<P>) void.class;
            List<Field> fields = new ArrayList<Field>();
            Collections.addAll(fields, clazz.getFields());
            Collections.addAll(fields, clazz.getDeclaredFields());
            for (Field f: fields) {
                if (type.equals(f.getType())) {
                    return new RefField(f);
                }
            }
            throw new RuntimeException("no such field");
        }
    }

    public static class MethodCondition implements Cloneable{
        private String name;
        private String prefix;
        private String suffix;
        private boolean checkForge;
        private boolean forge;
        private Class returnType;
        private List<Class> types;
        private int index = -1;
        private boolean checkAbstract = false;
        private boolean modAbstract;
        private boolean checkFinal = false;
        private boolean modFinal;
        private boolean checkStatic = false;
        private boolean modStatic;

        public MethodCondition withForge(boolean forge){
            this.checkForge = true;
            this.forge = forge;
            return this;
        }

        public MethodCondition withName(String name){
            this.name = name;
            return this;
        }

        public MethodCondition withPrefix(String prefix){
            this.prefix = prefix;
            return this;
        }

        public MethodCondition withSuffix(String suffix){
            this.suffix = suffix;
            return this;
        }

        public MethodCondition withReturnType(Class returnType){
            this.returnType = returnType;
            return this;
        }

        public MethodCondition withReturnType(String pattern){
            return withReturnType(getRefClass(pattern));
        }

        public MethodCondition withReturnType(RefClass returnType){
            this.returnType = returnType.getRealClass();
            return this;
        }

        public MethodCondition withTypes(Object... types){
            this.types = new ArrayList<Class>();
            for(Object type: types) {
                if (type instanceof Class) this.types.add((Class)type);
                else if (type instanceof RefClass) this.types.add(((RefClass)type).getRealClass());
                else if (type instanceof String) this.types.add(getRefClass((String)type).getRealClass());
                else throw new IllegalArgumentException(type+" is not a Class or RefClass");
            }
            return this;
        }

        public MethodCondition withAbstract(boolean modAbstract){
            this.checkAbstract = true;
            this.modAbstract = modAbstract;
            return this;
        }

        public MethodCondition withFinal(boolean modFinal){
            this.checkFinal = true;
            this.modFinal = modFinal;
            return this;
        }

        public MethodCondition withStatic(boolean modStatic){
            this.checkStatic = true;
            this.modStatic = modStatic;
            return this;
        }

        public MethodCondition withIndex(int index){
            this.index = index;
            return this;
        }

        private RefMethod find(RefClass clazz) {
            return find(clazz.getRealClass());
        }

        private RefMethod find(Class clazz) {
            List<Method> methods = new ArrayList<Method>();
            for(Method m: clazz.getMethods()) if (!methods.contains(m)) methods.add(m);
            for(Method m: clazz.getDeclaredMethods()) if (!methods.contains(m)) methods.add(m);

            if(checkForge) {
                if (isForge() != forge) throw new RuntimeException("Forge condition: "+forge);
            }
            if(name != null) {
                Iterator<Method> itr = methods.iterator();
                while(itr.hasNext()) if (!itr.next().getName().equals(name)) itr.remove();
            }
            if(prefix != null) {
                Iterator<Method> itr = methods.iterator();
                while(itr.hasNext()) if (!itr.next().getName().startsWith(prefix)) itr.remove();
            }
            if(suffix != null) {
                Iterator<Method> itr = methods.iterator();
                while(itr.hasNext()) if (!itr.next().getName().endsWith(suffix)) itr.remove();
            }
            if(returnType != null) {
                Iterator<Method> itr = methods.iterator();
                while(itr.hasNext()) if (!itr.next().getReturnType().equals(returnType)) itr.remove();
            }
            if (checkAbstract) {
                Iterator<Method> itr = methods.iterator();
                while(itr.hasNext()) if (Modifier.isAbstract(itr.next().getModifiers())!=modAbstract) itr.remove();
            }
            if (checkFinal) {
                Iterator<Method> itr = methods.iterator();
                while(itr.hasNext()) if (Modifier.isFinal(itr.next().getModifiers())!=modFinal) itr.remove();
            }
            if (checkStatic) {
                Iterator<Method> itr = methods.iterator();
                while(itr.hasNext()) if (Modifier.isStatic(itr.next().getModifiers())!=modStatic) itr.remove();
            }
            if(types != null) {
                Iterator<Method> itr = methods.iterator();
                itr: while(itr.hasNext()) {
                    Method method = itr.next();
                    Class[] classes = method.getParameterTypes();
                    if (classes.length != types.size()) {
                        itr.remove();
                        continue;
                    }
                    for (int i=0; i< classes.length; i++){
                        if ( ! classes[i].equals(types.get(i))) {
                            itr.remove();
                            continue itr;
                        }
                    }
                }
            }
            if (methods.size() == 0) {
                throw new RuntimeException("no such method");
            } else if (methods.size() == 1){
                return new RefMethod(methods.iterator().next());
            } else if (index < 0) {
                throw new RuntimeException("more than one method found: "+methods);
            } else if (index >= methods.size()) {
                throw new RuntimeException("No more methods: "+methods);
            } else {
                return new RefMethod(methods.get(index));
            }
        }
    }

    /**
     * Method wrapper
     */
    public static class RefMethod<Z> {
        private final Method method;
        private final int argumentsCount;

        /**
         * @return passed method
         */
        public Method getRealMethod(){
            return method;
        }
        /**
         * @return owner class of method
         */
        @SuppressWarnings("unchecked")
        public RefClass getRefClass(){
            return new RefClass(method.getDeclaringClass());
        }
        /**
         * @return class of method return type
         */
        @SuppressWarnings("unchecked")
        public RefClass<Z> getReturnRefClass(){
            return new RefClass(method.getReturnType());
        }
        public RefMethod (Method method) {
            this.method = method;
            this.argumentsCount = method.getParameterTypes().length;
            method.setAccessible(true);
        }
        public int getArgumentsCount(){
            return argumentsCount;
        }
        /**
         * apply method to object
         * @param e object to which the method is applied
         * @return RefExecutor with method call(params[])
         */
        public RefExecutor of(Object e) {
            return new RefExecutor(e);
        }

        /**
         * call static method
         * @param params sent parameters
         * @return return value
         */
        @SuppressWarnings("unchecked")
        public Z call(Object... params) {
            try{
                return (Z) method.invoke(null,params);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public class RefExecutor {
            Object e;
            public RefExecutor(Object e) {
                this.e = e;
            }

            /**
             * apply method for selected object
             * @param params sent parameters
             * @return return value
             * @throws RuntimeException if something went wrong
             */
            @SuppressWarnings("unchecked")
            public Z call(Object... params) {
                try{
                    return (Z) method.invoke(e,params);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Constructor wrapper
     */
    public static class RefConstructor<C> {
        private final Constructor<C> constructor;

        /**
         * @return passed constructor
         */
        public Constructor<C> getRealConstructor(){
            return constructor;
        }

        /**
         * @return owner class of method
         */
        public RefClass<C> getRefClass(){
            return new RefClass<C>(constructor.getDeclaringClass());
        }
        public RefConstructor (Constructor<C> constructor) {
            this.constructor = constructor;
            constructor.setAccessible(true);
        }

        /**
         * create new instance with constructor
         * @param params parameters for constructor
         * @return new object
         * @throws RuntimeException if something went wrong
         */
        public C create(Object... params) {
            try{
                return constructor.newInstance(params);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class RefField<T> {
        private Field field;

        /**
         * @return passed field
         */
        public Field getRealField(){
            return field;
        }

        /**
         * @return owner class of field
         */
        @SuppressWarnings("unchecked")
        public RefClass getRefClass(){
            return new RefClass(field.getDeclaringClass());
        }

        /**
         * @return type of field
         */
        @SuppressWarnings("unchecked")
        public RefClass<T> getFieldRefClass(){
            return new RefClass(field.getType());
        }
        public RefField (Field field) {
            this.field = field;
            field.setAccessible(true);
        }

        /**
         * apply fiend for object
         * @param e applied object
         * @return RefExecutor with getter and setter
         */
        public RefExecutor of(Object e) {
            return new RefExecutor(e);
        }
        public class RefExecutor {
            private Object e;
            public RefExecutor(Object e) {
                this.e = e;
            }

            /**
             * set field value for applied object
             * @param param value
             */
            public void set(T param) {
                try{
                    field.set(e,param);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            /**
             * get field value for applied object
             * @return value of field
             */
            @SuppressWarnings("unchecked")
            public T get() {
                try{
                    return (T) field.get(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
