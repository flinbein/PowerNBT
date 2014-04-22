package me.dpohvar.powernbt.api;

import me.dpohvar.powernbt.utils.NBTUtils;

import java.util.*;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;

public class NBTCompound implements Map<String,Object> {

    private final Map<String,Object> handleMap;
    private final Object handle;

    public static NBTCompound forNBT(Object tag){
        if (tag==null) return null;
        return new NBTCompound(tag);
    }

    public static NBTCompound forNBTCopy(Object tag){
        if (tag==null) return null;
        return forNBT(nbtUtils.cloneTag(tag));
    }

    NBTCompound(Object tag){
        assert nbtUtils.getTagType(tag) == 10;
        this.handle = tag;
        this.handleMap = nbtUtils.getHandleMap(tag);
    }

    public Object getHandle(){
        return handle;
    }

    public Object getHandleCopy(){
        return nbtUtils.cloneTag(handle);
    }

    public Object getHandleMap(){
        return handleMap;
    }

    public NBTCompound(){
        this(nbtUtils.createTagCompound());
    }

    @Override
    public boolean equals(Object t){
        return t instanceof NBTCompound
                && handle.equals(((NBTCompound) t).handle);
    }

    public NBTCompound(Map values){
        this(nbtUtils.createTagCompound());
        for (Object e:values.entrySet()){
            Map.Entry entry = (Map.Entry) e;
            put(entry.getKey().toString(),entry.getValue());
        }
    }

    @Override
    public NBTCompound clone(){
        return new NBTCompound(nbtUtils.cloneTag(handle));
    }

    @Override
    public int size() {
        return handleMap.size();
    }

    @Override
    public boolean isEmpty() {
        return handleMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return handleMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        Object tag = nbtUtils.createTag(value);
        return handleMap.containsValue(tag);
    }

    @Override
    public Object get(Object key) {
        return nbtUtils.getValue(handleMap.get(key));
    }

    @Override
    public Object put(String key, Object value) {
        Object tag = nbtUtils.createTag(value);
        Object oldTag = handleMap.put(key,tag);
        return nbtUtils.getValue(oldTag);
    }

    @Override
    public Object remove(Object key) {
        Object oldTag = handleMap.remove(key);
        return nbtUtils.getValue(oldTag);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        for (Entry<? extends String, ?> e: m.entrySet()) {
            put(e.getKey(),e.getValue());
        }
    }

    @Override
    public void clear() {
        handleMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return handleMap.keySet();
    }

    @Override
    public Collection<Object> values() {
        return new NBTValues(handleMap.values());
    }

    @Override
    public NBTEntrySet entrySet() {
        return new NBTEntrySet(handleMap.entrySet());
    }

    public void merge(Map map) {
        for(Object key: map.keySet()) {
            if (!containsKey(key)) {
                put(key.toString(), map.get(key));
                continue;
            }
            Object val = get(key);
            Object value = map.get(key);
            if (val instanceof NBTCompound && value instanceof Map) {
                ((NBTCompound)val).merge((Map)value);
            } else {
                put(key.toString(),value);
            }
        }
    }

    public class NBTValues extends AbstractCollection<Object>{

        Collection<Object> handle;

        private NBTValues(Collection<Object> values) {
            this.handle = values;
        }

        @Override
        public Iterator<Object> iterator() {
            return new NBTValuesIterator(handle.iterator());
        }

        @Override
        public int size() {
            return handle.size();
        }

        public class NBTValuesIterator implements Iterator<Object>{

            private Iterator<Object> handle;

            private NBTValuesIterator(Iterator<Object> iterator) {
                this.handle = iterator;
            }

            @Override
            public boolean hasNext() {
                return handle.hasNext();
            }

            @Override
            public Object next() {
                return nbtUtils.getValue(handle.next());
            }

            @Override
            public void remove() {
                handle.remove();
            }
        }
    }

    public class NBTEntrySet extends AbstractSet<Entry<String, Object>> {

        private Set<Entry<String, Object>> entries;

        public NBTEntrySet(Set<Entry<String, Object>> entries) {
            this.entries = entries;
        }

        @Override
        public NBTIterator iterator() {
            return new NBTIterator(entries.iterator());
        }

        @Override
        public int size() {
            return entries.size();
        }

        public class NBTIterator implements Iterator<Entry<String, Object>> {

            private Iterator<Entry<String, Object>> iterator;

            private NBTIterator(Iterator<Entry<String, Object>> iterator) {
                this.iterator = iterator;
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public NBTEntry next() {
                return new NBTEntry(iterator.next());
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            public class NBTEntry implements Entry<String, Object>{

                private Entry<String, Object> entry;

                public NBTEntry(Entry<String, Object> entry) {
                    this.entry = entry;
                }

                @Override
                public String getKey() {
                    return entry.getKey();
                }

                @Override
                public Object getValue() {
                    return nbtUtils.getValue(entry.getValue());
                }

                @Override
                public Object setValue(Object value) {
                    Object tag = nbtUtils.createTag(value);
                    Object oldTag = entry.setValue(tag);
                    return nbtUtils.getValue(oldTag);
                }
            }
        }
    }

    public String toString() {
        NBTEntrySet.NBTIterator i = entrySet().iterator();
        if (!i.hasNext()) return "{}";
        StringBuilder sb = new StringBuilder().append('{');
        for (;;) {
            NBTEntrySet.NBTIterator.NBTEntry e = i.next();
            Object val = e.getValue();
            sb.append(e.getKey()).append('=');
            if (val instanceof byte[]) {
                sb.append( "int[" + ((byte[])val).length + ']');
            } else if (val instanceof int[]) {
                sb.append( "byte[" + ((int[])val).length + ']');
            } else {
                sb.append(val);
            }
            if (!i.hasNext()) return sb.append('}').toString();
            sb.append(", ");
        }
    }





    public byte getByte(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number)val).byteValue();
        if (val instanceof String) try {
            return (byte) Long.parseLong((String)val);
        } catch (Exception e){
            try {
                return (byte) Double.parseDouble((String) val);
            } catch (Exception ignored){
            }
        }
        return 0;
    }

    public short getShort(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number)val).shortValue();
        if (val instanceof String) try {
            return (short) Long.parseLong((String)val);
        } catch (Exception e){
            try {
                return (short) Double.parseDouble((String)val);
            } catch (Exception ignored){
            }
        }
        return 0;
    }

    public int getInt(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number)val).intValue();
        if (val instanceof String) try {
            return (int) Long.parseLong((String)val);
        } catch (Exception e){
            try {
                return (int) Double.parseDouble((String)val);
            } catch (Exception ignored){
            }
        }
        return 0;
    }

    public long getLong(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number)val).longValue();
        if (val instanceof String) try {
            return Long.parseLong((String)val);
        } catch (Exception e){
            try {
                return (long) Double.parseDouble((String)val);
            } catch (Exception ignored){
            }
        }
        return 0;
    }

    public float getFloat(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number)val).floatValue();
        if (val instanceof String) try {
            return (float) Double.parseDouble((String)val);
        } catch (Exception ignored){
        }
        return 0;
    }

    public double getDouble(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number)val).doubleValue();
        if (val instanceof String) try {
            return Double.parseDouble((String)val);
        } catch (Exception ignored){
        }
        return 0;
    }

    public String getString(String key) {
        Object val = get(key);
        if (val == null) return "";
        else return val.toString();
    }

    /**
     * get NBTCompound or create new one
     * Example: new NBTCompound().compound("display").list("Lore").add("lore1")
     * @param key key
     * @return existing or created compound
     */
    public NBTCompound compound(String key) {
        Object val = get(key);
        if (val instanceof NBTCompound) return (NBTCompound) val;
        NBTCompound compound = new NBTCompound();
        put(key,compound);
        return compound;
    }

    /**
     * get NBTList or create new one
     * Example: new NBTCompound().compound("display").list("Lore").add("lore1")
     * @param key key
     * @return existing or created list
     */
    public NBTList list(String key) {
        Object val = get(key);
        if (val instanceof NBTList) return (NBTList) val;
        NBTList list = new NBTList();
        put(key,list);
        return list;
    }


}










