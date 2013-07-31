package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.StaticValues;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagFloat extends NBTTagNumeric {
    private static Class clazz = StaticValues.getClass("NBTTagFloat");
    private static Field fieldData;

    static {
        try {
            methodClone = StaticValues.getMethodByTypeTypes(class_NBTBase, NBTBase.class_NBTBase);
            fieldData = StaticValues.getFieldByType(clazz, float.class);
            methodClone.setAccessible(true);
            fieldData.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NBTTagFloat() {
        this("", (float) 0);
    }

    public NBTTagFloat(String s) {
        this(s, (float) 0);
    }

    public NBTTagFloat(float b) {
        this("", b);
    }

    public NBTTagFloat(String s, float b) {
        super(getNew(s, b));
    }

    private static Object getNew(String s, float b) {
        try{
            return clazz.getConstructor(String.class,float.class).newInstance(s,b);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public NBTTagFloat(boolean ignored, Object tag) {
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

    public Float get() {
        try {
            return (Float) fieldData.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void set(Number value) {
        try {
            fieldData.set(handle, value.floatValue());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte getTypeId() {
        return 5;
    }

}
