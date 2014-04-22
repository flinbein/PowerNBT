package me.dpohvar.powernbt.nbt;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagInt extends NBTTagNumeric<Integer> {
    public static final byte typeId = 3;

    public NBTTagInt() {
        this(0);
    }

    public NBTTagInt(String ignored) {
        this(0);
    }

    public NBTTagInt(int b) {
        super(nbtUtils.createTag(b,typeId));
    }

    public NBTTagInt(String ignored, int b) {
        this(b);
    }

    NBTTagInt(boolean ignored, Object tag) {
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
    public Integer get() {
        return (Integer) super.get();
    }

    @Override
    public void setNumber(Number value) {
        set(value.intValue());
    }

    @Override
    public byte getTypeId() {
        return typeId;
    }

}
