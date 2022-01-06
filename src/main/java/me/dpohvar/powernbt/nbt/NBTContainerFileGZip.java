package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        return new ArrayList<>();
    }

    @Override
    public NBTBase readTag() {
        try {
            NBTCompound nbtCompound = NBTManager.getInstance().readCompressed(file);
            return new NBTTagCompound(false, nbtCompound.getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeTag(NBTBase data) {
        try {
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
                file.createNewFile();
            }
            NBTCompound nbtCompound = NBTCompound.forNBT(data.getHandle());
            NBTManager.getInstance().writeCompressed(file, nbtCompound);
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
        return "gz:"+file;
    }
}
