package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;

public class NBTContainerVariable extends NBTContainer {

    Caller caller;
    String name;

    public NBTContainerVariable(Caller caller, String name) {
        this.caller = caller;
        this.name = name;
    }

    @Override
    public Caller getObject() {
        return this.caller;
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
    public XNBTBase getRootBase() {
        NBTContainer t = getContainer();
        if (t != null) return t.getRootBase();
        return null;
    }

    @Override
    public void setRootBase(XNBTBase base) {
        NBTContainer t = getContainer();
        if (t != null) t.setRootBase(base);
    }

    @Override
    public String getName() {
        return caller.getName() + " %" + name;
    }

    @Override
    public void removeRootBase() {
        NBTContainer t = getContainer();
        if (t != null) t.removeRootBase();
    }
}
