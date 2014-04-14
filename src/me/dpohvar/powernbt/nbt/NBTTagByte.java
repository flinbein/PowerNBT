package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;


/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagByte extends NBTTagNumeric<Byte> {
    private static Class clazz = Reflections.getClass("{nms}.NBTTagByte","net.minecraft.nbt.NBTTagByte");
    private static Field fieldData = Reflections.getField(clazz,byte.class);
    private static Constructor con = Reflections.getConstructorWithNoOrStringParam(clazz);

    public NBTTagByte() {
        this("", (byte) 0);
    }

    public NBTTagByte(String s) {
        this(s, (byte) 0);
    }

    public NBTTagByte(byte b) {
        this("", b);
    }

    public NBTTagByte(String s, byte b) {
        super(createHandle(con));
        set(b);
    }

    public NBTTagByte(boolean ignored, Object tag) {
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

    public Byte get() {
        return (Byte) Reflections.getFieldValue(fieldData,handle);
    }

    @Override
    public void set(Byte value) {
        Reflections.setFieldValue(fieldData,handle,(byte)value);
        update();
    }

    public void set(Number value) {
        Reflections.setFieldValue(fieldData,handle,value.byteValue());
        update();
    }

    @Override
    public byte getTypeId() {
        return 1;
    }

    @Override
    public void setNumber(Number number) {
        set(number.byteValue());
    }
}
