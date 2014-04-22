package me.dpohvar.powernbt.exception;

/**
 * Created with IntelliJ IDEA.
 * User: DPOH-VAR
 * Date: 14.09.13
 * Time: 14:13
 */
public class NBTQueryException extends Exception {

    public NBTQueryException(){
        super();
    }

    public NBTQueryException(String message){
        super(message);
    }

    public NBTQueryException(Throwable cause){
        super(cause);
    }

    public NBTQueryException(String message,Throwable cause){
        super(message,cause);
    }
}
