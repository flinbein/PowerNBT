package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTManager;

import java.util.ArrayList;
import java.util.List;

public class NBTContainerValue extends NBTContainer<Object> {

    private Object base;

    public NBTContainerValue(Object base) {
        this.base = base;
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<>();
    }

    @Override
    public Object readTag() {
        return this.base;
    }

    @Override
    public void writeTag(Object base) {
        this.base = base;
    }

    @Override
    public void eraseTag() {
        this.base = null;
    }

    @Override
    protected Class<Object> getContainerClass() {
        return Object.class;
    }

    @Override
    public String toString(){
        return NBTType.fromValue(base).name;
    }
}
