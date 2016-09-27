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
    public static enum Mode {
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
                case CHAR: {
                    switch (c) {
                        case '\\': {
                            mode = ESCAPE;
                            continue parse;
                        }
                        case '&': {
                            buffer.append(ChatColor.COLOR_CHAR);
                            continue parse;
                        }
                        case '\"': {
                            throw new ParseException(input, row, col, "unescaped '\"'");
                        }
                        default: {
                            buffer.append(c);
                            continue parse;
                        }
                    }
                }
                case ESCAPE: {
                    switch (c) {
                        case '\\':
                            buffer.append('\\');
                            break;
                        case '\'':
                            buffer.append('\'');
                            break;
                        case '\"':
                            buffer.append('\"');
                            break;
                        case '0':
                            buffer.append('\0');
                            break;
                        case '1':
                            buffer.append('\1');
                            break;
                        case '2':
                            buffer.append('\2');
                            break;
                        case '3':
                            buffer.append('\3');
                            break;
                        case '4':
                            buffer.append('\4');
                            break;
                        case '5':
                            buffer.append('\5');
                            break;
                        case '6':
                            buffer.append('\6');
                            break;
                        case '7':
                            buffer.append('\7');
                            break;
                        case 'r':
                            buffer.append('\r');
                            break;
                        case 't':
                            buffer.append('\t');
                            break;
                        case 'f':
                            buffer.append('\f');
                            break;
                        case 'b':
                            buffer.append('\b');
                            break;
                        case 'n':
                            buffer.append('\n');
                            break;
                        case '&':
                            buffer.append('&');
                            break;
                        case '_':
                            buffer.append(' ');
                            break;
                        case 'u': {
                            mode = UNICODE;
                            continue parse;
                        }
                        case ' ':
                        case '\t': {
                            mode = SPACE;
                            continue parse;
                        }
                        case '\r':
                        case '\n': {
                            buffer.append(c);
                            mode = SPACE;
                            continue parse;
                        }
                        default: {
                            throw new ParseException(input, row, col, "can't escape symbol " + c);
                        }
                    }
                    mode = CHAR;
                    continue parse;
                }
                case UNICODE: {
                    switch (c) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f': {
                            unicode.append(c);
                            if (unicode.length() == 4) {
                                char character = (char) Integer.parseInt(unicode.toString(), 16);
                                buffer.append(character);
                                unicode = new StringBuilder();
                                mode = CHAR;
                            }
                            continue parse;
                        }
                        default: {
                            throw new ParseException(input, row, col, "unexpected hex character: " + c);
                        }
                    }
                }
                case SPACE: {
                    switch (c) {
                        case '\t':
                        case '\r':
                        case ' ': {
                            continue parse;
                        }
                        case '\n': {
                            buffer.append(c);
                            continue parse;
                        }
                        case '\\': {
                            mode = ESCAPE;
                            continue parse;
                        }
                        case '\"': {
                            throw new ParseException(input, row, col, "unescaped '\"'");
                        }
                        default: {
                            buffer.append(c);
                            mode = CHAR;
                            continue parse;
                        }

                    }
                }
                default: {
                    throw new ParseException(input, row, col, "unknown");
                }
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
            .replace("&", "\\&")
            .replace(String.valueOf(ChatColor.COLOR_CHAR), "&")
        ;
    }
}
