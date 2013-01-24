package me.dpohvar.powernbt.nbt;

import java.util.List;

/**
 * 15.01.13 4:39
 * @author DPOH-VAR
 */
public abstract class NBTTagNumericArray extends NBTTagDatable {
    NBTTagNumericArray(Object handle) {
        super(handle);
    }

    abstract public int size();
    abstract public Number get(int i);
    abstract public List<Number> asList();
    abstract public void setList(List<Number> list);
    abstract public void set(int i, Number value);
    abstract public boolean remove(int i);
    abstract public void add(Number value);
    public void addAll(NBTTagNumericArray array){
        for(Number n:array.asList()) add(n);
    }
    public void add(int pos,Number value){
        List<Number> list = asList();
        while(list.size()<pos){
            list.add(0);
        }
        if(list.size()==pos){
            list.add(value);
        } else {
            list.add(pos,value);
        }
        setList(list);
    }
}
