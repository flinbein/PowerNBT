package me.dpohvar.powernbt.nbt;

import java.util.*;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;


/**
 * 14.01.13 17:54
 *
 * @author DPOH-VAR
 */
public class NBTTagByteArray extends NBTTagNumericArray<Byte> {
    public static final byte typeId = 7;

    public NBTTagByteArray() {
        this(new byte[0]);
    }

    public NBTTagByteArray(String ignored) {
        this(new byte[0]);
    }

    public NBTTagByteArray(byte[] b) {
        super(nbtUtils.createTag(b,typeId));
    }

    public NBTTagByteArray(String ignored, byte[] b) {
        this(b);
    }

    public NBTTagByteArray(boolean ignored, Object tag) {
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

    public byte[] get() {
        return (byte[]) super.get();
    }

    @Override
    public void set(Object value) {
        nbtUtils.setValue(handle, value);
    }

    public void set(byte[] value) {
        set((Object)value);
    }

    public int size() {
        return get().length;
    }

    @Override
    public boolean contains(Object o) {
        return byteArrToList(get()).contains(o);
    }

    public Byte get(int i) {
        byte[] array = get();
        if (i >= array.length) return null;
        return array[i];
    }

    public Byte set(int i, Number value) {
        Byte res = get(i);
        byte[] array = get();
        List<Byte> list = new LinkedList<Byte>();
        for (byte b : array) list.add(b);
        while (list.size() <= i) {
            list.add((byte) 0);
        }
        list.set(i, value.byteValue());
        byte[] result = new byte[list.size()];
        int t = 0;
        for (byte b : list) result[t++] = b;
        set(result);
        return res;
    }

    public Byte remove(int i) {
        Byte res = get(i);
        byte[] array = get();
        if (i < 0 || i >= array.length) return res;
        List<Byte> list = new LinkedList<Byte>();
        for (byte b : array) list.add(b);
        while (list.size() <= i) {
            list.add((byte) 0);
        }
        list.remove(i);
        byte[] result = new byte[list.size()];
        int t = 0;
        for (byte b : list) result[t++] = b;
        set(result);
        return res;
    }

    @Override
    public int indexOf(Object o) {
        return byteArrToList(get()).indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return byteArrToList(get()).lastIndexOf(o);
    }

    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        byte[] r = new byte[toIndex-fromIndex];
        int t=0;
        byte[] s = get();
        for(int i = fromIndex; i<toIndex;i++){
            r[t++] = s[i];
        }
        return new NBTTagByteArray(r);
    }

    @Override
    public boolean add(Number value) {
        byte[] array = get();
        List<Byte> list = new LinkedList<Byte>();
        for (byte b : array) list.add(b);
        list.add(value.byteValue());
        byte[] result = new byte[list.size()];
        int t = 0;
        for (byte b : list) result[t++] = b;
        set(result);
        return false;
    }

    @Override
    public boolean remove(Object o) {
        List<Byte> bytes = byteArrToList(get());
        boolean result = bytes.remove(o);
        if(result){
            set(listToByteArr(bytes));
        }
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        List<Byte> bytes = byteArrToList(get());
        boolean result = bytes.removeAll(c);
        if(result){
            set(listToByteArr(bytes));
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<Byte> bytes = byteArrToList(get());
        boolean result = bytes.retainAll(c);
        if(result){
            set(listToByteArr(bytes));
        }
        return result;
    }

    @Override
    public void clear() {
        set(new byte[0]);
    }

    @Override
    public String toString() {
        return Arrays.toString(get());
    }

    @Override
    public byte getTypeId() {
        return typeId;
    }



    public static ArrayList<Byte> byteArrToList(byte[] in) {
        ArrayList<Byte> temp = new ArrayList<Byte>(in.length);
        for (byte anIn : in) temp.add(anIn);
        return temp;
    }

    public static byte[] listToByteArr(Collection<Byte> in) {
        byte[] temp = new byte[in.size()];
        int i=0; for (Byte anIn : in) temp[i++] = anIn;
        return temp;
    }

}
