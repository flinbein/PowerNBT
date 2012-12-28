package me.dpohvar.powernbt.utils.nbt;

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

    public NBTQuery(String query) {
        if (query == null) return;

        String[] els = query.split(splitPattern);
        for (String s : els) {
            if (s.isEmpty()) continue;
            if (s.matches(tagPattern)) values.add(s);
            else if (s.equals("[]")) values.add(-1);
            else if (s.matches(indexPattern)) values.add(Integer.parseInt(s.substring(1, s.length() - 1)));
            else throw new RuntimeException(plugin.translate("error_querynode", query, s));
        }
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
