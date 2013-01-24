package me.dpohvar.powernbt.nbt;

/**
 * 15.01.13 4:39
 *
 * @author DPOH-VAR
 */
public abstract class NBTTagDatable extends NBTBase {

    NBTTagDatable(Object handle) {
        super(handle);
    }

    abstract public Object get();
}
