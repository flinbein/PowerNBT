package me.dpohvar.powernbt.completer;

import me.dpohvar.powernbt.utils.NBTQuery;
import me.dpohvar.powernbt.nbt.NBTType;
import org.bukkit.Bukkit;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.logging.Level;

public class TypeCompleter {

    private HashMap<String, Object> templates = new HashMap<String, Object>();

    public TypeCompleter(File ymlFolder) {
        try {
            Yaml yaml = new Yaml();
            File file = new File(ymlFolder, "templates.yml");
            if (!file.exists()) return;
            FileReader reader = new FileReader(file);
            Object ymlRoot = yaml.load(reader);
            reader.close();
            if (!(ymlRoot instanceof Map)) throw new RuntimeException("invalid yml format in file " + file);
            for (Map.Entry<String, Object> el : ((Map<String, Object>) ymlRoot).entrySet()) {
                String name = el.getKey();
                String filename = el.getValue().toString();
                file = new File(ymlFolder, filename);
                addToTemplates(name, file);
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.ALL, "can not autocomplete tml "+ymlFolder, e);
        }
    }

    private void addToTemplates(String name, File file) throws Exception {
        FileReader reader = new FileReader(file);
        Object ymlRoot = new Yaml().load(reader);
        reader.close();
        templates.put(name, ymlRoot);
    }

    public List<String> getNextKeys(String key, NBTQuery query) {
        List<String> res = new ArrayList<String>();
        Object x = getObjectByQueue(key, query.getQueue());
        if (x instanceof List) res.add("[]");
        else if (x instanceof Map) {
            for (String s : ((Map<String, Object>) x).keySet()) {
                res.add(s);
            }
        }
        return res;
    }

    public NBTType getType(String key, NBTQuery query) {
        Object x = getObjectByQueue(key, query.getQueue());
        if (x instanceof String) {
            return NBTType.fromString((String) x);
        }
        return null;
    }

    private Object getObjectByQueue(String key, Queue<Object> queue) {
        Object current = templates.get(key);
        if (key != null) while (true) {
            Object t = queue.poll();
            if (current == null) return null;
            if (current instanceof Map && t instanceof String) {
                current = ((Map) current).get(t);
                continue;
            }
            if (current instanceof List && t instanceof Integer) {
                current = ((List) current).get(0);
                continue;
            }
            if (current instanceof String) {
                String s = (String) current;
                if (s.endsWith("[]")) {
                    if (t == null) return s;
                    if (queue.isEmpty()) return s.substring(0, s.length() - 2);
                    return null;
                }
                if (templates.containsKey(current)) {
                    LinkedList<Object> l = new LinkedList<Object>(queue);
                    l.addFirst(t);
                    return getObjectByQueue((String) current, l);
                }
                return queue.isEmpty() ? current : null;
            }
            return current;
        }
        else {
            Object r = null;
            for (String subKey : templates.keySet()) {
                r = getObjectByQueue(subKey, new LinkedList<Object>(queue));
                if (r != null) break;
            }
            return r;
        }
    }

}
