package me.dpohvar.powernbt.nbt;

import java.util.ArrayList;
import java.util.List;

public class NBTContainerBase extends NBTContainer {

    NBTBase base;

    public NBTContainerBase(NBTBase base) {
        this.base = base;
    }

    public NBTBase getObject() {
        return this.base;
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<String>();
    }

    @Override
    public NBTBase getTag() {
        return this.base;
    }

    @Override
    public void setTag(NBTBase base) {
        this.base = base;
    }

    @Override
    public String getName() {
        return base.getName();
    }

    @Override
    public void removeTag() {
        this.base = null;
    }
}
