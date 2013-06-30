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
    private static Method methodRead;
    private static Method methodWrite;
    private static Method methodClone;

    static {
        try {
            methodRead = StaticValues.getMethodByTypeTypes(clazz, void.class, java.io.DataInput.class);
            methodWrite = StaticValues.getMethodByTypeTypes(clazz, void.class, java.io.DataOutput.class);
            methodClone = StaticValues.getMethodByTypeTypes(class_NBTBase, NBTBase.class_NBTBase);
            fieldData = StaticValues.getFieldByType(clazz, short.class);
            methodRead.setAccessible(true);
            methodWrite.setAccessible(true);
            methodClone.setAccessible(true);
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

    public NBTTagShort clone() {
        try {
            Object h = methodClone.invoke(handle);
            return new NBTTagShort(true, h);
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
