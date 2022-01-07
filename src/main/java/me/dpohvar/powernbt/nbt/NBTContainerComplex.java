package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.exception.NBTQueryException;
import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.utils.NBTQuery;

import java.util.ArrayList;
import java.util.List;

public class NBTContainerComplex extends NBTContainer<NBTContainer> {

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

    public Object getQuery() {
        return this.query;
    }

    @Override
    public Object readTag() {
        try {
            return query.get(container.getTag());
        } catch (NBTTagNotFound nbtTagNotFound) {
            return null;
        }
    }

    @Override
    public Object readCustomTag() {
        try {
            return query.get(container.getCustomTag());
        } catch (NBTTagNotFound nbtTagNotFound) {
            throw new RuntimeException(nbtTagNotFound);
        }
    }

    @Override
    public void writeTag(Object base) {
        try {
            Object res = query.set(container.getTag(),base);
            container.writeTag(res);
        } catch (NBTQueryException exception) {
            throw new RuntimeException(exception.getMessage(),exception);
        }
    }

    @Override
    public void writeCustomTag(Object base) {
        try {
            Object res = query.set(container.getCustomTag(),base);
            container.setCustomTag(res);
        } catch (NBTQueryException exception) {
            throw new RuntimeException(exception.getMessage(),exception);
        }
    }

    @Override
    public void eraseTag() {
        try {
            Object res = query.remove(container.getTag());
            container.setTag(res);
        } catch (NBTTagNotFound exception) {
            throw new RuntimeException(exception.getMessage(),exception);
        }
    }

    @Override
    public void eraseCustomTag() {
        try {
            Object res = query.remove(container.getCustomTag());
            container.setCustomTag(res);
        } catch (NBTTagNotFound exception) {
            throw new RuntimeException(exception.getMessage(),exception);
        }
    }

    @Override
    protected Class<NBTContainer> getContainerClass() {
        return NBTContainer.class;
    }

    @Override
    public String toString(){
        if (query==null || query.toString().isEmpty()) return "<"+container.toString()+">";
        else return "<"+container.toString()+" "+query+">";
    }
}
