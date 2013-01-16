package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.StringParser;

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

    public String getQuery() {
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
}
