package me.dpohvar.powernbt.utils;

import org.bukkit.ChatColor;

import java.util.LinkedList;
import java.util.Queue;

public class StringParser {
    public static String parse(String input) {
        Queue<Character> chars = new LinkedList<Character>();
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) chars.add(c);
        while (!chars.isEmpty()) {
            char c = chars.poll();
            if (c == '\\') {
                if (chars.isEmpty()) throw new RuntimeException("\\ is last symbol in string");
                char t = chars.poll();
                switch (t) {
                    case 'r':
                        sb.append('\r');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case 't':
                        sb.append('\t');
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
                        throw new RuntimeException("no char: \\" + t);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
