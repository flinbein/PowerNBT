package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.exception.NBTConvertException;
import me.dpohvar.powernbt.exception.NBTReadException;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static me.dpohvar.powernbt.utils.ChunkUtils.chunkUtils;
import static me.dpohvar.powernbt.utils.EntityUtils.entityUtils;
import static me.dpohvar.powernbt.utils.ItemStackUtils.itemStackUtils;
import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

public abstract class NBTUtils {

    public static final NBTUtils nbtUtils;

    static {
        boolean raw = false;
        if (isForge()) { // Forge classpath
            try{ // to get raw constructor
                getRefClass("{nm}.nbt.NBTTagByte, {NBTTagByte}").getConstructor(byte.class);
                raw = true; // on success
            } catch (Exception ignored) {}
            nbtUtils = raw ? new NBTUtils_MCPC_raw() : new NBTUtils_MCPC_named();
        } else { // NMS classpath
            try{ // to get raw constructor
                getRefClass("{nms}.NBTTagByte, {NBTTagByte}").getConstructor(byte.class);
                raw = true; // on success
            } catch (Exception ignored) {}
            nbtUtils = raw ? new NBTUtils_Bukkit_raw() : new NBTUtils_Bukkit_named();
        }
    }

    public Object createTag(Object javaObject) throws NBTConvertException {
        if (javaObject == null) return null;
        if (javaObject instanceof NBTCompound) return cloneTag(((NBTCompound)javaObject).getHandle());
        if (javaObject instanceof NBTList) return cloneTag(((NBTList)javaObject).getHandle());
        if (javaObject instanceof Boolean) {
            if ((Boolean)javaObject) return createTagByte((byte) 1);
            else return createTagByte((byte)0);
        }
        if (javaObject instanceof Collection) return new NBTList((Collection) javaObject).getHandle();
        if (javaObject instanceof Short) return createTagShort((Short) javaObject);
        if (javaObject instanceof CharSequence) return createTagString(javaObject.toString());
        if (javaObject instanceof Byte) return createTagByte((Byte) javaObject);
        if (javaObject instanceof Integer) return createTagInt((Integer) javaObject);
        if (javaObject instanceof Float) return createTagFloat((Float) javaObject);
        if (javaObject instanceof Double) return createTagDouble((Double) javaObject);
        if (javaObject instanceof Long) return createTagLong((Long) javaObject);
        if (javaObject instanceof BigDecimal) return createTagDouble(((BigDecimal) javaObject).doubleValue());
        if (javaObject instanceof BigInteger) return createTagLong(((BigInteger) javaObject).longValue());
        if (javaObject instanceof UUID) return createTagString(javaObject.toString());
        if (javaObject instanceof byte[]) return createTagByteArray((byte[]) javaObject);
        if (javaObject instanceof int[]) return createTagIntArray((int[]) javaObject);
        if (javaObject instanceof Map) return new NBTCompound((Map) javaObject).getHandle();
        if (javaObject instanceof Object[]) return new NBTList((Arrays.asList((Object[]) javaObject))).getHandle();
        if (javaObject instanceof Chunk) {
            Object compound = createTagCompound();
            chunkUtils.readChunk((Chunk)javaObject, compound);
            return compound;
        }
        if (javaObject instanceof Entity) {
            Object compound = createTagCompound();
            entityUtils.readEntity((Entity)javaObject, compound);
            return compound;
        }
        if (javaObject instanceof ItemStack) {
            Object tag = itemStackUtils.getTag((ItemStack)javaObject);
            return (tag == null) ? null : cloneTag(tag);
        }
        if (javaObject instanceof Block) {
            Object compound = createTagCompound();
            NBTBlockUtils.nbtBlockUtils.readTag((Block)javaObject,compound);
            return compound;
        }
        throw new NBTConvertException(javaObject);
    }

    public Object createTag(Object value, byte type) throws NBTConvertException{
        return createTag(convertValue(value, type));
    }

    public Object convertValue(Object value, byte type) throws NBTConvertException{
        switch (type) {
            case 0: {
                if (value==null) return null;
                throw new NBTConvertException(value, type);
            }
            case 1: {
                if (value instanceof Number) return ((Number) value).byteValue();
                if (value instanceof CharSequence) return new Long((String) value).byteValue();
                throw new NBTConvertException(value, type);
            }
            case 2: {
                if (value instanceof Number) return ((Number) value).shortValue();
                if (value instanceof CharSequence) return new Long(value.toString()).shortValue();
                throw new NBTConvertException(value, type);
            }
            case 3: {
                if (value instanceof Number) return (((Number) value).intValue());
                if (value instanceof CharSequence) return (new Long(value.toString()).intValue());
                throw new NBTConvertException(value, type);
            }
            case 4: {
                if (value instanceof Number) return ((Number) value).longValue();
                if (value instanceof CharSequence) return Long.parseLong(value.toString());
                throw new NBTConvertException(value, type);
            }
            case 5: {
                if (value instanceof Number) return ((Number)value).floatValue();
                if (value instanceof CharSequence) return new Double(value.toString()).floatValue();
                throw new NBTConvertException(value, type);
            }
            case 6: {
                if (value instanceof Number) return ((Number)value).doubleValue();
                if (value instanceof CharSequence) return new Double(value.toString());
                throw new NBTConvertException(value, type);
            }
            case 7: {
                if (value instanceof byte[]) return value;
                if (value instanceof int[]) {
                    int[] values = (int[]) value;
                    byte[] temp = new byte[values.length];
                    int t=0; for(int i:values) temp[t++]=(byte)i;
                    return temp;
                }
                if (value instanceof Collection) {
                    Collection values = (Collection) value;
                    byte[] temp = new byte[values.size()];
                    int t=0; for(Object obj: values) {
                        byte val;
                        if (obj instanceof Number) val = ((Number)obj).byteValue();
                        else throw new NBTConvertException(value, type);
                        temp[t++]=val;
                    }
                    return temp;
                }
                if (value instanceof CharSequence) return (value.toString()).getBytes(PowerNBT.charset);
                throw new NBTConvertException(value, type);
            }
            case 8: {
                return value.toString();
            }
            case 9: {
                if (value instanceof Collection) return value;
                if (value instanceof Object[]) return value;
                if (value instanceof byte[]) {
                    ArrayList<Byte> list = new ArrayList<Byte>( ((byte[])value).length );
                    for (byte b: (byte[]) value) list.add(b);
                    return list;
                }
                if (value instanceof int[]) {
                    ArrayList<Integer> list = new ArrayList<Integer>( ((int[])value).length );
                    for (int t: (int[]) value) list.add(t);
                    return list;
                }
                throw new NBTConvertException(value, type);
            }
            case 10: {
                if (value instanceof Map) return value;
                throw new NBTConvertException(value, type);
            }
            case 11: {
                if (value instanceof int[]) return value;
                if (value instanceof byte[]) {
                    byte[] values = (byte[]) value;
                    int[] temp = new int[values.length];
                    int t=0; for(byte i:values) temp[t++]=i;
                    return temp;
                }
                if (value instanceof Collection) {
                    Collection values = (Collection) value;
                    int[] temp = new int[values.size()];
                    int t=0; for(Object obj: values) {
                        int val;
                        if (obj instanceof Number) val = ((Number)obj).intValue();
                        else throw new NBTConvertException(value, type);
                        temp[t++]=val;
                    }
                    return temp;
                }
                throw new NBTConvertException(value, type);
            }
            default: throw new NBTConvertException(value, type);
        }
    }

    public abstract Object createTagByte(Byte a);

    public abstract Object createTagShort(Short a);

    public abstract Object createTagInt(Integer a);

    public abstract Object createTagLong(Long a);

    public abstract Object createTagFloat(Float a);

    public abstract Object createTagDouble(Double a);

    public abstract Object createTagString(CharSequence a);

    public abstract Object createTagByteArray(byte[] a);

    public abstract Object createTagIntArray(int[] a);

    public abstract Object getValue(Object tag) throws NBTReadException;

    protected abstract void setRawValue(Object tag, Object value) throws NBTReadException;

    public void setValue(Object tag, Object value) throws NBTConvertException{
        setRawValue(tag, convertValue(value, getTagType(tag)) );
    }

    public abstract byte getTagType(Object tag) throws NBTReadException;

    public abstract Object createTagOfType(byte type);

    public abstract Object cloneTag(Object tag);

    public abstract Object createTagCompound();

    public abstract Object createTagList();

    public abstract Map<String,Object> getHandleMap(Object nbtTagCompound);

    public abstract List<Object> getHandleList(Object nbtTagList);

    public abstract byte getNBTTagListType(Object nbtTagList);

    public abstract void setNBTTagListType(Object nbtTagList, byte type);

    public abstract boolean isNBTTag(Object tag);

    public abstract void readInputToTag(java.io.DataInput input, Object tag) throws IOException;

    public abstract void writeTagDataToOutput(java.io.DataOutput output, Object tag) throws IOException;

    public void writeTagToOutput(java.io.DataOutput output, Object tag) throws IOException{
        output.writeByte(getTagType(tag));
        output.writeUTF(getTagName(tag));
        writeTagDataToOutput(output, tag);
    }

    public Object readTagOfType(java.io.DataInput input, byte type) throws IOException {
        Object tag = createTagOfType(type);
        readInputToTag(input, tag);
        return tag;
    }

    public Object readTag(java.io.DataInput input) throws IOException {
        byte type = input.readByte();
        input.readUTF();
        return readTagOfType(input, type);
    }

    public abstract void seTagName(Object tag, String name);

    public abstract String getTagName(Object tag);
}