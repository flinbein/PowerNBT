package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.StaticValues;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagLong extends NBTTagNumeric {
    private static Class clazz = StaticValues.getClass("NBTTagLong");
    private static Field fieldData;

    static {
        try {
            fieldData = StaticValues.getFieldByType(clazz, long.class);
            fieldData.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NBTTagLong() {
        this("", (long) 0);
    }

    public NBTTagLong(String s) {
        this(s, (long) 0);
    }

    public NBTTagLong(long b) {
        this("", b);
    }

    public NBTTagLong(String s, long b) {
        super(getNew( s, b));
    }

    private static Object getNew(String s, long b) {
        try{
            return clazz.getConstructor(String.class,long.class).newInstance(s,b);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public NBTTagLong(boolean ignored, Object tag) {
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

    public Long get() {
        try {
            return (Long) fieldData.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void set(Number value) {
        try {
            fieldData.set(handle, value.longValue());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte getTypeId() {
        return 4;
    }

}
