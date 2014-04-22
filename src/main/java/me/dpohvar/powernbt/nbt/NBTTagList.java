package me.dpohvar.powernbt.nbt;

import java.util.*;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;
/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagList extends NBTBase implements List<NBTBase> {
    public static final byte typeId = 9;

    private static Random random;

    public boolean isEmpty() {
        return getHandleList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getHandleList().contains(NBTBase.getByValue(o).handle);
    }

    @Override
    public Iterator<NBTBase> iterator() {
        return listIterator();
    }

    public NBTTagList() {
        super(nbtUtils.createTagList());
    }

    public NBTTagList(String ignored) {
        this();
    }

    public NBTTagList(boolean ignored, Object tag) {
        super(tag);
        if (nbtUtils.getTagType(tag)!=typeId) throw new IllegalArgumentException();
    }

    public boolean equals(Object o) {
        if (o instanceof NBTBase) o = ((NBTBase) o).getHandle();
        return handle.equals(o);
    }

    public int hashCode() {
        return handle.hashCode();
    }

    public List<Object> getHandleList() {
        return nbtUtils.getHandleList(handle);
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

    public Number getNumber(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagNumeric) return (Number) ((NBTTagNumeric) t).get();
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

    public NBTTagCompound getCompound(int i) {
        NBTBase t = get(i);
        if (t instanceof NBTTagCompound) return ((NBTTagCompound) t);
        return null;
    }

    /**
     * Get list by key.
     * If not exist create new list and append to this
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

    @Override
    public NBTTagList clone(){
        return new NBTTagList(false, cloneHandle());
    }

    @Override
    public NBTBase remove(int i) {
        List<Object> list = getHandleList();
        if (i < 0 || i >= list.size()) return null;
        Object t = getHandleList().remove(i);
        if (t==null) return null;
        return NBTBase.getByValue(t);
    }

    @Override
    public int indexOf(Object o) {
        return getHandleList().indexOf(NBTBase.getByValue(o).handle);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getHandleList().lastIndexOf(NBTBase.getByValue(o).handle);
    }

    @Override
    public ListIterator<NBTBase> listIterator() {
        return new WrapListIterator(getHandleList().listIterator(0));
    }

    @Override
    public ListIterator<NBTBase> listIterator(int index) {
        return new WrapListIterator(getHandleList().listIterator(index));
    }

    @Override
    public NBTTagList subList(int fromIndex, int toIndex) {
        NBTTagList sub = new NBTTagList();
        for(Object tag: this.getHandleList().subList(fromIndex,toIndex)){
            sub.add(NBTBase.wrap(NBTBase.cloneHandle(tag)));
        }
        return sub;
    }

    public void clear() {
        getHandleList().clear();
    }

    public int size() {
        return getHandleList().size();
    }

    private void setSubTypeId(byte type) {
        nbtUtils.setNBTTagListType(handle, type);
    }

    @Override
    public NBTBase set(int i, NBTBase value) {
        return setValue(i, value);
    }

    public void set_b(int i, NBTBase value) {
        set_b(i,(Object)value);
    }

    @Override
    public void add(int index, NBTBase element) {
        byte listType = getSubTypeId();
        if (listType != 0 && listType != element.getTypeId()) throw new IllegalArgumentException();
        if (listType == 0) setSubTypeId(element.getTypeId());
        getHandleList().add(index,element.getHandle());
    }

    public void add(Integer index, Object element) {
        add(index, NBTBase.getByValue(element));
    }

    private NBTBase setValue(int i, Object value) {
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

    public NBTBase set_b(int i, Object value) {
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
            list.add(NBTBase.getDefault(type).handle);
        }
        if (list.size() == i) {
            list.add(base.getHandle());
            return null;
        } else {
            Object b = list.set(i, base.getHandle());
            return NBTBase.getByValue(b);
        }
    }

    @Override
    public boolean add(NBTBase base) {
        byte listType = getSubTypeId();
        if (listType != 0 && listType != base.getTypeId()) throw new IllegalArgumentException();
        if (listType == 0) setSubTypeId(base.getTypeId());
        boolean r = getHandleList().add(base.getHandle());
        return true;
    }

    void add_b(NBTBase base){
        byte listType = getSubTypeId();
        if (listType != 0 && listType != base.getTypeId()) throw new IllegalArgumentException();
        if (listType == 0) setSubTypeId(base.getTypeId());
    }

    public boolean addValue(Object val) {
        return add(NBTBase.getByValue(val));
    }

    public boolean leftShift(Object val) {
        return add(NBTBase.getByValue(val));
    }

    @Override
    public boolean remove(Object o) {
        return getHandleList().remove(NBTBase.getByValue(o).getHandle());
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        List<Object> tags = new ArrayList<Object>();
        for(Object t:c){
            tags.add(NBTBase.getByValue(t).handle);
        }
        return getHandleList().containsAll(tags);
    }

    public boolean addAll$$$(Collection<? extends NBTBase> c) {
        List<Object> list = getHandleList();
        for(NBTBase base:c) list.add(NBTBase.cloneHandle(base.handle));
        return !c.isEmpty();
    }

    @Override
    public boolean addAll(int index, Collection<? extends NBTBase> c) {
        List<Object> list = getHandleList();
        for(NBTBase base:c) list.add(index++,NBTBase.cloneHandle(base.handle));
        return !c.isEmpty();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        List<Object> tags = new ArrayList<Object>();
        for(Object t:c){
            tags.add(NBTBase.getByValue(t).handle);
        }
        return getHandleList().removeAll(tags);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<Object> tags = new ArrayList<Object>();
        for(Object t:c){
            tags.add(NBTBase.getByValue(t).handle);
        }
        return getHandleList().retainAll(tags);
    }

    public boolean addAll(Collection<? extends NBTBase> values) {
        List<Object> list = getHandleList();
        for (NBTBase base : values) {
            byte listType = getSubTypeId();
            if (listType != 0 && listType != base.getTypeId()) throw new IllegalArgumentException();
            if (listType == 0) setSubTypeId(base.getTypeId());
            list.add(NBTBase.cloneHandle(base.handle));
        }
        return !values.isEmpty();
    }

    public void addValue(int i, Object value) {
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
        if (getHandleList().isEmpty()) return 0;
            return nbtUtils.getNBTTagListType(handle);
    }

    public NBTBase getRandom() {
        List<Object> list = getHandleList();
        if (list.isEmpty()) return null;
        Object x = list.get(random.nextInt(list.size()));
        return NBTBase.getByValue(x);
    }

    @Override
    public String toString() {
        Iterator<NBTBase> i = iterator();
        if (! i.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            NBTBase e = i.next();
            sb.append(e instanceof NBTTagString ? "\""+e+"\"" : e);
            if (! i.hasNext())
                return sb.append(']').toString();
            sb.append(",");
        }
    }

    @Override
    public byte getTypeId() {
        return 9;
    }

    @Override
    public NBTBase[] toArray() {
        List<Object> list = getHandleList();
        NBTBase[] bases = new NBTBase[list.size()];
        int i=0; for(Object tag:list) bases[i++] = NBTBase.wrap(NBTBase.cloneHandle(tag));
        return bases;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        List<Object> list = getHandleList();
        int limit = list.size(); if(a.length < limit) limit = a.length;
        for(int i=0;i<limit;i++){
            a[i] = (T) NBTBase.wrap(NBTBase.cloneHandle(list.get(i)));
        }
        return a;
    }

    @Override
    public NBTBase get(int i) {
        List<Object> l = getHandleList();
        if (i < 0 || i >= l.size()) return null;
        Object t = getHandleList().get(i);
        return NBTBase.wrap(t);
    }

    private class WrapListIterator implements ListIterator<NBTBase>{
        public ListIterator<Object> handle;
        WrapListIterator(ListIterator<Object> handle){
            this.handle = handle;
        }

        @Override
        public boolean hasNext() {
            return handle.hasNext();
        }

        @Override
        public NBTBase next() {
            return NBTBase.wrap(handle.next());
        }

        @Override
        public boolean hasPrevious() {
            return handle.hasPrevious();
        }

        @Override
        public NBTBase previous() {
            return NBTBase.wrap(handle.previous());
        }

        @Override
        public int nextIndex() {
            return handle.nextIndex();
        }

        @Override
        public int previousIndex() {
            return handle.previousIndex();
        }

        @Override
        public void remove() {
            handle.remove();
        }

        @Override
        public void set(NBTBase nbtBase) {
            handle.set(NBTBase.cloneHandle(nbtBase.handle));
        }

        @Override
        public void add(NBTBase nbtBase) {
            handle.add(NBTBase.cloneHandle(nbtBase.handle));
        }
    }
}
