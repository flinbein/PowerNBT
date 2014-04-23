package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Caller;

import java.util.ArrayList;
import java.util.List;

public class NBTContainerVariable extends NBTContainer<Caller> {

    Caller caller;
    String name;

    public NBTContainerVariable(Caller caller, String name) {
        this.caller = caller;
        this.name = name;
    }


    public Caller getObject() {
        return this.caller;
    }

    @Override
    public List<String> getTypes() {
        NBTContainer c = getContainer();
        if (c == null) return new ArrayList<String>();
        return c.getTypes();
    }

    public String getVariableName() {
        return name;
    }

    public NBTContainer getContainer() {
        return caller.getVariable(name);
    }

    public void setContainer(NBTContainer t) {
        caller.setVariable(name, t);
    }

    public void removeContainer() {
        caller.removeVariable(name);
    }

    @Override
    public NBTBase readTag() {
        NBTContainer t = getContainer();
        if (t != null) return t.readTag();
        return null;
    }

    @Override
    public void writeTag(NBTBase base) {
        NBTContainer t = getContainer();
        if (t != null) t.writeTag(base);
    }

    @Override
    public void eraseTag() {
        NBTContainer t = getContainer();
        if (t != null) t.eraseTag();
    }

    @Override
    protected NBTBase readCustomTag() {
        NBTContainer t = getContainer();
        if (t != null) return t.readCustomTag();
        return null;
    }

    @Override
    protected void writeCustomTag(NBTBase base) {
        NBTContainer t = getContainer();
        if (t != null) t.writeCustomTag(base);
    }

    @Override
    protected void eraseCustomTag() {
        NBTContainer t = getContainer();
        if (t != null) t.eraseCustomTag();
    }

    @Override
    protected Class<Caller> getContainerClass() {
        return Caller.class;
    }

    @Override
    public String toString(){
        return "%"+name;
    }
}
