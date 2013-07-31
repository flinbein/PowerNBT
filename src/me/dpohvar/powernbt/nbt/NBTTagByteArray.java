package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.StaticValues;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagByteArray extends NBTTagNumericArray {
    private static Class clazz = StaticValues.getClass("NBTTagByteArray");
    private static Field fieldData;

    static {
        try {
            fieldData = StaticValues.getFieldByType(clazz, byte[].class);
            fieldData.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        super(getNew(s, b));
    }

    private static Object getNew(String s, byte[] b) {
        try{
            return clazz.getConstructor(String.class,byte[].class).newInstance(s,b);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
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
        try {
            return (byte[]) fieldData.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void set(byte[] value) {
        try {
            fieldData.set(handle, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<Byte> asList() {
        ArrayList<Byte> list = new ArrayList<Byte>();
        for (byte b : get()) list.add(b);
        return list;
    }

    @Override
    public void setList(List<Number> list) {
        byte[] bytes = new byte[list.size()];
        int t = 0;
        for(Number n:list) bytes[t++]=n.byteValue();
        set(bytes);
    }

    public int size() {
        return get().length;
    }

    public Byte get(int i) {
        byte[] array = get();
        if (i >= array.length) return null;
        return array[i];
    }

    public void set(int i, Number value) {
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
    }

    public boolean remove(int i) {
        byte[] array = get();
        if (i < 0 || i >= array.length) return false;
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
        return true;
    }

    @Override
    public void add(Number value) {
        byte[] array = get();
        List<Byte> list = new LinkedList<Byte>();
        for (byte b : array) list.add(b);
        list.add(value.byteValue());
        byte[] result = new byte[list.size()];
        int t = 0;
        for (byte b : list) result[t++] = b;
        set(result);
    }

    @Override
    public byte getTypeId() {
        return 7;
    }

}
