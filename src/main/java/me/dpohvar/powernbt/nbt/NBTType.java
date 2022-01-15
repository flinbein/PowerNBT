package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.api.NBTManager;
import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import static me.dpohvar.powernbt.PowerNBT.plugin;
import static org.bukkit.ChatColor.*;

public enum NBTType {

    END((byte) 0, "end", "\u24DE", WHITE, () -> null), // ⓞ
    BYTE((byte) 1, "byte", "\u24B7", RED, () -> (byte) 0), // Ⓑ
    SHORT((byte) 2, "short", "\u24C8", YELLOW, () -> (short) 0), // Ⓢ
    INT((byte) 3, "int", "\u24BE", BLUE, () -> (int) 0), // Ⓘ
    LONG((byte) 4, "long", "\u24C1", AQUA, () -> (long) 0), // Ⓛ
    FLOAT((byte) 5, "float", "\u24BB", DARK_PURPLE, () -> (float) 0), // Ⓕ
    DOUBLE((byte) 6, "double", "\u24B9", LIGHT_PURPLE, () -> (double) 0), // Ⓓ
    BYTEARRAY((byte) 7, "byte[]", ChatColor.BOLD + "\u24D1", DARK_RED, () -> new byte[0]), // ⓑ
    STRING((byte) 8, "string", "\u24C9", GREEN, () -> ""), // Ⓣ
    LIST((byte) 9, "list", "\u24B6", DARK_GRAY, NBTList::new), // Ⓐ
    COMPOUND((byte) 10, "compound", "\u24C2", GRAY, NBTCompound::new), // Ⓜ
    INTARRAY((byte) 11, "int[]", ChatColor.BOLD + "\u24D8", DARK_BLUE, () -> new int[0]), // ⓘ
    LONGARRAY((byte) 12, "long[]", ChatColor.BOLD + "\u24C1", DARK_AQUA, () -> new long[0]), // Ⓛ
    ;

    private static final NBTManager nbt = NBTManager.getInstance();

    public final String name;
    public final String prefix;
    public final byte type;
    public final ChatColor color;
    private final Supplier<Object> defaultValueGetter;

    NBTType(byte type, String name, String prefix, ChatColor color, Supplier<Object> defaultValueGetter) {
        this.type = type;
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.defaultValueGetter = defaultValueGetter;
    }

    public static NBTType fromByte(byte b) {
        for (NBTType t : values()) if (t.type == b) return t;
        return null;
    }

    public static NBTType fromValue(Object value) {
        if (value == null) return END;
        return fromByte(nbt.getValueType(value));
    }

    public static NBTType fromValueOrNull(Object value) {
        if (value == null) return null;
        try {
            return fromByte(nbt.getValueType(value));
        } catch (Throwable ignored) {
            return null;
        }

    }

    public static ChatColor getTypeColorByValue(Object value){
        if (value == null) return WHITE;
        if (value instanceof Boolean) return GOLD;
        if (value instanceof Byte) return RED;
        if (value instanceof Short) return YELLOW;
        if (value instanceof Integer) return BLUE;
        if (value instanceof Long) return AQUA;
        if (value instanceof Float) return DARK_PURPLE;
        if (value instanceof Double) return LIGHT_PURPLE;
        if (value instanceof byte[]) return DARK_RED;
        if (value instanceof int[]) return DARK_BLUE;
        if (value instanceof long[]) return DARK_AQUA;
        if (value instanceof String) return GREEN;
        if (value instanceof Map) return GRAY;
        if (NBTManager.convertToObjectArrayOrNull(value) != null) return DARK_GRAY;
        return MAGIC;
    }

    public static String getIconByValue(Object value){
        NBTType nbtType = fromValueOrNull(value);
        if (nbtType != null) return nbtType.prefix;
        if (value == null) return "\u24DE"; // ⓞ;
        if (value instanceof Boolean) return "\u2469"; // ⑩;
        if (value instanceof Map) return "\u24C2"; // Ⓜ;
        if (value instanceof Collection) return "\u24B6"; // Ⓐ;
        Object[] objects = NBTManager.convertToObjectArrayOrNull(value);
        if (objects != null) return "\u24D0"; // ⓐ

        return "\u24E7"; // ⓧ
    }

    public static NBTType fromString(String name) {
        if (name == null || name.isEmpty()) return END;
        String s = name.toLowerCase();
        for (NBTType t : values()) if (s.equalsIgnoreCase(t.name)) return t;
        for (NBTType t : values()) if (t.name.toLowerCase().startsWith(s)) return t;
        return END;
    }

    public NBTType getBaseType(){
        return switch (this) {
            case BYTEARRAY -> BYTE;
            case INTARRAY -> INT;
            case LONGARRAY -> LONG;
            default -> null;
        };
    }

    public NBTType getArrayType(){
        return switch (this) {
            case BYTE -> BYTEARRAY;
            case INT -> INTARRAY;
            case LONG -> LONGARRAY;
            default -> null;
        };
    }

    public Object getDefaultValue() {
        return this.defaultValueGetter.get();
    }

    public Object parse(String s) {
        return switch (this) {
            case STRING -> s;
            case BYTE -> {
                try {
                    yield Byte.parseByte(s);
                } catch (Throwable ignored) {}
                try {
                    yield (byte) Long.parseLong(s);
                } catch (Throwable ignored) {}
                try {
                    yield  (byte) Double.parseDouble(s);
                } catch (Throwable ignored) {}
                throw new RuntimeException(plugin.translate("error_parse", s, this.name));
            }
            case SHORT -> {
                try {
                    yield Short.parseShort(s);
                } catch (Throwable ignored) {}
                try {
                    yield (short) Long.parseLong(s);
                } catch (Throwable ignored) {}
                try {
                    yield (short) Double.parseDouble(s);
                } catch (Throwable ignored) {}
                throw new RuntimeException(plugin.translate("error_parse", s, this.name));
            }
            case INT -> {
                try {
                    yield Integer.parseInt(s);
                } catch (Throwable ignored) {}
                try {
                    yield (int) Long.parseLong(s);
                } catch (Throwable ignored) {}
                try {
                    yield (int) Double.parseDouble(s);
                } catch (Throwable ignored) {}
                throw new RuntimeException(plugin.translate("error_parse", s, this.name));
            }
            case LONG -> {
                try {
                    yield Long.parseLong(s);
                } catch (Throwable ignored) {}
                try {
                    yield (long) Double.parseDouble(s);
                } catch (Throwable ignored) {}
                throw new RuntimeException(plugin.translate("error_parse", s, this.name));
            }
            case DOUBLE -> {
                try {
                    if (s.equalsIgnoreCase("NaN")) yield Double.NaN;
                    yield Double.parseDouble(s);
                } catch (Throwable ignored) {}
                throw new RuntimeException(plugin.translate("error_parse", s, this.name));
            }
            case FLOAT -> {
                try {
                    if (s.equalsIgnoreCase("NaN")) yield  Float.NaN;
                    yield Float.parseFloat(s);
                } catch (Throwable ignored) {}
                try {
                    yield (float) Double.parseDouble(s);
                } catch (Throwable ignored) {}
                throw new RuntimeException(plugin.translate("error_parse", s, this.name));
            }
            case BYTEARRAY -> {
                if (!s.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
                    throw new RuntimeException(plugin.translate("error_parse", s, INTARRAY.name));
                }
                String sp = s.substring(1, s.length() - 1);
                if (sp.isEmpty()) yield new byte[0];
                String[] ss = sp.split(",");
                byte[] v = new byte[ss.length];
                for (int i = 0; i < v.length; i++) {
                    Byte t = null;
                    String x = ss[i];
                    if (x.startsWith("#")) try {
                        t = (byte) Long.parseLong(x.substring(1), 16);
                    } catch (Throwable ignored) {
                    }
                    try {
                        t = Byte.parseByte(x);
                    } catch (Throwable ignored) {
                    }
                    if (t == null) try {
                        t = (byte) Long.parseLong(x);
                    } catch (Throwable ignored) {
                    }
                    if (t == null) throw new RuntimeException(plugin.translate("error_parse", x, BYTE.name));
                    v[i] = t;
                }
                yield v;
            }
            case INTARRAY -> {
                if (!s.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
                    throw new RuntimeException(plugin.translate("error_parse", s, INTARRAY.name));
                }
                String sp = s.substring(1, s.length() - 1);
                if (sp.isEmpty()) yield new int[0];
                String[] ss = sp.split(",");
                int[] v = new int[ss.length];
                for (int i = 0; i < v.length; i++) {
                    Integer t = null;
                    String x = ss[i];
                    if (x.startsWith("#")) try {
                        t = (int) Long.parseLong(x.substring(1), 16);
                    } catch (Throwable ignored) {
                    }
                    try {
                        t = Integer.parseInt(x);
                    } catch (Throwable ignored) {
                    }
                    if (t == null) try {
                        t = (int) Long.parseLong(x);
                    } catch (Throwable ignored) {
                    }
                    if (t == null) throw new RuntimeException(plugin.translate("error_parse", x, INT.name));
                    v[i] = t;
                }
                yield v;
            }
            case LONGARRAY -> {
                if (!s.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
                    throw new RuntimeException(plugin.translate("error_parse", s, LONGARRAY.name));
                }
                String sp = s.substring(1, s.length() - 1);
                if (sp.isEmpty()) yield new long[0];
                String[] ss = sp.split(",");
                long[] v = new long[ss.length];
                for (int i = 0; i < v.length; i++) {
                    Long t = null;
                    String x = ss[i];
                    if (x.startsWith("#")) try {
                        t = Long.parseLong(x.substring(1), 16);
                    } catch (Throwable ignored) {
                    }
                    try {
                        t = Long.parseLong(x);
                    } catch (Throwable ignored) {
                    }
                    if (t == null) try {
                        t = Long.parseLong(x);
                    } catch (Throwable ignored) {
                    }
                    if (t == null) throw new RuntimeException(plugin.translate("error_parse", x, LONG.name));
                    v[i] = t;
                }
                yield v;
            }
            default -> throw new RuntimeException(plugin.translate("error_parse", s, this.name));
        };
    }
}
