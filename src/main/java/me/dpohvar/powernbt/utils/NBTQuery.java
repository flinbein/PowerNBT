package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.exception.NBTTagUnexpectedType;
import me.dpohvar.powernbt.nbt.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTQuery {

    private List<Object> values = new ArrayList<Object>();

    public List<Object> getValues() {
        return new ArrayList<Object>(values);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public NBTQuery getParent() {
        if (values.isEmpty()) return null;
        List<Object> v = getValues();
        v.remove(v.size() - 1);
        return new NBTQuery(v);
    }

    public String toString() {
        String s = "";
        for (Object node : values) {
            if (node instanceof Integer) s += "[" + node + "]";
            else s += "." + node;
        }
        if (s.startsWith(".")) s = s.substring(1);
        if (s.isEmpty()) s = ".";
        return s;
    }

    public static NBTQuery fromString(String string) {
        if (string == null || string.isEmpty()) return new NBTQuery();
        LinkedList<Object> v = new LinkedList<Object>();
        Queue<Character> chars = new LinkedList<Character>();
        StringBuilder buffer = new StringBuilder();
        for (char c : string.toCharArray()) chars.add(c);
        byte mode = 0; // 0 = default text;  1 = text in ""; 2 = text in []
        tokenizer:
        while (true) {
            Character c = chars.poll();
            switch (mode) {
                case 0: {
                    if (c == null) {
                        if (buffer.length() != 0) v.add(buffer.toString());
                        break tokenizer;
                    } else if (c == '.') {
                        if (buffer.length() != 0) {
                            v.add(buffer.toString());
                            buffer = new StringBuilder();
                        }
                    } else if (c == '\"') {
                        mode = 1;
                    } else if (c == '[') {
                        if (buffer.length() != 0) {
                            v.add(buffer.toString());
                            buffer = new StringBuilder();
                        }
                        mode = 2;
                    } else if (c == ']') {
                        throw new RuntimeException(plugin.translate("error_querynode", string));
                    } else {
                        buffer.append(c);
                    }
                    break;
                }
                case 1: {
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
                            v.add(StringParser.parse(buffer.toString()));
                            buffer = new StringBuilder();
                        }
                        mode = 0;
                    } else {
                        buffer.append(c);
                    }
                    break;
                }
                case 2: {
                    if (c == null) {
                        throw new RuntimeException(plugin.translate("error_querynode", string));
                    } else if (c == ']') {
                        String t = buffer.toString();
                        int r = -1;
                        if (!t.isEmpty()) r = Integer.parseInt(t);
                        v.add(r);
                        buffer = new StringBuilder();
                        mode = 0;
                    } else if (c.toString().matches("[0-9]")) {
                        buffer.append(c);
                    } else {
                        throw new RuntimeException(plugin.translate("error_querynode", string));
                    }
                    break;
                }
            }
        }
        NBTQuery q = new NBTQuery();
        q.values = v;
        return q;
    }

    public NBTQuery(Object... nodes) {
        for (Object node : nodes) {
            if (node instanceof String || node instanceof Integer) {
                values.add(node);
            } else throw new RuntimeException("invalid node: " + node);
        }
    }

    public NBTQuery(List<Object> nodes) {
        for (Object node : nodes) {
            if (node instanceof String || node instanceof Integer) {
                values.add(node);
            } else throw new RuntimeException("invalid node: " + node);
        }
    }

    public Queue<Object> getQueue() {
        return new LinkedList<Object>(values);
    }

    public NBTBase remove(NBTBase root) throws NBTTagNotFound {
        if (root == null) return null;
        root = root.clone();
        if (this.isEmpty()) return root;
        NBTBase current = root;
        Queue<Object> queue = this.getQueue();
        while (true) {
            if (queue.size() == 1) {
                Object t = queue.poll();
                if (current instanceof NBTTagCompound && t instanceof String) {
                    NBTTagCompound compound = (NBTTagCompound) current;
                    String key = (String) t;
                    boolean b = compound.remove(key);
                    return root;
                } else if (current instanceof NBTTagList && t instanceof Integer) {
                    NBTTagList list = (NBTTagList) current;
                    int index = (Integer) t;
                    if (index == -1) index = list.size() - 1;
                    boolean b = list.remove(index)!=null;
                    return root;
                } else if (current instanceof NBTTagNumericArray && t instanceof Integer) {
                    NBTTagNumericArray array = (NBTTagNumericArray) current;
                    int index = (Integer) t;
                    if (index == -1) index = array.size() - 1;
                    array.remove(index);
                    return root;
                } else throw new NBTTagNotFound(current,t);
            }
            Object t = queue.poll();
            if (current == null) throw new NBTTagNotFound(current,t);
            if (current instanceof NBTTagCompound && t instanceof String) {
                NBTTagCompound compound = (NBTTagCompound) current;
                String key = (String) t;
                if (!compound.has(key)) throw new NBTTagNotFound(compound,t);
                current = compound.get(key);
            } else if (current instanceof NBTTagList && t instanceof Integer) {
                NBTTagList list = (NBTTagList) current;
                int index = (Integer) t;
                if (index == -1) index = list.size() - 1;
                current = list.get(index);
            } else throw new NBTTagNotFound(current,t);
        }
    }


    private NBTBase call(NBTBase root)throws NBTTagNotFound{
        return get(root);
    }

    public NBTBase get(NBTBase root) throws NBTTagNotFound {
        Queue<Object> queue = this.getQueue();
        NBTBase current = root;
        while (true) {
            Object t = queue.poll();
            if (t == null || current == null) return current;
            if (current instanceof NBTTagCompound && t instanceof String) {
                NBTTagCompound compound = (NBTTagCompound) current;
                String key = (String) t;
                if (!compound.has(key)) return null;
                current = compound.get(key);
            } else if (current instanceof NBTTagList && t instanceof Integer) {
                NBTTagList list = (NBTTagList) current;
                int index = (Integer) t;
                if (index == -1) index = list.size() - 1;
                current = list.get(index);
            } else if (current instanceof NBTTagNumericArray && t instanceof Integer) {
                NBTTagNumericArray array = (NBTTagNumericArray) current;
                int index = (Integer) t;
                if (index == -1) index = array.size() - 1;
                current = NBTBase.getByValue(array.get(index));
            } else throw new NBTTagNotFound(current,t);
        }
    }

    private NBTBase call(NBTBase root,NBTBase value)throws RuntimeException, NBTTagNotFound, NBTTagUnexpectedType{
        return set(root, value);
    }

    public NBTBase set(NBTBase root, NBTBase value) throws RuntimeException, NBTTagNotFound, NBTTagUnexpectedType {
        if (this.isEmpty()) return root.clone();
        Queue<Object> queue = this.getQueue();
        if (root == null) {
            Object z = queue.peek();
            if (z instanceof String) root = new NBTTagCompound();
            else if (z instanceof Integer) root = new NBTTagList();
        } else {
            root = root.clone();
        }
        NBTBase current = root;
        while (true) {
            if (queue.size() == 1) {
                Object t = queue.poll();
                if (current instanceof NBTTagCompound && t instanceof String) {
                    NBTTagCompound compound = (NBTTagCompound) current;
                    String key = (String) t;
                    compound.putToHandle(key, value.clone());
                    return root;
                } else if (current instanceof NBTTagList && t instanceof Integer) {
                    NBTTagList list = (NBTTagList) current;
                    int index = (Integer) t;
                    if (index == -1) list.add(value.clone());
                    else list.set_b(index, value.clone());
                    return root;
                } else if (current instanceof NBTTagNumericArray && t instanceof Integer) {
                    NBTTagNumericArray array = (NBTTagNumericArray) current;
                    int index = (Integer) t;
                    if (index == -1) index = array.size();
                    if (!(value instanceof NBTTagNumeric)) throw new NBTTagUnexpectedType(value,NBTTagNumeric.class);
                    Number num = (Number) ((NBTTagNumeric) value).get();
                    array.set(index, num);
                    return root;
                } else {
                    throw new NBTTagNotFound(current,t);
                }
            }
            Object t = queue.poll();
            if (current instanceof NBTTagCompound && t instanceof String) {
                NBTTagCompound compound = (NBTTagCompound) current;
                String key = (String) t;
                if (!compound.has(key)) {
                    Object z = queue.peek();
                    if (z instanceof String) current = compound.nextCompound(key);
                    else if (z instanceof Integer) current = compound.nextList(key);
                } else {
                    current = compound.get(key);
                }
            } else if (current instanceof NBTTagList && t instanceof Integer) {
                NBTTagList list = (NBTTagList) current;
                int index = (Integer) t;
                if (index == -1) index = list.size()-1;
                NBTBase b = null;
                if (!list.isEmpty()) {
                    b = NBTBase.getDefault(list.getSubTypeId());
                } else {
                    Object z = queue.peek();
                    if (z instanceof String) b = new NBTTagCompound();
                    else if (z instanceof Integer) b = new NBTTagList();
                }
                while (list.size() <= index) {
                    list.add(b.clone());
                }
                current = list.get(index);
            } else throw new NBTTagNotFound(current,t);
        }
    }

}
