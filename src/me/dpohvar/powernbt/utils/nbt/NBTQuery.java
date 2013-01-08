package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.StringParser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTQuery {
    public static final String splitPattern = "(?=\\[)|\\.";
    public static final String indexPattern = "\\[[0-9]*\\]";
    public static final String tagPattern = "[^\\[\\]]*";

    private List<Object> values = new ArrayList<Object>();

    public NBTQuery(String query) {
        if (query == null || query.isEmpty()) return;
        Queue<Character> chars = new LinkedList<Character>();
        StringBuilder buffer = new StringBuilder();
        for (char c : query.toCharArray()) chars.add(c);
        byte mode = 0; // 0 = default text;  1 = text in ""; 2 = text in []
        tokenizer:
        while (true) {
            Character c = chars.poll();
            switch (mode) {
                case 0: {
                    if (c == null) {
                        if (buffer.length() != 0) values.add(buffer.toString());
                        break tokenizer;
                    } else if (c == '.') {
                        if (buffer.length() != 0) {
                            values.add(buffer.toString());
                            buffer = new StringBuilder();
                        }
                    } else if (c == '\"') {
                        mode = 1;
                    } else if (c == '[') {
                        if (buffer.length() != 0) {
                            values.add(buffer.toString());
                            buffer = new StringBuilder();
                        }
                        mode = 2;
                    } else if (c == ']') {
                        throw new RuntimeException(plugin.translate("error_querynode", query));
                    } else {
                        buffer.append(c);
                    }
                    break;
                }
                case 1: {
                    if (c == null) {
                        throw new RuntimeException(plugin.translate("error_querynode", query));
                    }
                    if (c == '\\') {
                        buffer.append(c);
                        Character t = chars.poll();
                        if (t == null) throw new RuntimeException(plugin.translate("error_querynode", query));
                        buffer.append(t);
                    } else if (c == '"') {
                        if (buffer.length() != 0) {
                            values.add(StringParser.parse(buffer.toString()));
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
                        throw new RuntimeException(plugin.translate("error_querynode", query));
                    } else if (c == ']') {
                        String t = buffer.toString();
                        int r = -1;
                        if (!t.isEmpty()) r = Integer.parseInt(t);
                        values.add(r);
                        buffer = new StringBuilder();
                        mode = 0;
                    } else if (c.toString().matches("[0-9]")) {
                        buffer.append(c);
                    } else {
                        throw new RuntimeException(plugin.translate("error_querynode", query));
                    }
                    break;
                }
            }
        }
    }

    public List<Object> getValues() {
        return new ArrayList<Object>(values);
    }

    public boolean isEmpty() {
        return values.isEmpty();
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
