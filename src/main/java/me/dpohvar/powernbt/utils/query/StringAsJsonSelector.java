package me.dpohvar.powernbt.utils.query;

import me.dpohvar.powernbt.exception.NBTTagNotFound;
import me.dpohvar.powernbt.utils.PowerJSONParser;

public record StringAsJsonSelector() implements QSelector {

    @Override
    public Object get(Object current, boolean useDefault) throws NBTTagNotFound {
        if (useDefault && current == null) return null;
        if (current instanceof String string) {
            if (string.isEmpty()) return null;
            try {
                return PowerJSONParser.parse(string);
            } catch (Throwable t) {
                if (useDefault) return null;
            }
        }
        throw new NBTTagNotFound(current, this.toString());
    }

    @Override
    public Object delete(Object current) throws NBTTagNotFound {
        if (current instanceof String) return "";
        throw new NBTTagNotFound(current, this.toString());
    }

    @Override
    public Object set(Object current, Object value, boolean createDir) throws NBTTagNotFound {
        return PowerJSONParser.stringify(value);
    }
    @Override
    public String toString() {
        return "#";
    }

}
