package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerFileGZip extends NBTContainer<File> {

    File file;
    private static final Class class_NBTTagCompound = Reflections.getClass("{nms}.NBTTagCompound", "net.minecraft.nbt.NBTTagCompound");
    private static final Class class_NBTCompressedStreamTools = getCompressedStreamToolsClass();
    private static Method method_read = Reflections.getMethodByTypes(class_NBTCompressedStreamTools,class_NBTTagCompound,InputStream.class);
    private static Method method_write = Reflections.getMethodByTypes(class_NBTCompressedStreamTools,void.class,class_NBTTagCompound,OutputStream.class);

    private static Class getCompressedStreamToolsClass() {
        try {
            return Reflections.getClass("{nms}.CompressedStreamTools","net.minecraft.nbt.CompressedStreamTools");
        } catch (Exception ex) {
            return Reflections.getClass("{nms}.NBTCompressedStreamTools","net.minecraft.nbt.NBTCompressedStreamTools");
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
    public NBTBase readTag() {
        try {
            FileInputStream input = new FileInputStream(file);
            Object tag = method_read.invoke(null,input);
            input.close();
            return NBTBase.wrap(tag);
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("IO error", e);
        }
    }

    @Override
    public void writeTag(NBTBase base) {
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
    public void eraseTag() {
        file.delete();
    }

    @Override
    protected Class<File> getContainerClass() {
        return File.class;
    }

    @Override
    public String toString(){
        return "gz:"+file;
    }
}
