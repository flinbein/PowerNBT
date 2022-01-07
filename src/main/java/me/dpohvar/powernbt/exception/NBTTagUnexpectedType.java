package me.dpohvar.powernbt.exception;

/**
 * Created with IntelliJ IDEA.
 * User: DPOH-VAR
 * Date: 14.09.13
 * Time: 14:16
 */
public class NBTTagUnexpectedType extends NBTQueryException{
    private final Object tag;
    public NBTTagUnexpectedType(Object tag, Class<?> expected){
        super("tag has wrong type "+tag.getClass().getSimpleName()+" but expected "+expected.getSimpleName());
        this.tag = tag;
    }
    public Object getTag(){
        return tag;
    }
}
