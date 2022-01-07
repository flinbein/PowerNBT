package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.api.NBTBox;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.exception.NBTTagUnexpectedType;
import me.dpohvar.powernbt.nbt.NBTType;
import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTQuery {

    private final Object[] values; // String, Number, null
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
            if (node instanceof Integer) s.append("[").append(node).append("]");
            else s.append(".").append(node);
        }
        if (s.toString().startsWith(".")) s = new StringBuilder(s.substring(1));
        if (s.length() == 0) return ".";
        return s.toString();
    }

    public static NBTQuery fromString(String string) {
        if (string == null || string.isEmpty()) return new NBTQuery();
        LinkedList<Object> tokens = new LinkedList<>();
        Queue<Character> chars = new LinkedList<>();
        StringBuilder buffer = new StringBuilder();
        for (char c : string.toCharArray()) chars.add(c);
        byte mode = 0; // 0 = default text;  1 = text in ""; 2 = text in []
        tokenizer:
        while (true) {
            Character c = chars.poll();
            switch (mode) {
                case 0 -> {
                    if (c == null) {
                        if (buffer.length() != 0) tokens.add(buffer.toString());
                        break tokenizer;
                    } else if (c == '.') {
                        if (buffer.length() != 0) {
                            tokens.add(buffer.toString());
                            buffer = new StringBuilder();
                        }
                    } else if (c == '\"') {
                        mode = 1;
                    } else if (c == '[') {
                        if (buffer.length() != 0) {
                            tokens.add(buffer.toString());
                            buffer = new StringBuilder();
                        }
                        mode = 2;
                    } else if (c == ']') {
                        throw new RuntimeException(plugin.translate("error_querynode", string));
                    } else {
                        buffer.append(c);
                    }
                }
                case 1 -> {
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
                        mode = 0;
                    } else {
                        buffer.append(c);
                    }
                }
                case 2 -> {
                    if (c == null) {
                        throw new RuntimeException(plugin.translate("error_querynode", string));
                    } else if (c == ']') {
                        String t = buffer.toString();
                        Integer r = null;
                        if (!t.isEmpty()) r = Integer.parseInt(t);
                        tokens.add(r);
                        buffer = new StringBuilder();
                        mode = 0;
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
            if (!(node instanceof String || node instanceof Integer || node == null)) {
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
            if (current instanceof NBTCompound compound && qKey instanceof String key) {
                compound.remove(key);
                return current;
            }
            if (qKey instanceof Number || qKey == null) {
                if (current instanceof NBTList list) {
                    int index = (qKey instanceof Number i) ? (int) i : list.size() - 1;
                    list.remove(index);
                    return current;
                }
                if (current instanceof byte[] arr){
                    int index = (qKey instanceof Number i) ? (int) i : arr.length - 1;
                    List<Byte> list = Arrays.asList(ArrayUtils.toObject(arr));
                    list.remove(index);
                    return ArrayUtils.toPrimitive(list.toArray(Byte[]::new));
                }
                if (current instanceof int[] arr){
                    int index = (qKey instanceof Number i) ? (int) i : arr.length - 1;
                    List<Integer> list = Arrays.asList(ArrayUtils.toObject(arr));
                    list.remove(index);
                    return ArrayUtils.toPrimitive(list.toArray(Integer[]::new));
                }
                if (current instanceof long[] arr){
                    int index = (qKey instanceof Number i) ? (int) i : arr.length - 1;
                    List<Long> list = Arrays.asList(ArrayUtils.toObject(arr));
                    list.remove(index);
                    return ArrayUtils.toPrimitive(list.toArray(Long[]::new));
                }
            }
            throw new NBTTagNotFound(current, qKey);
        }

        // chain, queue is not empty
        if (current == null) throw new NBTTagNotFound(null, qKey);
        if (current instanceof NBTCompound compound && qKey instanceof String key) {
            if (!compound.containsKey(key)) throw new NBTTagNotFound(compound, key);
            Object next = compound.get(key);
            Object result = remove(next, queue);
            if (next != result) compound.put(key, result);
            return compound;
        } else if (current instanceof NBTList list && qKey instanceof Integer index) {
            int accessIndex = index;
            if (index < 0) accessIndex = list.size() + index;
            if (accessIndex < 0 || accessIndex >= list.size()) throw new NBTTagNotFound(list, index);
            Object next = list.get(index);
            Object result = remove(next, queue);
            if (next != result) list.set(index, result);
            return current;
        } else throw new NBTTagNotFound(current, qKey);
    }

    public Object get(Object root) throws NBTTagNotFound {
        return get(root, getQueue());
    }

    private static Object get(Object current, Queue<Object> queue) throws NBTTagNotFound {
        if (queue.isEmpty()) return current;
        Object qKey = queue.poll();
        if (current instanceof NBTCompound compound && qKey instanceof String key) {
            return get(compound.get(key), queue);
        }
        if (qKey instanceof Number || qKey == null) {
            if (current instanceof NBTList list) {
                int currentIndex = qKey == null ? list.size() - 1 : ((Number)qKey).intValue();
                if (currentIndex < 0) currentIndex = list.size() - currentIndex;
                return get(list.get(currentIndex), queue);
            }
            if (current instanceof byte[] arr) {
                int currentIndex = qKey == null ? arr.length - 1 : ((Number)qKey).intValue();
                if (currentIndex < 0) currentIndex = arr.length - currentIndex;
                return get(arr[currentIndex], queue);
            }
            if (current instanceof int[] arr) {
                int currentIndex = qKey == null ? arr.length - 1 : ((Number)qKey).intValue();
                if (currentIndex < 0) currentIndex = arr.length - currentIndex;
                return get(arr[currentIndex], queue);
            }
            if (current instanceof long[] arr) {
                int currentIndex = qKey == null ? arr.length - 1 : ((Number)qKey).intValue();
                if (currentIndex < 0) currentIndex = arr.length - currentIndex;
                return get(arr[currentIndex], queue);
            }
        }
        throw new NBTTagNotFound(current, qKey);
    }

    public Object set(Object root, Object value) throws RuntimeException, NBTTagNotFound, NBTTagUnexpectedType {
        if (this.isEmpty()) return (value instanceof NBTBox box) ? box.clone() : value;

        Queue<Object> queue = this.getQueue();
        root = root instanceof NBTBox box ? box.clone() : root;
        return set(root, queue, value);
    }

    private static Object set(Object current, Queue<Object> queue, Object value) throws NBTTagNotFound {
        Object qKey = queue.poll();
        if (queue.isEmpty()) { // set value
            if (current instanceof NBTCompound compound && qKey instanceof String key) {
                compound.put(key, value);
                return compound;
            }
            if (qKey instanceof Number || qKey == null) {
                if (current instanceof NBTList list) {
                    putToFreeIndex(list, qKey == null ? null : ((Number) qKey).intValue(), value);
                    return list;
                }
                if (current instanceof byte[] arr){
                    List<Byte> list = Arrays.asList(ArrayUtils.toObject(arr));
                    putToFreeIndex(list, qKey == null ? null : ((Number) qKey).intValue(), NBTManager.convertValue(value, (byte)1));
                    return ArrayUtils.toPrimitive(list.toArray(Byte[]::new));
                }
                if (current instanceof int[] arr){
                    List<Integer> list = Arrays.asList(ArrayUtils.toObject(arr));
                    putToFreeIndex(list, qKey == null ? null : ((Number) qKey).intValue(), NBTManager.convertValue(value, (byte)3));
                    return ArrayUtils.toPrimitive(list.toArray(Integer[]::new));
                }
                if (current instanceof long[] arr){
                    List<Long> list = Arrays.asList(ArrayUtils.toObject(arr));
                    putToFreeIndex(list, qKey == null ? null : ((Number) qKey).intValue(), NBTManager.convertValue(value, (byte)4));
                    return ArrayUtils.toPrimitive(list.toArray(Long[]::new));
                }
            }
            throw new NBTTagNotFound(current, qKey);
        }

        if (current == null && qKey instanceof String) current = new NBTCompound();
        else if (current == null && (qKey instanceof Integer || qKey == null)) current = new NBTList();

        if (current instanceof NBTCompound compound && qKey instanceof String key) {
            Object next = compound.get(key);
            Object result = set(next, queue, value);
            if (next != result) compound.put(key, result);
            return compound;
        }
        if (current instanceof NBTList list && (qKey instanceof Number || qKey == null)) {
            int index = qKey == null ? list.size()-1 : ((Number)qKey).intValue();
            if (index < 0) index = list.size() - index;
            if (index >= list.size()) {
                Object nextKey = queue.peek();
                byte type = list.getType();
                if (type == 0) type = nextKey instanceof String ? (byte) 10 : (byte) 9;
                putToFreeIndex(list, index, NBTType.fromByte(type).getDefault());
            }
            Object next = list.get(index);
            Object result = set(next, queue, value);
            if (next != result) list.set(index, result);
            return list;
        }
        throw new NBTTagNotFound(current, qKey);
    }

    private static void putToFreeIndex(List list, Number keyIndex, Object value) throws NBTTagNotFound {
        int index = keyIndex == null ? list.size() : keyIndex.intValue();
        if (index < 0) index = list.size() - index;
        if (index < 0) throw new NBTTagNotFound(list, keyIndex);
        if (index < list.size()) {
            list.set(index, value);
            return;
        }
        int addDefaultCount = index - list.size();
        if (addDefaultCount > 0) {
            NBTType type = NBTType.fromByte(nbt.getValueType(value));
            while (addDefaultCount --> 0) list.add(type.getDefault());
        }
        list.add(value);
    }

}
