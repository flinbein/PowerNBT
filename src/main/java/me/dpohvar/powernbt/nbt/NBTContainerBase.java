package me.dpohvar.powernbt.nbt;

import java.util.ArrayList;
import java.util.List;

public class NBTContainerBase extends NBTContainer<NBTBase> {

    private NBTBase base;

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
    public NBTBase readTag() {
        return this.base;
    }

    @Override
    public void writeTag(NBTBase base) {
        this.base = base;
    }

    @Override
    public void eraseTag() {
        this.base = null;
    }

    @Override
    protected Class<NBTBase> getContainerClass() {
        return NBTBase.class;
    }

    @Override
    public String toString(){
        return base.getType().name;
    }
}
