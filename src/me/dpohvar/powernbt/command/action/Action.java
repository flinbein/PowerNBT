package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.nbt.*;
import org.bukkit.ChatColor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public abstract class Action {

    abstract public void execute();

    public static String getNBTShortView(NBTBase base, List<String> args) {
        if (base == null) return plugin.translate("data_null");
        NBTType type = NBTType.fromBase(base);
        return type.color + type.name + ' ' + ChatColor.BOLD + base.getName() + ChatColor.RESET
                + ": " + getNBTValue(base, args) + ChatColor.RESET;
    }

    public static String getNBTView(NBTBase base, List<String> args) {
        try {


            if (base == null) return plugin.translate("data_null");
            if (stringInList(args, "few", "short", "summary") != null) return getNBTShortView(base, args);
            NBTType type = NBTType.fromBase(base);
            String s = type.color + type.name + ' ' + ChatColor.BOLD + base.getName() + ChatColor.RESET
                    + ": " + getNBTValue(base, args) + ChatColor.RESET + '\n';
            switch (type) {
                case COMPOUND: {
                    Map<String, NBTBase> map = ((NBTTagCompound) base).asMap();
                    int min = 0;
                    int max = 0;
                    String pattern = regexInList(args, "[0-9]+-[0-9]+");
                    if (pattern != null) {
                        String[] ss = pattern.split("-");
                        min = Integer.parseInt(ss[0]);
                        max = Integer.parseInt(ss[1]);
                    }
                    int c = 0;
                    for (Map.Entry<String, NBTBase> e : map.entrySet()) {
                        if (pattern != null) {
                            int t = c++;
                            if (min > t || t > max) continue;
                        }
                        NBTBase b = e.getValue();
                        NBTType t = NBTType.fromBase(b);
                        if (t.equals(NBTType.LIST)) {
                            NBTType subType = NBTType.fromByte((Byte) ((NBTTagList)b).getSubTypeId());
                            s += subType.color + t.prefix + ChatColor.BOLD + b.getName() + ": " + ChatColor.RESET + getNBTValue(b, args) + '\n';
                        } else {
                            s += t.color + t.prefix + ChatColor.BOLD + b.getName() + ": " + ChatColor.RESET + getNBTValue(b, args) + '\n';
                        }
                    }
                    break;
                }
                case LIST: {
                    List<NBTBase> list = ((NBTTagList) base).asList();
                    NBTType subType = NBTType.fromByte(((NBTTagList) base).getSubTypeId());
                    int min = 0;
                    int max = 0;
                    String pattern = regexInList(args, "[0-9]+-[0-9]+");
                    if (pattern != null) {
                        String[] ss = pattern.split("-");
                        min = Integer.parseInt(ss[0]);
                        max = Integer.parseInt(ss[1]);
                    }
                    for (int i = 0; i < list.size(); i++) {
                        if (pattern != null) {
                            if (i < min) continue;
                            if (i > max) continue;
                        }
                        NBTBase b = list.get(i);
                        String prefix = subType.prefix;
                        s += subType.color + prefix + ChatColor.BOLD + "[" + i + "]: " + ChatColor.RESET + getNBTValue(b, args) + '\n';
                    }
                    break;
                }
                default:
                    break;
            }
            if (s.endsWith("\n")) s = s.substring(0, s.length() - 1);
            return s;
        } catch (Throwable t) {
            throw new RuntimeException("nbt to string error", t);
        }
    }

    public static String getNBTValue(NBTBase base, List<String> args) {
        NBTType type = NBTType.fromBase(base);
        switch (type) {
            case LIST:
                return plugin.translate("data_elements", ((NBTTagList) base).size());
            case COMPOUND:
                return plugin.translate("data_elements", ((NBTTagCompound) base).size());
            case BYTEARRAY: {
                if (stringInList(args, "few", "short", "summary") != null) {
                    return plugin.translate("data_bytes", ((NBTTagByteArray) base).size());
                }
                String s = "";
                byte[] bytes = ((NBTTagByteArray) base).get();
                String pattern = regexInList(args, "[0-9]+-[0-9]+");
                if (pattern != null) {
                    ArrayList<Byte> a = new ArrayList<Byte>();
                    String[] ss = pattern.split("-");
                    int min = Integer.parseInt(ss[0]);
                    int max = Integer.parseInt(ss[1]);
                    for (int i = 0; i < bytes.length; i++) {
                        if (i < min) continue;
                        if (i > max) continue;
                        a.add(bytes[i]);
                    }
                    bytes = new byte[a.size()];
                    for (int i = 0; i < bytes.length; i++) bytes[i] = a.get(i);
                }
                if (stringInList(args, "hex") != null) {
                    s += "#[";
                    for (byte b : bytes) {
                        s += toHex(b, 2) + ",";
                    }
                } else {
                    s += "[";
                    for (byte b : bytes) s += b + ",";
                }
                if (bytes.length != 0) s = s.substring(0, s.length() - 1);
                s += "]";
                return s;
            }
            case INTARRAY: {
                if (stringInList(args, "few", "short", "summary") != null) {
                    return plugin.translate("data_ints", ((NBTTagIntArray) base).size());
                }
                String s = "";
                int[] ints = ((NBTTagIntArray) base).get();
                String pattern = regexInList(args, "[0-9]+-[0-9]+");
                if (pattern != null) {
                    ArrayList<Integer> a = new ArrayList<Integer>();
                    String[] ss = pattern.split("-");
                    int min = Integer.parseInt(ss[0]);
                    int max = Integer.parseInt(ss[1]);
                    for (int i = 0; i < ints.length; i++) {
                        if (i < min) continue;
                        if (i > max) continue;
                        a.add(ints[i]);
                    }
                    ints = new int[a.size()];
                    for (int i = 0; i < ints.length; i++) ints[i] = a.get(i);
                }
                if (stringInList(args, "hex") != null) {
                    s += "#[";
                    for (int i : ints) s += toHex(i, 0) + ",";
                } else {
                    s += "[";
                    for (int b : ints) s += b + ",";
                }
                if (ints.length != 0) s = s.substring(0, s.length() - 1);
                s += "]";
                return s;
            }
            default:
                Object o = ((NBTTagDatable) base).get();
                if (stringInList(args, "hex") != null) {
                    if (o instanceof Byte) {
                        return "#" + toHex((Byte) o & 0xFF, 2);
                    } else if (o instanceof Number) {
                        return "#" + toHex((Number) o, 0);
                    } else if (o instanceof String) {
                        String t = "";
                        for (char c : ((String) o).toCharArray()) {
                            byte[] bytes = ByteBuffer.allocate(2).putChar(c).array();
                            t += " " + toHex(bytes[0], 2) + " " + toHex(bytes[0], 2);
                            if (t.startsWith(" ")) t = t.substring(1);
                        }
                        return "# " + t;
                    }
                }
                return o.toString();
        }
    }

    private static String regexInList(List<String> list, String regex) {
        if (list == null) return null;
        for (String s : list) if (s.matches(regex)) return s;
        return null;
    }

    private static String stringInList(List<String> list, String... strings) {
        if (list == null) return null;
        for (String s : list) for (String p : strings) if (s.equalsIgnoreCase(p)) return s;
        return null;
    }

    private static String toHex(Number n, int positions) {
        if (positions == 0) {
            return Long.toString(n.longValue(), 16);
        } else {
            String s = Long.toString(n.longValue(), 16);
            while (s.length() < positions) {
                s = "0" + s;
            }
            return s;
        }
    }
}
