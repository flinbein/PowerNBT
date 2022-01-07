package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NBTContainerFile extends NBTContainer<File> {

    private final File file;

    public NBTContainerFile(File file) {
        this.file = file;
    }

    public File getObject() {
        return file;
    }

    @Override
    public Object readTag() {
        try {
            return NBTManager.getInstance().read(file);
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
            NBTManager.getInstance().write(file, base);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file "+file+" not found", e);
        } catch (Exception e) {
            throw new RuntimeException("can't write to file", e);
        }
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<>();
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
        return "file:"+file.toString();
    }
}
