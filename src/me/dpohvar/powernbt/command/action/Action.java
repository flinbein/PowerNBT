package me.dpohvar.powernbt.command.action;

import me.dpohvar.powernbt.utils.nbt.NBTType;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Map;

import static me.dpohvar.powernbt.PowerNBT.plugin;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.getShell;

public abstract class Action {
    abstract public void execute();

    public static String getNBTShortView(XNBTBase base) {
        if (base == null) return plugin.translate("data_null");
        NBTType type = NBTType.fromBase(base);
        return type.color + type.name + ' ' + ChatColor.BOLD + base.getName() + ChatColor.RESET
                + ": " + getNBTValue(base) + ChatColor.RESET;
    }

    public static String getNBTView(XNBTBase base) {
        try {
            if (base == null) return plugin.translate("data_null");
            NBTType type = NBTType.fromBase(base);
            String s = type.color + type.name + ' ' + ChatColor.BOLD + base.getName() + ChatColor.RESET
                    + ": " + getNBTValue(base) + ChatColor.RESET + '\n';
            switch (type) {
                case COMPOUND:
                    Map<String, Object> map = (Map<String, Object>) base.getProxyField("map");
                    for (Map.Entry<String, Object> e : map.entrySet()) {
                        XNBTBase b = getShell(XNBTBase.class, e.getValue());
                        NBTType t = NBTType.fromBase(b);
                        if (t.equals(NBTType.LIST)) {
                            NBTType subType = NBTType.fromByte((Byte) b.getProxyField("type"));
                            s += "" + subType.color + ChatColor.BOLD + b.getName() + "[]: " + ChatColor.RESET + getNBTValue(b) + '\n';
                        } else {
                            s += "" + t.color + ChatColor.BOLD + b.getName() + ": " + ChatColor.RESET + getNBTValue(b) + '\n';
                        }
                    }
                    break;
                case LIST:
                    List<Object> list = (List<Object>) base.getProxyField("list");
                    NBTType subType = NBTType.fromByte((Byte) base.getProxyField("type"));
                    for (int i = 0; i < list.size(); i++) {
                        XNBTBase b = getShell(XNBTBase.class, list.get(i));
                        s += subType.color.toString() + ChatColor.BOLD + "[" + i + "]: " + ChatColor.RESET + getNBTValue(b) + '\n';
                    }
                    break;
                case BYTEARRAY:
                    byte[] bytes = (byte[]) base.getProxyField("data");
                    for (byte b : bytes) s += b + ",";
                    if (bytes.length != 0) s = s.substring(0, s.length() - 1);
                    break;
                case INTARRAY:
                    int[] ints = (int[]) base.getProxyField("data");
                    for (int b : ints) s += b + ",";
                    if (ints.length != 0) s = s.substring(0, s.length() - 1);
                    break;
                default:
                    break;
            }
            if (s.endsWith("\n")) s = s.substring(0, s.length() - 1);
            return s;
        } catch (Throwable t) {
            throw new RuntimeException("nbt to string error", t);
        }
    }

    public static String getNBTValue(XNBTBase base) {
        NBTType type = NBTType.fromBase(base);
        switch (type) {
            case LIST:
                return plugin.translate("data_elements", ((List) base.getProxyField("list")).size());
            case COMPOUND:
                return plugin.translate("data_elements", ((Map) base.getProxyField("map")).size());
            case BYTEARRAY:
                return plugin.translate("data_bytes", ((byte[]) base.getProxyField("data")).length);
            case INTARRAY:
                return plugin.translate("data_ints", ((int[]) base.getProxyField("data")).length);
            default:
                return base.getProxyField("data").toString();
        }
    }
}
