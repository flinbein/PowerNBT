package me.dpohvar.powernbt.nbt;

import java.lang.reflect.Field;

import static me.dpohvar.powernbt.utils.StaticValues.classNBTBase;
import static me.dpohvar.powernbt.utils.VersionFix.callMethod;

/**
 * 14.01.13 17:53
 *
 * @author DPOH-VAR
 */
public abstract class NBTBase {

    static Field fieldName;

    static {
        try {
            fieldName = classNBTBase.getDeclaredField("name");
            fieldName.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    protected final Object handle;

    NBTBase(Object handle) {
        this.handle = handle;
    }

    public Object getHandle() {
        return handle;
    }

    public String getName() {
        try {
            return (String) fieldName.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setName(String name) {
        try {
            fieldName.set(handle, name);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public NBTBase getDefault() {
        return getDefault(getTypeId());
    }

    public static NBTBase wrap(Object handle) {
        byte b = 0;
        if (classNBTBase.isInstance(handle)) b = (Byte) callMethod(handle, "getTypeId", new Class[0]);
        switch (b) {
            case 1:
                return new NBTTagByte(true, handle);
            case 2:
                return new NBTTagShort(true, handle);
            case 3:
                return new NBTTagInt(true, handle);
            case 4:
                return new NBTTagLong(true, handle);
            case 5:
                return new NBTTagFloat(true, handle);
            case 6:
                return new NBTTagDouble(true, handle);
            case 7:
                return new NBTTagByteArray(true, handle);
            case 8:
                return new NBTTagString(true, handle);
            case 9:
                return new NBTTagList(true, handle);
            case 10:
                return new NBTTagCompound(true, handle);
            case 11:
                return new NBTTagIntArray(true, handle);
            default:
                return null;
        }
    }

    public static NBTBase getDefault(byte type) {
        switch (type) {
            case 1:
                return new NBTTagByte();
            case 2:
                return new NBTTagShort();
            case 3:
                return new NBTTagInt();
            case 4:
                return new NBTTagLong();
            case 5:
                return new NBTTagFloat();
            case 6:
                return new NBTTagDouble();
            case 7:
                return new NBTTagByteArray();
            case 8:
                return new NBTTagString();
            case 9:
                return new NBTTagList();
            case 10:
                return new NBTTagCompound();
            case 11:
                return new NBTTagIntArray();
            default:
                return null;
        }
    }

    public abstract byte getTypeId();

    public abstract NBTBase clone();

    public static NBTBase getByValue(Object o) {
        if (o instanceof NBTBase) return ((NBTBase) o).clone();
        if (classNBTBase.isInstance(o)) return wrap(o);
        if (o instanceof Byte) return new NBTTagByte((Byte) o);
        if (o instanceof Short) return new NBTTagShort((Short) o);
        if (o instanceof Integer) return new NBTTagInt((Integer) o);
        if (o instanceof Long) return new NBTTagLong((Long) o);
        if (o instanceof Float) return new NBTTagFloat((Float) o);
        if (o instanceof Double) return new NBTTagDouble((Double) o);
        if (o instanceof byte[]) return new NBTTagByteArray((byte[]) o);
        if (o instanceof String) return new NBTTagString((String) o);
        if (o instanceof int[]) return new NBTTagIntArray((int[]) o);
        throw new IllegalArgumentException();
    }

}












