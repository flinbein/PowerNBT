package me.dpohvar.powernbt.extension.nbt;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingPropertyException;
import me.dpohvar.powernbt.api.NBTCompound;

public class NBTCompoundProperties extends GroovyObjectSupport{

    private final NBTCompound handle;

    public NBTCompoundProperties(NBTCompound handle){
        this.handle = handle;
    }

    @Override
    public Object getProperty(String property) {
        Object result = handle.get(property);
        if (result != null) return result;
        throw new MissingPropertyException(property, NBTCompound.class);
    }

    @Override
    public void setProperty(String property, Object newValue) {
        handle.put(property, newValue);
    }
}
