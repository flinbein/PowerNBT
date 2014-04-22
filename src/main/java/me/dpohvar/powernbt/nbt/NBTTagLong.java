package me.dpohvar.powernbt.nbt;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagLong extends NBTTagNumeric<Long> {
    public static final byte typeId = 4;

    public NBTTagLong() {
        this(0);
    }

    public NBTTagLong(String ignored) {
        this(0);
    }

    public NBTTagLong(long b) {
        super(nbtUtils.createTag(b, typeId));
    }

    public NBTTagLong(String ignored, long b) {
        this(b);
    }


    NBTTagLong(boolean ignored, Object tag) {
        super(tag);
        if (nbtUtils.getTagType(tag)!=typeId) throw new IllegalArgumentException();
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
        return (Long) nbtUtils.getValue(handle);
    }

    @Override
    public void set(Long value) {
        nbtUtils.setValue(handle, value);
    }

    @Override
    public void setNumber(Number value){
        set(value.longValue());
    }

    @Override
    public byte getTypeId() {
        return typeId;
    }

}
