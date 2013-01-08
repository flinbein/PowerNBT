package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.versionfix.XNBTBase;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.dpohvar.powernbt.PowerNBT.plugin;
import static me.dpohvar.powernbt.utils.versionfix.StaticValues.classCompressedStreamTools;
import static me.dpohvar.powernbt.utils.versionfix.StaticValues.classNBTTagCompound;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.*;

public class NBTContainerFileCustom extends NBTContainer {

    String name;
    File file;

    public NBTContainerFileCustom(String name) {
        this.name = name;
        if (name.contains(".") || name.contains(File.separator))
            throw new RuntimeException(plugin.translate("error_customfile", name));
        file = new File(plugin.getNBTFilesFolder(), name + ".nbtz");
    }

    @Override
    public String getObject() {
        return name;
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
            XNBTBase base = getShell(XNBTBase.class, tag);
            Map<String, Object> map = (Map<String, Object>) base.getProxyField("map");
            return getShell(XNBTBase.class, map.get("Data"));
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(plugin.translate("IO error", e));
        }
    }

    @Override
    public void setRootBase(XNBTBase data) {
        try {
            XNBTBase base = getShell(XNBTBase.class, getNew(classNBTTagCompound, new Class[]{String.class}, ""));
            data.setName("Data");
            Map<String, Object> map = (Map<String, Object>) base.getProxyField("map");
            map.put("Data", data.getProxyObject());
            if (!file.exists()) {
                new File(file.getParent()).mkdirs();
                file.createNewFile();
            }
            FileOutputStream output = new FileOutputStream(file);
            callStaticMethod(classCompressedStreamTools, "a", new Class[]{classNBTTagCompound, OutputStream.class}, base, output);
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
