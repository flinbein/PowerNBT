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
import java.util.Map;

/**
 * User: DPOH-VAR
 * Date: 07.06.13
 * Time: 1:11
 */
public class NBTViewer {

    private static int v_limit = 60;
    private static int h_limit = 10;

    public static void applyConfig(FileConfiguration config){
        v_limit = config.getInt("limit.vertical", 60);
        h_limit = config.getInt("limit.horizontal", 10);
    }

    @Deprecated
    public static String getShortValueWithPrefix(Object base, boolean hex){
        return getShortValueWithPrefix(base, hex, false);
    }
    public static String getShortValueWithPrefix(Object base, boolean hex, boolean bin){
        if (base==null) return ChatColor.RESET + PowerNBT.plugin.translate("error_null");
        NBTType type = NBTType.fromValue(base);
        return (type.color) + type.name + ':' + ' ' + ChatColor.RESET + getShortValue(base, hex, bin);
    }
    @Deprecated
    public static String getShortValue(Object base, boolean hex){
        return getShortValue(base, hex, false);
    }
    public static String getShortValue(Object base, boolean hex, boolean bin){
        String value = "";

        switch (NBTType.fromValue(base)) {
            case END -> { // null
                value = PowerNBT.plugin.translate("data_null");
            }
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
                        h.add(NBTType.fromValue(b.getValue()).color + b.getKey());
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
        if(base==null) return PowerNBT.plugin.translate("error_null");
        if(start>end) { int t=start; start=end; end=t; }
        String name = "";
        NBTType type = NBTType.fromValue(base);
        ChatColor color = type.color;
        String prefix;
        String value = "";
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
                if (start == 0 && end == 0) end = h_limit;
                byte[] v = (byte[]) base;
                if (v.length == 0) {
                    value = PowerNBT.plugin.translate("data_emptyarray");
                } else if (start > v.length) {
                    value = "\n" + PowerNBT.plugin.translate("data_outofrange");
                } else {
                    StringBuilder buffer = new StringBuilder();
                    for (int i = start; i < end; i++) {
                        if (i >= v.length) break;
                        buffer.append("\n").append(type.color).append("[").append(i).append("] ").append(ChatColor.RESET);
                        if (hex) buffer.append("#").append(Integer.toHexString(v[i] & 0xFF));
                        else if (bin) buffer.append("b").append(Integer.toBinaryString(v[i] & 0xFF));
                        else buffer.append(v[i] & 0xFF);
                    }
                    value = PowerNBT.plugin.translate("data_elements", v.length) + buffer;
                }
            }
            case STRING -> { //string
                boolean postfix = false;//
                if (start == 0 && end == 0) {
                    end = v_limit * h_limit;
                    postfix = true;
                }
                value = (String) base;
                if (start > value.length()) {
                    value = PowerNBT.plugin.translate("data_outofrange");
                    break;
                } else {
                    if (end > value.length()) {
                        end = value.length();
                        postfix = false;
                    }
                    value = value.substring(start, end);
                }
                if (hex) {
                    ArrayList<String> h = new ArrayList<>();
                    for (byte b : value.getBytes(StandardCharsets.UTF_8)) h.add(Integer.toHexString(b & 0xFF));
                    value = StringUtils.join(h, ' ');
                }
                if (postfix) value += '\u2026';
            }
            case LIST -> { // list
                if (start == 0 && end == 0) end = h_limit;
                NBTList list = ((NBTList) base);
                NBTType listType = NBTType.fromByte(list.getType());
                if (list.size() == 0) {
                    value = PowerNBT.plugin.translate("data_emptylist");
                    break;
                }
                StringBuilder buffer = new StringBuilder();
                for (int i = start; i < end; i++) {
                    if (i >= list.size()) break;
                    Object b = list.get(i);
                    buffer.append('\n')
                            .append(listType.color)
                            .append(ChatColor.BOLD)
                            .append("[")
                            .append(i)
                            .append("] ")
                            .append(ChatColor.RESET)
                            .append(getShortValue(b, hex, bin));
                }
                value = PowerNBT.plugin.translate("data_elements", list.size())
                        + " " + listType.color + listType.name + buffer;
            }
            case COMPOUND -> { // compound
                if (start == 0 && end == 0) end = h_limit;
                NBTCompound compound = (NBTCompound) base;
                ArrayList<Map.Entry<String, Object>> entries = new ArrayList<>(compound.entrySet());
                if (entries.size() == 0) {
                    value = PowerNBT.plugin.translate("data_emptycompound");
                    break;
                }
                StringBuilder buffer = new StringBuilder();
                for (int i = start; i < end; i++) {
                    if (i >= entries.size()) break;
                    Object b = entries.get(i).getValue();
                    String currentName = entries.get(i).getKey();
                    NBTType t = NBTType.fromValue(b);
                    ChatColor c;
                    if (b instanceof NBTList nbtListEntry) c = NBTType.fromByte(nbtListEntry.getType()).color;
                    else c = t.color;
                    String bolder = switch (t) {
                        case LIST, COMPOUND -> ChatColor.BOLD.toString();
                        default -> "";
                    };
                    buffer.append('\n')
                            .append(c)
                            .append(t.prefix)
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
            case INTARRAY -> { // intarray
                if (start == 0 && end == 0) end = h_limit;
                int[] v = ((int[]) base);
                if (v.length == 0) {
                    value = PowerNBT.plugin.translate("data_emptyarray");
                } else if (start > v.length) {
                    value = "\n" + PowerNBT.plugin.translate("data_outofrange");
                } else {
                    StringBuilder buffer = new StringBuilder();
                    for (int i = start; i < end; i++) {
                        if (i >= v.length) break;
                        buffer.append("\n").append(type.color).append("[").append(i).append("] ").append(ChatColor.RESET);
                        if (hex) buffer.append("#").append(Long.toHexString(v[i] & 0xFFFFFFFFL));
                        else if (bin) buffer.append("b").append(Long.toBinaryString(v[i] & 0xFFFFFFFFL));
                        else buffer.append(v[i]);
                    }
                    value = PowerNBT.plugin.translate("data_elements", v.length) + buffer.toString();
                }
            }
            case LONGARRAY -> { // longarray
                if (start == 0 && end == 0) end = h_limit;
                long[] v = ((long[]) base);
                if (v.length == 0) {
                    value = PowerNBT.plugin.translate("data_emptyarray");
                } else if (start > v.length) {
                    value = "\n" + PowerNBT.plugin.translate("data_outofrange");
                } else {
                    StringBuilder buffer = new StringBuilder();
                    for (int i = start; i < end; i++) {
                        if (i >= v.length) break;
                        buffer.append("\n").append(type.color).append("[").append(i).append("] ").append(ChatColor.RESET);
                        if (hex) buffer.append("#").append(Long.toHexString(v[i] & 0xFFFFFFFFL));
                        else if (bin) buffer.append("b").append(Long.toBinaryString(v[i] & 0xFFFFFFFFL));
                        else buffer.append(v[i]);
                    }
                    value = PowerNBT.plugin.translate("data_elements", v.length) + buffer.toString();
                }
            }

        }
        return color + type.name + ChatColor.RESET + ": " + value;
    }
}
