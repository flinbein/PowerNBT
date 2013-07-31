package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.StaticValues;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagShort extends NBTTagNumeric {
    private static Class clazz = StaticValues.getClass("NBTTagShort");
    private static Field fieldData;

    static {
        try {
            fieldData = StaticValues.getFieldByType(clazz, short.class);
            fieldData.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NBTTagShort() {
        this("", (short) 0);
    }

    public NBTTagShort(String s) {
        this(s, (short) 0);
    }

    public NBTTagShort(short b) {
        this("", b);
    }

    public NBTTagShort(String s, short b) {
        super(getNew(s, b));
    }

    private static Object getNew(String s, short b) {
        try{
            return clazz.getConstructor(String.class,short.class).newInstance(s,b);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public NBTTagShort(boolean ignored, Object tag) {
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

    public Short get() {
        try {
            return (Short) fieldData.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void set(Number value) {
        try {
            fieldData.set(handle, value.shortValue());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte getTypeId() {
        return 2;
    }

}
