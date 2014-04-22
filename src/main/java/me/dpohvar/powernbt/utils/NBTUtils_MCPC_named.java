package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.exception.NBTReadException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

final class NBTUtils_MCPC_named extends NBTUtils {

    RefClass class_NBTBase = getRefClass("{NBTBase}, {nm}.nbt.NBTBase");
    RefClass class_NBTTagByte = getRefClass("{NBTTagByte}, {nm}.nbt.NBTTagByte");
    RefClass class_NBTTagShort = getRefClass("{NBTTagShort}, {nm}.nbt.NBTTagShort");
    RefClass class_NBTTagInt = getRefClass("{NBTTagInt}, {nm}.nbt.NBTTagInt");
    RefClass class_NBTTagLong = getRefClass("{NBTTagLong}, {nm}.nbt.NBTTagLong");
    RefClass class_NBTTagFloat = getRefClass("{NBTTagFloat}, {nm}.nbt.NBTTagFloat");
    RefClass class_NBTTagDouble = getRefClass("{NBTTagDouble}, {nm}.nbt.NBTTagDouble");
    RefClass class_NBTTagString = getRefClass("{NBTTagString}, {nm}.nbt.NBTTagString");
    RefClass class_NBTTagByteArray = getRefClass("{NBTTagByteArray}, {nm}.nbt.NBTTagByteArray");
    RefClass class_NBTTagIntArray = getRefClass("{NBTTagIntArray}, {nm}.nbt.NBTTagIntArray");
    RefClass class_NBTTagList = getRefClass("{NBTTagList}, {nm}.nbt.NBTTagList");
    RefClass class_NBTTagCompound = getRefClass("{NBTTagCompound}, {nm}.nbt.NBTTagCompound");

    RefConstructor con_NBTagByte = class_NBTTagByte.getConstructor(String.class, byte.class);
    RefConstructor con_NBTagShort = class_NBTTagShort.getConstructor(String.class, short.class);
    RefConstructor con_NBTagInt =  class_NBTTagInt.getConstructor(String.class, int.class);
    RefConstructor con_NBTagLong = class_NBTTagLong.getConstructor(String.class, long.class);
    RefConstructor con_NBTagFloat = class_NBTTagFloat.getConstructor(String.class, float.class);
    RefConstructor con_NBTagDouble = class_NBTTagDouble.getConstructor(String.class, double.class);
    RefConstructor con_NBTagString = class_NBTTagString.getConstructor(String.class, String.class);
    RefConstructor con_NBTagByteArray = class_NBTTagByteArray.getConstructor(String.class, byte[].class);
    RefConstructor con_NBTagIntArray = class_NBTTagIntArray.getConstructor(String.class, int[].class);
    RefConstructor con_NBTagCompound = class_NBTTagCompound.getConstructor(String.class);
    RefConstructor con_NBTagList = class_NBTTagList.getConstructor(String.class);

    RefField field_NBTagByte_data = class_NBTTagByte.findField(byte.class);
    RefField field_NBTagShort_data = class_NBTTagShort.findField(short.class);
    RefField field_NBTagInt_data = class_NBTTagInt.findField(int.class);
    RefField field_NBTagLong_data = class_NBTTagLong.findField(long.class);
    RefField field_NBTagFloat_data = class_NBTTagFloat.findField(float.class);
    RefField field_NBTagDouble_data = class_NBTTagDouble.findField(double.class);
    RefField field_NBTagString_data = class_NBTTagString.findField(String.class);
    RefField field_NBTagByteArray_data = class_NBTTagByteArray.findField(byte[].class);
    RefField field_NBTagIntArray_data = class_NBTTagIntArray.findField(int[].class);
    RefField field_NBTagCompound_map = class_NBTTagCompound.findField(Map.class);
    RefField field_NBTagList_list = class_NBTTagList.findField(List.class);
    RefField field_NBTagList_byte = class_NBTTagList.findField(byte.class);
    RefField field_NBTBase_name = class_NBTBase.findField(String.class);

    RefMethod met_NBTBase_getTypeId = class_NBTBase.findMethodByReturnType(byte.class);
    RefMethod met_NBTBase_clone = class_NBTBase.findMethodByReturnType(class_NBTBase);
    RefMethod met_NBTBase_createTag = class_NBTBase.findMethodByParams(byte.class, String.class);
    RefMethod met_NBTBase_write = class_NBTBase.findMethodByParams(DataOutput.class);

    RefMethod met_NBTBase_load = null;
    boolean loadWithRestrict;
    {
        try{
            met_NBTBase_load = class_NBTBase.findMethodByParams(DataInput.class, int.class);
            loadWithRestrict = true; // minecraft 1.6+
        } catch (Exception e){
            met_NBTBase_load = class_NBTBase.findMethodByParams(DataInput.class);
            loadWithRestrict = false; // minecraft 1.5-
        }

    }

    @Override
    public Object createTagByte(Byte a) {
        return con_NBTagByte.create("", a);
    }

    @Override
    public Object createTagShort(Short a) {
        return con_NBTagShort.create("", a);
    }

    @Override
    public Object createTagInt(Integer a) {
        return con_NBTagInt.create("", a);
    }

    @Override
    public Object createTagLong(Long a) {
        return con_NBTagLong.create("", a);
    }

    @Override
    public Object createTagFloat(Float a) {
        return con_NBTagFloat.create("", a);
    }

    @Override
    public Object createTagDouble(Double a) {
        return con_NBTagDouble.create("", a);
    }

    @Override
    public Object createTagString(String a) {
        return con_NBTagString.create("", a);
    }

    @Override
    public Object createTagByteArray(byte[] a) {
        return con_NBTagByteArray.create("", a);
    }

    @Override
    public Object createTagIntArray(int[] a) {
        return con_NBTagIntArray.create("", a);
    }

    @Override
    public Object getValue(Object tag) throws NBTReadException {
        if (tag==null) return null;
        if (! class_NBTBase.isInstance(tag)) throw new NBTReadException(tag);
        switch (getTagType(tag)){
            case 1: return field_NBTagByte_data.of(tag).get();
            case 2: return field_NBTagShort_data.of(tag).get();
            case 3: return field_NBTagInt_data.of(tag).get();
            case 4: return field_NBTagLong_data.of(tag).get();
            case 5: return field_NBTagFloat_data.of(tag).get();
            case 6: return field_NBTagDouble_data.of(tag).get();
            case 7: return field_NBTagByteArray_data.of(tag).get();
            case 8: return field_NBTagString_data.of(tag).get();
            case 9: return NBTList.forNBT(tag);
            case 10: return NBTCompound.forNBT(tag);
            case 11: return field_NBTagIntArray_data.of(tag).get();
            default: throw new RuntimeException("unexpected tag: "+tag.getClass());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setRawValue(Object tag, Object value) throws NBTReadException {
        if (! class_NBTBase.isInstance(tag)) throw new NBTReadException(tag);
        switch (getTagType(tag)){
            case 1: field_NBTagByte_data.of(tag).set(value); break;
            case 2: field_NBTagShort_data.of(tag).set(value); break;
            case 3: field_NBTagInt_data.of(tag).set(value); break;
            case 4: field_NBTagLong_data.of(tag).set(value); break;
            case 5: field_NBTagFloat_data.of(tag).set(value); break;
            case 6: field_NBTagDouble_data.of(tag).set(value); break;
            case 7: field_NBTagByteArray_data.of(tag).set(value); break;
            case 8: field_NBTagString_data.of(tag).set(value); break;
            case 9: NBTList list = NBTList.forNBT(tag);
                list.clear();
                list.addAll((Collection)value);
                break;
            case 10: NBTCompound compound = NBTCompound.forNBT(tag);
                compound.clear();
                compound.putAll((Map)value);
                break;
            case 11: field_NBTagIntArray_data.of(tag).set(value); break;
            default: throw new RuntimeException("unexpected tag: "+tag.getClass());
        }
    }

    @Override
    public byte getTagType(Object tag) throws NBTReadException{
        if (! class_NBTBase.isInstance(tag)) throw new NBTReadException(tag);
        return (Byte) met_NBTBase_getTypeId.of(tag).call();
    }

    @Override
    public Object createTagOfType(byte type) {
        return met_NBTBase_createTag.call(type, "");
    }

    @Override
    public Object cloneTag(Object tag) {
        return met_NBTBase_clone.of(tag).call();
    }

    @Override
    public Object createTagCompound() {
        return con_NBTagCompound.create("");
    }

    @Override
    public Object createTagList() {
        return con_NBTagList.create("");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getHandleMap(Object nbtTagCompound) {
        return (Map<String, Object>) field_NBTagCompound_map.of(nbtTagCompound).get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> getHandleList(Object nbtTagList) {
        return (List<Object>) field_NBTagList_list.of(nbtTagList).get();
    }

    @Override
    public byte getNBTTagListType(Object nbtTagList) {
        return (Byte) field_NBTagList_byte.of(nbtTagList).get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setNBTTagListType(Object nbtTagList, byte type) {
        field_NBTagList_byte.of(nbtTagList).set(type);
    }

    @Override
    public boolean isNBTTag(Object tag) {
        return class_NBTBase.isInstance(tag);
    }

    @Override
    public void readInputToTag(DataInput input, Object tag) throws IOException {
    }

    @Override
    public void writeTagDataToOutput(DataOutput output, Object tag) throws IOException {
        if (loadWithRestrict) {
            met_NBTBase_write.of(tag).call(output,0);
        } else {
            met_NBTBase_write.of(tag).call(output);
        }
    }

    @Override
    public void seTagName(Object tag, String name) {
        field_NBTBase_name.of(tag).set(name);
    }

    @Override
    public String getTagName(Object tag) {
        return (String) field_NBTBase_name.of(tag).get();
    }

}
