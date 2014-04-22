package me.dpohvar.powernbt.exception;

import me.dpohvar.powernbt.nbt.NBTBase;

/**
 * Created with IntelliJ IDEA.
 * User: DPOH-VAR
 * Date: 14.09.13
 * Time: 14:16
 */
public class NBTTagNotFound extends NBTQueryException{
    private final NBTBase tag;
    public NBTTagNotFound(NBTBase tag,Object tagName){
        super("tag "+tagName+" not found");
        this.tag = tag;
    }
    public NBTBase getTag(){
        return tag;
    }
}
