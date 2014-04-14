package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;


/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagDouble extends NBTTagNumeric<Double> {
    private static Class clazz = Reflections.getClass("{nms}.NBTTagDouble", "net.minecraft.nbt.NBTTagDouble");
    private static Field field_Data;
    private static Constructor con = Reflections.getConstructorWithNoOrStringParam(clazz);

    static {
        try {
            field_Data = Reflections.getField(clazz, double.class);
            field_Data.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NBTTagDouble() {
        this("", (double) 0);
    }

    public NBTTagDouble(String s) {
        this(s, (double) 0);
    }

    public NBTTagDouble(double b) {
        this("", b);
    }

    public NBTTagDouble(String s, double b) {
        super(createHandle(con));
        set(b);
    }

    public NBTTagDouble(boolean ignored, Object tag) {
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

    public Double get() {
        try {
            return (Double) field_Data.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void set(Double value) {
        Reflections.setFieldValue(field_Data,handle,(double)value);
    }

    @Override
    public byte getTypeId() {
        return 6;
    }

    @Override
    public void setNumber(Number number) {
        set(number.doubleValue());
    }
}
