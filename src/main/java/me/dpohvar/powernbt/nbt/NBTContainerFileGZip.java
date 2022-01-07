package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
    public Object readTag() {
        return NBTManager.getInstance().readCompressed(file);
    }

    @Override
    public void writeTag(Object data) {
        try {
            NBTManager.getInstance().writeCompressed(file, data);
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
