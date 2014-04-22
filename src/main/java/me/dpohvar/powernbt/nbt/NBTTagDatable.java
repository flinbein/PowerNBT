package me.dpohvar.powernbt.nbt;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;

/**
 * 15.01.13 4:39
 *
 * @author DPOH-VAR
 */
public abstract class NBTTagDatable<T> extends NBTBase {

    NBTTagDatable(Object handle) {
        super(handle);
    }

    @SuppressWarnings("unchecked")
    public T get(){
        return (T) nbtUtils.getValue(handle);
    }

    public void set(T value){
        nbtUtils.setValue(handle, value);
    };
}
