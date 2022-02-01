package me.dpohvar.powernbt.utils.query;

import me.dpohvar.powernbt.api.NBTManagerUtils;
import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.nbt.NBTType;
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public record RangeSelector(Integer start, Integer end) implements QSelector {

    @Override
    public Object get(Object current, boolean useDefault) throws NBTTagNotFound {
        if (useDefault && current == null) return null;
        if (current instanceof String string) {
            int a = fixedIndex(start, string.length());
            int b = fixedIndex(end, string.length());
            boolean reverse = false;
            if (a == b) return "";
            if (a > b) {
                reverse = true;
                int c = a; a = b; b = c;
            }
            String result = string.substring(a, b);
            if (!reverse) return result;
            return new StringBuilder(result).reverse().toString();
        }
        if (current instanceof Collection col) {
            List<Object> list = IntegerSelector.cloneCollection(col);
            return getSubList(list);
        }
        Object arrayResult = NBTManagerUtils.mapArray(current, this::getSubList);
        if (arrayResult != null) return arrayResult;
        throw new NBTTagNotFound(current, this.toString());
    }

    private List<?> getSubList(List<?> list){
        int a = fixedIndex(start, list.size());
        int b = fixedIndex(end, list.size());
        boolean reverse = false;
        if (a > b) {
            reverse = true;
            int c = a; a = b; b = c;
        }
        List<?> result = list.subList(a, b);
        if (reverse) Collections.reverse(result);
        return result;
    }

    private boolean clearRange(List<?> col){
        int a = fixedIndex(start, col.size());
        int b = fixedIndex(end, col.size());
        if (a == b) return false;
        if (a > b) {int c = a; a = b; b = c;}
        col.subList(a, b).clear();
        return true;
    }

    public int fixedIndex(Integer index, int size){
        if (index == null) return size;
        return index < 0 ? size + index : index;
    }

    @Override
    public Object delete(Object current) throws NBTTagNotFound {
        if (current instanceof String string) {
            int a = fixedIndex(start, string.length());
            int b = fixedIndex(end, string.length());
            if (a == b) return string;
            if (a > b) { int c = a; a = b; b = c; }
            return string.substring(0, a) + string.substring(b);
        }
        if (current instanceof Collection col){
            List<Object> list = IntegerSelector.cloneCollection(col);
            boolean removed = this.clearRange(list);
            if (removed) return list;
            return current;
        }
        Object arrayResult = NBTManagerUtils.modifyArray(current, this::clearRange);
        if (arrayResult != null) return arrayResult;
        throw new NBTTagNotFound(current, this.toString());
    }

    private void insertSubList(List<Object> list, Object valueToInsert){
        int a = fixedIndex(start, list.size());
        int b = fixedIndex(end, list.size());
        boolean reverse = false;
        if (a > b) {
            reverse = true;
            int c = a; a = b; b = c;
        }
        Object[] objectsToInsert = NBTManagerUtils.convertToObjectArrayOrNull(valueToInsert);
        if (objectsToInsert == null) objectsToInsert = new Object[]{valueToInsert};
        if (reverse) ArrayUtils.reverse(objectsToInsert);
        List<Object> result = list.subList(a, b);
        result.clear();
        result.addAll(Arrays.asList(objectsToInsert));
    }

    @Override
    public Object set(Object current, Object value, boolean createDir) throws NBTTagNotFound {
        if (current instanceof String s) {
            int a = fixedIndex(start, s.length());
            int b = fixedIndex(end, s.length());
            boolean reverse = false;
            if (a > b) {
                reverse = true;
                int c = a; a = b; b = c;
            }
            String before = s.substring(0, a);
            String after = s.substring(b);
            String pasteValue = (String) NBTManagerUtils.convertValue(value, NBTType.STRING.type);
            if (reverse) pasteValue = new StringBuilder(pasteValue).reverse().toString();
            return before + pasteValue + after;
        }
        if (current instanceof Collection col){
            List<Object> list = IntegerSelector.cloneCollection(col);
            insertSubList(list, value);
            return list;
        }
        Object arrayResult = NBTManagerUtils.modifyArray(current, list -> insertSubList(list, value));
        if (arrayResult != null) return arrayResult;
        throw new NBTTagNotFound(current, this.toString());
    }


    @Override
    public String toString() {
        String a = start == null ? "" : start.toString();
        String b = end == null ? "" : end.toString();
        return "["+a+".."+b+"]";
    }
}
