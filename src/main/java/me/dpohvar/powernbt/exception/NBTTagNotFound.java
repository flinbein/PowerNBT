package me.dpohvar.powernbt.exception;

/**
 * Created with IntelliJ IDEA.
 * User: DPOH-VAR
 * Date: 14.09.13
 * Time: 14:16
 */
public class NBTTagNotFound extends NBTQueryException{
    private final Object tag;
    public NBTTagNotFound(Object tag,Object tagName){
        super("tag "+tagName+" not found");
        this.tag = tag;
    }
    public Object getTag(){
        return tag;
    }
}
