package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerFile extends NBTContainer<File> {
    final File file;
    protected static Method method_NBTWrite = Reflections.getMethodByTypes(class_NBTBase,void.class,java.io.DataOutput.class);
    protected static Method method_NBTRead = Reflections.getMethodByTypes(class_NBTBase,void.class,java.io.DataInput.class,int.class);
    protected static Method method_NBTCreate = Reflections.getMethodByTypes(class_NBTBase,NBTBase.class_NBTBase,byte.class);

    public NBTContainerFile(File file) {
        this.file = file;
    }

    public File getObject() {
        return file;
    }

    @Override
    public NBTBase readTag() {
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(file));
            byte type = (byte)input.read();
            input.readUTF();
            Object mBase = Reflections.invoke(method_NBTCreate,null,type);
            Reflections.invoke(method_NBTRead,mBase,input,0);
            return NBTBase.wrap(mBase);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException("can't read file",e);
        } catch (Exception e) {
            throw new RuntimeException("wrong format",e);
        } finally {
            if (input!=null) try{
                input.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void writeTag(NBTBase base) {
        try {
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
                file.createNewFile();
            }
            DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
            output.write(base.getTypeId());
            output.writeUTF("");
            Reflections.invoke(method_NBTWrite,base.getHandle(), output);
            output.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file "+file+" not found", e);
        } catch (Exception e) {
            throw new RuntimeException("can't write to file", e);
        }
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<String>();
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
        return "file:"+file.toString();
    }
}
