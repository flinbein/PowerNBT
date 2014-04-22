package me.dpohvar.powernbt.nbt;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;


/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagByte extends NBTTagNumeric<Byte> {
    public static final byte typeId = 1;

    public NBTTagByte() {
        this((byte) 0);
    }

    public NBTTagByte(String ignored) {
        this((byte) 0);
    }

    public NBTTagByte(byte b) {
        super(nbtUtils.createTag(b,typeId));
    }

    public NBTTagByte(String ignored, byte b) {
        super(nbtUtils.createTag(b,typeId));
    }

    public NBTTagByte(boolean ignored, Object tag) {
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

    public Byte get() {
        return (Byte) nbtUtils.getValue(handle);
    }

    public void set(Number value) {
        set(value.byteValue());
    }

    @Override
    public byte getTypeId() {
        return typeId;
    }

    @Override
    public void setNumber(Number number) {
        set(number.byteValue());
    }
}
