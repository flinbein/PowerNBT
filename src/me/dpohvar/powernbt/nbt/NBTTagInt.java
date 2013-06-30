package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.StaticValues;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagInt extends NBTTagNumeric {
    private static Class clazz = StaticValues.getClass("NBTTagInt");
    private static Field fieldData;
    private static Method methodRead;
    private static Method methodWrite;
    private static Method methodClone;

    static {
        try {
            methodRead = StaticValues.getMethodByTypeTypes(clazz, void.class, java.io.DataInput.class);
            methodWrite = StaticValues.getMethodByTypeTypes(clazz, void.class, java.io.DataOutput.class);
            methodClone = StaticValues.getMethodByTypeTypes(class_NBTBase, NBTBase.class_NBTBase);
            fieldData = StaticValues.getFieldByType(clazz, int.class);
            methodRead.setAccessible(true);
            methodWrite.setAccessible(true);
            methodClone.setAccessible(true);
            fieldData.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NBTTagInt() {
        this("", (int) 0);
    }

    public NBTTagInt(String s) {
        this(s, (int) 0);
    }

    public NBTTagInt(int b) {
        this("", b);
    }

    public NBTTagInt(String s, int b) {
        super(getNew(s, b));
    }

    private static Object getNew(String s, int b) {
        try{
            return clazz.getConstructor(String.class,int.class).newInstance(s,b);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public NBTTagInt(boolean ignored, Object tag) {
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

    public NBTTagInt clone() {
        try {
            Object h = methodClone.invoke(handle);
            return new NBTTagInt(true, h);
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

    public Integer get() {
        try {
            return (Integer) fieldData.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void set(Number value) {
        try {
            fieldData.set(handle, value.intValue());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte getTypeId() {
        return 3;
    }

}
