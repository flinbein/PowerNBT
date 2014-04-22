package me.dpohvar.powernbt.nbt;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;


/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagDouble extends NBTTagNumeric<Double> {
    public static final byte typeId = 6;

    public NBTTagDouble() {
        this((double) 0);
    }

    public NBTTagDouble(String ignored) {
        this((double) 0);
    }

    public NBTTagDouble(double b) {
        super(nbtUtils.createTag(b, typeId));
    }

    public NBTTagDouble(String ignored, double b) {
        this(b);
    }

    public NBTTagDouble(boolean ignored, Object tag) {
        super(tag);
        if (nbtUtils.getTagType(tag)!=typeId) throw new IllegalArgumentException();
    }

    public boolean equals(Object o) {
        if (o instanceof NBTBase) o = ((NBTBase) o).getHandle();
        return handle.equals(o);
    }

    public int hashCode() {
        return handle.hashCode();
    }

    public Double get() {
        return (Double) nbtUtils.getValue(handle);
    }

    @Override
    public byte getTypeId() {
        return typeId;
    }

    @Override
    public void setNumber(Number number) {
        set(number.doubleValue());
    }
}
