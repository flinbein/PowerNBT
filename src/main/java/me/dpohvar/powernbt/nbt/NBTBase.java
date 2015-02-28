package me.dpohvar.powernbt.nbt;

import org.bukkit.Bukkit;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;


/**
 * 14.01.13 17:53
 *
 * @author DPOH-VAR
 */
public abstract class NBTBase {

    final protected Object handle;

    final public void read(java.io.DataInput input) throws IOException {
        nbtUtils.readInputToTag(input, handle);
    }

    final public void write(java.io.DataOutput output) throws IOException {
        nbtUtils.writeTagDataToOutput(output, handle);
    }

    final public byte[] toBytes(){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        try {
            write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toByteArray();
    }

    final public void fromBytes(byte[] source){
        ByteArrayInputStream buffer = new ByteArrayInputStream(source);
        DataInputStream in = new DataInputStream(buffer);
        try {
            read(in);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.ALL, "can not read NBT from bytes", e);
        }
    }

    @Override
    abstract public String toString();

    @Override
    abstract public int hashCode();

    NBTBase(Object handle) {
        this.handle = handle;
    }

    public Object getHandle() {
        return handle;
    }

    public String getName() {
        return nbtUtils.getTagName(handle);
    }

    @Deprecated
    public void setName(String name) {
       nbtUtils.seTagName(handle,name);
    }

    NBTBase getDefault() {
        return getDefault(getTypeId());
    }

    public static NBTBase wrap(Object handle) {
        if(handle==null) return null;
        byte b = nbtUtils.getTagType(handle);
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

    public final NBTType getType(){
        return NBTType.fromByte(getTypeId());
    }

    public NBTBase clone() {
        return wrap(nbtUtils.cloneTag(handle));
    }

    public static Object cloneHandle(Object handle) {
        return nbtUtils.cloneTag(handle);
    }

    public Object cloneHandle() {
        return cloneHandle(handle);
    }

    public static NBTBase getByValue(Object o) {
        if (o == null) return null;
        if (o instanceof NBTBase) return (NBTBase) o;
        if (nbtUtils.isNBTTag(o)) return wrap(o);
        if (o instanceof Byte) return new NBTTagByte((Byte) o);
        if (o instanceof Short) return new NBTTagShort((Short) o);
        if (o instanceof Integer) return new NBTTagInt((Integer) o);
        if (o instanceof Long) return new NBTTagLong((Long) o);
        if (o instanceof Float) return new NBTTagFloat((Float) o);
        if (o instanceof Double) return new NBTTagDouble((Double) o);
        if (o instanceof byte[]) return new NBTTagByteArray((byte[]) o);
        if (o instanceof CharSequence) return new NBTTagString(o.toString());
        if (o instanceof int[]) return new NBTTagIntArray((int[]) o);
        if (o instanceof Map){
            NBTTagCompound tag = new NBTTagCompound();
            for(Map.Entry e:((Map<?,?>)o).entrySet()){
                tag.putToHandle(e.toString(), getByValue(e.getValue()));
            }
            return tag;
        }
        if (o instanceof Object[]) o = Arrays.asList((Object[])o);
        if (o instanceof List) {
            NBTTagList tag = new NBTTagList();
            for(Object t:(List)o){
                tag.add_b(getByValue(t));
            }
            return tag;
        }
        throw new IllegalArgumentException();
    }

}












