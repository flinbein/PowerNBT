package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.exception.NBTTagUnexpectedType;
import me.dpohvar.powernbt.utils.NBTQuery;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public abstract class NBTContainer<T> {

    abstract public T getObject();

    protected NBTBase readCustomTag(){
        return readTag();
    }

    abstract protected NBTBase readTag();

    abstract protected void writeTag(NBTBase base);

    protected void writeCustomTag(NBTBase base){
        writeTag(base.clone());
    }

    protected void eraseTag() {
        writeTag(new NBTTagCompound());
    }
    protected void eraseCustomTag() {
        eraseTag();
    }

    abstract protected Class<T> getContainerClass();

    final public String getName(){
        return getContainerClass().getSimpleName();
    }

    public abstract List<String> getTypes();

    // ########################## PowerNBT API ##########################

    /**
     * Set value of container root tag
     * @see #removeTag() remove tag if value is null
     * @param value root tag
     */
    final public void setTag(NBTBase value){
        if(value==null) {
            eraseTag();
            return;
        }
        writeTag(value.clone());
    }

    @Deprecated
    final public void setTag(NBTQuery query,NBTBase value) throws NBTTagNotFound, NBTTagUnexpectedType {
        setTag(query.set(getTag(),value));
    }

    /**
     * Set value of container root tag using PowerNBT options
     * @see #removeCustomTag() remove tag if value is null
     * @param value root tag
     */
    final public void setCustomTag(NBTBase value){
        if(value==null) {
            eraseCustomTag();
            return;
        }
        value = value.clone();
        if(value instanceof NBTTagCompound){
            NBTTagCompound tag = (NBTTagCompound) value;
            List<String> ignoreList = plugin.getConfig().getStringList("ignore_set."+getName());
            if(ignoreList!=null) for(String ignore:ignoreList) tag.remove(ignore);
        }
        writeCustomTag(value);
    }

    @Deprecated
    final public void setCustomTag(NBTQuery query,NBTBase value) throws NBTTagNotFound, NBTTagUnexpectedType {
        if (query.isEmpty())  setCustomTag(value);
        else  setCustomTag(query.set(getCustomTag(),value));
    }


    /**
     * Get root tag of container
     * @return NBT tag
     */
    final public NBTBase getTag(){
        return readTag();
    }

    @Deprecated
    final public NBTBase getTag(NBTQuery query) throws NBTTagNotFound {
        return query.get(this.getTag());
    }

    /**
     * Get root tag of container using PowerNBT options
     * @return NBT tag
     */
    final public NBTBase getCustomTag(){
        NBTBase value = readTag();
        if(value instanceof NBTTagCompound){
            NBTTagCompound tag = (NBTTagCompound) value;
            List<String> ignoreList = plugin.getConfig().getStringList("ignore_set."+getName());
            if(ignoreList!=null) for(String ignore:ignoreList) tag.remove(ignore);
        }
        return value;
    }

    @Deprecated
    final public NBTBase getCustomTag(NBTQuery query) throws NBTTagNotFound {
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



    private NBTBase get(){
        return readTag();
    }

    private void set(Object value){
       writeTag(NBTBase.getByValue(value));
    }

    public String toString(){
        return getName();
    }


}
