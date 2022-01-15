package me.dpohvar.powernbt.utils.query;

import me.dpohvar.powernbt.exception.NBTTagNotFound;

public interface QSelector {

    /**
     * select next element by key
     * @return next element
     */
    public Object get(Object current, boolean useDefault) throws NBTTagNotFound;

    /**
     * delete next element by key
     * @return current element
     */
    public Object delete(Object current) throws NBTTagNotFound;

    /**
     * delete next element by key
     * @return current element
     */
    public Object set(Object current, Object value, boolean createDir) throws NBTTagNotFound;

    public default String getSeparator(QSelector prevSelector){
        return null;
    }
}
