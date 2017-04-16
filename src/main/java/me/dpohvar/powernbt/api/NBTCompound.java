package me.dpohvar.powernbt.api;

import java.util.*;

import static me.dpohvar.powernbt.api.NBTManager.*;
import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;

/**
 * Represent net.minecraft.server.NBTTagCompound.<br>
 * Allows you to work with NBTTagCompound as with Map.<br>
 * values of this map will be converted to java primitive types if it possible.<br>
 * net.minecraft.server.NBTTagList converted to NBTList<br>
 * net.minecraft.server.NBTTagCompound converted to NBTCompound<br>
 * types allowed to put:<br>
 * * all primitive types (boolean as NBTTagByte 0 or 1)<br>
 * * Object[] as NBTTagList<br>
 * * java.util.Collection as NBTTagList<br>
 * * java.util.Map as NBTTagCompound<br>
 * arrays, collections and maps must contains only the allowed values.<br>
 * Difference from {@link java.util.Map}:<br>
 * {@link me.dpohvar.powernbt.api.NBTCompound#put(String, Object)} creates a clone of NBT tag before put:<br><pre>
 *   NBTCompound cmp = new NBTCompound(); // cmp = {}
 *   cmp.put("foo", "bar"); // cmp = {foo=bar}
 *   cmp.put("self", cmp); // cloning cmp before put, cmp = {foo=bar, self={foo=bar}}
 *   cmp.get("self"); // result = {foo=bar}
 * </pre><br>
 * {@link me.dpohvar.powernbt.api.NBTCompound} can not contain empty keys or values (null)<br>
 * {@link me.dpohvar.powernbt.api.NBTCompound} can not contain cross-references.
 */
@SuppressWarnings("UnusedDeclaration")
public class NBTCompound implements Map<String,Object> {

    private final Map<String,Object> handleMap;
    private final Object handle;

    /**
     * Create new instance of NBTCompound by NBTTagCompound
     * @param tag instance of net.minecraft.server.NBTTagCompound
     * @return NBTCompound
     */
    public static NBTCompound forNBT(Object tag){
        if (tag==null) return null;
        return new NBTCompound(tag);
    }

    /**
     * Create new instance NBTCompound by copy of NBTTagCompound
     * @param tag instance of net.minecraft.server.NBTTagCompound
     * @return NBTCompound
     */
    public static NBTCompound forNBTCopy(Object tag){
        if (tag==null) return null;
        return forNBT(nbtUtils.cloneTag(tag));
    }

    /**
     * Convert NBT compound to java {@link java.util.Map}
     * @param map Empty map to fill
     * @param <T> T
     * @return map
     */
    public <T extends Map<String, Object>> T toMap(T map){
        map.clear();
        for (Map.Entry<String,Object> e: handleMap.entrySet()) {
            String key = e.getKey();
            Object nbtTag = e.getValue();
            byte type = nbtUtils.getTagType(nbtTag);
            if (type==9) {
                map.put(key, NBTList.forNBT(nbtTag).toArrayList());
            } else if (type==10) {
                map.put(key, forNBT(nbtTag).toHashMap());
            } else {
                map.put(key, nbtUtils.getValue(nbtTag));
            }
        }
        return map;
    }

    /**
     * Convert nbt compound to {@link java.util.HashMap}
     * @return HashMap
     */
    public HashMap<String,Object> toHashMap(){
        return toMap(new HashMap<String, Object>());
    }

    NBTCompound(Object tag){
        assert nbtUtils.getTagType(tag) == 10;
        this.handle = tag;
        this.handleMap = nbtUtils.getHandleMap(tag);
    }

    /**
     * Get original NBTTagCompound.
     *
     * @return NBTTagCompound
     */
    public Object getHandle(){
        return handle;
    }

    /**
     * Get copy of original NBTTagCompound.
     *
     * @return NBTTagCompound
     */
    public Object getHandleCopy(){
        return nbtUtils.cloneTag(handle);
    }

    /**
     * get Map stored in original NBTTagCompound.
     *
     * @return Map
     */
    public Map<String,Object> getHandleMap(){
        return handleMap;
    }

    /**
     * Create new empty NBTCompound
     */
    public NBTCompound(){
        this(nbtUtils.createTagCompound());
    }

    @Override
    public boolean equals(Object t){
        return t instanceof NBTCompound
                && handle.equals(((NBTCompound) t).handle);
    }

    /**
     * Convert java {@link java.util.Map} to NBTCompound.<br>
     * map should not contain cross-references!
     *
     * @param map map to convert
     */
    public NBTCompound(Map map){
        this(nbtUtils.createTagCompound());
        for (Object key: map.keySet()) {
            put(key.toString(), map.get(key));
        }
    }

    /**
     * Create clone of this NBT compouns
     * @return cloned {@link me.dpohvar.powernbt.api.NBTCompound}
     */
    @Override
    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException, CloneDoesntCallSuperClone"})
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

    /**
     * Put the <code>copy</code> of value to NBTTagCompound
     * @param key Key with which the value is to be associated
     * @param value Value to be associated with the specified key
     * @return The copy of previous value associated with key
     */
    @Override
    public Object put(String key, Object value) {
        if (key==null) return null;
        if (value==null) return remove(key);
        Object tag = nbtUtils.createTag(value);
        Object oldTag = put_handle(key,tag);
        return nbtUtils.getValue(oldTag);
    }

    private Object put_handle(String key, Object tag){
        nbtUtils.seTagName(tag, key);
        return handleMap.put(key, tag);
    }

    @Override
    public Object remove(Object key) {
        Object oldTag = handleMap.remove(key);
        return nbtUtils.getValue(oldTag);
    }

    /**
     * Copies all of the mappings from the map to this NBTTagCompound
     * @param map Mappings to be stored in this map
     */
    @Override
    public void putAll(@SuppressWarnings("NullableProblems") Map<? extends String, ?> map) {
        if (map==null) return;
        for (Entry<? extends String, ?> e: map.entrySet()) {
            String key = e.getKey();
            if (key==null) continue;
            put(key,e.getValue());
        }
    }

    @Override
    public void clear() {
        handleMap.clear();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Set<String> keySet() {
        return handleMap.keySet();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Collection<Object> values() {
        return new NBTValues(handleMap.values());
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public NBTEntrySet entrySet() {
        return new NBTEntrySet(handleMap.entrySet());
    }

    /**
     * Merge this compound with map.<br>
     * Merging occurs recursively for inner maps
     *
     * @param map map to merge
     */
    public void merge(Map map) {
        for(Object key: map.keySet()) {
            if (key == null) continue;
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

    public String toString() {
        NBTEntrySet.NBTIterator i = entrySet().iterator();
        if (!i.hasNext()) return "{}";
        StringBuilder sb = new StringBuilder().append('{');
        for (;;) {
            NBTEntrySet.NBTIterator.NBTEntry e = i.next();
            Object val = e.getValue();
            sb.append(e.getKey()).append('=');
            if (val instanceof byte[]) {
                sb.append("int[").append(((byte[]) val).length).append(']');
            } else if (val instanceof int[]) {
                sb.append("byte[").append(((int[]) val).length).append(']');
            } else {
                sb.append(val);
            }
            if (!i.hasNext()) return sb.append('}').toString();
            sb.append(", ");
        }
    }

    /**
     * Try to get value and convert to boolean
     * @param key key
     * @return value, false by default
     */
    public boolean getBoolean(String key) {
        Object val = get(key);
        if (val instanceof Float) return ((Float)val)!=0.f;
        if (val instanceof Double) return ((Double)val)!=0.d;
        if (val instanceof Number) return ((Number)val).longValue()!=0;
        if (val instanceof CharSequence) return ((CharSequence)val).length()!=0;
        if (val instanceof int[]) return ((int[])val).length!=0;
        if (val instanceof byte[]) return ((byte[])val).length!=0;
        if (val instanceof Collection) return !((Collection)val).isEmpty();
        if (val instanceof Map) return !((Map)val).isEmpty();
        return false;
    }

    /**
     * Try to get byte value or convert to byte
     * @param key key
     * @return value, 0 by default
     */
    public byte getByte(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number)val).byteValue();
        if (val instanceof CharSequence) try {
            return (byte) Long.parseLong(val.toString());
        } catch (Exception e){
            try {
                return (byte) Double.parseDouble(val.toString());
            } catch (Exception ignored){
            }
        }
        return 0;
    }

    /**
     * Try to get short value or convert to short
     * @param key key
     * @return value, 0 by default
     */
    public short getShort(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number)val).shortValue();
        if (val instanceof CharSequence) try {
            return (short) Long.parseLong(val.toString());
        } catch (Exception e){
            try {
                return (short) Double.parseDouble(val.toString());
            } catch (Exception ignored){
            }
        }
        return 0;
    }

    /**
     * Try to get int value or convert to int
     * @param key key
     * @return value, 0 by default
     */
    public int getInt(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number)val).intValue();
        if (val instanceof CharSequence) try {
            return (int) Long.parseLong(val.toString());
        } catch (Exception e){
            try {
                return (int) Double.parseDouble(val.toString());
            } catch (Exception ignored){
            }
        }
        return 0;
    }

    /**
     * Try to get long value or convert to long
     * @param key key
     * @return value, 0 by default
     */
    public long getLong(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number)val).longValue();
        if (val instanceof CharSequence) try {
            return Long.parseLong(val.toString());
        } catch (Exception e){
            try {
                return (long) Double.parseDouble(val.toString());
            } catch (Exception ignored){
            }
        }
        return 0;
    }

    /**
     * Try to get float value or convert to float
     * @param key key
     * @return value, 0 by default
     */
    public float getFloat(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number)val).floatValue();
        if (val instanceof CharSequence) try {
            return (float) Double.parseDouble(val.toString());
        } catch (Exception ignored){
        }
        return 0;
    }

    /**
     * Try to get double value or convert to double
     * @param key key
     * @return value, 0 by default
     */
    public double getDouble(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number)val).doubleValue();
        if (val instanceof CharSequence) try {
            return Double.parseDouble(val.toString());
        } catch (Exception ignored){
        }
        return 0;
    }

    /**
     * Try to get string value or convert string
     * @param key key
     * @return value, empty string by default
     */
    public String getString(String key) {
        Object val = get(key);
        if (val == null) return "";
        else return val.toString();
    }

    /**
     * Try to get int[]
     * @param key key
     * @return array, empty array by default
     */
    public int[] getIntArray(String key) {
        Object val = get(key);
        if (val instanceof int[]) return (int[]) val;
        if (val instanceof byte[]) {
            byte[] bytes = (byte[]) val;
            int[] result = new int[bytes.length];
            for(int i=0; i<bytes.length; i++) result[i]=bytes[i];
            return result;
        }
        return new int[0];
    }

    /**
     * Try to get byte[]
     * @param key key
     * @return array, empty array by default
     */
    public byte[] getByteArray(String key) { // sorry for typo
        Object val = get(key);
        if (val instanceof byte[]) return (byte[]) val;
        if (val instanceof int[]) {
            int[] ints = (int[]) val;
            byte[] result = new byte[ints.length];
            for(int i=0; i<ints.length; i++) result[i]=(byte)ints[i];
            return result;
        }
        return new byte[0];
    }

    /**
     * Try to get NBTCompound
     * @param key key
     * @return NBTCompound value, or null if there is no compound
     */
    public NBTCompound getCompound(String key) {
        Object val = get(key);
        if (val instanceof NBTCompound) return (NBTCompound) val;
        return null;
    }

    /**
     * Try to get NBTList
     * @param key key
     * @return NBTList value, or null if there is no list
     */
    public NBTList getList(String key) {
        Object val = get(key);
        if (val instanceof NBTList) return (NBTList) val;
        return null;
    }

    /**
     * Get NBTCompound or create new one<br>
     * Example: <br><pre>
     *     NBTCompound cmp = new NBTCompound().compound("display").list("Lore").add("lore1");
     *     // cmp = {display:{Lore:["lore1"]}}
     * </pre>
     * @param key Key
     * @return Existing or created compound
     */
    public NBTCompound compound(String key) {
        Object val = get(key);
        if (val instanceof NBTCompound) return (NBTCompound) val;
        NBTCompound compound = new NBTCompound();
        put_handle(key,compound.getHandle());
        return compound;
    }

    /**
     * get NBTList or create new one<br>
     * Example: <br><pre>
     *     NBTCompound cmp = new NBTCompound().compound("display").list("Lore").add("lore1");
     *     // cmp = {display:{Lore:["lore1"]}}
     * </pre>
     * @param key Key
     * @return Existing or created list
     */
    public NBTList list(String key) {
        Object val = get(key);
        if (val instanceof NBTList) return (NBTList) val;
        NBTList list = new NBTList();
        put_handle(key,list.getHandle());
        return list;
    }

    /**
     * Put NBTCompound to handle without using cloning.<br>
     * Be sure that you do not have cross-reference.<br>
     * Do not bind NBTCompound to itself!
     * @param key key with which the NBTCompound is to be associated
     * @param value NBTCompound to be associated with key
     * @return the previous value associated with key
     */
    public Object bind(String key, NBTCompound value) {
        Object val = get(key);
        put_handle(key, value.getHandle());
        return val;
    }

    /**
     * Put NBTList to handle without using cloning.<br>
     * Be sure that you do not have cross-reference.<br>
     * @param key Key with which the NBTList is to be associated
     * @param value NBTList to be associated with key
     * @return the previous value associated with key
     */
    public Object bind(String key, NBTList value) {
        Object val = get(key);
        put_handle(key, value.getHandle());
        return val;
    }

    /**
     * Check if compound contains key with value of specific type
     * @param key key
     * @param type type of value
     * @return true if compound has key with specific value
     */
    public boolean containsKey(String key, Class type){
        Object t = get(key);
        return t!=null && type.isInstance(t);
    }

    /**
     * Check if compound contains key with value of specific type
     * @param key key
     * @param type byte type of NBT tag
     * @return true if compound has key with specific value
     */
    public boolean containsKey(String key, byte type){
        Object tag = handleMap.get(key);
        return tag!=null && nbtUtils.getTagType(tag) == type;
    }

    public class NBTValues extends AbstractCollection<Object>{

        Collection<Object> handle;

        private NBTValues(Collection<Object> values) {
            this.handle = values;
        }

        @Override
        @SuppressWarnings("NullableProblems")
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

        NBTEntrySet(Set<Entry<String, Object>> entries) {
            this.entries = entries;
        }

        @Override
        @SuppressWarnings("NullableProblems")
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

                NBTEntry(Entry<String, Object> entry) {
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
                    if (value==null) {
                        Object val = getValue();
                        remove();
                        return val;
                    } else {
                        Object tag = nbtUtils.createTag(value);
                        Object oldTag = entry.setValue(tag);
                        return nbtUtils.getValue(oldTag);
                    }
                }
            }
        }
    }

}










