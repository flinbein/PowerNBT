package me.dpohvar.powernbt.exception;

/**
 * Created by DPOH-VAR on 23.01.14
 */
public class NBTConvertException extends RuntimeException {

    private final Object convert;

    private byte type = -1;

    public NBTConvertException(Object convert){
        super("can't convert "+convert.getClass().getSimpleName()+" to NBTBase tag");
        this.convert = convert;
    }

    public NBTConvertException(Object convert, byte type){
        super("can't convert "+convert.getClass().getSimpleName()+" to NBT type "+type);
        this.convert = convert;
        this.type = type;
    }

    public Object getConvertedObject(){
        return convert;
    }

    public byte getType() {
        return type;
    }
}
