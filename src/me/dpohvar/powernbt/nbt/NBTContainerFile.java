package me.dpohvar.powernbt.nbt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;
import static me.dpohvar.powernbt.utils.StaticValues.classNBTBase;
import static me.dpohvar.powernbt.utils.VersionFix.callStaticMethod;

public class NBTContainerFile extends NBTContainer {

    File file;

    public NBTContainerFile(File file) {
        this.file = file;
    }

    public File getObject() {
        return file;
    }

    @Override
    public NBTBase getTag() {
        try {
            DataInputStream input = new DataInputStream(new FileInputStream(file));
            Object mBase = callStaticMethod(classNBTBase, "b", new Class[]{java.io.DataInput.class}, input);
            input.close();
            return NBTBase.wrap(mBase);
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
            DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
            callStaticMethod(classNBTBase, "a", new Class[]{classNBTBase, DataOutput.class}, base.getHandle(), output);
            output.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(plugin.translate("error_nofile", file.getName()), e);
        } catch (Exception e) {
            throw new RuntimeException(plugin.translate("IO error"), e);
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
    public void removeTag() {
        file.delete();
    }
}
