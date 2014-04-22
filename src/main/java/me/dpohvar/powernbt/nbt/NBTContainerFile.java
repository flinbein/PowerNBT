package me.dpohvar.powernbt.nbt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class NBTContainerFile extends NBTContainer<File> {

    private File file;

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
            NBTBase tag = NBTBase.getDefault(type);
            tag.read(input);
            return tag;
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
            output.writeUTF(base.getName());
            base.write(output);
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
