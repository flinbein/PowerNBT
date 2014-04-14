package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagFloat extends NBTTagNumeric<Float> {
    private static Class clazz = Reflections.getClass("{nms}.NBTTagFloat","net.minecraft.nbt.NBTTagFloat");
    private static Field field_Data = Reflections.getField(clazz, float.class);
    private static Constructor con = Reflections.getConstructorWithNoOrStringParam(clazz);

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
        super(createHandle(con));
        set(b);
    }

    NBTTagFloat(boolean ignored, Object tag) {
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
            return (Float) field_Data.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void set(Float value) {
        Reflections.setFieldValue(field_Data,handle,(float)value);
    }

    @Override
    public void setNumber(Number value) {
        set(value.floatValue());
    }

    @Override
    public byte getTypeId() {
        return 5;
    }
}
