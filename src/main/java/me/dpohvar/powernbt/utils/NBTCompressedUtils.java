package me.dpohvar.powernbt.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.InputStream;
import java.io.OutputStream;

import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

/**
 * access to NBTCompressedStreamTools
 */
public class NBTCompressedUtils {

    /**
     * static access to util
     */
    public static final NBTCompressedUtils nbtCompressedUtils = new NBTCompressedUtils();

    RefClass classStreamTools = getRefClass(
            "{NBTCompressedStreamTools} {nms}.NBTCompressedStreamTools, {nm}.nbt.CompressedStreamTools"
    );
    RefClass classNBTTagCompound = getRefClass(
            "{NBTTagCompound}, {nms}.NBTTagCompound {nm}.nbt.NBTTagCompound"
    );
    RefMethod readInputStream = classStreamTools.findMethodByParams(InputStream.class);
    RefMethod writeToOutputStream = classStreamTools.findMethodByParams(classNBTTagCompound, OutputStream.class);
    RefMethod writeToDataOutput = classStreamTools.findMethodByParams(classNBTTagCompound, DataOutput.class);
    RefMethod readDataInput;
    RefClass class_NBTReadLimiter;
    RefConstructor con_NBTReadLimiter;
    long readLimit = Long.MAX_VALUE/2;
    boolean useLimiter;

    private NBTCompressedUtils(){
        try{
            class_NBTReadLimiter = getRefClass("{NBTReadLimiter}, {nms}.NBTReadLimiter, {nm}.nbt.NBTReadLimiter");
            con_NBTReadLimiter = class_NBTReadLimiter.getConstructor(long.class);
            readDataInput = classStreamTools.findMethodByParams(DataInput.class, class_NBTReadLimiter);
            useLimiter = true;
        } catch (Exception e){
            readDataInput = classStreamTools.findMethodByParams(DataInput.class);
            useLimiter = false;
        }
    }


    /**
     * Read NBT compound from input stream
     * @param input input stream
     * @return read compound
     */
    public Object readCompound(InputStream input){
        return readInputStream.call(input);
    }

    /**
     * Read NBT compound from data input
     * @param input data input
     * @return read compound
     */
    public Object readCompound(DataInput input){
        if (useLimiter) return readDataInput.call(input, con_NBTReadLimiter.create(readLimit));
        else return readDataInput.call(input);
    }

    /**
     * Store NBT compound to output stream
     * @param nbtTagCompound NBT compound
     * @param output output stream
     */
    public void writeCompound(Object nbtTagCompound, java.io.OutputStream output){
        writeToOutputStream.call(nbtTagCompound, output);
    }

    /**
     * Store NBT compound to output stream
     * @param nbtTagCompound NBT compound
     * @param output output stream
     */
    public void writeCompound(Object nbtTagCompound, java.io.DataOutput output){
        writeToDataOutput.call(nbtTagCompound, output);
    }
}