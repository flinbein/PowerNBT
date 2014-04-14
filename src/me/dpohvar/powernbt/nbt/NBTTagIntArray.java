package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagIntArray extends NBTTagNumericArray<Integer> {
    private static Class clazz = Reflections.getClass("{nms}.NBTTagIntArray", "net.minecraft.nbt.NBTTagIntArray");
    private static Constructor con = Reflections.getConstructorWithNoOrStringParam(clazz);
    private static Field fieldData = Reflections.getField(clazz,int[].class);

    public NBTTagIntArray() {
        this("", new int[0]);
    }

    public NBTTagIntArray(String s) {
        this(s, new int[0]);
    }

    public NBTTagIntArray(int[] b) {
        this("", b);
    }

    public NBTTagIntArray(String s, int[] b) {
        super(createHandle(con));
        set(b);
    }

    public NBTTagIntArray(boolean ignored, Object tag) {
        super(tag);
        if (!clazz.isInstance(tag)) throw new IllegalArgumentException();
    }

    public boolean equals(Object o) {
        if (o instanceof NBTBase) o = ((NBTBase) o).getHandle();
        return handle.equals(o);
    }

    public int hashCode() {
        return handle.hashCode();
    }

    public int[] get() {
        return (int[]) Reflections.getFieldValue(fieldData,handle);
    }

    @Override
    public void set(Object value) {
        if(value instanceof int[]){
            int[] src = (int[]) value;
            int[] des = new int[src.length];
            System.arraycopy(src,0,des,0,src.length);
            Reflections.setFieldValue(fieldData,handle,des);
        }
        if(value instanceof int[]){
            int[] src = (int[]) value;
            int[] des = new int[src.length];
            int i=0; for(int t:src) des[i++] = (int) t;
            Reflections.setFieldValue(fieldData,handle,des);
        }
        if (value instanceof Object[]) value = Arrays.asList((Object[])value);
        if(value instanceof Collection){
            Collection src = (Collection) value;
            int[] des = new int[src.size()];
            int i=0; for(Object t:src) {
                if(t instanceof NBTTagDatable) t = ((NBTTagDatable) t).get();
                if(t instanceof Number) des[i] = ((Number) t).intValue();
                else if(t instanceof String) des[i] = (int) Integer.parseInt((String) t);
                else throw new IllegalArgumentException();
                i++;
            }
            Reflections.setFieldValue(fieldData,handle,des);
        }
    }

    public void set(int[] value) {
        try {
            fieldData.set(handle, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public int size() {
        return get().length;
    }

    @Override
    public boolean contains(Object o) {
        return intArrToList(get()).contains(o);
    }

    public Integer get(int i) {
        int[] array = get();
        if (i >= array.length) return null;
        return array[i];
    }

    public Integer set(int i, Number value) {
        int res = get(i);
        int[] array = get();
        List<Integer> list = new LinkedList<Integer>();
        for (int b : array) list.add(b);
        while (list.size() <= i) {
            list.add(0);
        }
        list.set(i, value.intValue());
        int[] result = new int[list.size()];
        int t = 0;
        for (int b : list) result[t++] = b;
        set(result);
        update();
        return res;
    }

    public Integer remove(int i) {
        int res = get(i);
        int[] array = get();
        if (i < 0 || i >= array.length) return res;
        List<Integer> list = new LinkedList<Integer>();
        for (int b : array) list.add(b);
        while (list.size() <= i) {
            list.add((int) 0);
        }
        list.remove(i);
        int[] result = new int[list.size()];
        int t = 0;
        for (int b : list) result[t++] = b;
        set(result);
        return res;
    }

    @Override
    public int indexOf(Object o) {
        return intArrToList(get()).indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return intArrToList(get()).lastIndexOf(o);
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex) {
        int[] r = new int[toIndex-fromIndex];
        int t=0;
        int[] s = get();
        for(int i = fromIndex; i<toIndex;i++){
            r[t++] = s[i];
        }
        return new NBTTagIntArray(r);
    }

    @Override
    public boolean add(Number value) {
        int[] array = get();
        List<Integer> list = new LinkedList<Integer>();
        for (int b : array) list.add(b);
        list.add(value.intValue());
        int[] result = new int[list.size()];
        int t = 0;
        for (int b : list) result[t++] = b;
        set(result);
        return false;
    }

    @Override
    public boolean remove(Object o) {
        List<Integer> ints = intArrToList(get());
        boolean result = ints.remove(o);
        if(result){
            set(listToIntArr(ints));
            update();
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        List<Integer> ints = intArrToList(get());
        boolean result = ints.removeAll(c);
        if(result){
            set(listToIntArr(ints));
            update();
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<Integer> ints = intArrToList(get());
        boolean result = ints.retainAll(c);
        if(result){
            set(listToIntArr(ints));
            update();
        }
        return result;
    }

    @Override
    public void clear() {
        set(new int[0]);
        update();
    }

    @Override
    public String toString() {
        return Arrays.toString(get());
    }

    @Override
    public byte getTypeId() {
        return 11;
    }



    public static ArrayList<Integer> intArrToList(int[] in) {
        ArrayList<Integer> temp = new ArrayList<Integer>(in.length);
        for (int anIn : in) temp.add(anIn);
        return temp;
    }

    public static int[] listToIntArr(Collection<Integer> in) {
        int[] temp = new int[in.size()];
        int i=0; for (Integer anIn : in) temp[i++] = anIn;
        return temp;
    }

}
