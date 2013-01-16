package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Caller;

import java.util.ArrayList;
import java.util.List;

public class NBTContainerVariable extends NBTContainer {

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
    public NBTBase getTag() {
        NBTContainer t = getContainer();
        if (t != null) return t.getTag();
        return null;
    }

    @Override
    public void setTag(NBTBase base) {
        NBTContainer t = getContainer();
        if (t != null) t.setTag(base);
    }

    @Override
    public String getName() {
        return caller.getName() + " %" + name;
    }

    @Override
    public void removeTag() {
        NBTContainer t = getContainer();
        if (t != null) t.removeTag();
    }
}
