package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagShort extends NBTTagNumeric<Short> {
    private static Class clazz = Reflections.getClass("{nms}.NBTTagShort", "net.minecraft.nbt.NBTTagShort");
    private static Field field_Data = Reflections.getField(clazz, short.class);
    private static Constructor con = Reflections.getConstructorWithNoOrStringParam(clazz);

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
        super(createHandle(con));
        set(b);
    }

    NBTTagShort(boolean ignored, Object tag) {
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
            return (Short) field_Data.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void set(Short value) {
        Reflections.setFieldValue(field_Data,handle,value);
    }

    public void setNumber(Number value) {
        Reflections.setFieldValue(field_Data,handle,value.shortValue());
    }

    @Override
    public byte getTypeId() {
        return 2;
    }

}
