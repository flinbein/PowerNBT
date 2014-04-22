package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.exception.NBTConvertException;
import me.dpohvar.powernbt.exception.NBTReadException;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;

import java.io.IOException;
import java.util.*;

import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

/**
 * Created by DPOH-VAR on 14.01.14
 */
public abstract class NBTUtils {

    /**
     * static access to util
     */
    public static final NBTUtils nbtUtils;

    /**
     * select the util implementation
     */
    static {
        boolean raw = false;
        if (isForge()) { // Forge classpath
            try{ // to get raw constructor
                getRefClass("{NBTTagByte}, {nm}.nbt.NBTTagByte").getConstructor(byte.class);
                raw = true; // on success
            } catch (Exception ignored) {}
            nbtUtils = raw ? new NBTUtils_MCPC_raw() : new NBTUtils_MCPC_named();
        } else { // NMS classpath
            try{ // to get raw constructor
                getRefClass("{NBTTagByte}, {nms}.NBTTagByte").getConstructor(byte.class);
                raw = true; // on success
            } catch (Exception ignored) {}
            nbtUtils = raw ? new NBTUtils_Bukkit_raw() : new NBTUtils_Bukkit_named();
        }
    }

    /**
     * convert java object to NBT tag\n
     * Supports all primitive types, Map, Object[], Collection, byte[], int[]
     * @param javaObject java object
     * @return NBTBase tag
     * @throws me.dpohvar.powernbt.exception.NBTConvertException if object can not be converted to NBTBase
     */
    public Object createTag(Object javaObject) throws NBTConvertException {
        if (javaObject == null) return null;
        if (javaObject instanceof Boolean) {
            if ((Boolean)javaObject) return createTagByte((byte) 1);
            else return (byte)0;
        }
        if (javaObject instanceof Map) return new NBTCompound((Map) javaObject).getHandle();
        if (javaObject instanceof Collection) return new NBTList((List) javaObject).getHandle();
        if (javaObject instanceof Short) return createTagShort((Short) javaObject);
        if (javaObject instanceof String) return createTagString((String) javaObject);
        if (javaObject instanceof Byte) return createTagByte((Byte) javaObject);
        if (javaObject instanceof Integer) return createTagInt((Integer) javaObject);
        if (javaObject instanceof Float) return createTagFloat((Float) javaObject);
        if (javaObject instanceof Double) return createTagDouble((Double) javaObject);
        if (javaObject instanceof Long) return createTagLong((Long) javaObject);
        if (javaObject instanceof byte[]) return createTagByteArray((byte[]) javaObject);
        if (javaObject instanceof int[]) return createTagIntArray((int[]) javaObject);
        if (javaObject instanceof Object[]) return new NBTList((Arrays.asList((Object[]) javaObject))).getHandle();
        throw new NBTConvertException(javaObject);
    }

    /**
     * convert java object to NBT tag with special type\n
     * Supports all primitive types, Map, Object[], Collection, byte[], int[]
     * @param value java object
     * @param type nbt type
     * @return NBTBase tag
     * @throws me.dpohvar.powernbt.exception.NBTConvertException if object can not be converted to NBT tag
     */
    public Object createTag(Object value, byte type) throws NBTConvertException{
        return createTag(convertValue(value, type));
    }

    /**
     * convert java object to special type\n
     * Supports all primitive types, Map, Object[], Collection, byte[], int[]
     * @param value java object
     * @param type nbt type
     * @return NBTBase tag
     * @throws me.dpohvar.powernbt.exception.NBTConvertException if object can not be converted to NBT tag
     */
    public Object convertValue(Object value, byte type) throws NBTConvertException{
        switch (type) {
            case 0: {
                if (value==null) return null;
                throw new NBTConvertException(value, type);
            }
            case 1: {
                if (value instanceof Number) return ((Number) value).byteValue();
                if (value instanceof String) return new Long((String) value).byteValue();
                throw new NBTConvertException(value, type);
            }
            case 2: {
                if (value instanceof Number) return ((Number) value).shortValue();
                if (value instanceof String) return new Long((String) value).shortValue();
                throw new NBTConvertException(value, type);
            }
            case 3: {
                if (value instanceof Number) return (((Number) value).intValue());
                if (value instanceof String) return (new Long((String)value).intValue());
                throw new NBTConvertException(value, type);
            }
            case 4: {
                if (value instanceof Number) return ((Number) value).longValue();
                if (value instanceof String) return Long.parseLong((String)value);
                throw new NBTConvertException(value, type);
            }
            case 5: {
                if (value instanceof Number) return ((Number)value).floatValue();
                if (value instanceof String) return new Double((String)value).floatValue();
                throw new NBTConvertException(value, type);
            }
            case 6: {
                if (value instanceof Number) return ((Number)value).doubleValue();
                if (value instanceof String) return new Double((String)value);
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
                if (value instanceof String) return ((String)value).getBytes(PowerNBT.charset);
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

    /**
     * create NBTTagByte by value
     * @param a value
     * @return NBTTagByte
     */
    public abstract Object createTagByte(Byte a);

    /**
     * create NBTTagShort by value
     * @param a value
     * @return NBTTagShort
     */
    public abstract Object createTagShort(Short a);

    /**
     * create NBTTagInt by value
     * @param a value
     * @return NBTTagInt
     */
    public abstract Object createTagInt(Integer a);

    /**
     * create NBTTagLong by value
     * @param a value
     * @return NBTTagLong
     */
    public abstract Object createTagLong(Long a);

    /**
     * create NBTTagFloat by value
     * @param a value
     * @return NBTTagFloat
     */
    public abstract Object createTagFloat(Float a);

    /**
     * create NBTTagDouble by value
     * @param a value
     * @return NBTTagDouble
     */
    public abstract Object createTagDouble(Double a);

    /**
     * create NBTTagString by value
     * @param a value
     * @return NBTTagString
     */
    public abstract Object createTagString(String a);

    /**
     * create NBTTagByteArray by value
     * @param a value
     * @return NBTTagByteArray
     */
    public abstract Object createTagByteArray(byte[] a);

    /**
     * create NBTTagIntArray by value
     * @param a value
     * @return NBTTagIntArray
     */
    public abstract Object createTagIntArray(int[] a);

    /**
     * getByteArray raw value of NBTBase tag
     * @param tag tag
     * @return value
     * @throws me.dpohvar.powernbt.exception.NBTReadException if tag is not NBTBase
     */
    public abstract Object getValue(Object tag) throws NBTReadException;


    protected abstract void setRawValue(Object tag, Object value) throws NBTReadException;

    /**
     * set raw value of NBTBase tag
     * @param tag tag
     * @param value value
     * @throws me.dpohvar.powernbt.exception.NBTReadException if tag is not NBTBase
     */
    public void setValue(Object tag, Object value) throws NBTConvertException{
        setRawValue(tag, convertValue(value, getTagType(tag)) );
    }

    /**
     * getByteArray type of tag
     * @param tag tag
     * @return type of tag
     * @throws me.dpohvar.powernbt.exception.NBTReadException if tag is not NBTBase
     */
    public abstract byte getTagType(Object tag) throws NBTReadException;

    /**
     * create new empty NBT tag by type
     * @param type type of tag
     * @return NBTBase
     */
    public abstract Object createTagOfType(byte type);

    /**
     * clone NBT tag
     * @param tag tag
     * @return cloned tag
     */
    public abstract Object cloneTag(Object tag);

    /**
     * create new empty NBTTagCompound
     * @return NBTTagCompound
     */
    public abstract Object createTagCompound();

    /**
     * create new empty NBTTagList
     * @return NBTTagList
     */
    public abstract Object createTagList();

    /**
     * getByteArray handle map of NBTTagCompound
     * @return handle map
     */
    public abstract Map<String,Object> getHandleMap(Object nbtTagCompound);

    /**
     * getByteArray handle list of NBTTagCompound
     * @return handle list
     */
    public abstract List<Object> getHandleList(Object nbtTagList);

    /**
     * getByteArray type of contained elements in NBTTagList
     * @return type
     */
    public abstract byte getNBTTagListType(Object nbtTagList);

    /**
     * Set type of contained elements in NBTTagList. Unsafe!
     * @param nbtTagList NBTTagList
     * @param type type
     */
    public abstract void setNBTTagListType(Object nbtTagList, byte type);

    /**
     * check given object
     * @param tag object
     * @return true if object is NBT tag. False otherwise.
     */
    public abstract boolean isNBTTag(Object tag);

    //todo: documentation
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