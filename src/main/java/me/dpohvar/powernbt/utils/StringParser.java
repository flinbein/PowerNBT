package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.exception.ParseException;
import org.bukkit.ChatColor;

import static me.dpohvar.powernbt.utils.StringParser.Mode.*;

/**
 * Created with IntelliJ IDEA.
 * User: DPOH-VAR
 * Date: 25.07.13
 * Time: 1:35
 */
public class StringParser {
    public enum Mode {
        CHAR, ESCAPE, UNICODE, SPACE
    }

    public static String parse(String input) throws ParseException {
        char[] chars = input.toCharArray();
        StringBuilder buffer = new StringBuilder();
        StringBuilder unicode = new StringBuilder();
        Mode mode = Mode.CHAR;
        int row = 0;
        int col = -1;
        parse:
        for (char c : chars) {
            if (c == '\n') {
                col = 0;
                row++;
            } else {
                col++;
            }
            switch (mode) {
                case CHAR -> {
                    switch (c) {
                        case '\\' -> mode = ESCAPE;
                        case '&' -> buffer.append(ChatColor.COLOR_CHAR);
                        case '\"' -> throw new ParseException(input, row, col, "unescaped '\"'");
                        default -> buffer.append(c);
                    }
                }
                case ESCAPE -> {
                    switch (c) {
                        case '\\' -> buffer.append('\\');
                        case '\'' -> buffer.append('\'');
                        case '\"' -> buffer.append('\"');
                        case '0' -> buffer.append('\0');
                        case '1' -> buffer.append('\1');
                        case '2' -> buffer.append('\2');
                        case '3' -> buffer.append('\3');
                        case '4' -> buffer.append('\4');
                        case '5' -> buffer.append('\5');
                        case '6' -> buffer.append('\6');
                        case '7' -> buffer.append('\7');
                        case 'r' -> buffer.append('\r');
                        case 't' -> buffer.append('\t');
                        case 'f' -> buffer.append('\f');
                        case 'b' -> buffer.append('\b');
                        case 'n' -> buffer.append('\n');
                        case '&' -> buffer.append('&');
                        case '_' -> buffer.append(' ');
                        case 'u' -> {
                            mode = UNICODE;
                            continue parse;
                        }
                        case ' ', '\t' -> {
                            mode = SPACE;
                            continue parse;
                        }
                        case '\r', '\n' -> {
                            buffer.append(c);
                            mode = SPACE;
                            continue parse;
                        }
                        default -> throw new ParseException(input, row, col, "can't escape symbol " + c);
                    }
                    mode = CHAR;
                }
                case UNICODE -> {
                    switch (c) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'a', 'b', 'c', 'd', 'e', 'f' -> {
                            unicode.append(c);
                            if (unicode.length() == 4) {
                                char character = (char) Integer.parseInt(unicode.toString(), 16);
                                buffer.append(character);
                                unicode = new StringBuilder();
                                mode = CHAR;
                            }
                        }
                        default -> {
                            throw new ParseException(input, row, col, "unexpected hex character: " + c);
                        }
                    }
                }
                case SPACE -> {
                    switch (c) {
                        case '\t', '\r', ' ' -> {}
                        case '\n' -> buffer.append(c);
                        case '\\' -> mode = ESCAPE;
                        case '\"' -> throw new ParseException(input, row, col, "unescaped '\"'");
                        default -> {
                            buffer.append(c);
                            mode = CHAR;
                        }
                    }
                }
                default -> throw new ParseException(input, row, col, "unknown");
            }
        }
        if (mode == CHAR) return buffer.toString();
        else throw new ParseException(input, row, col, "unexpected end of string");
    }

    public static String wrap(String raw) {
        return raw
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\b", "\\b")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\f", "\\f")
                .replace("\"", "\\\"")
                .replace("  ", " \\_")
                .replace("&", "\\&")
                .replace(String.valueOf(ChatColor.COLOR_CHAR), "&")
                ;
    }

    public static String wrapToQuotes(String raw) {
        return "\""+wrap(raw)+"\"";
    }

    public static String wrapToQuotesIfNeeded(String raw) {
        if (raw.isEmpty()) return "\"\"";
        if (raw.matches("\\s")) return wrapToQuotes(raw);
        String wrapped = wrap(raw);
        if (wrapped.equals(raw)) return raw;
        return "\""+wrapped+"\"";
    }
}
