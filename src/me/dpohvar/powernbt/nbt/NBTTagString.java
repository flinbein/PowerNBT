package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagString extends NBTTagDatable<String> {
    private static Class clazz = Reflections.getClass("{nms}.NBTTagString", "net.minecraft.nbt.NBTTagString");
    private static Field field_Data = Reflections.getField(clazz, String.class);
    private static Constructor con = Reflections.getConstructorWithNoOrStringParam(clazz);

    public NBTTagString() {
        this("", "");
    }

    public NBTTagString(String b) {
        this("", b);
    }

    public NBTTagString(String s, String b) {
        super(createHandle(con));
        set(b);
    }

    NBTTagString(boolean ignored, Object tag) {
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
    public String get() {
        return (String) Reflections.getFieldValue(field_Data,handle);
    }

    @Override
    public void set(String value) {
        Reflections.setFieldValue(field_Data,handle,value);
        update();
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
