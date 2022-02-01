package me.dpohvar.powernbt.utils.query;

import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.api.NBTManagerUtils;
import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.nbt.NBTType;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface IntegerSelector extends QSelector {

    @Override
    default Object get(Object current, boolean useDefault) throws NBTTagNotFound {
        if (useDefault && current == null) return null;
        if (current instanceof List<?> list) {
            int index = indexToGet(list.size());
            if (index >= list.size()) {
                if (useDefault) return null;
                throw new NBTTagNotFound(current, this.toString());
            }
            return list.get(index);
        }
        if (current instanceof Collection<?> col) {
            int index = indexToGet(col.size());
            if (index >= col.size()) {
                if (useDefault) return null;
                throw new NBTTagNotFound(current, this.toString());
            }
            return col.toArray()[index];
        }
        if (current instanceof String s) {
            int index = indexToGet(s.length());
            if (index >= s.length()) {
                if (useDefault) return "";
                throw new NBTTagNotFound(current, this.toString());
            }
            return s.substring(index, index+1);
        }
        Object[] objects = NBTManagerUtils.convertToObjectArrayOrNull(current);
        if (objects != null) {
            int index = indexToGet(objects.length);
            if (index >= objects.length) {
                if (useDefault) return "";
                throw new NBTTagNotFound(current, this.toString());
            }
            return objects[index];
        }
        throw new NBTTagNotFound(current, this.toString());
    }

    int indexToGet(int size);
    int indexToSet(int size);
    int indexToDelete(int size);

    @Override
    default Object delete(Object current) throws NBTTagNotFound {
        if (current instanceof Collection<?> col) {
            List<?> list = cloneCollection(col);
            list.remove(indexToDelete(list.size()));
            return list;
        }
        if (current instanceof String s) {
            int index = indexToDelete(s.length());
            return s.substring(0, index) + s.substring(index+1);
        }
        Object[] array = NBTManagerUtils.convertToObjectArrayOrNull(current);
        if (array != null) {
            return NBTManagerUtils.modifyArray(array, list -> list.remove(indexToDelete(array.length)));
        }
        throw new NBTTagNotFound(current, this.toString());
    }

    @Override
    default Object set(Object current, Object value, boolean createDir) throws NBTTagNotFound {
        if (current == null && createDir) current = new ArrayList<>();
        if (current instanceof Collection<?> col) {
            List<Object> resultList = cloneCollection(col);
            putToFreeIndex(resultList, indexToSet(col.size()), value);
            return resultList;
        }
        if (current instanceof String s) {
            int length = s.length();
            int index = indexToSet(length);
            String pasteValue = (String) NBTManagerUtils.convertValue(value, NBTType.STRING.type);
            if (index < length) {
                return s.substring(0, index) + pasteValue + s.substring(index+1);
            }
            return s + StringUtils.repeat(" ",length - index) + pasteValue;
        }
        Object array = NBTManagerUtils.modifyArray(current, list -> putToFreeIndex(list, indexToSet(list.size()), value));
        if (array != null) return array;
        throw new NBTTagNotFound(current, this.toString());
    }

    private static void putToFreeIndex(List<Object> list, int index, Object value) {
        int selectIndex = index;
        if (selectIndex < 0) selectIndex = list.size() - selectIndex;
        if (selectIndex < 0) throw new IndexOutOfBoundsException(selectIndex);
        if (selectIndex < list.size()) {
            list.set(selectIndex, value);
            return;
        }
        int addDefaultCount = selectIndex - list.size();
        if (addDefaultCount > 0) {
            Object defaultValue = null;
            if (list instanceof NBTList nbtList) {
                NBTType nbtType = NBTType.fromByte(nbtList.getType());
                if (nbtType != null) defaultValue = nbtType.getDefaultValue();
            }
            while (addDefaultCount --> 0) list.add(defaultValue);
        }
        list.add(value);
    }

    public static List<Object> cloneCollection(Collection<?> col){
        if (col instanceof NBTList c) return c.clone();
        return new ArrayList<>(col);
    }
}
