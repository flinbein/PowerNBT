package me.dpohvar.powernbt.api;

public interface NBTBox extends Cloneable {

    /**
     * Get original NBT tag.
     * @return NBTBase
     */
    public Object getHandle();

    /**
     * Get copy of original nbt box.
     * @return NBTTagCompound
     */
    public Object getHandleCopy();

    /**
     * Create clone of this NBT tag
     * @return cloned {@link NBTBox}
     */
    public NBTBox clone();

    public int size();

    public boolean isEmpty();

}










