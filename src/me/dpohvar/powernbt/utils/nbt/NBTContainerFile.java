package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.versionfix.XNBTBase;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;
import static me.dpohvar.powernbt.utils.versionfix.StaticValues.classNBTBase;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.callStaticMethod;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.getShell;

public class NBTContainerFile extends NBTContainer {

    File file;

    public NBTContainerFile(File file) {
        this.file = file;
    }

    @Override
    public File getObject() {
        return file;
    }

    @Override
    public XNBTBase getRootBase() {
        try {
            DataInputStream input = new DataInputStream(new FileInputStream(file));
            Object mBase = callStaticMethod(classNBTBase, "b", new Class[]{java.io.DataInput.class}, input);
            input.close();
            return getShell(XNBTBase.class, mBase);
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
            DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
            callStaticMethod(classNBTBase, "a", new Class[]{classNBTBase, DataOutput.class}, base.getProxyObject(), output);
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
    public List<String> getTypes() {
        return new ArrayList<String>();
    }

    @Override
    public void removeRootBase() {
        file.delete();
    }
}
