package me.dpohvar.powernbt.nbt;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;

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
            Object tag = nbtUtils.readTag(input);
            return NBTBase.wrap(tag);
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
        FileOutputStream outputStream = null;
        try {
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);
            DataOutputStream output = new DataOutputStream(outputStream);
            nbtUtils.writeTagToOutput(output,base.getHandle());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file "+file+" not found", e);
        } catch (Exception e) {
            throw new RuntimeException("can't write to file", e);
        } finally {
            if (outputStream!=null) try {
                outputStream.close();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.ALL, "can not close NBT file " + file, e);
            }
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
