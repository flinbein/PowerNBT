package me.dpohvar.powernbt.nbt;

import java.util.ArrayList;
import java.util.List;

public class NBTContainerValue extends NBTContainer<Object> {

    private Object base;
    private final String selectorTag;

    public NBTContainerValue(Object base) {
        this(base, null);
    }

    public NBTContainerValue(Object base, String selectorTag) {
        super(null);
        this.base = base;
        this.selectorTag = selectorTag;
    }

    @Override
    public String getSelector() {
        if (selectorTag != null) return selectorTag;
        return NBTContainer.parseValueToSelector(base);
    }

    @Override
    public Object getObject() {
        return base;
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<>();
    }

    @Override
    public Object readTag() {
        return this.base;
    }

    @Override
    public void writeTag(Object base) {
        this.base = base;
    }

    @Override
    public void eraseTag() {
        this.base = null;
    }

    @Override
    protected Class<Object> getContainerClass() {
        return Object.class;
    }

    @Override
    public String toString(){
        return base == null ? "null" : base.getClass().getSimpleName();
    }

    @Override
    public boolean isObjectReadonly(){
        return true;
    }
}
