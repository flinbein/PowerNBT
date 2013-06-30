package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.StaticValues;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerFile extends NBTContainer {
    File file;
    protected static Method method_NBTRead;
    protected static Method method_NBTWrite;
    static{
        try{
            method_NBTRead = StaticValues.getMethodByTypeTypes(class_NBTBase,class_NBTBase,java.io.DataInput.class);
            method_NBTWrite = StaticValues.getMethodByTypeTypes(class_NBTBase,void.class,class_NBTBase,java.io.DataOutput.class);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public NBTContainerFile(File file) {
        this.file = file;
    }

    public File getObject() {
        return file;
    }

    @Override
    public NBTBase getTag() {
        try {
            DataInputStream input = new DataInputStream(new FileInputStream(file));
            Object mBase = method_NBTRead.invoke(null,input);
            //callStaticMethod(class_NBTBase, "b", new Class[]{java.io.DataInput.class}, input);
            input.close();
            return NBTBase.wrap(mBase);
        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException(plugin.translate("IO error", e));
            return null;
        }
    }

    @Override
    public void setTag(NBTBase base) {
        try {
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
                file.createNewFile();
            }
            DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
            method_NBTWrite.invoke(null,base.getHandle(), output);
            //callStaticMethod(class_NBTBase, "a", new Class[]{class_NBTBase, DataOutput.class}, base.getHandle(), output);
            output.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(plugin.translate("error_nofile", file.getName()), e);
        } catch (Exception e) {
            throw new RuntimeException(plugin.translate("IO error"), e);
        }
    }

    @Override
    public String getName() {
        return "file " + file.getName();
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<String>();
    }

    @Override
    public void removeTag() {
        file.delete();
    }
}
