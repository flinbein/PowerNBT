package me.dpohvar.powernbt.utils.viewer;

import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.api.NBTManagerUtils;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

public class DisplayValueHelper {

    public static String getShortValue(ViewerStyle style, Object base, int rowLimit, boolean hex, boolean bin){
        String value = getShortValueFull(style, base, hex, bin);
        int overText = Math.max(org.bukkit.ChatColor.stripColor(value).length()-rowLimit,value.length()-rowLimit*2);
        String resetPattern = org.bukkit.ChatColor.RESET.toString();
        if ( value.endsWith(resetPattern) ) {
            value = value.substring(0,value.length()-resetPattern.length());
        }
        if (overText>0){
            value=value.substring(0,rowLimit-1).concat(style.getColor("etc")+"\u2026");
        }
        return value;
    }

    public static String getStringValue(ViewerStyle style, String textValue, int start, int end, int limit, boolean hex){
        boolean postfix = false;//
        int toEnd = end;
        if (start == 0 && end == 0) {
            toEnd = limit;
            postfix = true;
        }
        if (start > textValue.length()) {
            return "";
        }

        if (toEnd >= textValue.length()) {
            toEnd = textValue.length();
            postfix = false;
        }
        textValue = textValue.substring(start, toEnd);

        if (hex) {
            ArrayList<String> h = new ArrayList<>();
            for (byte b : textValue.getBytes(StandardCharsets.UTF_8)) h.add(Integer.toHexString(b & 0xFF));
            textValue = StringUtils.join(h, ' ');
        }
        if (postfix) textValue += '\u2026';

        return textValue;
    }

    private static String getShortValueFull(ViewerStyle style, Object base, boolean hex, boolean bin){

        if (base == null) return "null";
        if (base instanceof Boolean) return base.toString();
        if (base instanceof Character) return base.toString();
        if (base instanceof Number number) {
            if (hex) return toHex(number);
            if (bin) return toBinary(number);
            return number.toString();
        }
        if (base instanceof String value) {
            if (hex) {
                ArrayList<String> h = new ArrayList<>();
                for (byte b : value.getBytes(StandardCharsets.UTF_8)) h.add(Integer.toHexString(b & 0xFF));
                return StringUtils.join(h, ' ');
            }
            return value;
        }
        if (base instanceof Map<?,?> tag) {
            if (tag.size() == 0) {
                return "empty";
            } else {
                ArrayList<String> h = new ArrayList<>();
                for (Map.Entry<?, ?> b : tag.entrySet()) {
                    Object key = b.getKey();
                    if (!(key instanceof String stringKey)) continue;
                    ChatColor color = style.getColorByValue(b.getValue());
                    h.add(color + stringKey + ChatColor.RESET);
                }
                return "{" + tag.size() + "}: " + StringUtils.join(h, ",");
            }
        }
        Object[] array = NBTManagerUtils.convertToObjectArrayOrNull(base);
        if (array != null) {
            if (array.length == 0) return "[0] empty";
            return "["+array.length+"]";
        }
        return "unknown value";
    }

    private static String toHex(Number value){
        if (value instanceof Byte number) return "#"+Integer.toHexString(number & 0xFF);
        if (value instanceof Short number) return "#"+Integer.toHexString(number & 0xFFFF);
        if (value instanceof Integer number) return "#"+Long.toHexString(number.longValue() & 0xFFFFFFFFL);
        if (value instanceof Long number) return "#"+Long.toHexString(number);
        if (value instanceof Float number) return "#"+Float.toHexString(number);
        if (value instanceof Double number) return "#"+Double.toHexString(number);
        return String.valueOf(value);
    }

    private static String toBinary(Number value){
        if (value instanceof Byte number) return "#"+Integer.toBinaryString(number & 0xFF);
        if (value instanceof Short number) return "#"+Integer.toBinaryString(number & 0xFFFF);
        if (value instanceof Integer number) return "#"+Long.toBinaryString(number.longValue() & 0xFFFFFFFFL);
        if (value instanceof Long number) return "#"+Long.toBinaryString(number);
        return String.valueOf(value);
    }
}
