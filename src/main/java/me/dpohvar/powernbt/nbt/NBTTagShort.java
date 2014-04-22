package me.dpohvar.powernbt.nbt;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagShort extends NBTTagNumeric<Short> {
    public static final byte typeId = 2;

    public NBTTagShort() {
        this((short)0);
    }

    public NBTTagShort(String ignored) {
        this((short)0);
    }

    public NBTTagShort(short b) {
        super(nbtUtils.createTag(b,typeId));
    }

    public NBTTagShort(String ignored, short b) {
        this(b);
    }

    NBTTagShort(boolean ignored, Object tag) {
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

    public Short get() {
        return (Short) super.get();
    }

    public void setNumber(Number value) {
        set(value.shortValue());
    }

    @Override
    public byte getTypeId() {
        return typeId;
    }

}
