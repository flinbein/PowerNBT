package me.dpohvar.powernbt.api;

import java.util.*;

/**
 * Represent net.minecraft.server.NBTTagList.<br>
 * Allows you to work with NBTTagList as with List.<br>
 * values on get() will be converted to java primitive types if it possible.<br>
 * net.minecraft.server.NBTTagList converted to NBTList<br>
 * net.minecraft.server.NBTTagCompound converted to NBTCompound<br>
 * types allowed to set to empty NBTList:<br>
 * * all primitive types (boolean as NBTTagByte 0 or 1)<br>
 * * Object[] as NBTTagList<br>
 * * java.util.Collection as NBTTagList<br>
 * * java.util.Map as NBTTagCompound<br>
 * arrays, collections and maps must contains only the allowed values.<br>
 * You can add any allowed value to empty NBTList<br>
 * if NBTList is not empty, you can add only values that can be converted to type of NBTList<br>
 * Example: <br><pre>
 *   NBTList list = new NBTList(); // ok
 *   list.getType(); // type is 0 - list is empty
 *   list.add( (int) 15 ); // ok
 *   list.getType(); // type is 3 - contains integers
 *   list.add( (float) 3.14 ); // 3.14 converted to 3
 *   list.add("some text"); // NBTConvertException, can not convert "some text" to int
 * </pre><br>
 * Difference from {@link java.util.List}:<br>
 * {@link me.dpohvar.powernbt.api.NBTList#set(int, Object)} and {@link me.dpohvar.powernbt.api.NBTList#add(int, Object)} creates clone of NBT tag before set:<br><pre>
 *   NBTList list = new NBTList();
 *   NBTCompound cmp = new NBTCompound();
 *   list.add(cmp);
 *   cmp.put("foo", "bar");
 *   NBTCompound innerCmp = list.get(0);
 *   // now innerCmp is empty, because cmp was added to the list before changes.
 * </pre><br>
 * {@link me.dpohvar.powernbt.api.NBTList} can not contain empty values (null).<br>
 * {@link me.dpohvar.powernbt.api.NBTList} can not contain cross-references.
 */
public class NBTList implements List<Object>, NBTBox {

    private final List<Object> handleList;
    private final Object handle;
    private static final NBTBridge nbtBridge = NBTBridge.getInstance();
    private static final NBTManager nbt = NBTManager.getInstance();

    /**
     * Create a new instance of NBTList by NBTTagList.<br>
     * all changes of created list will affect to NBTTagList.
     *
     * @param tag instance of net.minecraft.server.NBTTagCompound
     * @return NBTList
     */
    public static NBTList forNBT(Object tag){
        if (tag==null) return null;
        return new NBTList(tag);
    }

    /**
     * Create a new instance of NBTList by copy of NBTTagList.
     *
     * @param tag instance of net.minecraft.server.NBTTagCompound
     * @return NBTList
     */
    public static NBTList forNBTCopy(Object tag){
        if (tag==null) return null;
        return new NBTList(nbtBridge.cloneTag(tag));
    }

    NBTList(Object tag) {
        assert nbtBridge.getTagType(tag) == 9;
        this.handle = tag;
        this.handleList = nbtBridge.getNbtInnerList(tag);
    }

    /**
     * Convert java {@link java.util.Collection} to NBTList.<br>
     * Map should not contain cross-references!
     *
     * @param collection collection
     */
    public NBTList(Collection<?> collection) {
        this(nbtBridge.createNBTTagList());
        this.addAll(collection);
    }

    /**
     * Convert java array to NBTList
     *
     * @param array array
     */
    public NBTList(Object[] array) {
        this(nbtBridge.createNBTTagList());
        this.addAll(Arrays.asList(array));
    }

    /**
     * Create a new empty NBTList
     */
    public NBTList() {
        this(nbtBridge.createNBTTagList());
    }

    @Override
    public boolean equals(Object t){
        return t instanceof NBTList && handle.equals(((NBTList) t).handle);
    }

    /**
     * Get list stored in original NBTTagList.
     *
     * @return handle list
     */
    public List<Object> getHandleList(){
        return handleList;
    }

    /**
     * Get original NBTTagList.
     *
     * @return NBTTagList
     */
    public Object getHandle(){
        return handle;
    }

    /**
     * Get copy of original NBTTagList.
     *
     * @return NBTTagList
     */
    public Object getHandleCopy(){
        return nbtBridge.cloneTag(handle);
    }

    /**
     * Get byte type of original NBTTagList.
     *
     * @return type of list or 0 if list is empty
     */
    public byte getType(){
        if (size()==0) return 0;
        else return nbtBridge.getNBTTagListType(handle);
    }

    private void setType(byte type){
        nbtBridge.setNBTTagListType(handle, type);
    }

    private Object convertToCurrentTypeTag(Object value){
        byte type = getType();
        if (type == 0) {
            Object tag;
            if (value instanceof Map map) tag = new NBTCompound(map).getHandle();
            else if (value instanceof Collection col) tag = new NBTList(col).getHandle();
            else if (value instanceof Object[] col) tag = new NBTList(col).getHandle();
            else tag = nbtBridge.getTagValueByPrimitive(value);
            type = nbtBridge.getTagType(tag);
            setType(type);
            return tag;
        }
        else return nbt.getTagOfValue(NBTManager.convertValue(value, type)); // CREATE TAG AND CONVERT FOR TYPE
    }


    /**
     * Convert NBTList to java {@link java.util.List}
     * @param list empty list to fill
     * @param <T> T
     * @return list
     */
    public <T extends List<Object>> T toList(T list) {
        return toCollection(list);
    }

    /**
     * Convert NBTList to java {@link java.util.Collection}
     * @param collection empty collection to fill
     * @param <T> T
     * @return collection
     */
    public <T extends Collection<Object>> T toCollection(T collection) {
        collection.clear();
        for (Object nbtTag: handleList) {
            byte type = nbtBridge.getTagType(nbtTag);
            if (type==9) {
                collection.add(forNBT(nbtTag).toList(new ArrayList<>()));
            } else if (type==10) {
                collection.add(NBTCompound.forNBT(nbtTag).toMap(new HashMap<>()));
            } else {
                collection.add(nbt.getValueOfTag(nbtTag));
            }
        }
        return collection;
    }

    /**
     * Convert nbt list to {@link java.util.ArrayList}
     * @return ArrayList
     */
    public ArrayList<Object> toArrayList() {
        return toList(new ArrayList<Object>());
    }

    /**
     * Create clone of this NBT list
     * @return cloned {@link me.dpohvar.powernbt.api.NBTList}
     */
    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone, CloneDoesntDeclareCloneNotSupportedException")
    public NBTList clone(){
        return new NBTList(nbtBridge.cloneTag(handle));
    }

    @Override
    public int size() {
        return handleList.size();
    }

    @Override
    public boolean isEmpty() {
        return handleList.isEmpty();
    }

    @Override
    public boolean contains(Object value) {
        Object tag;
        if (value instanceof Map map) tag = new NBTCompound(map).getHandle();
        else if (value instanceof Collection col) tag = new NBTList(col).getHandle();
        else if (value instanceof Object[] col) tag = new NBTList(col).getHandle();
        else tag = nbtBridge.getTagValueByPrimitive(value);
        return handleList.contains(tag);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public NBTIterator iterator() {
        return new NBTIterator(handleList.listIterator());
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Object[] toArray() {
        Object[] result = new Object[size()];
        int i=0;
        NBTIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            Object val = iterator.next();
            result[i++] = val;
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked,NullableProblems")
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (size > a.length) size = a.length;
        for (int i=0; i<size; i++){
            a[i] = (T) get(i);
        }
        return a;
    }

    /**
     * Appends <code>clone</code> of the value to the end of NBTList<br>
     * @param o value to be appended to NBTList
     * @return true if NBTList is changed
     */
    @Override
    public boolean add(Object o) {
        Object tag = convertToCurrentTypeTag(o);
        return handleList.add(tag);
    }

    @Override
    public boolean remove(Object value) {
        Object tag;
        if (value instanceof Map map) tag = new NBTCompound(map).getHandle();
        else if (value instanceof Collection col) tag = new NBTList(col).getHandle();
        else if (value instanceof Object[] col) tag = new NBTList(col).getHandle();
        else tag = nbtBridge.getTagValueByPrimitive(value);
        return handleList.remove(tag);
    }

    @Override
    public boolean containsAll(@SuppressWarnings("NullableProblems") Collection<?> col) {
        for (Object value : col) {
            if (!contains(value)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(@SuppressWarnings("NullableProblems") Collection<?> primCollection) {
        boolean modified = false;
        for (Object t: primCollection) {
            Object tag = convertToCurrentTypeTag(t);
            modified |= handleList.add(tag);
        }
        return modified;
    }

    @Override
    public boolean addAll(int index,@SuppressWarnings("NullableProblems") Collection<?> c) {
        boolean modified = false;
        for (Object t: c) {
            if (t == null) continue;
            Object tag = convertToCurrentTypeTag(t);
            modified = true;
            handleList.add(index++, tag);
        }
        return modified;
    }

    @Override
    public boolean removeAll(@SuppressWarnings("NullableProblems") Collection<?> c) {
        boolean modified = false;
        for (Object value: c) {
            Object tag;
            if (value instanceof Map map) tag = new NBTCompound(map).getHandle();
            else if (value instanceof Collection col) tag = new NBTList(col).getHandle();
            else if (value instanceof Object[] col) tag = new NBTList(col).getHandle();
            else tag = nbtBridge.getTagValueByPrimitive(value);
            modified |= handleList.remove(tag);
        }
        return modified;
    }

    @Override
    public boolean retainAll(@SuppressWarnings("NullableProblems") Collection<?> c) {
        boolean modified = false;
        Iterator<Object> itr = iterator();
        while (itr.hasNext()) {
            if (!c.contains(itr.next())) {
                itr.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        handleList.clear();
    }

    public void clear(int fromIndex, int toIndex) {
        handleList.subList(fromIndex, toIndex).clear();
    }

    @Override
    public Object get(int index) {
        Object tag = handleList.get(index);
        return nbt.getValueOfTag(tag);
    }

    @Override
    public Object set(int index, Object element) {
        if (element == null) return remove(index);
        Object tag = convertToCurrentTypeTag(element);
        Object oldTag = handleList.set(index, tag);
        return nbt.getValueOfTag(oldTag);
    }

    @Override
    public void add(int index, Object element) {
        if (element == null) return;
        Object tag = convertToCurrentTypeTag(element);
        handleList.add(index, tag);
    }

    @Override
    public Object remove(int index) {
        return nbt.getValueOfTag(handleList.remove(index));
    }

    @Override
    public int indexOf(Object value) {
        Object tag;
        if (value instanceof Map map) tag = new NBTCompound(map).getHandle();
        else if (value instanceof Collection col) tag = new NBTList(col).getHandle();
        else if (value instanceof Object[] col) tag = new NBTList(col).getHandle();
        else tag = nbtBridge.getTagValueByPrimitive(value);
        return handleList.indexOf(tag);
    }

    @Override
    public int lastIndexOf(Object value) {
        Object tag;
        if (value instanceof Map map) tag = new NBTCompound(map).getHandle();
        else if (value instanceof Collection col) tag = new NBTList(col).getHandle();
        else if (value instanceof Object[] col) tag = new NBTList(col).getHandle();
        else tag = nbtBridge.getTagValueByPrimitive(value);
        return handleList.lastIndexOf(tag);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public NBTIterator listIterator() {
        return new NBTIterator(handleList.listIterator());
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public NBTIterator listIterator(int index) {
        return new NBTIterator(handleList.listIterator(index));
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public NBTSubList subList(int fromIndex, int toIndex) {
        return new NBTSubList(this,fromIndex,toIndex);
    }

    public class NBTIterator implements ListIterator<Object>{

        protected ListIterator<Object> iterator;

        private NBTIterator(ListIterator<Object> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Object next() {
            Object nbtTag = iterator.next();
            return nbt.getValueOfTag(nbtTag);
        }

        @Override
        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        @Override
        public Object previous() {
            return nbt.getValueOfTag(iterator.previous());
        }

        @Override
        public int nextIndex() {
            return iterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return iterator.previousIndex();
        }

        @Override
        public void remove() {
            iterator.remove();
        }

        @Override
        public void set(Object o) {
            if (o==null) {
                remove();
            } else {
                Object tag = convertToCurrentTypeTag(o);
                iterator.set(tag);
            }
        }

        @Override
        public void add(Object o) {
            Object tag = convertToCurrentTypeTag(o);
            iterator.add(tag);
        }
    }

    public static class NBTSubList extends NBTList {
        private final NBTList list;
        private final int offset;
        private int size;

        private NBTSubList(NBTList list, int fromIndex, int toIndex) {
            if (fromIndex < 0)
                throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
            if (toIndex > list.size())
                throw new IndexOutOfBoundsException("toIndex = " + toIndex);
            if (fromIndex > toIndex)
                throw new IllegalArgumentException("fromIndex(" + fromIndex +
                        ") > toIndex(" + toIndex + ")");
            this.list = list;
            offset = fromIndex;
            size = toIndex - fromIndex;
        }

        @Override
        public Object set(int index, Object element) {
            if (element == null) return remove(index);
            rangeCheck(index);
            return list.set(index+offset, element);
        }

        @Override
        public Object get(int index) {
            rangeCheck(index);
            return list.get(index+offset);
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            list.clear(offset, offset + size);
            size = 0;
        }

        @Override
        public void clear(int fromIndex, int toIndex) {
            list.clear(fromIndex + offset, toIndex + offset);
            size -= (toIndex - fromIndex);
        }

        @Override
        public void add(int index, Object element) {
            if (element == null) return;
            rangeCheckForAdd(index);
            list.add(index + offset, element);
            size++;
        }

        @Override
        public Object remove(int index) {
            rangeCheck(index);
            size--;
            return list.remove(index+offset);
        }

        @Override
        public boolean addAll(Collection<?> primCollection) {
            return addAll(size, primCollection);
        }

        @Override
        public boolean addAll(int index, Collection<?> c) {
            List<Object> objectToAdd = new ArrayList<>();
            for (Object o: c) {
                if (o != null) objectToAdd.add(o);
            }
            rangeCheckForAdd(index);
            int cSize = objectToAdd.size();
            if (cSize==0) return false;
            list.addAll(offset + index, objectToAdd);
            size += cSize;
            return true;
        }

        @Override
        @SuppressWarnings("NullableProblems")
        public NBTIterator iterator() {
            return this.listIterator();
        }

        @Override
        @SuppressWarnings("NullableProblems")
        public NBTIterator listIterator() {
            return this.listIterator(0);
        }

        @Override
        @SuppressWarnings("NullableProblems")
        public NBTIterator listIterator(final int index) {
            rangeCheckForAdd(index);

            return new NBTIterator(list.listIterator(index+offset).iterator) {

                @Override
                public boolean hasNext() {
                    return nextIndex() < size;
                }

                @Override
                public Object next() {
                    if (hasNext())
                        return super.next();
                    else
                        throw new NoSuchElementException();
                }

                @Override
                public boolean hasPrevious() {
                    return previousIndex() >= 0;
                }

                @Override
                public Object previous() {
                    if (hasPrevious())
                        return super.previous();
                    else
                        throw new NoSuchElementException();
                }

                @Override
                public int nextIndex() {
                    return super.nextIndex() - offset;
                }

                @Override
                public int previousIndex() {
                    return super.previousIndex() - offset;
                }

                @Override
                public void remove() {
                    super.remove();
                    size--;
                }

                @Override
                public void set(Object e) {
                    if (e == null) remove();
                    else super.set(e);
                }

                @Override
                public void add(Object e) {
                    if (e == null) return;
                    super.add(e);
                    size++;
                }
            };
        }

        @Override
        @SuppressWarnings("NullableProblems")
        public NBTSubList subList(int fromIndex, int toIndex) {
            return new NBTSubList(this, fromIndex, toIndex);
        }

        private void rangeCheck(int index) {
            if (index < 0 || index >= size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private String outOfBoundsMsg(int index) {
            return "Index: "+index+", Size: "+size;
        }
    }

    @Override
    public String toString() {
        return handle.toString();
    }

}
