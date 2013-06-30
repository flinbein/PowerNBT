package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.StaticValues;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerFileGZip extends NBTContainer {

    File file;
    private static final Class class_NBTCompressedStreamTools = StaticValues.getClass("NBTCompressedStreamTools");
    private static final Class class_NBTTagCompound = StaticValues.getClass("NBTTagCompound");
    private static Method method_read;
    private static Method method_write;
    static{
        try {
            method_read = StaticValues.getMethodByTypeTypes(class_NBTCompressedStreamTools,class_NBTTagCompound,InputStream.class);
            method_write = StaticValues.getMethodByTypeTypes(class_NBTCompressedStreamTools,void.class,class_NBTCompressedStreamTools,OutputStream.class);
        }catch (Throwable e){
            throw new RuntimeException("NBTContainerFileGZip can not init",e);
        }
    }

    public NBTContainerFileGZip(File file) {
        this.file = file;
    }

    public File getObject() {
        return file;
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<String>();
    }

    @Override
    public NBTBase getTag() {
        try {
            FileInputStream input = new FileInputStream(file);
            Object tag = method_read.invoke(null,input);
            //callStaticMethod(classCompressedStreamTools, "a", new Class[]{InputStream.class}, input);
            input.close();
            return NBTBase.wrap(tag);
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("IO error", e);
        }
    }

    @Override
    public void setTag(NBTBase base) {
        try {
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
                file.createNewFile();
            }
            FileOutputStream output = new FileOutputStream(file);
            method_write.invoke(null,base.getHandle(),output);
            //callStaticMethod(classCompressedStreamTools, "a", new Class[]{classNBTTagCompound, OutputStream.class}, base.getHandle(), output);
            output.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(plugin.translate("error_nofile", file.getName()), e);
        } catch (Exception e) {
            throw new RuntimeException(plugin.translate("IO error", e));
        }
    }

    @Override
    public String getName() {
        return "file " + file.getName();
    }

    @Override
    public void removeTag() {
        file.delete();
    }
}
