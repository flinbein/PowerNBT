package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTBox;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.utils.PowerJSONParser;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        return value;
    }

    @Override
    public void writeTag(Object base) {
        if ((base instanceof Map || base instanceof Collection || base instanceof Object[] || base instanceof Boolean) && !(base instanceof NBTBox)) { // json
            super.writeTag(base);
        } else {
            NBTCompound compound = new NBTCompound();
            compound.put("Data", base);
            super.writeTag(compound);
        }

    }

    @Override
    public String toString(){
        return "$$" + name;
    }
}
