package me.dpohvar.powernbt.nbt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static me.dpohvar.powernbt.utils.StaticValues.classNBTBase;
import static me.dpohvar.powernbt.utils.StaticValues.classNBTTagCompound;
import static me.dpohvar.powernbt.utils.VersionFix.getNew;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagCompound extends NBTBase {
    private static Class clazz = classNBTTagCompound;
    private static Class[] classes = new Class[]{String.class};
    private static Field fieldMap;
    private static Method methodRead;
    private static Method methodWrite;
    private static Method methodClone;
    private static Method methodSet;

    static {
        try {
            methodRead = clazz.getDeclaredMethod("load", java.io.DataInput.class);
            methodWrite = clazz.getDeclaredMethod("write", java.io.DataOutput.class);
            methodClone = clazz.getDeclaredMethod("clone");
            methodSet = clazz.getDeclaredMethod("set", String.class, classNBTBase);
            fieldMap = clazz.getDeclaredField("map");
            methodRead.setAccessible(true);
            methodWrite.setAccessible(true);
            methodClone.setAccessible(true);
            methodSet.setAccessible(true);
            fieldMap.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NBTTagCompound() {
        this("");
    }

    public NBTTagCompound(String b) {
        super(getNew(clazz, classes, b));
    }

    public NBTTagCompound(boolean fromHandle, Object tag) {
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

    public NBTTagCompound clone() {
        try {
            Object h = methodClone.invoke(handle);
            return new NBTTagCompound(true, h);
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

    public Map<String, Object> getHandleMap() {
        try {
            return (Map<String, Object>) fieldMap.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public NBTBase get(String key) {
        Object t = getHandleMap().get(key);
        return NBTBase.wrap(t);
    }

    public Byte getByte(String key) {
        NBTBase t = get(key);
        if (t instanceof NBTTagByte) return ((NBTTagByte) t).get();
        return null;
    }

    public Short getShort(String key) {
        NBTBase t = get(key);
        if (t instanceof NBTTagShort) return ((NBTTagShort) t).get();
        return null;
    }

    public Integer getInt(String key) {
        NBTBase t = get(key);
        if (t instanceof NBTTagInt) return ((NBTTagInt) t).get();
        return null;
    }

    public Long getLong(String key) {
        NBTBase t = get(key);
        if (t instanceof NBTTagLong) return ((NBTTagLong) t).get();
        return null;
    }

    public Float getFloat(String key) {
        NBTBase t = get(key);
        if (t instanceof NBTTagFloat) return ((NBTTagFloat) t).get();
        return null;
    }

    public String getString(String key) {
        NBTBase t = get(key);
        if (t instanceof NBTTagString) return ((NBTTagString) t).get();
        return null;
    }

    public Double getDouble(String key) {
        NBTBase t = get(key);
        if (t instanceof NBTTagDouble) return ((NBTTagDouble) t).get();
        return null;
    }

    public int[] getIntArray(String key) {
        NBTBase t = get(key);
        if (t instanceof NBTTagIntArray) return ((NBTTagIntArray) t).get();
        return null;
    }

    public byte[] getByteArray(String key) {
        NBTBase t = get(key);
        if (t instanceof NBTTagByteArray) return ((NBTTagByteArray) t).get();
        return null;
    }

    public NBTTagList getList(String key) {
        NBTBase t = get(key);
        if (t instanceof NBTTagList) return ((NBTTagList) t);
        return null;
    }

    public NBTTagCompound getCompound(String key) {
        NBTBase t = get(key);
        if (t instanceof NBTTagCompound) return ((NBTTagCompound) t);
        return null;
    }

    public boolean getBool(String key) {
        Byte b = getByte(key);
        if (b == null) return false;
        return b != 0;
    }

    public boolean has(String key) {
        return getHandleMap().containsKey(key);
    }

    /**
     * Get compound by key.
     * If not exist create new compound and append to this
     *
     * @param key key
     * @return compound
     */
    public NBTTagCompound nextCompound(String key) {
        NBTBase t = get(key);
        if (t instanceof NBTTagCompound) return ((NBTTagCompound) t);
        NBTTagCompound c = new NBTTagCompound();
        try {
            methodSet.invoke(handle, key, c.getHandle());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    /**
     * Get list by key.
     * If not exist create new list and append to this
     *
     * @param key key
     * @return list
     */
    public NBTTagList nextList(String key) {
        NBTBase t = get(key);
        if (t instanceof NBTTagList) return ((NBTTagList) t);
        NBTTagList c = new NBTTagList();
        try {
            methodSet.invoke(handle, key, c.getHandle());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return c;
    }

    public boolean remove(String key) {
        return getHandleMap().remove(key) != null;
    }

    public void rename(String oldKey, String newKey) {
        Map<String, Object> map = getHandleMap();
        map.put(newKey, map.remove(oldKey));
    }

    public void clear() {
        getHandleMap().clear();
    }

    public int size() {
        return getHandleMap().size();
    }

    public Map<String, NBTBase> asMap() {
        Map<String, NBTBase> map = new HashMap<String, NBTBase>();
        for (Map.Entry<String, Object> e : getHandleMap().entrySet()) {
            map.put(e.getKey(), NBTBase.wrap(e.getValue()));
        }
        return map;
    }

    public List<NBTBase> asList() {
        List<NBTBase> list = new LinkedList<NBTBase>();
        for (Object o : getHandleMap().values()) {
            list.add(NBTBase.wrap(o));
        }
        return list;
    }

    /**
     * set("key1",(long)15);
     * set("key2","string");
     * set("key3",elementNBTBase);
     *
     * @param key key
     * @param value NBTBase or some primitive value
     */

    public void set(String key, Object value) {
        Object base = null;
        if (value instanceof NBTBase) {
            base = ((NBTBase) value).getHandle();
        } else if (classNBTBase.isInstance(value)) {
            //do nothing//
        } else if (value instanceof Byte) {
            base = new NBTTagByte((Byte) value).getHandle();
        } else if (value instanceof Short) {
            base = new NBTTagShort((Short) value).getHandle();
        } else if (value instanceof Integer) {
            base = new NBTTagInt((Integer) value).getHandle();
        } else if (value instanceof Long) {
            base = new NBTTagLong((Long) value).getHandle();
        } else if (value instanceof Float) {
            base = new NBTTagFloat((Float) value).getHandle();
        } else if (value instanceof Double) {
            base = new NBTTagDouble((Double) value).getHandle();
        } else if (value instanceof byte[]) {
            base = new NBTTagByteArray((byte[]) value).getHandle();
        } else if (value instanceof String) {
            base = new NBTTagString((String) value).getHandle();
        } else if (value instanceof int[]) {
            base = new NBTTagIntArray((int[]) value).getHandle();
        } else {
            throw new IllegalArgumentException();
        }
        try {
            methodSet.invoke(handle, key, base);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(Map<String,?> values){
        for(Map.Entry<String,?> e:values.entrySet()){
            set(e.getKey(),e.getValue());
        }
    }

    public void fill(Map<String,?> values){
        for(Map.Entry<String,?> e:values.entrySet()){
            if (!has(e.getKey())){
                set(e.getKey(),e.getValue());
            }
        }
    }

    @Override
    public byte getTypeId() {
        return 10;
    }

}
