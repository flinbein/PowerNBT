package me.dpohvar.powernbt.api;

import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * PowerNBT API.<br>
 * This class has methods to read and write NBT tags.<br>
 * {@link NBTCompound} is used to work with NBTTagCompound,<br>
 * {@link NBTList} is used to work with NBTTagList,<br>
 * {@link String} is used to work with NBTTagString,<br>
 * all other NBT tags represents by primitive values.<br>
 * @since 0.7.1
 */

@SuppressWarnings("UnusedDeclaration")
public class NBTManagerUtils {

    public static Object[] convertToObjectArrayOrNull(Object someArray){
        if (someArray instanceof Object[] res) return res;
        if (someArray instanceof boolean[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof byte[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof short[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof int[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof long[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof float[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof double[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof char[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof Collection a) return a.toArray();
        return null;
    }

    public static Object convertToPrimitiveArrayOrNull(Object[] objArray){
        if (objArray instanceof Boolean[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Character[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Byte[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Short[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Integer[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Long[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Float[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Double[] a) return ArrayUtils.toPrimitive(a);
        return null;
    }

    public static Object convertToPrimitiveClassType(Class<?> clazz, Object value){
        if (clazz == Boolean.class || clazz == boolean.class) {
            if (value == null) return false;
            if (value instanceof Boolean b) return b;
            if (value instanceof Character b) return b != 0;
            if (value instanceof Number b) return b.doubleValue() != 0 && !Double.isNaN(b.doubleValue());
            if (value instanceof String b) return !b.isEmpty();
            if (value instanceof Object[] b) return b.length > 0;
            if (value instanceof Collection b) return b.size() > 0;
            if (value instanceof Map b) return b.size() > 0;
            Object[] objects = convertToObjectArrayOrNull(value);
            if (objects != null) return objects.length > 0;
            return true;
        } else if (clazz == Character.class || clazz == char.class) {
            if (value == null) return '\0';
            if (value instanceof Boolean b) return b ? '1' : '0';
            if (value instanceof Character b) return b;
            if (value instanceof Number b) return (char) b.intValue();
            if (value instanceof String s) return s.isEmpty() ? '\0' : s.charAt(0);
        } else if (clazz == Byte.class || clazz == byte.class) {
            if (value instanceof Boolean b) return b ? (byte) 1 : (byte) 0;
            if (value instanceof Character b) return (byte) (char) b;
            if (value instanceof Number b) return b.byteValue();
            if (value instanceof String s) return Byte.valueOf(s);
        } else if (clazz == Short.class || clazz == short.class) {
            if (value instanceof Boolean b) return b ? (short) 1 : (short) 0;
            if (value instanceof Character b) return (short) (char) b;
            if (value instanceof Number b) return b.shortValue();
            if (value instanceof String s) return Short.valueOf(s);
        } else if (clazz == Integer.class || clazz == int.class) {
            if (value instanceof Boolean b) return b ? 1 : 0;
            if (value instanceof Character b) return (int) (char) b;
            if (value instanceof Number b) return b.intValue();
            if (value instanceof String s) return Integer.valueOf(s);
        } else if (clazz == Long.class || clazz == long.class) {
            if (value instanceof Boolean b) return b ? (long) 1 : (long) 0;
            if (value instanceof Character b) return (long) (char) b;
            if (value instanceof Number b) return b.longValue();
            if (value instanceof String s) return Long.valueOf(s);
        } else if (clazz == Float.class || clazz == float.class) {
            if (value instanceof Boolean b) return b ? (float) 1 : (float) 0;
            if (value instanceof Character b) return (float) (char) b;
            if (value instanceof Number b) return b.floatValue();
            if (value instanceof String s) return Float.valueOf(s);
        } else if (clazz == Double.class || clazz == double.class) {
            if (value instanceof Boolean b) return b ? (double) 1 : (double) 0;
            if (value instanceof Character b) return (double) (char) b;
            if (value instanceof Number b) return b.doubleValue();
            if (value instanceof String s) return Double.valueOf(s);
        }
        return null;
    }

    public static Object modifyArray(Object array, Consumer<List<Object>> consumer){
        if (array == null) return null;
        Class<?> baseClass = array.getClass().getComponentType();
        if (baseClass == null) return null;
        Object[] objectArray = convertToObjectArrayOrNull(array);
        List<Object> list = new ArrayList<>(List.of(objectArray));
        consumer.accept(list);
        Object[] copyArray = (Object[]) Array.newInstance(objectArray.getClass().getComponentType(), list.size());
        if (baseClass.isPrimitive()) {
            Object[] resultArray = list.stream().map(v -> convertToPrimitiveClassType(baseClass, v)).toList().toArray(copyArray);
            return convertToPrimitiveArrayOrNull(resultArray);
        } else {
            return list.toArray(copyArray);
        }
    }

    public static Object mapArray(Object array, Function<List<?>, List<?>> function){
        if (array == null) return null;
        Class<?> baseClass = array.getClass().getComponentType();
        if (baseClass == null) return null;
        Object[] objectArray = convertToObjectArrayOrNull(array);
        List<?> list = new ArrayList<>(List.of(objectArray));
        List<?> resultList = function.apply(list);
        Object[] copyArray = (Object[]) Array.newInstance(objectArray.getClass().getComponentType(), resultList.size());
        if (baseClass.isPrimitive()) {
            Object[] resultArray = resultList.stream().map(v -> convertToPrimitiveClassType(baseClass, v)).toList().toArray(copyArray);
            return convertToPrimitiveArrayOrNull(resultArray);
        } else {
            return resultList.toArray(copyArray);
        }
    }

    public static Object convertValue(Object value, byte type) {
        return switch (type) {
            case 0 /*end*/ -> null;
            case 1 /*byte*/ -> {
                if (value instanceof Byte current) yield current;
                if (value == null) yield (byte) 0;
                if (value instanceof Boolean b) yield (byte) (b ? 1 : 0);
                if (value instanceof Number n) yield n.byteValue();
                if (value instanceof CharSequence) yield Byte.valueOf(value.toString());
                if (value instanceof Character n) yield (byte) (char) n;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 2 /*short*/ -> {
                if (value instanceof Short current) yield current;
                if (value == null) yield (short) 0;
                if (value instanceof Boolean b) yield (short) (b ? 1 : 0);
                if (value instanceof Number n) yield n.shortValue();
                if (value instanceof CharSequence) yield Short.valueOf(value.toString());
                if (value instanceof Character n) yield (short) (char) n;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 3 /*int*/ -> {
                if (value instanceof Integer current) yield current;
                if (value == null) yield (int) 0;
                if (value instanceof Boolean b) yield (int) (b ? 1 : 0);
                if (value instanceof Number n) yield n.intValue();
                if (value instanceof CharSequence) yield Integer.valueOf(value.toString());
                if (value instanceof Character n) yield (int) (char) n;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 4 /*long*/ -> {
                if (value instanceof Long current) yield current;
                if (value == null) yield (long) 0;
                if (value instanceof Boolean b) yield (long) (b ? 1 : 0);
                if (value instanceof Number n) yield n.longValue();
                if (value instanceof CharSequence) yield Long.valueOf(value.toString());
                if (value instanceof Character n) yield (long) (char) n;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 5 /*float*/ -> {
                if (value instanceof Float current) yield current;
                if (value == null) yield (float) 0;
                if (value instanceof Boolean b) yield (float) (b ? 1 : 0);
                if (value instanceof Number n) yield n.floatValue();
                if (value instanceof CharSequence) yield Float.valueOf(value.toString());
                if (value instanceof Character n) yield (float) (char) n;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 6 /*double*/ -> {
                if (value instanceof Double current) yield current;
                if (value == null) yield (double) 0;
                if (value instanceof Boolean b) yield (double) (b ? 1 : 0);
                if (value instanceof Number n) yield n.doubleValue();
                if (value instanceof CharSequence) yield Double.valueOf(value.toString());
                if (value instanceof Character n) yield (double) (char) n;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 7 /*byte[]*/ -> {
                if (value instanceof byte[] current) yield current;
                if (value == null) yield new byte[0];
                Object[] arr = convertToObjectArrayOrNull(value);
                if (arr != null) {
                    Byte[] array = Arrays.stream(arr).map(val -> (Byte) convertValue(val, (byte) 1)).toArray(Byte[]::new);
                    yield ArrayUtils.toPrimitive(array);
                }
                if (value instanceof CharSequence cs) yield cs.toString().getBytes(StandardCharsets.UTF_8);
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 8 /*String*/ -> {
                if (value instanceof String s) yield s;
                if (value instanceof CharSequence) yield value.toString();
                if (value instanceof char[] c) yield String.copyValueOf(c);
                if (value instanceof byte[] c) yield new String(c, StandardCharsets.UTF_8);
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 9 /*List*/ -> {
                if (value instanceof CharSequence s) yield Arrays.stream(s.toString().split("")).toList();
                if (value instanceof Collection a) yield a;
                Object[] arr = convertToObjectArrayOrNull(value);
                if (arr != null) yield Arrays.stream(arr).toList();
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 10 /*Compound*/ -> {
                if (value instanceof Map s) yield s;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 11 /*int[]*/ -> {
                if (value instanceof int[] current) yield current;
                if (value == null) yield new int[0];
                Object[] arr = convertToObjectArrayOrNull(value);
                if (arr != null) {
                    Integer[] array = Arrays.stream(arr).map(val -> (Integer) convertValue(val, (byte) 3)).toArray(Integer[]::new);
                    yield ArrayUtils.toPrimitive(array);
                }
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 12 /*long[]*/ -> {
                if (value instanceof long[] current) yield current;
                if (value == null) yield new long[0];
                Object[] arr = convertToObjectArrayOrNull(value);
                if (arr != null) {
                    Long[] array = Arrays.stream(arr).map(val -> (Long) convertValue(val, (byte) 4)).toArray(Long[]::new);
                    yield ArrayUtils.toPrimitive(array);
                }
                throw new RuntimeException("Wrong value of type "+type);
            }
            default -> throw new RuntimeException("unknown tag type:"+type);
        };
    }

    static boolean checkCrossReferences(LinkedList<Object> list, Collection<?> values){
        for (Object value : values) {
            if (list.contains(value)) return true;
            if (value instanceof Collection col) {
                list.push(value);
                if (checkCrossReferences(list, col)) return true;
                list.pop();
            } else if (value instanceof Map map) {
                list.push(value);
                if (checkCrossReferences(list, map.values())) return true;
                list.pop();
            } else if (value instanceof Object[]) {
                list.push(value);
                if (checkCrossReferences(list, Arrays.asList((Object[])value))) return true;
                list.pop();
            }
        }
        return false;
    }

    static boolean checkCrossReferences(Map<?,?> map){
        LinkedList<Object> list = new LinkedList<>();
        list.push(map);
        return checkCrossReferences(list, map.values());
    }

    static boolean checkCrossReferences(Collection<?> collection){
        LinkedList<Object> list = new LinkedList<>();
        list.push(collection);
        return checkCrossReferences(list, collection);
    }

    static boolean checkCrossReferences(Object[] collection){
        LinkedList<Object> list = new LinkedList<>();
        list.push(collection);
        return checkCrossReferences(list, Arrays.asList(collection));
    }

}
