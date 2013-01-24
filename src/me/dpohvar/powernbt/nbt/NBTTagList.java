package me.dpohvar.powernbt.nbt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static me.dpohvar.powernbt.utils.StaticValues.classNBTTagList;
import static me.dpohvar.powernbt.utils.VersionFix.getNew;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagList extends NBTBase {
    private static Class clazz = classNBTTagList;
    private static Class[] classes = new Class[]{String.class};
    private static Field fieldList;
    private static Field fieldType;
    private static Method methodRead;
    private static Method methodWrite;
    private static Method methodClone;

    static {
        try {
            methodRead = clazz.getDeclaredMethod("load", java.io.DataInput.class);
            methodWrite = clazz.getDeclaredMethod("write", java.io.DataOutput.class);
            methodClone = clazz.getDeclaredMethod("clone");
            fieldList = clazz.getDeclaredField("list");
            fieldType = clazz.getDeclaredField("type");
            methodRead.setAccessible(true);
            methodWrite.setAccessible(true);
            methodClone.setAccessible(true);
            fieldList.setAccessible(true);
            fieldType.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isEmpty() {
        return getHandleList().isEmpty();
    }

    public NBTTagList() {
        this("");
    }

    public NBTTagList(String b) {
        super(getNew(clazz, classes, b));
    }

    public NBTTagList(boolean fromHandle, Object tag) {
        super(tag);
        if (!clazz.isInstance(tag)) throw new IllegalArgumentException();
    }

    public void write(java.io.DataOutput output) {
        try {
            methodWrite.invoke(handle, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void read(java.io.DataInput input) {
        try {
            methodRead.invoke(handle, input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NBTTagList clone() {
        try {
            Object h = methodClone.invoke(handle);
            return new NBTTagList(true, h);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean equals(Object o) {
        if (o instanceof NBTBase) o = ((NBTBase) o).getHandle();
        return handle.equals(o);
    }

    public int hashCode() {
        return handle.hashCode();
    }

    public List<Object> getHandleList() {
        try {
            return (List<Object>) fieldList.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public NBTBase get(int i) {
        List<Object> l = getHandleList();
        if (i < 0 || i >= l.size()) return null;
        Object t = getHandleList().get(i);
        return NBTBase.wrap(t);
    }

    public Byte getByte(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagByte) return ((NBTTagByte) t).get();
        return null;
    }

    public Short getShort(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagShort) return ((NBTTagShort) t).get();
        return null;
    }

    public Integer getInt(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagInt) return ((NBTTagInt) t).get();
        return null;
    }

    public Long getLong(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagLong) return ((NBTTagLong) t).get();
        return null;
    }

    public Float getFloat(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagFloat) return ((NBTTagFloat) t).get();
        return null;
    }

    public Double getDouble(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagDouble) return ((NBTTagDouble) t).get();
        return null;
    }

    public int[] getIntArray(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagIntArray) return ((NBTTagIntArray) t).get();
        return null;
    }

    public byte[] getByteArray(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagByteArray) return ((NBTTagByteArray) t).get();
        return null;
    }

    public NBTTagList getList(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagList) return ((NBTTagList) t);
        return null;
    }

    public NBTTagList getCompound(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagList) return ((NBTTagList) t);
        return null;
    }

    /**
     * Get list by key.
     * If not exist create new list and append to this
     *
     * @param i key
     * @return list
     */
    public NBTTagList compound(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagList) return ((NBTTagList) t);
        NBTTagList c = new NBTTagList();
        set(i, c);
        return c;
    }

    /**
     * Get list by key.
     * If not exist create new list and append to this
     *
     * @param i key
     * @return list
     */
    public NBTTagList list(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagList) return ((NBTTagList) t);
        NBTTagList c = new NBTTagList();
        set(i, c);
        return c;
    }

    public boolean remove(int i) {
        List<Object> list = getHandleList();
        if (i < 0 || i >= list.size()) return false;
        getHandleList().remove(i);
        return true;
    }

    public void clear() {
        getHandleList().clear();
    }

    public int size() {
        return getHandleList().size();
    }

    public Map<Integer, NBTBase> asMap() {
        Map<Integer, NBTBase> map = new HashMap<Integer, NBTBase>();
        List<Object> l = getHandleList();
        for (int i = 0; i < l.size(); i++) {
            NBTBase b = NBTBase.wrap(l.get(i));
            map.put(i, b);
        }
        return map;
    }

    public List<NBTBase> asList() {
        List<NBTBase> list = new LinkedList<NBTBase>();
        for (Object o : getHandleList()) {
            list.add(NBTBase.wrap(o));
        }
        return list;
    }

    private void setSubTypeId(byte type) {
        try {
            fieldType.set(handle, type);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * set(4,(long)15);
     * set(26,"string");
     * set(99,elementNBTBase);
     *
     * @param i     key
     * @param value NBTBase or some primitive value
     */
    public void set(int i, Object value) {
        NBTBase base = NBTBase.getByValue(value);
        byte type = base.getTypeId();
        if (type == 0) {
            throw new IllegalArgumentException();
        }
        List<Object> list = getHandleList();
        byte listType = getSubTypeId();
        if (listType != 0 && listType != type) throw new IllegalArgumentException();
        if (listType == 0) setSubTypeId(type);
        while (list.size() < i) {
            list.add(NBTBase.getDefault(type));
        }
        if (list.size() == i) {
            list.add(base.getHandle());
        } else {
            list.set(i, base.getHandle());
        }
    }

    public void add(Object value) {
        NBTBase base = NBTBase.getByValue(value);
        byte listType = getSubTypeId();
        if (listType != 0 && listType != base.getTypeId()) throw new IllegalArgumentException();
        if (listType == 0) setSubTypeId(base.getTypeId());
        getHandleList().add(base.getHandle());
    }

    public void addAll(Collection<?> values) {
        List<Object> list = getHandleList();
        for (Object value : values) {
            NBTBase base = NBTBase.getByValue(value);
            byte listType = getSubTypeId();
            if (listType != 0 && listType != base.getTypeId()) throw new IllegalArgumentException();
            if (listType == 0) setSubTypeId(base.getTypeId());
            list.add(base.getHandle());
        }
    }

    public void add(int i, Object value) {
        NBTBase base = NBTBase.getByValue(value);
        byte listType = getSubTypeId();
        if (listType != 0 && listType != base.getTypeId()) throw new IllegalArgumentException();
        if (listType == 0) setSubTypeId(base.getTypeId());
        NBTBase def = base.getDefault();
        List<Object> list = getHandleList();
        while(size()<i){
            list.add(def);
        }
        if (size() == i){
            list.add(base.getHandle());
        } else{
            list.add(i, base.getHandle());
        }
    }

    public byte getSubTypeId() {
        try {
            if (getHandleList().isEmpty()) return 0;
            return (Byte) fieldType.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public byte getTypeId() {
        return 9;
    }

}
