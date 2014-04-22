package me.dpohvar.powernbt.exception;

/**
 * Created by DPOH-VAR on 23.01.14
 */
public class NBTReadException extends RuntimeException {

    private final Object convert;

    public NBTReadException(Object convert){
        super( (convert==null) ? "null" : convert.getClass().getSimpleName() + " is not valid NBT tag");
        this.convert = convert;
    }

    public Object getConvertedObject(){
        return convert;
    }
}
