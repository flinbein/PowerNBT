package me.dpohvar.powernbt.nbt;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerFileGZip extends NBTContainer<File> {

    File file;

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
    public NBTTagCompound readTag() {
        FileInputStream input = null;
        try {
            input = new FileInputStream(file);
            return NBTTagCompound.readGZip(input);
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("IO error", e);
        } finally {
            if (input != null) try {
                input.close();
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.ALL, "can not close NBT file " + file, e);
            }
        }
    }

    @Override
    public void writeTag(NBTBase base) {
        FileOutputStream output = null;
        try {
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
                file.createNewFile();
            }
            output = new FileOutputStream(file);
            ((NBTTagCompound)base).writeGZip(output);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(plugin.translate("error_nofile", file.getName()), e);
        } catch (Exception e) {
            throw new RuntimeException(plugin.translate("IO error", e));
        } finally {
            if (output != null) try {
                output.close();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.ALL, "can not close NBT file "+file, e);
            }
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
