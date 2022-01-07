package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTCompound;

import java.io.File;

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
    public Object readTag() {
        Object value = super.readTag();
        if (value instanceof NBTCompound compound) {
            return compound.get("Data");
        }
        return null;
    }

    @Override
    public void writeTag(Object data) {
        NBTCompound compound = new NBTCompound();
        compound.put("Data", data);
        super.writeTag(compound);
    }

    @Override
    public String toString(){
        return "$$" + name;
    }
}
