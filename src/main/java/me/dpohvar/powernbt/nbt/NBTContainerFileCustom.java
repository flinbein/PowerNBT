package me.dpohvar.powernbt.nbt;

import java.io.*;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerFileCustom extends NBTContainerFileGZip {

    String name;

    public NBTContainerFileCustom(String name) {
        super(getFileByName(name));
        this.name = name;
    }

    private static File getFileByName(String name){
        if (name.contains(".") || name.contains(File.separator)) {
            throw new RuntimeException(plugin.translate("error_customfile", name));
        }
        return new File(plugin.getNBTFilesFolder(), name + ".nbtz");
    }

    @Override
    public NBTBase readTag() {
        NBTTagCompound compound = (NBTTagCompound) super.readTag();
        if (compound == null) return null;
        return compound.get("Data");
    }

    @Override
    public void writeTag(NBTBase data) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.put("Data", data);
        super.writeTag(compound);
    }

    @Override
    public String toString(){
        return "$$" + name;
    }
}
