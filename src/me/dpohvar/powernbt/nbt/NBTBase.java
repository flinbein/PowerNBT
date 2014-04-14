package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 14.01.13 17:53
 *
 * @author DPOH-VAR
 */
public abstract class NBTBase {

    public static final Class class_NBTBase = Reflections.getClass("{nms}.NBTBase","net.minecraft.nbt.NBTBase");
    protected static Method methodRead;
    private static Method methodGetTypeId = Reflections.getMethodByTypes(class_NBTBase,byte.class);
    private static Method methodWrite = Reflections.getMethodByTypes(class_NBTBase, void.class, java.io.DataOutput.class);
    private static Method methodClone = Reflections.getMethodByTypes(class_NBTBase, NBTBase.class_NBTBase);
    private static boolean useInt = false;

    static {
        try {
            methodRead = Reflections.getMethodByTypes(class_NBTBase, void.class, java.io.DataInput.class);
        } catch (Exception e) {
            useInt = true;
            methodRead = Reflections.getMethodByTypes(class_NBTBase, void.class, java.io.DataInput.class, int.class);
        }
    }

    private NBTContainer container;
    private boolean customMethod;
    private NBTBase rootTag;

    final public void update(){
        if(container != null && rootTag!= null){
            if(customMethod) container.writeCustomTag(rootTag);
            else container.writeTag(rootTag);
        }
    }

    final public void read(java.io.DataInput input) {
        if (useInt) Reflections.invoke(methodRead,handle, input, 0);
        else Reflections.invoke(methodRead,handle, input);
    }

    final public void write(java.io.DataOutput output) {
        Reflections.invoke(methodWrite,handle,output);
    }

    final public byte[] toBytes(){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(buffer);
        write(out);
        return buffer.toByteArray();
    }

    final public void fromBytes(byte[] source){
        ByteArrayInputStream buffer = new ByteArrayInputStream(source);
        DataInputStream in = new DataInputStream(buffer);
        read(in);
    }

    @Override
    abstract public String toString();

    @Override
    abstract public int hashCode();

    final protected Object handle;

    NBTBase(Object handle) {
        this.handle = handle;
    }

    public Object getHandle() {
        return handle;
    }

    @Deprecated
    public String getName() {
        return "";
    }

    @Deprecated
    public void setName(String name) {
       throw new UnsupportedOperationException("NBTBase has no name!");
    }

    NBTBase getDefault() {
        return getDefault(getTypeId());
    }

    protected static Object createHandle(Constructor<?> constructor) {
        try {
            if (constructor.getParameterTypes().length == 0)
                return constructor.newInstance();
            else
                return constructor.newInstance("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static NBTBase wrap(Object handle) {
        if(handle==null) return null;
        byte b = 0;
        if (class_NBTBase.isInstance(handle)){
            try {
                b= (Byte) methodGetTypeId.invoke(handle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        try {
            Object h = methodClone.invoke(handle);
            return wrap(h);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object cloneHandle(Object handle) {
        try {
            return methodClone.invoke(handle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object cloneHandle() {
        return cloneHandle(handle);
    }

    public static NBTBase getByValue(Object o) {
        if (o instanceof NBTBase) return (NBTBase) o;
        if (class_NBTBase.isInstance(o)) return wrap(o);
        if (o instanceof Byte) return new NBTTagByte((Byte) o);
        if (o instanceof Short) return new NBTTagShort((Short) o);
        if (o instanceof Integer) return new NBTTagInt((Integer) o);
        if (o instanceof Long) return new NBTTagLong((Long) o);
        if (o instanceof Float) return new NBTTagFloat((Float) o);
        if (o instanceof Double) return new NBTTagDouble((Double) o);
        if (o instanceof byte[]) return new NBTTagByteArray((byte[]) o);
        if (o instanceof String) return new NBTTagString((String) o);
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












