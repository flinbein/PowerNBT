package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerFileCustom extends NBTContainer<File> {

    String name;
    File file;

    private static final Class class_NBTTagCompound = Reflections.getClass("{nms}.NBTTagCompound","net.minectaft.nbt.NBTTagCompound");
    private static final Class class_NBTCompressedStreamTools = Reflections.getClass("{nms}.NBTCompressedStreamTools","net.minectaft.nbt.NBTCompressedStreamTools");
    private static Method method_read = Reflections.getMethodByTypes(class_NBTCompressedStreamTools,class_NBTTagCompound,InputStream.class);
    private static Method method_write = Reflections.getMethodByTypes(class_NBTCompressedStreamTools,void.class,class_NBTTagCompound,OutputStream.class);

    public NBTContainerFileCustom(String name) {
        this.name = name;
        if (name.contains(".") || name.contains(File.separator))
            throw new RuntimeException(plugin.translate("error_customfile", name));
        file = new File(plugin.getNBTFilesFolder(), name + ".nbtz");
    }

    @Override
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
            Object tag = Reflections.invoke(method_read,null,input);
            input.close();
            NBTTagCompound base = (NBTTagCompound) NBTBase.wrap(tag);
            return base.get("Data");
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    @Override
    public void writeTag(NBTBase data) {
        try {
            NBTTagCompound base = new NBTTagCompound();
            base.putToHandle("Data", data);
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
                file.createNewFile();
            }
            FileOutputStream output = new FileOutputStream(file);
            Reflections.invoke(method_write,null,base.getHandle(),output);
            output.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(plugin.translate("error_nofile", file.getName()), e);
        } catch (Exception e) {
            throw new RuntimeException("IO error", e);
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
        return "$$" + name;
    }
}
