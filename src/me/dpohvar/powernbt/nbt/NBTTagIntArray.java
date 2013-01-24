package me.dpohvar.powernbt.nbt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static me.dpohvar.powernbt.utils.StaticValues.classNBTTagIntArray;
import static me.dpohvar.powernbt.utils.VersionFix.getNew;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagIntArray extends NBTTagNumericArray {
    private static Class clazz = classNBTTagIntArray;
    private static Class[] classes = new Class[]{String.class, int[].class};
    private static Field fieldData;
    private static Method methodRead;
    private static Method methodWrite;
    private static Method methodClone;

    static {
        try {
            methodRead = clazz.getDeclaredMethod("load", java.io.DataInput.class);
            methodWrite = clazz.getDeclaredMethod("write", java.io.DataOutput.class);
            methodClone = clazz.getDeclaredMethod("clone");
            fieldData = clazz.getDeclaredField("data");
            methodRead.setAccessible(true);
            methodWrite.setAccessible(true);
            methodClone.setAccessible(true);
            fieldData.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        super(getNew(clazz, classes, s, b));
    }

    public NBTTagIntArray(boolean ignored, Object tag) {
        super(tag);
        if (!clazz.isInstance(tag)) throw new IllegalArgumentException();
    }

    public void write(java.io.DataOutput output) {
        try {
            methodWrite.invoke(handle, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read(java.io.DataInput input) {
        try {
            methodRead.invoke(handle, input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NBTTagIntArray clone() {
        try {
            Object h = methodClone.invoke(handle);
            return new NBTTagIntArray(true, h);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean equals(Object o) {
        if (o instanceof NBTBase) o = ((NBTBase) o).getHandle();
        return handle.equals(o);
    }

    public int hashCode() {
        return handle.hashCode();
    }

    public int[] get() {
        try {
            return (int[]) fieldData.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void set(int[] value) {
        try {
            fieldData.set(handle, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Integer get(int i) {
        int[] array = get();
        if (i >= array.length) return null;
        return array[i];
    }

    @Override
    public ArrayList<Number> asList() {
        ArrayList<Number> list = new ArrayList<Number>();
        for (int b : get()) list.add(b);
        return list;
    }

    @Override
    public void setList(List<Number> list) {
        int[] val = new int[list.size()];
        int t = 0;
        for(Number n:list) val[t++]=n.intValue();
        set(val);
    }

    public int size() {
        return get().length;
    }

    public void set(int i, Number value) {
        int[] array = get();
        List<Integer> list = new LinkedList<Integer>();
        for (int b : array) list.add(b);
        while (list.size() <= i) {
            list.add((int) 0);
        }
        list.set(i, value.intValue());
        int[] result = new int[list.size()];
        int t = 0;
        for (int b : list) result[t++] = b;
        set(result);
    }

    public boolean remove(int i) {
        int[] array = get();
        if (i < 0 || i >= array.length) return false;
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
        return true;
    }

    public void add(Number value) {
        int[] array = get();
        List<Integer> list = new LinkedList<Integer>();
        for (int b : array) list.add(b);
        list.add(value.intValue());
        int[] result = new int[list.size()];
        int t = 0;
        for (int b : list) result[t++] = b;
        set(result);
    }

    @Override
    public byte getTypeId() {
        return 11;
    }

}
