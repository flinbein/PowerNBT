package me.dpohvar.powernbt.utils;

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

    private RefClass classStreamTools = getRefClass(
            "{nms}.NBTCompressedStreamTools, {nm}.nbt.CompressedStreamTools, {NBTCompressedStreamTools}"
    );
    private RefClass classNBTTagCompound = getRefClass(
            "{nms}.NBTTagCompound {nm}.nbt.NBTTagCompound, {NBTTagCompound}"
    );
    private RefMethod readInputStream = classStreamTools.findMethodByParams(InputStream.class);
    private RefMethod writeToOutputStream = classStreamTools.findMethodByParams(classNBTTagCompound, OutputStream.class);

    /**
     * Read NBT compound from input stream
     * @param input input stream
     * @return read compound
     */
    public Object readCompound(InputStream input){
        return readInputStream.call(input);
    }

    /**
     * Store NBT compound to output stream
     * @param nbtTagCompound NBT compound
     * @param output output stream
     */
    public void writeCompound(Object nbtTagCompound, OutputStream output){
        writeToOutputStream.call(nbtTagCompound, output);
    }

}