package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.StaticValues;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagString extends NBTTagDatable {
    private static Class clazz = StaticValues.getClass("NBTTagString");
    private static Field fieldData;

    static {
        try {
            fieldData = StaticValues.getFieldByType(clazz, String.class);
            fieldData.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NBTTagString() {
        this("", "");
    }

    public NBTTagString(String b) {
        this("", b);
    }

    public NBTTagString(String s, String b) {
        super(getNew(s, b));
    }

    private static Object getNew(String s, String b) {
        try{
            return clazz.getConstructor(String.class,String.class).newInstance(s,b);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public NBTTagString(boolean ignored, Object tag) {
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

    public String get() {
        try {
            return (String) fieldData.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void set(String value) {
        try {
            fieldData.set(handle, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return get();
    }

    @Override
    public byte getTypeId() {
        return 8;
    }

}
