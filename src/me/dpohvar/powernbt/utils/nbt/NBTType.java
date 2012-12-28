package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.versionfix.XNBTBase;
import org.bukkit.ChatColor;

import static me.dpohvar.powernbt.PowerNBT.plugin;
import static me.dpohvar.powernbt.utils.versionfix.StaticValues.*;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.getNew;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.getShell;
import static org.bukkit.ChatColor.*;

public enum NBTType {
    END((byte) 0, "end", null, classNBTTagEnd, WHITE, false),
    BYTE((byte) 1, "byte", byte.class, classNBTTagByte, RED, false),//b
    SHORT((byte) 2, "short", short.class, classNBTTagShort, YELLOW, false),//s
    INT((byte) 3, "int", int.class, classNBTTagInt, BLUE, false),//i
    LONG((byte) 4, "long", long.class, classNBTTagLong, AQUA, false),//l
    FLOAT((byte) 5, "float", float.class, classNBTTagFloat, DARK_PURPLE, false),//f
    DOUBLE((byte) 6, "double", double.class, classNBTTagDouble, LIGHT_PURPLE, false),//d
    BYTEARRAY((byte) 7, "byte[]", byte[].class, classNBTTagByteArray, DARK_RED, false),//b
    STRING((byte) 8, "string", String.class, classNBTTagString, GREEN, false),
    LIST((byte) 9, "list", null, classNBTTagList, DARK_GRAY, true),
    COMPOUND((byte) 10, "compound", null, classNBTTagCompound, GRAY, true),
    INTARRAY((byte) 11, "int[]", int[].class, classNBTTagIntArray, DARK_BLUE, false),//i
    ;
    public final String name;
    public final byte type;
    public final Class dataClass;
    public final Class tagClass;
    public final ChatColor color;
    public final boolean tagable;

    NBTType(byte type, String name, Class dataClass, Class tagClass, ChatColor color, boolean taged) {
        this.name = name;
        this.type = type;
        this.dataClass = dataClass;
        this.tagClass = tagClass;
        this.color = color;
        this.tagable = taged;
    }

    public XNBTBase newBase(Object o) {
        if (dataClass == null)
            throw new RuntimeException("can not construct new " + tagClass.getSimpleName() + " by value");
        try {
            return getShell(XNBTBase.class, getNew(tagClass, new Class[]{String.class, dataClass}, "", o));
        } catch (Throwable t) {
            throw new RuntimeException("wrong type: " + o.getClass().getSimpleName() + " in " + name(), t);
        }
    }

    public static NBTType fromByte(byte b) {
        for (NBTType t : values()) if (t.type == b) return t;
        return END;
    }

    public static NBTType fromBase(XNBTBase base) {
        if (base == null) return END;
        return fromByte(base.getTypeId());
    }

    public static NBTType fromClass(Class c) {
        for (NBTType t : values()) if (t.dataClass.isAssignableFrom(c)) return t;
        return END;
    }

    public static NBTType fromString(String name) {
        String s = name.toLowerCase();
        for (NBTType t : values()) if (s.equalsIgnoreCase(t.name)) return t;
        for (NBTType t : values()) if (t.name.toLowerCase().startsWith(s)) return t;
        return END;
    }

    public XNBTBase getDefault() {
        return getShell(XNBTBase.class, defTypes[type].clone());
    }

    public XNBTBase parse(String s) {
        switch (this) {
            case STRING: {
                return getShell(XNBTBase.class, getNew(tagClass, new Class[]{String.class, String.class}, "", s));
            }
            case BYTE: {
                Byte v = null;
                try {
                    v = Byte.parseByte(s);
                } catch (Throwable ignored) {
                }
                if (v == null) try {
                    v = (byte) Long.parseLong(s);
                } catch (Throwable ignored) {
                }
                if (v == null) try {
                    v = (byte) Double.parseDouble(s);
                } catch (Throwable ignored) {
                }
                if (v == null) throw new RuntimeException(plugin.translate("error_parse", s, this.name));
                return getShell(XNBTBase.class, getNew(tagClass, new Class[]{String.class, byte.class}, "", v));
            }
            case SHORT: {
                Short v = null;
                try {
                    v = Short.parseShort(s);
                } catch (Throwable ignored) {
                }
                if (v == null) try {
                    v = (short) Long.parseLong(s);
                } catch (Throwable ignored) {
                }
                if (v == null) try {
                    v = (short) Double.parseDouble(s);
                } catch (Throwable ignored) {
                }
                if (v == null) throw new RuntimeException(plugin.translate("error_parse", s, this.name));
                return getShell(XNBTBase.class, getNew(tagClass, new Class[]{String.class, short.class}, "", v));
            }
            case INT: {
                Integer v = null;
                try {
                    v = Integer.parseInt(s);
                } catch (Throwable ignored) {
                }
                if (v == null) try {
                    v = (int) Long.parseLong(s);
                } catch (Throwable ignored) {
                }
                if (v == null) try {
                    v = (int) Double.parseDouble(s);
                } catch (Throwable ignored) {
                }
                if (v == null) throw new RuntimeException(plugin.translate("error_parse", s, this.name));
                return getShell(XNBTBase.class, getNew(tagClass, new Class[]{String.class, int.class}, "", v));
            }
            case LONG: {
                Long v = null;
                try {
                    v = Long.parseLong(s);
                } catch (Throwable ignored) {
                }
                if (v == null) try {
                    v = (long) Double.parseDouble(s);
                } catch (Throwable ignored) {
                }
                if (v == null) throw new RuntimeException(plugin.translate("error_parse", s, this.name));
                return getShell(XNBTBase.class, getNew(tagClass, new Class[]{String.class, long.class}, "", v));
            }
            case DOUBLE: {
                Double v = null;
                try {
                    v = Double.parseDouble(s);
                } catch (Throwable ignored) {
                }
                if (v == null) throw new RuntimeException(plugin.translate("error_parse", s, this.name));
                return getShell(XNBTBase.class, getNew(tagClass, new Class[]{String.class, double.class}, "", v));
            }
            case FLOAT: {
                Float v = null;
                try {
                    v = Float.parseFloat(s);
                } catch (Throwable ignored) {
                }
                if (v == null) try {
                    v = (float) (double) Double.parseDouble(s);
                } catch (Throwable ignored) {
                }
                if (v == null) throw new RuntimeException(plugin.translate("error_parse", s, this.name));
                return getShell(XNBTBase.class, getNew(tagClass, new Class[]{String.class, float.class}, "", v));
            }
            case BYTEARRAY: {
                if (!s.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
                    throw new RuntimeException(plugin.translate("error_parse", s, INTARRAY.name));
                }
                String sp = s.substring(1, s.length() - 1);
                if (sp.isEmpty()) return newBase(new byte[0]);
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
                        t = (byte) (long) Long.parseLong(x);
                    } catch (Throwable ignored) {
                    }
                    if (t == null) throw new RuntimeException(plugin.translate("error_parse", x, BYTE.name));
                    v[i] = t;
                }
                return getShell(XNBTBase.class, getNew(tagClass, new Class[]{String.class, byte[].class}, "", v));
            }
            case INTARRAY: {
                if (!s.matches("\\[((-?[0-9]+|#-?[0-9a-fA-F]+)(,(?!\\])|(?=\\])))*\\]")) {
                    throw new RuntimeException(plugin.translate("error_parse", s, INTARRAY.name));
                }
                String sp = s.substring(1, s.length() - 1);
                if (sp.isEmpty()) return newBase(new int[0]);
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
                        t = (int) (long) Long.parseLong(x);
                    } catch (Throwable ignored) {
                    }
                    if (t == null) throw new RuntimeException(plugin.translate("error_parse", x, INT.name));
                    v[i] = t;
                }
                return getShell(XNBTBase.class, getNew(tagClass, new Class[]{String.class, int[].class}, "", v));
            }
            default: {
                throw new RuntimeException(plugin.translate("error_parsetype", name));
            }
        }
    }

    public static XNBTBase defEnd = null;
    public static XNBTBase defByte = getShell(XNBTBase.class, getNew(classNBTTagByte, new Class[]{String.class, byte.class}, "", (byte) 0));
    public static XNBTBase defShort = getShell(XNBTBase.class, getNew(classNBTTagShort, new Class[]{String.class, short.class}, "", (short) 0));
    public static XNBTBase defInt = getShell(XNBTBase.class, getNew(classNBTTagInt, new Class[]{String.class, int.class}, "", (int) 0));
    public static XNBTBase defLong = getShell(XNBTBase.class, getNew(classNBTTagLong, new Class[]{String.class, long.class}, "", (long) 0));
    public static XNBTBase defFloat = getShell(XNBTBase.class, getNew(classNBTTagFloat, new Class[]{String.class, float.class}, "", (float) 0));
    public static XNBTBase defDouble = getShell(XNBTBase.class, getNew(classNBTTagDouble, new Class[]{String.class, double.class}, "", (double) 0));
    public static XNBTBase defString = getShell(XNBTBase.class, getNew(classNBTTagString, new Class[]{String.class, String.class}, "", ""));
    public static XNBTBase defByteArray = getShell(XNBTBase.class, getNew(classNBTTagByteArray, new Class[]{String.class, byte[].class}, "", new byte[0]));
    public static XNBTBase defIntArray = getShell(XNBTBase.class, getNew(classNBTTagIntArray, new Class[]{String.class, int[].class}, "", new int[0]));
    public static XNBTBase defList = getShell(XNBTBase.class, getNew(classNBTTagList, new Class[]{String.class}, ""));
    public static XNBTBase defCompound = getShell(XNBTBase.class, getNew(classNBTTagCompound, new Class[]{String.class}, ""));

    public static XNBTBase[] defTypes = new XNBTBase[]{
            defEnd,
            defByte,
            defShort,
            defInt,
            defList,
            defFloat,
            defDouble,
            defByteArray,
            defString,
            defList,
            defCompound,
            defIntArray,
    };
}
