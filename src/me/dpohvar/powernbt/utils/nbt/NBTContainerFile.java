package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.versionfix.XNBTBase;

import java.io.*;

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
            DataInput input = new DataInputStream(new FileInputStream(file));
            Object mBase = callStaticMethod(classNBTBase, "b", new Class[]{java.io.DataInput.class}, input);
            return getShell(XNBTBase.class, mBase);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public void setRootBase(XNBTBase base) {
        try {
            DataOutput output = new DataOutputStream(new FileOutputStream(file));
            callStaticMethod(classNBTBase, "a", new Class[]{classNBTBase, java.io.DataInput.class}, base.getProxyObject(), output);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(plugin.translate("error_nofile", file.getName()), e);
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
