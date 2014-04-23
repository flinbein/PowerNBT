package me.dpohvar.powernbt.nbt;

import java.util.*;
import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;

/**
 * 15.01.13 4:39
 * @author DPOH-VAR
 */
public abstract class NBTTagNumericArray<T extends Number> extends NBTTagDatable implements List<T> {
    NBTTagNumericArray(Object handle) {
        super(handle);
    }

    abstract public int size();

    @Override
    public boolean isEmpty() {
        return size()==0;
    }

    abstract public T get(int i);
    public Object get() {
        return nbtUtils.getValue(handle);
    }
    abstract public T set(int i, Number value);
    abstract public T remove(int i);

    abstract public boolean add(Number value);

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object t:c){
            if(!contains(t)) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        List<Number> copy = new ArrayList<Number>(c);
        for(Number n:copy) add(n);
        return !c.isEmpty();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        List<Number> copy = new ArrayList<Number>(c);
        for(Number n:copy) add(index++,n);
        return true;
    }

    public void add(int pos,Number value){
        while(this.size()<pos){
            this.add(0);
        }
        if(this.size()==pos){
            this.add(value);
        } else {
            this.add(pos,value);
        }
    }

    @Override
    public Object[] toArray() {
        int size = size();
        Object[] r = new Object[size];
        for(int i=0; i<size; i++) r[i] = get(i);
        return r;
    }

    @Override
    public <R> R[] toArray(R[] a) {
        int size=0;
        int limit = (size<a.length)?size:a.length;
        for(int i=0;i<limit;i++){
            a[i]=(R)get(i);
        }
        return a;
    }

    @Override
    public ListIterator<T> listIterator(final int index){
        if (index<0 || index>size()) throw new IndexOutOfBoundsException("Index: "+index);
        return new ListItr(index);
    }

    @Override
    public ListIterator<T> listIterator(){
        return listIterator(0);
    }

    @Override
    public Iterator<T> iterator(){
        return new Itr();
    }



















    private class Itr implements Iterator<T> {

        int cursor = 0;

        int lastRet = -1;

        public boolean hasNext() {
            return cursor != size();
        }

        public T next() {
            try {
                T next = get(cursor);
                lastRet = cursor++;
                return next;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (lastRet == -1)
                throw new IllegalStateException();
            try {
                NBTTagNumericArray.this.remove(lastRet);
                if (lastRet < cursor)
                    cursor--;
                lastRet = -1;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

    }

    private class ListItr extends Itr implements ListIterator<T> {
        ListItr(int index) {
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public T previous() {
            try {
                int i = cursor - 1;
                T previous = get(i);
                lastRet = cursor = i;
                return previous;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor-1;
        }

        public void set(Number e) {
            if (lastRet == -1)
                throw new IllegalStateException();
            try {
                NBTTagNumericArray.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(Number e) {

            try {
                NBTTagNumericArray.this.add(cursor++, e);
                lastRet = -1;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }


}
