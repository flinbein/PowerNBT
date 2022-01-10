package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.api.NBTBox;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.exception.NBTTagUnexpectedType;
import me.dpohvar.powernbt.nbt.NBTType;
import org.bukkit.Bukkit;

import java.util.*;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTQuery {

    public static final Object jsonSelector = new Object();
    public static final Float anyIndexSelector = Float.NaN;

    private final Object[] values; // String, Number (Integer or Float.NaN), jsonSelector
    private static final NBTManager nbt = NBTManager.getInstance();

    public List<Object> getValues() {
        return Arrays.asList(values);
    }

    public boolean isEmpty() {
        return values.length == 0;
    }

    public NBTQuery getParent() {
        if (isEmpty()) return null;
        List<Object> v = getValues();
        v.remove(v.size() - 1);
        return new NBTQuery(v);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Object node : values) {
            if (node == null) s.append("[]");
            else if (node == anyIndexSelector) s.append("[]");
            else if (node == jsonSelector) s.append("#");
            else if (node instanceof Number) s.append("[").append(node).append("]");
            else s.append(".").append(node);
        }
        if (s.toString().startsWith(".")) s = new StringBuilder(s.substring(1));
        if (s.length() == 0) return ".";
        return s.toString();
    }

    private enum ParseMode { DEFAULT, TEXT, INDEX }

    public static NBTQuery fromString(String string) {
        if (string == null || string.isEmpty()) return new NBTQuery();
        LinkedList<Object> tokens = new LinkedList<>();
        Queue<Character> chars = new LinkedList<>();
        StringBuilder buffer = new StringBuilder();
        for (char c : string.toCharArray()) chars.add(c);
        ParseMode mode = ParseMode.DEFAULT; // 0 = default text;  1 = text in ""; 2 = text in []
        tokenizer:
        while (true) {
            Character c = chars.poll();
            switch (mode) {
                case DEFAULT -> {
                    if (c == null) {
                        if (buffer.length() != 0) tokens.add(buffer.toString());
                        break tokenizer;
                    } else if (c == '.') {
                        if (buffer.length() != 0) {
                            tokens.add(buffer.toString());
                            buffer = new StringBuilder();
                        }
                    } else if (c == '#') {
                        if (buffer.length() != 0) {
                            tokens.add(buffer.toString());
                            buffer = new StringBuilder();
                        }
                        tokens.add(jsonSelector);
                    } else if (c == '\"') {
                        mode = ParseMode.TEXT;
                    } else if (c == '[') {
                        if (buffer.length() != 0) {
                            tokens.add(buffer.toString());
                            buffer = new StringBuilder();
                        }
                        mode = ParseMode.INDEX;
                    } else if (c == ']') {
                        throw new RuntimeException(plugin.translate("error_querynode", string));
                    } else {
                        buffer.append(c);
                    }
                }
                case TEXT -> {
                    if (c == null) {
                        throw new RuntimeException(plugin.translate("error_querynode", string));
                    }
                    if (c == '\\') {
                        buffer.append(c);
                        Character t = chars.poll();
                        if (t == null) throw new RuntimeException(plugin.translate("error_querynode", string));
                        buffer.append(t);
                    } else if (c == '"') {
                        if (buffer.length() != 0) {
                            tokens.add(StringParser.parse(buffer.toString()));
                            buffer = new StringBuilder();
                        }
                        mode = ParseMode.DEFAULT;
                    } else {
                        buffer.append(c);
                    }
                }
                case INDEX -> {
                    if (c == null) {
                        throw new RuntimeException(plugin.translate("error_querynode", string));
                    } else if (c == ']') {
                        String t = buffer.toString();
                        Number r = anyIndexSelector;
                        if (!t.isEmpty()) r = Integer.parseInt(t);
                        tokens.add(r);
                        buffer = new StringBuilder();
                        mode = ParseMode.DEFAULT;
                    } else if (c == '-') {
                        if (buffer.isEmpty()) buffer.append(c);
                        else throw new RuntimeException(plugin.translate("error_querynode", string));
                    } else if (c.toString().matches("[0-9]")) {
                        buffer.append(c);
                    } else {
                        throw new RuntimeException(plugin.translate("error_querynode", string));
                    }
                }
            }
        }
        return new NBTQuery(tokens.toArray());
    }

    public NBTQuery(Object... nodes) {
        for (Object node : nodes) {
            if (!(node instanceof String || node instanceof Integer || node == jsonSelector || node == anyIndexSelector)) {
                throw new RuntimeException("invalid node: " + node);
            }
        }
        this.values = nodes;
    }

    public NBTQuery(List<Object> nodes) {
        this(nodes.toArray());
    }

    public Queue<Object> getQueue() {
        return new LinkedList<>(Arrays.asList(this.values));
    }

    public Object remove(Object root) throws NBTTagNotFound {
        if (root == null) return null;
        return remove(root, getQueue());
    }

    private static Object remove(Object current, Queue<Object> queue) throws NBTTagNotFound {
        if (queue.isEmpty()) return current;
        Object qKey = queue.poll();
        if (queue.isEmpty()) { // request to delete qKey from current, return current or modified
            if (current instanceof Map<?,?> map && qKey instanceof String key) {
                if (!map.containsKey(key)) return map;
                Map<?,?> resultMap = cloneMap(map);
                resultMap.remove(key);
                return resultMap;
            }
            if (current instanceof String && qKey == jsonSelector) {
                return "";
            }
            if (qKey instanceof Number) {
                if (current instanceof Collection<?> col) {
                    List<?> list = cloneCollection(col);
                    int index = (qKey == anyIndexSelector) ? list.size() - 1 : (int) qKey;
                    list.remove(index);
                    return list;
                }
                Object[] array = NBTManager.convertToObjectArrayOrNull(current);
                if (array != null) {
                    int index = (qKey == anyIndexSelector) ? array.length - 1 : (int) qKey;
                    return NBTManager.modifyArray(array, list -> list.remove(index));
                }
            }
            throw new NBTTagNotFound(current, qKey);
        }

        // chain, queue is not empty
        if (current == null) throw new NBTTagNotFound(null, qKey);
        if (current instanceof Map<?,?> map && qKey instanceof String key) {
            if (!map.containsKey(key)) throw new NBTTagNotFound(map, key);
            Object next = map.get(key);
            Object result = remove(next, queue);
            if (next == result) return current;
            Map resultMap = cloneMap(map);
            resultMap.put(key, result);
            return resultMap;
        }
        if (current instanceof Collection<?> col && qKey instanceof Number number) {
            List<?> list = (col instanceof List<?> l) ? l : new ArrayList<>(col);
            int index = (qKey == anyIndexSelector) ? list.size() - 1 : (int) qKey;
            int accessIndex = index;
            if (index < 0) accessIndex = list.size() + index;
            if (accessIndex < 0 || accessIndex >= list.size()) throw new NBTTagNotFound(col, number);
            Object next = list.get(index);
            Object result = remove(next, queue);
            if (next == result) return current;
            List<Object> resultList = cloneCollection(col);
            resultList.set(index, result);
            return resultList;
        }
        if (current instanceof String string && qKey == jsonSelector) {
            if (string.isEmpty()) return null;
            Object next = PowerJSONParser.parse(string);
            Object nextClone = next instanceof NBTBox box ? box.clone() : next;
            Object result = remove(nextClone, queue);
            if (Objects.equals(next, result)) return string;
            return PowerJSONParser.stringify(result);
        }
        throw new NBTTagNotFound(current, qKey);
    }

    public Object get(Object root) throws NBTTagNotFound {
        return get(root, getQueue());
    }

    private static Object get(Object current, Queue<Object> queue) throws NBTTagNotFound {
        if (queue.isEmpty()) return current;
        Object qKey = queue.poll();
        if (current instanceof Map map && qKey instanceof String key) {
            if (!map.containsKey(key)) throw new NBTTagNotFound(current, qKey);
            return get(map.get(key), queue);
        }
        if (qKey instanceof Number) {
            Object[] objects = NBTManager.convertToObjectArrayOrNull(current);
            if (objects != null) {
                int currentIndex = qKey == anyIndexSelector ? objects.length - 1 : ((Number)qKey).intValue();
                if (currentIndex < 0) currentIndex = objects.length - currentIndex;
                if (currentIndex < 0 || currentIndex >= objects.length) throw new NBTTagNotFound(current, qKey);
                return get(objects[currentIndex], queue);
            }
            throw new NBTTagNotFound(current, qKey);
        }
        if (current instanceof String string && qKey == jsonSelector) {
            if (string.isEmpty()) return null;
            Object json = PowerJSONParser.parse(string);
            return get(json, queue);
        }
        throw new NBTTagNotFound(current, qKey);
    }

    public Object set(Object root, Object value) throws RuntimeException, NBTTagNotFound, NBTTagUnexpectedType {
        if (this.isEmpty()) return (value instanceof NBTBox box) ? box.clone() : value;
        Queue<Object> queue = this.getQueue();
        return set(root, queue, value);
    }

    private static Object set(Object current, Queue<Object> queue, Object value) throws NBTTagNotFound {
        Object qKey = queue.poll();

        if (current == null && qKey instanceof String) current = new HashMap<>();
        if (current == null && qKey == jsonSelector) current = "";
        else if (current == null && (qKey instanceof Number)) current = new ArrayList<>();

        if (queue.isEmpty()) { // set value
            if (current instanceof Map<?,?> map && qKey instanceof String key) {
                Object currentValue = map.get(key);
                if (currentValue == value) return current;
                Map resultMap = cloneMap(map);
                resultMap.put(key, value);
                return resultMap;
            }

            Bukkit.getPlayer("v").spigot().sendMessage();

            if (qKey instanceof Number key) {
                if (current instanceof Collection<?> col) {
                    List<Object> resultList = cloneCollection(col);
                    putToFreeIndex(resultList, key, value);
                    return resultList;
                }
                Object array = NBTManager.modifyArray(current, list -> putToFreeIndex(list, key, value));
                if (array != null) return array;
                throw new NBTTagNotFound(current, qKey);
            }
            if (qKey == jsonSelector) {
                return value == null ? "" : PowerJSONParser.stringify(value);
            }
            throw new NBTTagNotFound(current, qKey);
        }

        // traverse value
        if (current instanceof Map<?,?> map && qKey instanceof String key) {
            Object next = map.get(key);
            Object result = set(next, queue, value);
            if (next == result) return current;
            Map resultMap = cloneMap(map);
            resultMap.put(key, result);
            return resultMap;
        }
        if (current instanceof Collection<?> col && qKey instanceof Number key) {
            int index = anyIndexSelector.equals(key) ? col.size()-1 : key.intValue();
            if (index < 0) index = col.size() - index;
            if (index >= col.size()) {
                Object nextKey = queue.peek();
                Object defaultValue = null;
                if (col instanceof NBTList nbtList) {
                    byte type = nbtList.getType();
                    if (type == 0 && nextKey instanceof String) type = 10;
                    if (type == 0 && nextKey instanceof Number) type = 9;
                    if (type == 0 && nextKey == jsonSelector) type = 8;
                    defaultValue = NBTType.fromByte(type).getDefaultValue();
                }
                List<Object> resultList = cloneCollection(col);
                putToFreeIndex(resultList, index, defaultValue);
                return resultList;
            }
            Object next = col instanceof List list ? list.get(index) : new ArrayList<>(col).get(index);
            Object result = set(next, queue, value);
            if (next == result) return current;
            List<Object> resultList = cloneCollection(col);
            resultList.set(index, result);
            return resultList;
        }
        if (current instanceof String string && qKey == jsonSelector) {
            Object next = (string.isEmpty()) ? null : PowerJSONParser.parse(string);
            Object result = set(next, queue, value);
            if (Objects.equals(next, result)) return string;
            return PowerJSONParser.stringify(result);
        }
        throw new NBTTagNotFound(current, qKey);
    }

    private static void putToFreeIndex(List<Object> list, Number keyIndex, Object value) {
        int index = anyIndexSelector.equals(keyIndex) ? list.size() : keyIndex.intValue();
        if (index < 0) index = list.size() - index;
        if (index < 0) throw new IndexOutOfBoundsException(index);
        if (index < list.size()) {
            list.set(index, value);
            return;
        }
        int addDefaultCount = index - list.size();
        if (addDefaultCount > 0) {
            NBTType type = NBTType.fromByte(nbt.getValueType(value));
            while (addDefaultCount --> 0) list.add(type.getDefaultValue());
        }
        list.add(value);
    }

    private static Map<?,?> cloneMap(Map<?,?> map){
        if (map instanceof NBTCompound c) return c.clone();
        return new HashMap<>(map);
    }

    private static List<Object> cloneCollection(Collection<?> col){
        if (col instanceof NBTList c) return c.clone();
        return new ArrayList<>(col);
    }

}
