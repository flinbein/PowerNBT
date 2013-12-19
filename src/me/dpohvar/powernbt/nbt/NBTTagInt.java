package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagInt extends NBTTagNumeric<Integer> {
    private static Class clazz = Reflections.getClass("{nms}.NBTTagInt","net.minecraft.nbt.NBTTagInt");
    private static Field field_Data = Reflections.getField(clazz, int.class);
    private static Constructor con = Reflections.getConstructorByTypes(clazz,int.class);

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
        super(Reflections.create(con,b));
    }

    NBTTagInt(boolean ignored, Object tag) {
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
    public Integer get() {
        return Reflections.getFieldValue(field_Data,handle);
    }

    @Override
    public void set(Integer value) {
        Reflections.setFieldValue(field_Data, handle, (int) value);
        update();
    }

    @Override
    public void setNumber(Number value) {
        set(value.intValue());
    }

    @Override
    public byte getTypeId() {
        return 3;
    }

}
