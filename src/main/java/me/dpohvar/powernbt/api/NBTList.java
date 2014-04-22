package me.dpohvar.powernbt.api;

import me.dpohvar.powernbt.utils.NBTUtils;

import java.util.*;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;

/**
 * Created by DPOH-VAR on 15.01.14
 */
public class NBTList implements List<Object> {

    private final List<Object> handleList;
    private final Object handle;

    public static NBTList forNBT(Object tag){
        if (tag==null) return null;
        return new NBTList(tag);
    }

    public static NBTList forNBTCopy(Object tag){
        if (tag==null) return null;
        return new NBTList(nbtUtils.cloneTag(tag));
    }

    NBTList(Object tag) {
        assert nbtUtils.getTagType(tag) == 9;
        this.handle = tag;
        this.handleList = nbtUtils.getHandleList(tag);
    }

    public NBTList(Collection a) {
        this(nbtUtils.createTagList());
        this.addAll(a);
    }

    public NBTList() {
        this(nbtUtils.createTagList());
    }

    @Override
    public boolean equals(Object t){
        return t instanceof NBTList && handle.equals(((NBTList) t).handle);
    }

    @SuppressWarnings("unused")
    public List<Object> getHandleList(){
        return handleList;
    }

    public Object getHandle(){
        return handle;
    }

    public Object getHandleCopy(){
        return nbtUtils.cloneTag(handle);
    }

    public byte getType(){
        if (size()==0) return 0;
        else return nbtUtils.getNBTTagListType(handle);
    }

    private void setType(byte type){
        nbtUtils.setNBTTagListType(handle, type);
    }

    private Object convertToCurrentType(Object javaObject){
        byte type = getType();
        if (type == 0) {
            Object tag = nbtUtils.createTag(javaObject);
            type = nbtUtils.getTagType(tag);
            setType(type);
            return javaObject;
        }
        else return nbtUtils.createTag(javaObject, type);
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone, CloneDoesntDeclareCloneNotSupportedException")
    public NBTList clone(){
        return new NBTList(nbtUtils.cloneTag(handle));
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
    public boolean contains(Object o) {
        return handleList.contains(nbtUtils.createTag(o));
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
        for (Object t: this) result[i++] = t;
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

    @Override
    public boolean add(Object o) {
        Object tag = convertToCurrentType(o);
        return handleList.add(tag);
    }

    @Override
    public boolean remove(Object o) {
        return handleList.remove(nbtUtils.createTag(o));
    }

    @Override
    public boolean containsAll(@SuppressWarnings("NullableProblems") Collection<?> c) {
        for (Object value : c) {
            if (!handleList.contains(nbtUtils.createTag(value))) return false;
        }
        return true;
    }


    @Override
    public boolean addAll(@SuppressWarnings("NullableProblems") Collection<?> c) {
        boolean modified = false;
        for (Object t: c) {
            Object tag = convertToCurrentType(t);
            modified |= handleList.add(tag);
        }
        return modified;
    }

    @Override
    public boolean addAll(int index,@SuppressWarnings("NullableProblems") Collection<?> c) {
        boolean modified = false;
        for (Object t: c) {
            Object tag = convertToCurrentType(t);
            modified = true;
            handleList.add(index++, tag);
        }
        return modified;
    }

    @Override
    public boolean removeAll(@SuppressWarnings("NullableProblems") Collection<?> c) {
        boolean modified = false;
        for (Object t: c) {
            modified |= handleList.remove(nbtUtils.createTag(t));
        }
        return modified;
    }

    @Override
    public boolean retainAll(@SuppressWarnings("NullableProblems") Collection<?> c) {
        boolean modified = false;
        Iterator itr = iterator();
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

    @Override
    public Object get(int index) {
        return nbtUtils.getValue(handleList.get(index));
    }

    @Override
    public Object set(int index, Object element) {
        Object tag = convertToCurrentType(element);
        Object oldTag = handleList.set(index, nbtUtils.createTag(element));
        return nbtUtils.getValue(oldTag);
    }

    @Override
    public void add(int index, Object element) {
        Object tag = convertToCurrentType(element);
        handleList.add(index, tag);
    }

    @Override
    public Object remove(int index) {
        return nbtUtils.getValue(handleList.remove(index));
    }

    @Override
    public int indexOf(Object o) {
        return handleList.indexOf(nbtUtils.createTag(o));
    }

    @Override
    public int lastIndexOf(Object o) {
        return handleList.lastIndexOf(nbtUtils.createTag(o));
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
            return nbtUtils.getValue(iterator.next());
        }

        @Override
        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        @Override
        public Object previous() {
            return nbtUtils.getValue(iterator.previous());
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
            Object tag = convertToCurrentType(o);
            iterator.set(tag);
        }

        @Override
        public void add(Object o) {
            Object tag = convertToCurrentType(o);
            iterator.add(tag);
        }
    }




    public class NBTSubList extends NBTList {
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
        public void add(int index, Object element) {
            rangeCheckForAdd(index);
            list.add(index + offset, element);
            size++;
        }

        @Override
        public Object remove(int index) {
            rangeCheck(index);
            return list.remove(index+offset);
        }

        @Override
        public boolean addAll(Collection<?> c) {
            return addAll(size, c);
        }

        @Override
        public boolean addAll(int index, Collection<?> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if (cSize==0) return false;
            list.addAll(offset + index, c);
            size += cSize;
            return true;
        }

        @Override
        @SuppressWarnings("NullableProblems")
        public NBTIterator iterator() {
            return listIterator();
        }

        @Override
        @SuppressWarnings("NullableProblems")
        public NBTIterator listIterator(final int index) {
            rangeCheckForAdd(index);

            return new NBTIterator(list.listIterator(index+offset)) {

                @Override
                public boolean hasNext() {
                    return nextIndex() < size;
                }

                @Override
                public Object next() {
                    if (hasNext())
                        return iterator.next();
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
                        return iterator.previous();
                    else
                        throw new NoSuchElementException();
                }

                @Override
                public int nextIndex() {
                    return iterator.nextIndex() - offset;
                }

                @Override
                public int previousIndex() {
                    return iterator.previousIndex() - offset;
                }

                @Override
                public void remove() {
                    iterator.remove();
                    size--;
                }

                @Override
                public void set(Object e) {
                    iterator.set(e);
                }

                @Override
                public void add(Object e) {
                    iterator.add(e);
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
        NBTIterator it = iterator();
        if (! it.hasNext()) return "[]";
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            Object e = it.next();
            sb.append(e);
            if (! it.hasNext()) return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }

}
