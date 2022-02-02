package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.utils.PowerJSONParser;
import me.dpohvar.powernbt.utils.StringParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        try {
            try {
                return NBTManager.getInstance().readCompressed(this.getObject());
            } catch (Exception ignored) {}
            return PowerJSONParser.readCompressed(this.getObject());
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException("can't read file",e);
        } catch (Exception e) {
            throw new RuntimeException("wrong format",e);
        }
    }

    @Override
    public void writeTagNBT(Object base) {
        NBTManager.getInstance().writeCompressed(this.getObject(), base);
    }

    @Override
    public void writeTagJSON(Object base) throws IOException {
        PowerJSONParser.writeCompressed(base, this.getObject());
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
