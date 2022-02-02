package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.utils.StringParser;

import java.io.File;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerFileGZipCustom extends NBTContainerFileGZip {

    String name;

    public NBTContainerFileGZipCustom(String name) {
        super(getFileByName(name), "$$" + StringParser.wrapToQuotesIfNeeded(name));
        this.name = name;
    }

    private static File getFileByName(String name){
        if (name.contains(".") || name.contains(File.separator)) {
            throw new RuntimeException(plugin.translate("error_customfile", name));
        }
        return new File(plugin.getNBTFilesFolder(), name + ".nbt.gz");
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
        if (isNBT(base)) {
            NBTCompound compound = new NBTCompound();
            compound.put("Data", base);
            super.writeTag(compound);
        } else { // json
            super.writeTag(base);
        }

    }
}
