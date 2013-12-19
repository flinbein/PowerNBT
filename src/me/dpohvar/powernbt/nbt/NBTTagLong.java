package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagLong extends NBTTagNumeric<Long> {
    private static Class clazz = Reflections.getClass("{nms}.NBTTagLong", "net.minecraft.nbt.NBTTagLong");
    private static Field field_Data = Reflections.getField(clazz, long.class);
    private static Constructor con = Reflections.getConstructorByTypes(clazz,long.class);

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
        super(Reflections.create(con,b));
    }


    NBTTagLong(boolean ignored, Object tag) {
        super(tag);
        if (!clazz.isInstance(tag)) throw new IllegalArgumentException();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NBTBase) o = ((NBTBase) o).getHandle();
        return handle.equals(o);
    }

    @Override
    public int hashCode() {
        return handle.hashCode();
    }

    @Override
    public Long get() {
        try {
            return (Long) field_Data.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void set(Long value) {
        Reflections.setFieldValue(field_Data,handle,(long)value);
    }

    @Override
    public void setNumber(Number value){
        set(value.longValue());
    }

    @Override
    public byte getTypeId() {
        return 4;
    }

}
