package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.PowerNBT;
import org.bukkit.ChatColor;

import java.util.LinkedList;
import java.util.Queue;

public class StringParser {
    private static char char_color = 0;
    private static char char_space = 0;
    static {
        try{
            PowerNBT plugin = PowerNBT.plugin;
            String s;
            s = plugin.getConfig().getString("formatting.char_color");
            if(s!=null && s.length()==1) char_color = s.charAt(0);
            s = plugin.getConfig().getString("formatting.char_space");
            if(s!=null && s.length()==1) char_space = s.charAt(0);
        } catch (Exception ignored){
        }
    }

    public static String parse(String input) {
        Queue<Character> chars = new LinkedList<Character>();
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) chars.add(c);
        while (!chars.isEmpty()) {
            char c = chars.poll();
            if (c == char_color && c != 0) {
                sb.append(ChatColor.COLOR_CHAR);
            } else if (c == char_space && c != 0) {
                sb.append(' ');
            } else if (c == '\\') {
                if (chars.isEmpty()) throw new RuntimeException("\\ is last symbol in string");
                char t = chars.poll();
                switch (t) {
                    case 'r':
                        sb.append('\r');
                        break;
                    case '&':
                        sb.append('&');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case '_':
                        sb.append(' ');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'c':
                        sb.append(ChatColor.COLOR_CHAR);
                        break;
                    case 'u': {
                        if (chars.size() < 4) throw new RuntimeException("\\uXXXX");
                        StringBuilder s = new StringBuilder();
                        s.append(chars.poll());
                        s.append(chars.poll());
                        s.append(chars.poll());
                        s.append(chars.poll());
                        String l = s.toString();
                        if (!l.matches("[a-fA-F0-9]{4}")) throw new RuntimeException("unresolved unicode char \\u" + l);
                        int u = Integer.parseInt(l, 16);
                        sb.append((char) u);
                        break;
                    }
                    default:
                        if(t != 0 && t == char_color) sb.append(char_color);
                        else if (t != 0 && t == char_space) sb.append(char_space);
                        else throw new RuntimeException("no char: \\" + t);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String wrap(String raw) {
        raw = raw.replace("\\", "\\\\");
        raw = raw.replace("\n", "\\n");
        raw = raw.replace("\b", "\\b");
        raw = raw.replace("\r", "\\r");
        raw = raw.replace("\t", "\\t");
        raw = raw.replace("\f", "\\f");
        if(char_color!=0) raw = raw.replace(""+char_color, "\\"+char_color);
        if(char_space!=0) raw = raw.replace(""+char_space, "\\"+char_space);
        if(char_color!=0) raw = raw.replace("" + ChatColor.COLOR_CHAR, ""+char_color);
        if(char_space!=0) raw = raw.replace(" ", ""+char_space);
        return raw;
    }
}
