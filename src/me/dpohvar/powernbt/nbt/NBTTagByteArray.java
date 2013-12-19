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
public class NBTTagByteArray extends NBTTagNumericArray<Byte> {
    private static Class clazz = Reflections.getClass("{nms}.NBTTagByteArray","net.minecraft.nbt.NBTTagByteArray");
    private static Constructor con = Reflections.getConstructorByTypes(clazz,byte[].class);
    private static Field fieldData = Reflections.getField(clazz,byte[].class);

    public NBTTagByteArray() {
        this("", new byte[0]);
    }

    public NBTTagByteArray(String s) {
        this(s, new byte[0]);
    }

    public NBTTagByteArray(byte[] b) {
        this("", b);
    }

    public NBTTagByteArray(String s, byte[] b) {
        super(Reflections.create(con, b));
    }

    public NBTTagByteArray(boolean ignored, Object tag) {
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

    public byte[] get() {
        return (byte[]) Reflections.getFieldValue(fieldData,handle);
    }

    @Override
    public void set(Object value) {
        if(value instanceof byte[]){
            byte[] src = (byte[]) value;
            byte[] des = new byte[src.length];
            System.arraycopy(src,0,des,0,src.length);
            Reflections.setFieldValue(fieldData,handle,des);
        }
        if(value instanceof int[]){
            int[] src = (int[]) value;
            byte[] des = new byte[src.length];
            int i=0; for(int t:src) des[i++] = (byte) t;
            Reflections.setFieldValue(fieldData,handle,des);
        }
        if (value instanceof Object[]) value = Arrays.asList((Object[])value);
        if(value instanceof Collection){
            Collection src = (Collection) value;
            byte[] des = new byte[src.size()];
            int i=0; for(Object t:src) {
                if(t instanceof NBTTagDatable) t = ((NBTTagDatable) t).get();
                if(t instanceof Number) des[i] = ((Number) t).byteValue();
                else if(t instanceof String) des[i] = (byte) Integer.parseInt((String) t);
                else throw new IllegalArgumentException();
                i++;
            }
            Reflections.setFieldValue(fieldData,handle,des);
        }
    }

    public void set(byte[] value) {
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
        return byteArrToList(get()).contains(o);
    }

    public Byte get(int i) {
        byte[] array = get();
        if (i >= array.length) return null;
        return array[i];
    }

    public Byte set(int i, Number value) {
        byte res = get(i);
        byte[] array = get();
        List<Byte> list = new LinkedList<Byte>();
        for (byte b : array) list.add(b);
        while (list.size() <= i) {
            list.add((byte) 0);
        }
        list.set(i, value.byteValue());
        byte[] result = new byte[list.size()];
        int t = 0;
        for (byte b : list) result[t++] = b;
        set(result);
        update();
        return res;
    }

    public Byte remove(int i) {
        byte res = get(i);
        byte[] array = get();
        if (i < 0 || i >= array.length) return res;
        List<Byte> list = new LinkedList<Byte>();
        for (byte b : array) list.add(b);
        while (list.size() <= i) {
            list.add((byte) 0);
        }
        list.remove(i);
        byte[] result = new byte[list.size()];
        int t = 0;
        for (byte b : list) result[t++] = b;
        set(result);
        return res;
    }

    @Override
    public int indexOf(Object o) {
        return byteArrToList(get()).indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return byteArrToList(get()).lastIndexOf(o);
    }

    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        byte[] r = new byte[toIndex-fromIndex];
        int t=0;
        byte[] s = get();
        for(int i = fromIndex; i<toIndex;i++){
            r[t++] = s[i];
        }
        return new NBTTagByteArray(r);
    }

    @Override
    public boolean add(Number value) {
        byte[] array = get();
        List<Byte> list = new LinkedList<Byte>();
        for (byte b : array) list.add(b);
        list.add(value.byteValue());
        byte[] result = new byte[list.size()];
        int t = 0;
        for (byte b : list) result[t++] = b;
        set(result);
        return false;
    }

    @Override
    public boolean remove(Object o) {
        List<Byte> bytes = byteArrToList(get());
        boolean result = bytes.remove(o);
        if(result){
            set(listToByteArr(bytes));
            update();
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        List<Byte> bytes = byteArrToList(get());
        boolean result = bytes.removeAll(c);
        if(result){
            set(listToByteArr(bytes));
            update();
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<Byte> bytes = byteArrToList(get());
        boolean result = bytes.retainAll(c);
        if(result){
            set(listToByteArr(bytes));
            update();
        }
        return result;
    }

    @Override
    public void clear() {
        set(new byte[0]);
        update();
    }

    @Override
    public String toString() {
        return Arrays.toString(get());
    }

    @Override
    public byte getTypeId() {
        return 7;
    }



    public static ArrayList<Byte> byteArrToList(byte[] in) {
        ArrayList<Byte> temp = new ArrayList<Byte>(in.length);
        for (byte anIn : in) temp.add(anIn);
        return temp;
    }

    public static byte[] listToByteArr(Collection<Byte> in) {
        byte[] temp = new byte[in.size()];
        int i=0; for (Byte anIn : in) temp[i++] = anIn;
        return temp;
    }

}
