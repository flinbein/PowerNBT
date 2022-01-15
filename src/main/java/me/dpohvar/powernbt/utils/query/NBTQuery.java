package me.dpohvar.powernbt.utils.query;

import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.exception.NBTTagUnexpectedType;
import me.dpohvar.powernbt.utils.StringParser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTQuery {

    private final QSelector[] selectors;
    private static final NBTManager nbt = NBTManager.getInstance();

    public List<QSelector> getSelectors() {
        return Arrays.asList(selectors);
    }

    public boolean isEmpty() {
        return selectors.length == 0;
    }

    public int getSize(){
        return selectors.length;
    }

    public NBTQuery getSlice(int start, int end){
        return new NBTQuery(Arrays.stream(selectors, start, end).toArray(QSelector[]::new));
    }

    public NBTQuery join(NBTQuery query){
        QSelector[] objects = Stream.concat(
                Arrays.stream(selectors),
                Arrays.stream(query.selectors)
        ).toArray(QSelector[]::new);
        return new NBTQuery(objects);
    }

    public NBTQuery getParent() {
        if (isEmpty()) return null;
        List<QSelector> v = getSelectors();
        v.remove(v.size() - 1);
        return new NBTQuery(v);
    }

    public NBTQuery add(QSelector selector) {
        QSelector[] newValues = Arrays.copyOf(selectors, selectors.length + 1);
        newValues[selectors.length] = selector;
        return new NBTQuery(newValues);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        QSelector lastNode = null;
        for (QSelector node : selectors) {
            String separator = node.getSeparator(lastNode);
            if (separator != null) s.append(separator);
            s.append(node);
            lastNode = node;
        }
        if (s.length() == 0) return ".";
        return s.toString();
    }

    private enum ParseMode { DEFAULT, TEXT, INDEX }

    public static NBTQuery fromString(String string) {
        if (string == null || string.isEmpty()) return new NBTQuery();
        List<QSelector> tokens = new LinkedList<>();
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
                        if (buffer.length() != 0) tokens.add(new KeySelector(buffer.toString()));
                        break tokenizer;
                    } else if (c == '.') {
                        if (buffer.length() != 0) {
                            tokens.add(new KeySelector(buffer.toString()));
                            buffer = new StringBuilder();
                        }
                    } else if (c == '#') {
                        if (buffer.length() != 0) {
                            tokens.add(new KeySelector(buffer.toString()));
                            buffer = new StringBuilder();
                        }
                        tokens.add(new StringAsJsonSelector());
                    } else if (c == '\"') {
                        mode = ParseMode.TEXT;
                    } else if (c == '[') {
                        if (buffer.length() != 0) {
                            tokens.add(new KeySelector(buffer.toString()));
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
                        tokens.add(new KeySelector(StringParser.parse(buffer.toString())));
                        buffer = new StringBuilder();
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
                        if (t.isEmpty()) tokens.add(new FreeIndexSelector());
                        else if (t.contains("..")) {
                            String[] parts = t.split("\\.\\.");
                            Integer start = null;
                            if (parts.length >= 1) if (!parts[0].isEmpty()) start = Integer.parseInt(parts[0]);
                            Integer end = null;
                            if (parts.length >= 2) if (!parts[1].isEmpty()) end = Integer.parseInt(parts[1]);
                            tokens.add(new RangeSelector(start, end));
                        } else {
                            tokens.add(new IndexSelector(Integer.parseInt(t)));
                        }
                        buffer = new StringBuilder();
                        mode = ParseMode.DEFAULT;
                    } else if (c == '-') {
                        if (buffer.isEmpty()) buffer.append(c);
                        else throw new RuntimeException(plugin.translate("error_querynode", string));
                    } else if (c.toString().matches("[0-9.]")) {
                        buffer.append(c);
                    } else {
                        throw new RuntimeException(plugin.translate("error_querynode", string));
                    }
                }
            }
        }
        return new NBTQuery(tokens);
    }

    public NBTQuery(QSelector... nodes) {
        this.selectors = nodes;
    }

    public NBTQuery(List<QSelector> nodes) {
        this(nodes.toArray(new QSelector[0]));
    }

    public Queue<Object> getQueue() {
        return new LinkedList<>(Arrays.asList(this.selectors));
    }

    public Object remove(Object root) throws NBTTagNotFound {
        if (selectors.length == 0) return null;
        QSelector[] tailSelectors = Arrays.copyOf(selectors, selectors.length - 1);
        QSelector headSelector = selectors[selectors.length - 1];
        LinkedList<Object> stepValues = new LinkedList<>();
        stepValues.add(root);
        for (QSelector selector : tailSelectors) {
            Object current = selector.get(stepValues.getLast(), false);
            stepValues.add(current);
        }
        Object reduceValue = headSelector.delete(stepValues.pollLast());
        for (int i = tailSelectors.length-1; i >= 0 ; i--) {
            QSelector selector = tailSelectors[i];
            reduceValue = selector.set(stepValues.getLast(), reduceValue, false);
        }
        return reduceValue;
    }

    public Object get(Object root) throws NBTTagNotFound {
        Object current = root;
        for (QSelector selector : selectors) {
            current = selector.get(current, false);
        }
        return current;
    }


    public Object set(Object root, Object value) throws RuntimeException, NBTTagNotFound, NBTTagUnexpectedType {
        if (selectors.length == 0) return null;
        QSelector[] tailSelectors = Arrays.copyOf(selectors, selectors.length - 1);
        QSelector headSelector = selectors[selectors.length - 1];
        LinkedList<Object> stepValues = new LinkedList<>();
        stepValues.add(root);
        for (QSelector selector : tailSelectors) {
            Object current = selector.get(stepValues.getLast(), true);
            stepValues.add(current);
        }
        Object reduceValue = headSelector.set(stepValues.pollLast(), value, true);
        for (int i = tailSelectors.length-1; i >= 0 ; i--) {
            QSelector selector = tailSelectors[i];
            reduceValue = selector.set(stepValues.pollLast(), reduceValue, true);
        }
        return reduceValue;
    }

}
