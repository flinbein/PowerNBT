package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTManager;
import net.minecraft.nbt.NBTTagTypes;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class NBTContainerFile extends NBTContainer<File> {

    private File file;

    public NBTContainerFile(File file) {
        this.file = file;
    }

    public File getObject() {
        return file;
    }

    @Override
    public NBTBase readTag() {
        try {
            return NBTType.createFromJavaValue(NBTManager.getInstance().read(file));
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException("can't read file",e);
        } catch (Exception e) {
            throw new RuntimeException("wrong format",e);
        }
    }

    @Override
    public void writeTag(NBTBase base) {
        try {
            Object tag = base.getHandle();
            NBTManager.getInstance().write(file, NBTManager.getInstance().getValueOfTag(tag));
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
