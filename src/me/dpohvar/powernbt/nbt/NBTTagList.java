package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.StaticValues;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagList extends NBTBase implements Iterable<NBTBase> {
    private static Class clazz = StaticValues.getClass("NBTTagList");
    private static Field fieldList;
    private static Field fieldType;
    private static Method methodRead;
    private static Method methodWrite;
    private static Method methodClone;
    private static Random random;

    static {
        try {
            methodRead = StaticValues.getMethodByTypeTypes(clazz, void.class, java.io.DataInput.class);
            methodWrite = StaticValues.getMethodByTypeTypes(clazz, void.class, java.io.DataOutput.class);
            methodClone = StaticValues.getMethodByTypeTypes(class_NBTBase, NBTBase.class_NBTBase);
            fieldList = StaticValues.getFieldByType(clazz,List.class);
            fieldType = StaticValues.getFieldByType(clazz,byte.class);
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
        super(getNew(b));
    }

    private static Object getNew(String b) {
        try{
            return clazz.getConstructor(String.class).newInstance(b);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
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

    public NBTBase remove(int i) {
        List<Object> list = getHandleList();
        if (i < 0 || i >= list.size()) return null;
        Object t = getHandleList().remove(i);
        if (t==null) return null;
        return NBTBase.getByValue(t);
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

    public NBTBase set(int i, NBTBase value) {
        return set(i,(Object)value);
    }
    public NBTBase set(int i, Object value) {
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
            return null;
        } else {
            Object b = list.set(i, base.getHandle());
            return NBTBase.getByValue(b);
        }
    }

    public boolean add(NBTBase base) {
        byte listType = getSubTypeId();
        if (listType != 0 && listType != base.getTypeId()) throw new IllegalArgumentException();
        if (listType == 0) setSubTypeId(base.getTypeId());
        return getHandleList().add(base.getHandle());
    }
    public boolean add(Object val) {
        return add(NBTBase.getByValue(val));
    }

    public boolean addAll(Collection<?> values) {
        List<Object> list = getHandleList();
        for (Object value : values) {
            NBTBase base = NBTBase.getByValue(value);
            byte listType = getSubTypeId();
            if (listType != 0 && listType != base.getTypeId()) throw new IllegalArgumentException();
            if (listType == 0) setSubTypeId(base.getTypeId());
            list.add(base.getHandle());
        }
        return !values.isEmpty();
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

    public NBTBase getRandom() {
        List<Object> list = getHandleList();
        if (list.isEmpty()) return null;
        Object x = list.get(random.nextInt(list.size()));
        return NBTBase.getByValue(x);
    }

    @Override
    public byte getTypeId() {
        return 9;
    }

    @Override
    public NBTTagListIterator iterator() {
        return new NBTTagListIterator(getHandleList().iterator());
    }

    public class NBTTagListIterator implements Iterator<NBTBase>{
        Iterator<Object> iterator;
        NBTTagListIterator(Iterator<Object> iterator){
         this.iterator=iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public NBTBase next() {
            return NBTBase.getByValue(iterator.next());
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
}
