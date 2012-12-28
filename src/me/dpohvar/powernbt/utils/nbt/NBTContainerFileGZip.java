package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.versionfix.XNBTBase;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;
import static me.dpohvar.powernbt.utils.versionfix.StaticValues.classCompressedStreamTools;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.callStaticMethod;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.getShell;

public class NBTContainerFileGZip extends NBTContainer {

    File file;

    public NBTContainerFileGZip(File file) {
        this.file = file;
    }

    @Override
    public File getObject() {
        return file;
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<String>();
    }

    @Override
    public XNBTBase getRootBase() {
        try {
            FileInputStream input = new FileInputStream(file);
            Object tag = callStaticMethod(classCompressedStreamTools, "a", new Class[]{InputStream.class}, input);
            input.close();
            return getShell(XNBTBase.class, tag);
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(plugin.translate("IO error", e));
        }
    }

    @Override
    public void setRootBase(XNBTBase base) {
        try {
            if (!file.exists()) file.createNewFile();
            FileOutputStream output = new FileOutputStream(file);
            Object tag = callStaticMethod(classCompressedStreamTools, "a", new Class[]{OutputStream.class}, output);
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
    public void removeRootBase() {
        file.delete();
    }
}
