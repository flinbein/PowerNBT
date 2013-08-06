package me.dpohvar.powernbt.nbt;

/**
 * 15.01.13 4:39
 *
 * @author DPOH-VAR
 */
public abstract class NBTTagNumeric extends NBTTagDatable{
    NBTTagNumeric(Object handle) {
        super(handle);
    }

    public final String toString(){
        return get().toString();
    }

    abstract public Number get();
    abstract public void set(Number value);
}
