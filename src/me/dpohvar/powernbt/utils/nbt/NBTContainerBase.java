package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.versionfix.XNBTBase;

import java.util.ArrayList;
import java.util.List;

public class NBTContainerBase extends NBTContainer {

    XNBTBase base;

    public NBTContainerBase(XNBTBase base) {
        this.base = base;
    }

    @Override
    public XNBTBase getObject() {
        return this.base;
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<String>();
    }

    @Override
    public XNBTBase getRootBase() {
        return this.base;
    }

    @Override
    public void setRootBase(XNBTBase base) {
        this.base = base;
    }

    @Override
    public String getName() {
        return base.getName();
    }

    @Override
    public void removeRootBase() {
        this.base = null;
    }
}
