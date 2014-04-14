package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;
import me.dpohvar.powernbt.utils.StringParser;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagCompound extends NBTBase implements Map<String,NBTBase> {

    private static final Class clazz = Reflections.getClass("{nms}.NBTTagCompound","net.minecraft.nbt.NBTTagCompound");
    private static final Class class_NBTCompressedStreamTools = Reflections.getClass("{nms}.CompressedStreamTools","net.minecraft.nbt.CompressedStreamTools");
    private static Method method_read = Reflections.getMethodByTypes(class_NBTCompressedStreamTools, clazz, InputStream.class);
    private static Method method_write = Reflections.getMethodByTypes(class_NBTCompressedStreamTools,void.class, clazz,OutputStream.class);
    private static Method method_set = Reflections.getMethodByTypes(clazz, void.class, String.class, class_NBTBase);
    private static Field fieldMap = Reflections.getField(clazz, java.util.Map.class);
    private static Constructor con = Reflections.getConstructorByTypes(clazz);


    public NBTTagCompound() {
        this("");
    }

    public NBTTagCompound(String b) {
        super(Reflections.create(con));
    }

    static public NBTTagCompound readGZip(java.io.DataInput input) {
        try {
            return (NBTTagCompound) NBTBase.wrap(method_read.invoke(null,input));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    final public void writeGZip(java.io.DataOutput output) {
        try {
            method_write.invoke(null,handle,output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] toBytesGZip(){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        writeGZip(out);
        return buffer.toByteArray();
    }

    public static NBTTagCompound fromBytesGzip(byte[] source){
        ByteArrayInputStream buffer = new ByteArrayInputStream(source);
        DataInputStream in = new DataInputStream(buffer);
        return readGZip(in);
    }

    public NBTTagCompound(boolean ignored, Object tag) {
        super(tag);
        if (!clazz.isInstance(tag)) throw new IllegalArgumentException();
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
        Reflections.invoke(method_set,handle, key, c.getHandle());
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
        Reflections.invoke(method_set,handle, key, c.getHandle());
        return c;
    }

    public boolean remove(String key) {
        return getHandleMap().remove(key) != null;
    }

    public void rename(String oldKey, String newKey) {
        Map<String, Object> map = getHandleMap();
        map.put(newKey, map.remove(oldKey));
    }

    @Override
    public void clear() {
        getHandleMap().clear();
        update();
    }

    @Override
    public Set<String> keySet() {
        return new WrapMapKeySet(getHandleMap().keySet());
    }

    @Override
    public Collection<NBTBase> values() {
        return new WrapMapValues(getHandleMap().values());
    }

    @Override
    public Set<Entry<String, NBTBase>> entrySet() {
        return new WrapEntrySet(getHandleMap().entrySet());
    }

    public int size() {
        return getHandleMap().size();
    }

    @Override
    public boolean isEmpty() {
        return getHandleMap().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return getHandleMap().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return getHandleMap().containsValue(NBTBase.getByValue(value).handle);
    }

    @Override
    public NBTBase get(Object key) {
        return NBTBase.wrap(getHandleMap().get(key));
    }

    @Override
    public NBTBase put(String key, NBTBase value) {
        NBTBase r = put_a(key,value);
        update();
        return r;
    }

    public NBTBase put(String key, Object value) {
        return put(key,NBTBase.getByValue(value));
    }

    NBTBase put_a(String key, NBTBase value) {
        NBTBase r = NBTBase.wrap(getHandleMap().get(key));
        Reflections.invoke(method_set,handle,key,value.clone().handle);
        return r;
    }

    public void putToHandle(String key, NBTBase value) {
        Reflections.invoke(method_set,handle,key,value.clone().handle);
    }

    @Override
    public NBTBase remove(Object key) {
        NBTBase r = NBTBase.wrap(getHandleMap().get(key));
        getHandleMap().remove(key);
        update();
        return r;
    }

    @Override
    public void putAll(Map<? extends String, ? extends NBTBase> m) {
        for(Entry<? extends String, ? extends NBTBase> entry:m.entrySet()){
            put_a(entry.getKey(),entry.getValue());
        }
        update();
    }

    @Override
    public String toString() {
        Iterator<Entry<String,NBTBase>> i = entrySet().iterator();
        if (! i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<String,NBTBase> e = i.next();
            String key = e.getKey();
            NBTBase value = e.getValue();
            sb.append(key);
            sb.append('=');
            if(value==this) {
                sb.append("(this Compound)");
            } else if (value instanceof NBTTagString) {
                sb.append("\"")
                        .append(StringParser.wrap(((NBTTagString) value).get()))
                        .append("\"");
            } else {
                sb.append(value);
            }
            if (! i.hasNext())
                return sb.append('}').toString();
            sb.append(",");
        }
    }

    @Override
    public byte getTypeId() {
        return 10;
    }

    private class WrapMapKeySet implements Set<String>{

        Set<String> handle;

        public WrapMapKeySet(Set<String> handle){
            this.handle = handle;
        }

        @Override
        public int size() {
            return handle.size();
        }

        @Override
        public boolean isEmpty() {
            return handle.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return handle.contains(o);
        }

        @Override
        public Iterator<String> iterator() {
            return handle.iterator();
        }

        @Override
        public Object[] toArray() {
            return handle.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return handle.toArray(a);
        }

        @Override
        public boolean add(String s) {
            boolean r = handle.add(s);
            NBTTagCompound.this.update();
            return r;
        }

        @Override
        public boolean remove(Object o) {
            boolean r = handle.remove(o);
            NBTTagCompound.this.update();
            return r;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return handle.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends String> c) {
            boolean r = handle.addAll(c);
            NBTTagCompound.this.update();
            return r;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            boolean r = handle.retainAll(c);
            NBTTagCompound.this.update();
            return r;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean r = handle.removeAll(c);
            NBTTagCompound.this.update();
            return r;
        }

        @Override
        public void clear() {
            handle.clear();
        }
    }

    private class WrapMapValues implements Collection<NBTBase>{

        private Collection<Object> handle;

        public WrapMapValues(Collection<Object> handle){
            this.handle = handle;
        }

        @Override
        public int size() {
            return handle.size();
        }

        @Override
        public boolean isEmpty() {
            return handle.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            NBTBase base = NBTBase.getByValue(o);
            return handle.contains(base.handle);
        }

        @Override
        public Iterator<NBTBase> iterator() {
            return new WrapIterator(handle.iterator());
        }

        @Override
        public NBTBase[] toArray() {
            NBTBase[] base = new NBTBase[handle.size()];
            int i=0; for(Object t:handle) base[i++] = NBTBase.wrap(t);
            return base;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            Object[] h = handle.toArray(a);
            int limit = h.length; if (a.length < limit) limit = a.length;
            for(int i=0;i<limit;i++) h[i]=NBTBase.wrap(h[i]);
            return a;
        }

        @Override
        public boolean add(NBTBase nbtBase) {
            boolean r = handle.add(nbtBase.handle);
            // todo: needed ?
            NBTTagCompound.this.update();
            return r;
        }

        @Override
        public boolean remove(Object o) {
            NBTBase base = NBTBase.getByValue(o);
            boolean r = handle.remove(base.handle);
            // todo: needed?
            NBTTagCompound.this.update();
            return r;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for(Object o:c){
                if(!contains(o)) return false;
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends NBTBase> c) {
            boolean r = false;
            for(NBTBase base:c){
                if(handle.add(base.handle)) r = true;
            }
            NBTTagCompound.this.update();
            return r;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean r = false;
            for(Object o:c){
                NBTBase base = NBTBase.getByValue(o);
                if(handle.remove(base.handle)) r = true;
            }
            NBTTagCompound.this.update();
            return r;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            ArrayList<Object> bases = new ArrayList<Object>();
            for(Object o:c){
                NBTBase base = NBTBase.getByValue(o);
                bases.add(base.handle);
            }
            boolean r = handle.retainAll(bases);
            NBTTagCompound.this.update();
            return r;
        }

        @Override
        public void clear() {
            handle.clear();
            NBTTagCompound.this.update();
        }

        private class WrapIterator implements Iterator<NBTBase>{
            Iterator<Object> handle;
            public WrapIterator(Iterator<Object> handle){
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
            public void remove() {
                handle.remove();
                NBTTagCompound.this.update();
            }
        }
    }

    private class WrapEntrySet extends AbstractSet<Entry<String, NBTBase>>{

        private final Set<Entry<String, Object>> handle;

        public WrapEntrySet(Set<Entry<String, Object>> handle){
            this.handle = handle;
        }

        @Override
        public int size() {
            return handle.size();
        }

        @Override
        public Iterator<Entry<String, NBTBase>> iterator() {
            return new WrapIterator(handle.iterator());
        }

        @Override
        public Entry<String,NBTBase>[] toArray() {
            Entry<String,NBTBase>[] r = new Entry[size()];
            Iterator<Entry<String,NBTBase>> it = iterator();
            for (int i = 0; i < r.length; i++) {
                if (! it.hasNext())	// fewer elements than expected
                    return Arrays.copyOf(r, i);
                r[i] = it.next();
            }
            return it.hasNext() ? finishToArray(r, it) : r;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            Entry<String,NBTBase>[] r = new Entry[size()];
            Iterator<Entry<String,NBTBase>> it = iterator();
            for (int i = 0; i < r.length; i++) {
                if (! it.hasNext())	// fewer elements than expected
                    return (T[]) Arrays.copyOf(r, i);
                r[i] = it.next();
            }
            return it.hasNext() ? (T[]) finishToArray(r, it) : (T[]) r;
        }

        @Override
        public boolean remove(Object o) {
            return handle.remove(NBTBase.getByValue(o).handle);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            Collection<Object> bases = new ArrayList<Object>();
            for(Object t:c) bases.add(NBTBase.getByValue(t).handle);
            return handle.containsAll(bases);
        }

        @Override
        @Deprecated
        public boolean addAll(Collection<? extends Entry<String, NBTBase>> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Deprecated
        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            handle.clear();
        }

        private class WrapIterator implements Iterator<Entry<String, NBTBase>>{

            Iterator<Entry<String, Object>> handle;

            public WrapIterator(Iterator<Entry<String, Object>> handle){
                this.handle = handle;
            }

            @Override
            public boolean hasNext() {
                return handle.hasNext();
            }

            @Override
            public Entry<String, NBTBase> next() {
                return new WrapEntry(handle.next());
            }

            @Override
            public void remove() {
                handle.remove();
                NBTTagCompound.this.update();
            }

            class WrapEntry implements Entry<String, NBTBase>{
                Entry<String, Object> handle;
                public WrapEntry(Entry<String, Object> handle){
                    this.handle = handle;
                }

                @Override
                public String getKey() {
                    return handle.getKey();
                }

                @Override
                public NBTBase getValue() {
                    return NBTBase.wrap(handle.getValue());
                }

                @Override
                public NBTBase setValue(NBTBase value) {
                    NBTBase r = NBTBase.wrap(handle.getValue());
                    handle.setValue(value.clone().handle);
                    NBTTagCompound.this.update();
                    return r;
                }
            }
        }
    }

    private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
        int i = r.length;
        while (it.hasNext()) {
            int cap = r.length;
            if (i == cap) {
                int newCap = ((cap / 2) + 1) * 3;
                if (newCap <= cap) { // integer overflow
                    if (cap == Integer.MAX_VALUE)
                        throw new OutOfMemoryError
                                ("Required array size too large");
                    newCap = Integer.MAX_VALUE;
                }
                r = Arrays.copyOf(r, newCap);
            }
            r[i++] = (T)it.next();
        }
        // trim if overallocated
        return (i == r.length) ? r : Arrays.copyOf(r, i);
    }

}
