package me.dpohvar.powernbt.nbt;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerFileCustom extends NBTContainer<File> {

    String name;
    File file;

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
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(file));
            NBTTagCompound compound = NBTTagCompound.readGZip(input);
            return compound.get("Data");
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        } finally {
            if (input != null) try{
                input.close();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.ALL, "can not close NBT file " + file, e);
            }
        }
    }

    @Override
    public void writeTag(NBTBase data) {
        DataOutputStream output = null;
        try {
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
                file.createNewFile();
            }
            NBTTagCompound compound = new NBTTagCompound();
            compound.putToHandle("Data", data);

            output = new DataOutputStream(new FileOutputStream(file));
            compound.writeGZip(output);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(plugin.translate("error_nofile", file.getName()), e);
        } catch (Exception e) {
            throw new RuntimeException("IO error", e);
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
        return "$$" + name;
    }
}
