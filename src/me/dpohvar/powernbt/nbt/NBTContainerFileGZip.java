package me.dpohvar.powernbt.nbt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;
import static me.dpohvar.powernbt.utils.StaticValues.classCompressedStreamTools;
import static me.dpohvar.powernbt.utils.StaticValues.classNBTTagCompound;
import static me.dpohvar.powernbt.utils.VersionFix.callStaticMethod;

public class NBTContainerFileGZip extends NBTContainer {

    File file;

    public NBTContainerFileGZip(File file) {
        this.file = file;
    }

    public File getObject() {
        return file;
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<String>();
    }

    @Override
    public NBTBase getTag() {
        try {
            FileInputStream input = new FileInputStream(file);
            Object tag = callStaticMethod(classCompressedStreamTools, "a", new Class[]{InputStream.class}, input);
            input.close();
            return NBTBase.wrap(tag);
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(plugin.translate("IO error", e));
        }
    }

    @Override
    public void setTag(NBTBase base) {
        try {
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
                file.createNewFile();
            }
            FileOutputStream output = new FileOutputStream(file);
            callStaticMethod(classCompressedStreamTools, "a", new Class[]{classNBTTagCompound, OutputStream.class}, base.getHandle(), output);
            output.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(plugin.translate("error_nofile", file.getName()), e);
        } catch (Exception e) {
            throw new RuntimeException(plugin.translate("IO error", e));
        }
    }

    @Override
    public String getName() {
        return "file " + file.getName();
    }

    @Override
    public void removeTag() {
        file.delete();
    }
}
