package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.exception.NBTTagUnexpectedType;
import me.dpohvar.powernbt.utils.NBTQuery;

import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

// TODO: MAKE

public abstract class NBTContainer<T> {

    abstract public T getObject();

    protected Object readCustomTag(){
        return readTag();
    }

    abstract protected Object readTag();

    abstract protected void writeTag(Object value);

    protected void writeCustomTag(Object value){
        writeTag(value);
    }

    protected void eraseTag() {
        writeTag(new NBTCompound());
    }
    protected void eraseCustomTag() {
        eraseTag();
    }

    abstract protected Class<T> getContainerClass();

    final public String getName(){
        return getContainerClass().getSimpleName();
    }

    public List<String> getTypes() {
        return new ArrayList<>();
    };

    // ########################## PowerNBT API ##########################

    /**
     * Set value of container root tag
     * @see #removeTag() remove tag if value is null
     * @param value root tag
     */
    final public void setTag(Object value){
        if(value==null) {
            eraseTag();
            return;
        }
        writeTag(value);
    }

    @Deprecated
    final public void setTag(NBTQuery query, Object value) throws NBTTagNotFound, NBTTagUnexpectedType {
        setTag(query.set(getTag(),value));
    }

    /**
     * Set value of container root tag using PowerNBT options
     * @see #removeCustomTag() remove tag if value is null
     * @param value root tag
     */
    final public void setCustomTag(Object value){
        if(value==null) {
            eraseCustomTag();
            return;
        }
        if (value instanceof NBTCompound tag){
            NBTCompound tagClone = tag.clone();
            List<String> ignoreList = plugin.getConfig().getStringList("ignore_set."+getName());
            if (!ignoreList.isEmpty()) {
                for (String ignore:ignoreList) tagClone.remove(ignore);
                value = tagClone;
            }
        }
        writeCustomTag(value);
    }

    @Deprecated
    final public void setCustomTag(NBTQuery query,Object value) throws NBTTagNotFound, NBTTagUnexpectedType {
        if (query.isEmpty())  setCustomTag(value);
        else  setCustomTag(query.set(getCustomTag(),value));
    }


    /**
     * Get root tag of container
     * @return NBT tag
     */
    final public Object getTag(){
        return readTag();
    }

    @Deprecated
    final public Object getTag(NBTQuery query) throws NBTTagNotFound {
        return query.get(this.getTag());
    }

    /**
     * Get root tag of container using PowerNBT options
     * @return NBT tag
     */
    final public Object getCustomTag(){
        Object value = readTag();
        if (value instanceof NBTCompound tag){
            NBTCompound tagClone = tag.clone();
            List<String> ignoreList = plugin.getConfig().getStringList("ignore_get."+getName());
            if (!ignoreList.isEmpty()) {
                for (String ignore:ignoreList) tagClone.remove(ignore);
                value = tagClone;
            }
        }
        return value;
    }

    @Deprecated
    final public Object getCustomTag(NBTQuery query) throws NBTTagNotFound {
        return query.get(this.getCustomTag());
    }

    /**
     * remove all NBT tags from container or remove contained object
     */
    public final void removeTag(){
        eraseTag();
    }

    @Deprecated
    final public void removeTag(NBTQuery query) throws NBTTagNotFound {
        setTag(query.remove(this.getTag()));
    }

    /**
     * remove all NBT tags from container or remove contained object using PowerNBT options
     */
    public final void removeCustomTag(){
        eraseCustomTag();
    }

    @Deprecated
    final public void removeCustomTag(NBTQuery query) throws NBTTagNotFound {
        if (query==null || query.isEmpty()) removeCustomTag();
        else setCustomTag(query.remove(this.getCustomTag()));
    }

    public String toString(){
        return getName();
    }
}
