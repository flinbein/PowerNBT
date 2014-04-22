package me.dpohvar.powernbt.nbt;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagFloat extends NBTTagNumeric<Float> {
    public static final byte typeId = 5;

    public NBTTagFloat() {
        this((float) 0);
    }

    public NBTTagFloat(String ignored) {
        this((float) 0);
    }

    public NBTTagFloat(float b) {
        super(nbtUtils.createTag(b,typeId));
    }

    public NBTTagFloat(String ignored, float b) {
        this(b);
    }

    NBTTagFloat(boolean ignored, Object tag) {
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

    public Float get() {
        return (Float) super.get();
    }

    @Override
    public void setNumber(Number value) {
        set(value.floatValue());
    }

    @Override
    public byte getTypeId() {
        return typeId;
    }
}
