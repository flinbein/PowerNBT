package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTBox;
import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.utils.PowerJSONParser;
import me.dpohvar.powernbt.utils.StringParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class NBTContainerFileGZip extends NBTContainerFile {


    public NBTContainerFileGZip(File file) {
        super(file, "gz:"+ StringParser.wrapToQuotesIfNeeded(file.toString()));
    }

    protected NBTContainerFileGZip(File file, String selector) {
        super(file, selector);
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<>();
    }

    @Override
    public Object readTag() {
        boolean isNBT;
        try (var input = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(this.getObject()))))) {
            byte b = input.readByte();
            NBTType nbtType = NBTType.fromByte(b);
            isNBT = (nbtType != null);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("no file",e);
        } catch (IOException e) {
            throw new RuntimeException("can't read file",e);
        }

        try {
            if (isNBT) return NBTManager.getInstance().readCompressed(this.getObject());
            else return PowerJSONParser.readCompressed(this.getObject());
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException("can't read file",e);
        } catch (Exception e) {
            throw new RuntimeException("wrong format",e);
        }
    }

    @Override
    public void writeTag(Object base) {
        try {
            if ((base instanceof Map || base instanceof Collection || base instanceof Object[] || base instanceof Boolean) && !(base instanceof NBTBox)) { // json
                PowerJSONParser.writeCompressed(base, this.getObject());
            } else {
                NBTManager.getInstance().writeCompressed(this.getObject(), base);
            }
        } catch (Exception e) {
            throw new RuntimeException("IO error", e);
        }
    }

    @Override
    protected Class<File> getContainerClass() {
        return File.class;
    }

    @Override
    public String toString(){
        return this.getObject().getName();
    }
}
