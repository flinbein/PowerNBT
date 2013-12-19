package me.dpohvar.powernbt.exception;

import me.dpohvar.powernbt.nbt.NBTBase;

/**
 * Created with IntelliJ IDEA.
 * User: DPOH-VAR
 * Date: 14.09.13
 * Time: 14:16
 */
public class NBTTagUnexpectedType extends NBTQueryException{
    private final NBTBase tag;
    public NBTTagUnexpectedType(NBTBase tag, Class<? extends NBTBase> expected){
        super("tag has wrong type "+tag.getClass().getSimpleName()+" but expected "+expected.getSimpleName());
        this.tag = tag;
    }
    public NBTBase getTag(){
        return tag;
    }
}
