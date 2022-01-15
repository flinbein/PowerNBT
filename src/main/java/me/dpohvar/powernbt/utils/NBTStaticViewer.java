package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.nbt.NBTType;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * User: DPOH-VAR
 * Date: 07.06.13
 * Time: 1:11
 */
public class NBTStaticViewer {

    private static int v_limit = 60;
    private static int h_limit = 10;

    public static void applyConfig(FileConfiguration config){
        v_limit = config.getInt("limit.vertical", 60);
        h_limit = config.getInt("limit.horizontal", 10);
    }

    public static String getShortValueWithPrefix(Object base, boolean hex, boolean bin){
        if (base==null) return ChatColor.RESET + PowerNBT.plugin.translate("error_null");
        ChatColor typeColor = NBTType.getTypeColorByValue(base);
        NBTType type = NBTType.fromValueOrNull(base);
        String typeName = type != null ? type.name : base.getClass().getSimpleName();
        return typeColor + typeName + ':' + ' ' + ChatColor.RESET + getShortValue(base, hex, bin);
    }

    public static String getShortValue(Object base, boolean hex, boolean bin){
        String value = "";
        NBTType type = NBTType.fromValueOrNull(base);
        if (type == null) type = NBTType.END;

        switch (type) {
            case BYTE -> { // byte
                byte v = (byte) base;
                if (hex) value = "#" + Integer.toHexString(v & 0xFF);
                else if (bin) value = "b" + Integer.toBinaryString(v & 0xFF);
                else value = String.valueOf(v);
            }
            case SHORT -> { // short
                short v = (short) base;
                if (hex) value = "#" + Integer.toHexString(v & 0xFFFF);
                else if (bin) value = "b" + Integer.toBinaryString(v & 0xFFFF);
                else value = String.valueOf(v);
            }
            case INT -> { // int
                int v = (int) base;
                if (hex) value = "#" + Long.toHexString(v & 0xFFFFFFFFL);
                else if (bin) value = "b" + Long.toBinaryString(v & 0xFFFFFFFFL);
                else value = String.valueOf(v);
            }
            case LONG -> { // long
                long v = (long) base;
                if (hex) value = "#" + Long.toHexString(v);
                else if (bin) value = "b" + Long.toBinaryString(v);
                else value = String.valueOf(v);
            }
            case FLOAT -> { // float
                float v = (float) base;
                if (hex) value = "#" + Float.toHexString(v);
                else value = String.valueOf(v);
            }
            case DOUBLE -> { // double
                double v = (double) base;
                if (hex) value = "#" + Double.toHexString(v);
                else value = String.valueOf(v);
            }
            case BYTEARRAY -> { // bytearray
                byte[] v = (byte[]) base;
                if (v.length == 0) value = PowerNBT.plugin.translate("data_emptyarray");
                else {
                    value = v.length + ": [";
                    if (hex) {
                        ArrayList<String> h = new ArrayList<>(v.length);
                        for (byte b : v) h.add(Integer.toHexString(b & 0xFF));
                        value += "#" + StringUtils.join(h, ',');
                    } else if (bin) {
                        ArrayList<String> h = new ArrayList<>(v.length);
                        for (byte b : v) h.add(Integer.toBinaryString(b & 0xFF));
                        value += "b" + StringUtils.join(h, ',');
                    } else {
                        value += StringUtils.join(ArrayUtils.toObject(v), ',');
                    }
                    value += "]";
                }
            }
            case STRING -> { //string
                value = (String) base;
                if (hex) {
                    ArrayList<String> h = new ArrayList<>();
                    for (byte b : value.getBytes(StandardCharsets.UTF_8)) h.add(Integer.toHexString(b & 0xFF));
                    value = StringUtils.join(h, ' ');
                }
            }
            case LIST -> { // list
                NBTList list = (NBTList) base;
                NBTType listType = NBTType.fromByte(list.getType());
                if (listType == null) listType = NBTType.END;
                if (list.size() == 0) {
                    value = PowerNBT.plugin.translate("data_emptylist");
                } else {
                    value = PowerNBT.plugin.translate("data_elements", list.size())
                            + " " + listType.color + listType.name + ChatColor.RESET;
                }
            }
            case COMPOUND -> { // compound
                NBTCompound tag = (NBTCompound) base;
                if (tag.size() == 0) {
                    value = PowerNBT.plugin.translate("data_emptycompound");
                } else {
                    ArrayList<String> h = new ArrayList<>();
                    for (Map.Entry<String, Object> b : tag.entrySet()) {
                        h.add(NBTType.getTypeColorByValue(b.getValue()) + b.getKey());
                    }
                    value = tag.size() + ": " + StringUtils.join(h, ChatColor.RESET + ",");
                }
            }
            case INTARRAY -> { // intarray
                int[] v = (int[]) base;
                if (v.length == 0) value = PowerNBT.plugin.translate("data_emptyarray");
                else {
                    value = v.length + ": [";
                    if (hex) {
                        ArrayList<String> h = new ArrayList<String>(v.length);
                        for (int b : v) h.add(Long.toHexString(b & 0xFFFFFFFFL));
                        value += "#" + StringUtils.join(h, ',');
                    } else if (bin) {
                        ArrayList<String> h = new ArrayList<String>(v.length);
                        for (int b : v) h.add(Long.toBinaryString(b & 0xFFFFFFFFL));
                        value += "b" + StringUtils.join(h, ',');
                    } else {
                        value += StringUtils.join(ArrayUtils.toObject(v), ',');
                    }
                    value += "]";
                }
            }
            case LONGARRAY -> { // longarray
                long[] v = (long[]) base;
                if (v.length == 0) value = PowerNBT.plugin.translate("data_emptyarray");
                else {
                    value = v.length + ": [";
                    if (hex) {
                        ArrayList<String> h = new ArrayList<>(v.length);
                        for (long b : v) h.add(Long.toHexString(b & 0xFFFFFFFFL));
                        value += "#" + StringUtils.join(h, ',');
                    } else if (bin) {
                        ArrayList<String> h = new ArrayList<>(v.length);
                        for (long b : v) h.add(Long.toBinaryString(b & 0xFFFFFFFFL));
                        value += "b" + StringUtils.join(h, ',');
                    } else {
                        value += StringUtils.join(ArrayUtils.toObject(v), ',');
                    }
                    value += "]";
                }
            }
            default -> { // null
                if (base == null) {
                    value = "null";
                } else if (base instanceof Boolean) {
                    value = base.toString();
                } else if (base instanceof Map<?,?> tag) {
                    if (tag.size() == 0) {
                        value = PowerNBT.plugin.translate("data_emptycompound");
                    } else {
                        ArrayList<String> h = new ArrayList<>();
                        for (Map.Entry<?,?> b : tag.entrySet()) {
                            Object key = b.getKey();
                            if (!(key instanceof String s)) continue;
                            h.add(NBTType.getTypeColorByValue(b.getValue()) + s);
                        }
                        value = tag.size() + ": " + StringUtils.join(h, ChatColor.RESET + ",");
                    }
                } else if (base instanceof Collection<?> col){
                    NBTType listType = NBTType.END;
                    if (col.size() == 0) {
                        value = PowerNBT.plugin.translate("data_emptylist");
                    } else {
                        value = PowerNBT.plugin.translate("data_elements", col.size());
                    }
                } else {
                    Object[] array = NBTManager.convertToObjectArrayOrNull(base);
                    if (array != null) {
                        if (array.length == 0) {
                            value = PowerNBT.plugin.translate("data_emptylist");
                        } else {
                            value = PowerNBT.plugin.translate("data_elements", array.length);
                        }
                    } else {
                        value = PowerNBT.plugin.translate("data_null");
                    }
                }

            }

        }
        int overText = Math.max(ChatColor.stripColor(value).length()-v_limit,value.length()-v_limit*2);
        String resetPattern = ChatColor.RESET.toString();
        if ( value.endsWith(resetPattern) ) {
            value = value.substring(0,value.length()-resetPattern.length());
        }
        if (overText>0){
            value=value.substring(0,v_limit-1).concat("\u2026");
        }
        return value;
    }

    public static String getFullValue(Object base, int start, int end, boolean hex, boolean bin){
        if(start>end) { int t=start; start=end; end=t; }

        String value = "";
        if (base == null) {
            value = "null";
        } else if (base instanceof Boolean) {
            value = String.valueOf(base);
        } else if (base instanceof Byte v) {
            if (hex) value = "#" + Integer.toHexString(v & 0xFF);
            else if (bin) value = "b" + Integer.toBinaryString(v & 0xFF);
            else value = String.valueOf(v);
        } else if (base instanceof Short v) {
            if (hex) value = "#" + Integer.toHexString(v & 0xFFFF);
            else if (bin) value = "b" + Integer.toBinaryString(v & 0xFFFF);
            else value = String.valueOf(v);
        } else if (base instanceof Integer v) {
            if (hex) value = "#" + Long.toHexString(v & 0xFFFFFFFFL);
            else if (bin) value = "b" + Long.toBinaryString(v & 0xFFFFFFFFL);
            else value = String.valueOf(v);
        } else if (base instanceof Long v) {
            if (hex) value = "#" + Long.toHexString(v);
            else if (bin) value = "b" + Long.toBinaryString(v);
            else value = String.valueOf(v);
        } else if (base instanceof Float v) { // float
            if (hex) value = "#" + Float.toHexString(v);
            else value = String.valueOf(v);
        } else if (base instanceof Double v) { // float
            if (hex) value = "#" + Double.toHexString(v);
            else value = String.valueOf(v);
        } else if (base instanceof byte[] v) { // float
            if (start == 0 && end == 0) end = h_limit;
            if (v.length == 0) {
                value = PowerNBT.plugin.translate("data_emptyarray");
            } else if (start > v.length) {
                value = "\n" + PowerNBT.plugin.translate("data_outofrange");
            } else {
                ChatColor color = NBTType.BYTEARRAY.color;
                StringBuilder buffer = new StringBuilder();
                for (int i = start; i < end; i++) {
                    if (i >= v.length) break;
                    buffer.append("\n").append(color).append("[").append(i).append("] ").append(ChatColor.RESET);
                    if (hex) buffer.append("#").append(Integer.toHexString(v[i] & 0xFF));
                    else if (bin) buffer.append("b").append(Integer.toBinaryString(v[i] & 0xFF));
                    else buffer.append(v[i] & 0xFF);
                }
                value = PowerNBT.plugin.translate("data_elements", v.length) + buffer;
            }
        } else if (base instanceof int[] v) {
            if (start == 0 && end == 0) end = h_limit;
            if (v.length == 0) {
                value = PowerNBT.plugin.translate("data_emptyarray");
            } else if (start > v.length) {
                value = "\n" + PowerNBT.plugin.translate("data_outofrange");
            } else {
                ChatColor color = NBTType.INTARRAY.color;
                StringBuilder buffer = new StringBuilder();
                for (int i = start; i < end; i++) {
                    if (i >= v.length) break;
                    buffer.append("\n").append(color).append("[").append(i).append("] ").append(ChatColor.RESET);
                    if (hex) buffer.append("#").append(Long.toHexString(v[i] & 0xFFFFFFFFL));
                    else if (bin) buffer.append("b").append(Long.toBinaryString(v[i] & 0xFFFFFFFFL));
                    else buffer.append(v[i]);
                }
                value = PowerNBT.plugin.translate("data_elements", v.length) + buffer.toString();
            }
        } else if (base instanceof long[] v) {
            if (start == 0 && end == 0) end = h_limit;
            if (v.length == 0) {
                value = PowerNBT.plugin.translate("data_emptyarray");
            } else if (start > v.length) {
                value = "\n" + PowerNBT.plugin.translate("data_outofrange");
            } else {
                ChatColor color = NBTType.LONGARRAY.color;
                StringBuilder buffer = new StringBuilder();
                for (int i = start; i < end; i++) {
                    if (i >= v.length) break;
                    buffer.append("\n").append(color).append("[").append(i).append("] ").append(ChatColor.RESET);
                    if (hex) buffer.append("#").append(Long.toHexString(v[i]));
                    else if (bin) buffer.append("b").append(Long.toBinaryString(v[i]));
                    else buffer.append(v[i]);
                }
                value = PowerNBT.plugin.translate("data_elements", v.length) + buffer.toString();
            }
        } else if (base instanceof String) {
            boolean postfix = false;//
            boolean br = false;
            if (start == 0 && end == 0) {
                end = v_limit * h_limit;
                postfix = true;
            }
            value = (String) base;
            if (start > value.length()) {
                value = PowerNBT.plugin.translate("data_outofrange");
                br = true;
            } else {
                if (end > value.length()) {
                    end = value.length();
                    postfix = false;
                }
                value = value.substring(start, end);
            }
            if (!br) {
                if (hex) {
                    ArrayList<String> h = new ArrayList<>();
                    for (byte b : value.getBytes(StandardCharsets.UTF_8)) h.add(Integer.toHexString(b & 0xFF));
                    value = StringUtils.join(h, ' ');
                }
                if (postfix) value += '\u2026';
            }
        } else if (base instanceof Map<?,?> map) {
            if (start == 0 && end == 0) end = h_limit;
            ArrayList<Map.Entry<?, ?>> entries = new ArrayList<>(map.entrySet());
            if (entries.size() == 0) {
                value = PowerNBT.plugin.translate("data_emptycompound");
            } else {
                StringBuilder buffer = new StringBuilder();
                for (int i = start; i < end; i++) {
                    if (i >= entries.size()) break;
                    Object b = entries.get(i).getValue();
                    Object key = entries.get(i).getKey();
                    if (!(key instanceof String currentName)) continue;
                    NBTType t = NBTType.fromValueOrNull(b);
                    if (t == null) t = NBTType.END;
                    ChatColor c = NBTType.getTypeColorByValue(b);
                    if (b instanceof NBTList nbtListEntry) c = NBTType.fromByte(nbtListEntry.getType()).color;
                    String bolder = switch (t) {
                        case LIST, COMPOUND -> ChatColor.BOLD.toString();
                        default -> "";
                    };
                    buffer.append('\n')
                            .append(c)
                            .append(NBTType.getIconByValue(b))
                            .append(' ')
                            .append(bolder)
                            .append(currentName)
                            .append(':')
                            .append(ChatColor.RESET)
                            .append(' ')
                            .append(getShortValue(b, hex, bin));
                }
                value = PowerNBT.plugin.translate("data_elements", entries.size()) + buffer;
            }
        } else {
            Object[] array = NBTManager.convertToObjectArrayOrNull(base);
            if (array != null) {
                if (start == 0 && end == 0) end = h_limit;
                ChatColor typeColor = ChatColor.WHITE;
                String typeName = base.getClass().getSimpleName();
                if (base instanceof NBTList nbtList) {
                    NBTType listType = NBTType.fromByte(nbtList.getType());
                    typeColor = listType.color;
                    typeName = listType.name;
                }
                if (array.length == 0) {
                    value = PowerNBT.plugin.translate("data_emptylist");
                } else {
                    StringBuilder buffer = new StringBuilder();
                    for (int i = start; i < end; i++) {
                        if (i >= array.length) break;
                        Object b = array[i];
                        ChatColor itemTypeColor = NBTType.getTypeColorByValue(b);
                        if (typeColor == ChatColor.MAGIC) typeColor = ChatColor.WHITE;
                        buffer.append('\n')
                                .append(itemTypeColor)
                                .append(ChatColor.BOLD)
                                .append("[")
                                .append(i)
                                .append("] ")
                                .append(ChatColor.RESET)
                                .append(getShortValue(b, hex, bin));
                    }
                    value = PowerNBT.plugin.translate("data_elements", array.length)
                            + " " + typeColor + typeName + buffer;
                }
            } else {
                value = PowerNBT.plugin.translate("data_unknown");
            }
        }

        NBTType type = NBTType.fromValueOrNull(base);
        String typeName = type != null ? type.name : (base == null ? "" : base.getClass().getSimpleName());
        ChatColor color = NBTType.getTypeColorByValue(base);
        if (color == ChatColor.MAGIC) {
            return ChatColor.MAGIC + "x" + ChatColor.RESET + ": " + typeName;
        }
        return color + typeName + ChatColor.RESET + ": " + value;
    }
}
