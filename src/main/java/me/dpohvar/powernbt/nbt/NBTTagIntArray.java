package me.dpohvar.powernbt.nbt;

import java.util.*;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;

/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagIntArray extends NBTTagNumericArray<Integer> {
    public static final byte typeId = 11;

    public NBTTagIntArray() {
        this(new int[0]);
    }

    public NBTTagIntArray(String ignored) {
        this(new int[0]);
    }

    public NBTTagIntArray(int[] b) {
        super(nbtUtils.createTag(b,typeId));
    }

    public NBTTagIntArray(String ignored, int[] b) {
        this(b);
    }

    public NBTTagIntArray(boolean ignored, Object tag) {
        super(tag);
        if (nbtUtils.getTagType(handle)!=typeId) throw new IllegalArgumentException();
    }

    public boolean equals(Object o) {
        if (o instanceof NBTBase) o = ((NBTBase) o).getHandle();
        return handle.equals(o);
    }

    public int hashCode() {
        return handle.hashCode();
    }

    public int[] get() {
        return (int[]) super.get();
    }

    public void set(int[] value) {
        super.set(value);
    }

    public int size() {
        return get().length;
    }

    @Override
    public boolean contains(Object o) {
        return intArrToList(get()).contains(o);
    }

    public Integer get(int i) {
        int[] array = get();
        if (i >= array.length) return null;
        return array[i];
    }

    public Integer set(int i, Number value) {
        Integer res = get(i);
        int[] array = get();
        List<Integer> list = new LinkedList<Integer>();
        for (int b : array) list.add(b);
        while (list.size() <= i) {
            list.add(0);
        }
        list.set(i, value.intValue());
        int[] result = new int[list.size()];
        int t = 0;
        for (int b : list) result[t++] = b;
        set(result);
        return res;
    }

    public Integer remove(int i) {
        Integer res = get(i);
        int[] array = get();
        if (i < 0 || i >= array.length) return res;
        List<Integer> list = new LinkedList<Integer>();
        for (int b : array) list.add(b);
        while (list.size() <= i) {
            list.add((int) 0);
        }
        list.remove(i);
        int[] result = new int[list.size()];
        int t = 0;
        for (int b : list) result[t++] = b;
        set(result);
        return res;
    }

    @Override
    public int indexOf(Object o) {
        return intArrToList(get()).indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return intArrToList(get()).lastIndexOf(o);
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex) {
        int[] r = new int[toIndex-fromIndex];
        int t=0;
        int[] s = get();
        for(int i = fromIndex; i<toIndex;i++){
            r[t++] = s[i];
        }
        return new NBTTagIntArray(r);
    }

    @Override
    public boolean add(Number value) {
        int[] array = get();
        List<Integer> list = new LinkedList<Integer>();
        for (int b : array) list.add(b);
        list.add(value.intValue());
        int[] result = new int[list.size()];
        int t = 0;
        for (int b : list) result[t++] = b;
        set(result);
        return false;
    }

    @Override
    public boolean remove(Object o) {
        List<Integer> ints = intArrToList(get());
        boolean result = ints.remove(o);
        if(result){
            set(listToIntArr(ints));
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        List<Integer> ints = intArrToList(get());
        boolean result = ints.removeAll(c);
        if(result){
            set(listToIntArr(ints));
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<Integer> ints = intArrToList(get());
        boolean result = ints.retainAll(c);
        if(result){
            set(listToIntArr(ints));
        }
        return result;
    }

    @Override
    public void clear() {
        set(new int[0]);
    }

    @Override
    public String toString() {
        return Arrays.toString(get());
    }

    @Override
    public byte getTypeId() {
        return 11;
    }



    public static ArrayList<Integer> intArrToList(int[] in) {
        ArrayList<Integer> temp = new ArrayList<Integer>(in.length);
        for (int anIn : in) temp.add(anIn);
        return temp;
    }

    public static int[] listToIntArr(Collection<Integer> in) {
        int[] temp = new int[in.size()];
        int i=0; for (Integer anIn : in) temp[i++] = anIn;
        return temp;
    }

}
