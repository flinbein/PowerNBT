package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.versionfix.XNBTBase;

import java.util.ArrayList;
import java.util.List;

public class NBTContainerComplex extends NBTContainer {

    private final NBTContainer container;
    private final NBTQuery query;

    public NBTContainerComplex(NBTContainer container, NBTQuery query) {
        this.container = container;
        this.query = query;
    }

    @Override
    public NBTContainer getObject() {
        return this.container;
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<String>();
    }

    public NBTQuery getQuery() {
        return this.query;
    }

    @Override
    public XNBTBase getRootBase() {
        return container.getBase(getQuery());
    }

    @Override
    public void setRootBase(XNBTBase base) {
        container.setBase(getQuery(), base);
    }

    @Override
    public String getName() {
        return container.getName() + " (" + query.getQuery() + ")";
    }

    @Override
    public void removeRootBase() {
        container.removeBase(getQuery());
    }
}
