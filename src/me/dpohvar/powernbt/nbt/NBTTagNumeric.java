package me.dpohvar.powernbt.nbt;

/**
 * 15.01.13 4:39
 *
 * @author DPOH-VAR
 */
public abstract class NBTTagNumeric<T extends Number> extends NBTTagDatable<T> {
    NBTTagNumeric(Object handle) {
        super(handle);
    }

    public abstract void setNumber(Number number);

    public final String toString(){
        return get().toString();
    }
}
