package me.dpohvar.powernbt.nbt;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagString extends NBTTagDatable<String> {
    public static final byte typeId = 8;

    public NBTTagString() {
        this("");
    }

    public NBTTagString(String b) {
        super(nbtUtils.createTag(b,typeId));
    }

    public NBTTagString(String ignored, String b) {
        this(b);
    }

    NBTTagString(boolean ignored, Object tag) {
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
    public String get() {
        return (String) super.get();
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
