package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Caller;

import java.util.ArrayList;
import java.util.List;

public class NBTContainerVariable extends NBTContainer<Caller> {

    Caller caller;
    String name;

    public NBTContainerVariable(Caller caller, String name) {
        super("%"+name);
        this.caller = caller;
        this.name = name;
    }


    public Caller getObject() {
        return this.caller;
    }

    @Override
    public List<String> getTypes() {
        NBTContainer c = getContainer();
        if (c == null) return new ArrayList<>();
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
    public Object readTag() {
        NBTContainer t = getContainer();
        if (t != null) return t.readTag();
        return null;
    }

    @Override
    public void writeTag(Object base) {
        NBTContainer t = getContainer();
        if (t != null) t.writeTag(base);
    }

    @Override
    public void eraseTag() {
        caller.removeVariable(name);
    }

    @Override
    protected Object readCustomTag() {
        NBTContainer t = getContainer();
        if (t != null) return t.readCustomTag();
        return null;
    }

    @Override
    protected void writeCustomTag(Object base) {
        NBTContainer t = getContainer();
        if (t == null) caller.setVariable(name, new NBTContainerValue(base));
        else t.writeCustomTag(base);
    }

    @Override
    protected Class<Caller> getContainerClass() {
        return Caller.class;
    }

    @Override
    public String toString() {
        return "%"+name;
    }
}
