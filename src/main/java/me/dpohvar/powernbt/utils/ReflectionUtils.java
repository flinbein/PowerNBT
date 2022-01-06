package me.dpohvar.powernbt.utils;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
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
    private static final ClassLoader classLoader = Bukkit.getServer().getClass().getClassLoader();
    /** classLoader in class names */
    private static final HashMap<String,String> replacements = new HashMap<>();

    /* check server version and class names */
    static {
        replacements.put("cb","org.bukkit.craftbukkit");
        replacements.put("nm","net.minecraft");
        replacements.put("nms","net.minecraft.server");
        String version = Bukkit.getVersion();
        if (version.contains("MCPC")) forge = true;
        else if (version.contains("Forge")) forge = true;
        else if (version.contains("Cauldron")) forge = true;
        Server server = Bukkit.getServer();
        Class<?> bukkitServerClass = server.getClass();
        String[] pas = bukkitServerClass.getName().split("\\.");
        if (pas.length == 5) {
            replacements.put("cb","org.bukkit.craftbukkit."+pas[3]);
        }
        try {
            Method getHandle = bukkitServerClass.getDeclaredMethod("getHandle");
            Object handle = getHandle.invoke(server);
            Class<?> handleServerClass = handle.getClass();
            pas = handleServerClass.getName().split("\\.");
            if (pas.length > 3 && pas[3].equals("dedicated")) {
                replacements.put("nms","net.minecraft.server");
            } else if (pas.length == 5) {
                replacements.put("nms","net.minecraft.server."+pas[3]);
            }
        } catch (Exception ignored) {
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
        Object yamlMap = yaml.load(reader);
        if (yamlMap instanceof Map map) {
            Set<?> keys = (map).keySet();
            for(Object key: keys) {
                replacements.put(""+key, ""+(map).get(key));
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
    @Contract(pure = true)
    public static boolean isForge(){
        return forge;
    }

    /**
     * Get class for name.
     * Replace {nms} to net.minecraft.server.V*.
     * Replace {cb} to org.bukkit.craftbukkit.V*.
     * Replace {nm} to net.minecraft
     * @param patterns possible class path
     * @return RefClass object
     * @throws RuntimeException if no class found
     */
    public static @NotNull RefClass<?> getRefClass(@NotNull String... patterns){
        for(String name: patterns) if (!name.isEmpty()) try{
            Class<?> clazz = classByName(name);
            if (clazz==null) throw new RuntimeException("no class found: "+Arrays.deepToString(patterns));
            else return new RefClass<>(clazz);
        } catch (ClassNotFoundException ignored) {
        }
        throw new RuntimeException("no class found: "+Arrays.deepToString(patterns));
    }

    private static final HashMap<String,Class<?>> classPatterns = new HashMap<>(){{
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
    }};

    private static Class<?> classByName(String pattern) throws ClassNotFoundException {
        if (pattern.endsWith("[]")) return classByName(pattern.substring(0, pattern.length() - 2)).arrayType();
        if (classPatterns.containsKey(pattern)) return classPatterns.get(pattern);
        for(Map.Entry<String,String> e: replacements.entrySet()) {
            pattern = pattern.replace("{"+e.getKey()+"}", e.getValue());
        }
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
     *
     * @param <T> type of inner class
     */
    public record RefClass<T>(Class<T> getRealClass) {

        /**
         * see {@link Class#isInstance(Object)}
         *
         * @param object the object to check
         * @return true if object is an instance of this class
         */
        public boolean isInstance(Object object) {
            return getRealClass.isInstance(object);
        }

        private static Class<?> [] getParamClasses(Object[] types) {
            Class<?>[] classes = new Class[types.length];
            int i = 0;
            for (Object e : types) classes[i++] = getParamClass(e);
            return classes;
        }


        @Contract("null -> null")
        private static Class<?> getParamClass(Object type) {
            if (type == null) return null;
            if (type instanceof RefClass rc) return rc.getRealClass();
            if (type instanceof String s) return getRefClass(s).getRealClass();
            if (type instanceof Class c) return c;
            throw new IllegalArgumentException(type + " is not a Class or RefClass");
        }

        /**
         * get existing method by name and types
         *
         * @param name  name
         * @param types method parameters. can be Class or RefClass
         * @return RefMethod object
         * @throws RuntimeException if method not found
         */
        public RefMethod<?> getMethod(String name, Object... types) {
            try {
                Class<?>[] classes = getParamClasses(types);
                try {
                    return new RefMethod<>(getRealClass.getMethod(name, classes));
                } catch (NoSuchMethodException ignored) {
                    return new RefMethod<>(getRealClass.getDeclaredMethod(name, classes));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * get existing constructor by types
         *
         * @param types parameters. can be Class, RefClass or String
         * @return RefMethod object
         * @throws RuntimeException if constructor not found
         */
        public RefConstructor<T> getConstructor(Object... types) {
            try {
                Class<?>[] classes = getParamClasses(types);
                try {
                    return new RefConstructor<>(getRealClass.getConstructor(classes));
                } catch (NoSuchMethodException ignored) {
                    return new RefConstructor<>(getRealClass.getDeclaredConstructor(classes));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * find method by type parameters
         * @param types parameters. can be Class or RefClass or String
         * @return RefMethod object
         * @throws RuntimeException if method not found
         */
        public RefMethod<?> findMethodByParams(Object... types) {
            return findMethodByNameAndParams(null, types);
        }

        /**
         * find method by type parameters
         *
         * @param name method name
         * @param types parameters. can be Class or RefClass or String
         * @return RefMethod object
         * @throws RuntimeException if method not found
         */
        public RefMethod<?> findMethodByNameAndParams(@Nullable final String name, final Object... types) {
            var methods = this.findMethodsByNameAndParams(null, types);
            if (methods.length == 0) {
                throw new RuntimeException("no such method");
            }
            return methods[0];
        }

        /**
         * find method by type parameters
         *
         * @param types parameters. can be Class or RefClass or String
         * @return array of RefMethods
         */
        public RefMethod<?>[] findMethodsByParams(Object... types) {
            return findMethodsByNameAndParams(null, types);
        }

        /**
         * find method by type parameters
         *
         * @param types parameters. can be Class or RefClass or String
         * @return array of RefMethods
         */
        public RefMethod<?>[] findMethodsByNameAndParams(@Nullable final String name, final Object... types) {
            final Class<?>[] classes = getParamClasses(types);
            List<Method> methods = new ArrayList<>();
            List<Method> resultMethods = new ArrayList<>();
            Collections.addAll(methods, getRealClass.getMethods());
            Collections.addAll(methods, getRealClass.getDeclaredMethods());
            return methods.stream().filter(m -> {
                Class<?>[] methodTypes = m.getParameterTypes();
                if (methodTypes.length != classes.length) return false;
                if (name != null && !m.getName().equals(name)) return false;
                for (int i = 0; i < classes.length; i++) {
                    if (!classes[i].equals(methodTypes[i])) return false;
                }
                return true;
            }).map(RefMethod::new).toArray(RefMethod[]::new);
        }

        /**
         * find method by type parameters
         *
         * @param types parameters. can be Class or RefClass or String
         * @return array of RefMethods
         */
        public RefMethod<?>[] findMethodsByNameTypeAndParams(@Nullable final String name, Object type, final Object... types) {
            final Class<?>[] classes = getParamClasses(types);
            List<Method> methods = new ArrayList<>();
            List<Method> resultMethods = new ArrayList<>();
            Collections.addAll(methods, getRealClass.getMethods());
            Collections.addAll(methods, getRealClass.getDeclaredMethods());
            Class<?> returnType = type == null ? null : getParamClass(type);
            return methods.stream().filter(m -> {
                Class<?>[] methodTypes = m.getParameterTypes();
                if (methodTypes.length != classes.length) return false;
                if (name != null && !m.getName().equals(name)) return false;
                if (returnType != null && !m.getReturnType().equals(returnType)) return false;
                for (int i = 0; i < classes.length; i++) {
                    if (!classes[i].equals(methodTypes[i])) return false;
                }
                return true;
            }).map(RefMethod::new).toArray(RefMethod[]::new);
        }

        /**
         * find method by conditions
         *
         * @param condition conditions to method
         * @return RefMethod object
         * @throws RuntimeException if method not found
         */
        public RefMethod<?> findMethod(MethodCondition... condition) {
            for (MethodCondition c : condition)
                try {
                    if (c == null) continue;
                    return c.find(this);
                } catch (Exception ignored) {
                }
            throw new RuntimeException("no such method: " + Arrays.toString(condition) + " for class " + this.getRealClass.getName());
        }
        /**
         * find method by conditions
         *
         * @param condition conditions to method
         * @return RefMethod object
         * @throws RuntimeException if method not found
         */
        public RefMethod<?>[] findMethods(@NotNull MethodCondition condition) {
            return condition.findAll(this.getRealClass());
        }

        /**
         * find method by name
         *
         * @param pattern possible names of method, split by ","
         * @return RefMethod object
         * @throws RuntimeException if method not found
         */
        public RefMethod<?> findMethodByName(String pattern) {
            String[] vars;
            if (pattern.contains(" ") || pattern.contains(",")) {
                vars = pattern.split("[ ,]");
            } else {
                vars = new String[1];
                vars[0] = pattern;
            }
            List<Method> methods = new ArrayList<>();
            Collections.addAll(methods, getRealClass.getMethods());
            Collections.addAll(methods, getRealClass.getDeclaredMethods());
            for (Method m : methods) {
                for (String name : vars) {
                    if (m.getName().equals(name)) {
                        return new RefMethod<>(m);
                    }
                }
            }
            throw new RuntimeException("no such method");
        }

        /**
         * find method by return value
         *
         * @param types type of returned value
         * @return RefMethod
         * @throws RuntimeException if method not found
         */
        @SafeVarargs
        public final <Z> RefMethod<Z> findMethodByReturnType(RefClass<Z>... types) {
            Class<Z>[] classes = new Class[types.length];
            for (int i = 0; i < types.length; i++) classes[i] = types[i].getRealClass;
            return findMethodByReturnType(classes);
        }

        /**
         * find method by return value
         *
         * @param patterns type of returned value, see {@link #getRefClass(String...)}
         * @return RefMethod
         * @throws RuntimeException if method not found
         */
        public RefMethod<?> findMethodByReturnType(String... patterns) {
            for (String pattern : patterns)
                try {
                    return findMethodByReturnType(getRefClass(pattern));
                } catch (RuntimeException ignored) {
                }
            throw new RuntimeException("no such method");
        }

        /**
         * find method by return value
         *
         * @param types type of returned value
         * @return RefMethod
         * @throws RuntimeException if method not found
         */
        @SuppressWarnings("unchecked")
        public <Z> RefMethod<Z> findMethodByReturnType(Class<Z>... types) {
            for (Class<Z> type : types) {
                if (type == null) type = (Class<Z>) void.class;
                List<Method> methods = new ArrayList<Method>();
                Collections.addAll(methods, getRealClass.getMethods());
                Collections.addAll(methods, getRealClass.getDeclaredMethods());
                for (Method m : methods) {
                    if (type.equals(m.getReturnType())) {
                        return new RefMethod<>(m);
                    }
                }
            }
            throw new RuntimeException("no such method");

        }

        /**
         * find constructor by number of arguments
         *
         * @param number number of arguments
         * @return RefConstructor
         * @throws RuntimeException if constructor not found
         */
        @SuppressWarnings("unchecked")
        public RefConstructor<T> findConstructor(int number) {
            List<Constructor<?>> constructors = new ArrayList<>();
            Collections.addAll(constructors, getRealClass.getConstructors());
            Collections.addAll(constructors, getRealClass.getDeclaredConstructors());
            for (Constructor m : constructors) {
                if (m.getParameterTypes().length == number) return new RefConstructor(m);
            }
            throw new RuntimeException("no such constructor");
        }

        /**
         * get field by name
         *
         * @param name field name
         * @return RefField
         * @throws RuntimeException if field not found
         */
        public RefField getField(String name) {
            try {
                try {
                    return new RefField(getRealClass.getField(name));
                } catch (NoSuchFieldException ignored) {
                    return new RefField(getRealClass.getDeclaredField(name));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * find field by type
         *
         * @param type field type
         * @return RefField
         * @throws RuntimeException if field not found
         */
        public <P> RefField<P> findField(RefClass<P> type) {
            return findField(type.getRealClass);
        }

        /**
         * find field by type
         *
         * @param pattern field type, see {@link #getRefClass(String...)}
         * @return RefField
         * @throws RuntimeException if field not found
         */
        public RefField<?> findField(String pattern) {
            return findField(getRefClass(pattern));
        }

        /**
         * find field by type
         *
         * @param type field type
         * @return RefField
         * @throws RuntimeException if field not found
         */
        @SuppressWarnings("unchecked")
        public <P> RefField<P> findField(Class<P> type) {
            if (type == null) type = (Class<P>) void.class;
            List<Field> fields = new ArrayList<>();
            Collections.addAll(fields, getRealClass.getFields());
            Collections.addAll(fields, getRealClass.getDeclaredFields());
            for (Field f : fields) {
                if (type.equals(f.getType())) {
                    return new RefField<>(f);
                }
            }
            throw new RuntimeException("no such field");
        }
    }

    public static class MethodCondition {
        private String[] names;
        private String prefix;
        private String suffix;
        private boolean checkForge;
        private boolean forge;
        private Class<?> returnType;
        private List<Class<?>> types;
        private int index = -1;
        private boolean checkAbstract = false;
        private boolean modAbstract;
        private boolean checkFinal = false;
        private boolean modFinal;
        private boolean checkStatic = false;
        private boolean modStatic;

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder("");
            if (names != null) b.append("names:").append(Arrays.toString(names)).append(",");
            if (prefix != null) b.append("prefix:").append(prefix).append(",");
            if (suffix != null) b.append("suffix:").append(suffix).append(",");
            if (checkForge) b.append("forge:").append(forge).append(",");
            if (types != null) b.append("types:").append(types).append(",");
            if (index != -1) b.append("index:").append(index).append(",");
            if (checkAbstract) b.append("abstract:").append(modAbstract).append(",");
            if (checkFinal) b.append("final:").append(modFinal).append(",");
            if (checkStatic) b.append("modStatic:").append(modStatic).append(",");
            return b.substring(0,b.length()-1);
        }

        public MethodCondition withForge(boolean forge){
            this.checkForge = true;
            this.forge = forge;
            return this;
        }

        public MethodCondition withName(String... names){
            this.names = names;
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

        public MethodCondition withReturnType(Class<?> returnType){
            this.returnType = returnType;
            return this;
        }

        public @NotNull MethodCondition withReturnType(@NotNull String pattern){
            RefClass<?> refClass = getRefClass(pattern);
            if (refClass == null) throw new RuntimeException("class not found: "+pattern);
            return withReturnType(refClass);
        }

        public @NotNull MethodCondition withReturnType(@NotNull RefClass<?> returnType){
            this.returnType = returnType.getRealClass();
            return this;
        }

        public MethodCondition withTypes(Object... types){
            this.types = new ArrayList<>();
            var classes = RefClass.getParamClasses(types);
            this.types.addAll(Arrays.asList(classes));
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

        private RefMethod<?> find(@NotNull RefClass<?> clazz) {
            return find(clazz.getRealClass());
        }

        private RefMethod<?> find(Class<?> clazz) {
            var refMethods = this.findAll(clazz);
            if (refMethods.length == 0) {
                throw new RuntimeException("no such method");
            } else if (refMethods.length == 1){
                return refMethods[0];
            } else if (index < 0) {
                throw new RuntimeException("more than one method found: "+ Arrays.toString(refMethods));
            } else if (index >= refMethods.length) {
                throw new RuntimeException("No more methods: "+Arrays.toString(refMethods));
            } else {
                return refMethods[index];
            }
        }

        private RefMethod<?>[] findAll(Class<?> clazz) {
            List<Method> methods = new ArrayList<>();
            for (Method m: clazz.getMethods()) if (!methods.contains(m)) methods.add(m);
            for (Method m: clazz.getDeclaredMethods()) if (!methods.contains(m)) methods.add(m);

            if (checkForge) {
                if (isForge() != forge) throw new RuntimeException("Forge condition: "+forge);
            }
            if (names != null) {
                Iterator<Method> itr = methods.iterator();

                while(itr.hasNext()) {
                    var name = itr.next().getName();
                    if (Arrays.stream(names).noneMatch(name::equals)) itr.remove();
                }
            }
            if (prefix != null) {
                methods.removeIf(method -> !method.getName().startsWith(prefix));
            }
            if (suffix != null) {
                methods.removeIf(method -> !method.getName().endsWith(suffix));
            }
            if (returnType != null) {
                methods.removeIf(method -> !method.getReturnType().equals(returnType));
            }
            if (checkAbstract) {
                methods.removeIf(method -> Modifier.isAbstract(method.getModifiers()) != modAbstract);
            }
            if (checkFinal) {
                methods.removeIf(method -> Modifier.isFinal(method.getModifiers()) != modFinal);
            }
            if (checkStatic) {
                methods.removeIf(method -> Modifier.isStatic(method.getModifiers()) != modStatic);
            }
            if (types != null) {
                Iterator<Method> itr = methods.iterator();
                itr: while(itr.hasNext()) {
                    Method method = itr.next();
                    Class<?>[] classes = method.getParameterTypes();
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
            return methods.stream().map(RefMethod::new).toArray(RefMethod[]::new);
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

        @Override
        public String toString() {
            var paramTypes = Arrays.stream(method.getParameterTypes()).map(Class::getName).toArray(String[]::new);
            return method.getDeclaringClass().getName() +
                    " " +
                    method.getName() +
                    "(" +
                    String.join(", ", paramTypes)
                    + ")";
        }

        public static @NotNull RefMethod<?> parse(@NotNull String string){
            var args = string.split("[|]+");
            Error lastError = null;
            for (String arg : args) {
                try {
                    return parse0(arg);
                } catch (Error error) {
                    lastError = error;
                }
            }
            throw new RuntimeException("parse RefMethod error: "+string, lastError);


        }

        // {cb}.entity.CraftEntity public !static final testName*(String, Number): net.minecraft.nbt.NBTTagCompound
        public static @NotNull RefMethod<?> parse0(@NotNull String string){
            var split1 = string.strip().split("\\s+", 2);
            RefClass<?> refClass = ReflectionUtils.getRefClass(split1[0]); // {cb}.entity.CraftEntity
            String methodDescription = split1[1]; // public !static final testName*(String, Number): net.minecraft.nbt.NBTTagCompound
            var split2 = methodDescription.strip().split("[:]+", 2);
            String methodAttrsAndNameAndArgs = split2[0]; // // public !static final testName*(String, Number) | public !static final testName*
            String methodReturnTypePattern = split2.length > 1 ? split2[1] : null; // net.minecraft.nbt.NBTTagCompound | null

            String[] split3 = methodAttrsAndNameAndArgs.split("[(]", 2);
            String methodAttrsAndNamePattern = split3[0].replaceAll("[:()]+",""); // public !static final testName*
            String methodParamsPattern = split3.length >= 2 ? split3[1].replaceAll("[:()]+","") : null; // String, Number) | null

            String name = split3[split3.length - 1]; // testName*
            String[] methodAttrs = Arrays.copyOfRange(split3, 0, split3.length - 1); // public !static final

            String[] methodParams = methodParamsPattern == null ? null : methodParamsPattern.replace(")", "").strip().split("[(), ]+");

            var condition = new MethodCondition();

            for (String methodAttr : methodAttrs) {
                condition = switch (methodAttr) {
                    case "abstract", "!abstract" -> condition.withAbstract(methodAttr.equals("abstract"));
                    case "final", "!final" -> condition.withFinal(methodAttr.equals("final"));
                    case "static", "!static" -> condition.withStatic(methodAttr.equals("static"));
                    default -> throw new RuntimeException("Wrong method attribute " + methodAttr + ": " + string);
                };
            }

            if (!name.equals("*")) {
                if (name.startsWith("*")) {
                    condition = condition.withPrefix(name.substring(1));
                } else if (name.endsWith("*")) {
                    condition = condition.withSuffix(name.substring(0, name.length() - 1));
                } else if (name.contains("*")) {
                    var index = name.indexOf("*");
                    condition = condition.withPrefix(name.substring(0, index-1));
                    condition = condition.withSuffix(name.substring(index));
                }
            }

            if (methodReturnTypePattern != null && !methodReturnTypePattern.isEmpty() && !methodReturnTypePattern.equals("*")){
                condition.withReturnType(methodReturnTypePattern);
            }

            if (methodParams != null) {
                condition = condition.withTypes((Object[]) methodParams);
            }

            return refClass.findMethod(condition);
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

        public static @NotNull RefConstructor<?> parse(@NotNull String string){
            var args = string.split("[(), ]+");
            String[] argTypes = Arrays.stream(args, 1, args.length).toArray(String[]::new);
            return ReflectionUtils.getRefClass(args[0]).getConstructor(/*...*/ (Object[]) argTypes);
        }
    }

    public static class RefField<T> {
        private final Field field;

        /**
         * @return passed field
         */
        public Field getRealField(){
            return field;
        }

        /**
         * @return owner class of field
         */
        public RefClass<?> getRefClass(){
            return new RefClass<>(field.getDeclaringClass());
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

        //
        public static @NotNull RefField<?> parse(@NotNull String string){
            var args = string.split("[ ]+",2);
            RefClass<?> refClass = ReflectionUtils.getRefClass(args[0]);
            var nameAndType = args[1];
            if (nameAndType.startsWith("*:")) {
                return refClass.findField(nameAndType.substring(2).strip());
            } else {
                return refClass.getField(nameAndType);
            }

        }
    }

}
