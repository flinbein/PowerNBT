package me.dpohvar.powernbt.utils.query;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.utils.StringParser;
import org.bukkit.ChatColor;

import java.util.*;

public record KeySelector(String key) implements QSelector {

    @Override
    public Object get(Object current, boolean useDefault) throws NBTTagNotFound {
        if (useDefault && current == null) return null;
        if (current instanceof Map<?,?> map) {
            if (!map.containsKey(key)) {
                if (useDefault) return null;
                throw new NBTTagNotFound(current, this.toString());
            }
            return map.get(key);
        }
        throw new NBTTagNotFound(current, this.toString());
    }

    @Override
    public Object delete(Object current) throws NBTTagNotFound {
        if (current instanceof Map<?,?> map) {
            if (!map.containsKey(key)) throw new NBTTagNotFound(current, this.toString());
            Map<?,?> resultMap = cloneMap(map);
            resultMap.remove(key);
            return resultMap;
        }
        throw new NBTTagNotFound(current, this.toString());
    }

    @Override
    public Object set(Object current, Object value, boolean createDir) throws NBTTagNotFound {
        if (current == null && createDir) current = new HashMap<>();
        if (current instanceof Map<?,?> map) {
            Object prevValue = map.get(key);
            if (map.containsKey(key) && Objects.equals(prevValue, value)) return map;
            Map resultMap = cloneMap(map);
            resultMap.put(key, value);
            return resultMap;
        }
        throw new NBTTagNotFound(current, this.toString());
    }

    private static Map<?,?> cloneMap(Map<?,?> map){
        if (map instanceof NBTCompound c) return c.clone();
        return new HashMap<>(map);
    }

    @Override
    public String getSeparator(QSelector prevSelector) {
        if (prevSelector == null) return null;
        if (prevSelector instanceof StringAsJsonSelector) return null;
        return ".";
    }

    private static final Set<String> badKeys = new HashSet<>(Arrays.asList("copy","rm","rem","remove","remame","paste","add","cut","set","select","as","view","debug","cancel","swap","insert","ins","spawn"));
    @Override
    public String toString() {
        if (key.isEmpty()) return StringParser.wrapToQuotes(key);
        if (badKeys.contains(key)) return StringParser.wrapToQuotes(key);
        if (key.matches("[\\[\\].(){}#*\\s]")) return StringParser.wrapToQuotes(key);
        if (!ChatColor.stripColor(key).equals(key)) return StringParser.wrapToQuotes(key);
        return key;
    }
}
