package me.dpohvar.powernbt.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeMap;

public class Tokenizer {
    private enum Mode {
        OPERAND, LINE_COMMENT, FULL_COMMENT, TEXT,
    }

    private String lineComment;
    private String openComment;
    private String closeComment;
    private HashSet<Character> quotes = new HashSet<Character>();
    private HashSet<Character> singleChars = new HashSet<Character>();
    private HashSet<Character> delimiters = new HashSet<Character>();

    public Tokenizer(
            String lineComment,
            String openComment,
            String closeComment,
            Collection<Character> quotes,
            Collection<Character> singleChars,
            Collection<Character> delimiters
    ) {
        this.lineComment = lineComment;
        this.openComment = openComment;
        this.closeComment = closeComment;
        if (quotes != null) this.quotes = new HashSet<Character>(quotes);
        if (singleChars != null) this.singleChars = new HashSet<Character>(singleChars);
        if (delimiters != null) this.delimiters = new HashSet<Character>(delimiters);
    }

    private boolean isQuote(char c) {
        return quotes.contains(c);
    }

    private boolean isSingleChar(char c) {
        return singleChars.contains(c);
    }

    private boolean isDelimiter(char c) {
        return delimiters.contains(c);
    }

    private boolean isOpenComment(String s, char c) {
        return isOpenComment(s + c);
    }

    private boolean isOpenComment(String s) {
        return openComment != null && openComment.equals(s);
    }

    private boolean isCloseComment(String s, char c) {
        return isCloseComment(s + c);
    }

    private boolean isCloseComment(String s) {
        return closeComment != null && closeComment.equals(s);
    }

    private boolean isLineComment(String s, char c) {
        return isLineComment(s + c);
    }

    private boolean isLineComment(String s) {
        return lineComment != null && lineComment.equals(s);
    }

    public TreeMap<Integer, String> tokenize(String inputString) {
        VarCharInputStream input = new VarCharInputStream(inputString);
        TreeMap<Integer, String> tokens = new TreeMap<Integer, String>();
        Mode mode = Mode.OPERAND;
        VarStringBuffer buffer = new VarStringBuffer();
        char quote = 0;
        tokenizer:
        while (true) {
            int position = input.getPosition();
            Character c = input.read();
            switch (mode) {
                case OPERAND: {
                    if (c == null) {
                        if (buffer.hasSome()) tokens.put(position - buffer.length(), buffer.toString());
                        break tokenizer;
                    } else if (isOpenComment(buffer.toString(), c)) {
                        buffer.clear();
                        mode = Mode.FULL_COMMENT;
                    } else if (isLineComment(buffer.toString(), c)) {
                        buffer.clear();
                        mode = Mode.LINE_COMMENT;
                    } else if (isDelimiter(c)) {
                        if (buffer.hasSome()) tokens.put(position - buffer.length(), buffer.toString());
                        buffer.clear();
                    } else if (isQuote(c)) {
                        buffer.append(c);
                        quote = c;
                        mode = Mode.TEXT;
                    } else if (isSingleChar(c)) {
                        if (buffer.hasSome()) tokens.put(position - buffer.length(), buffer.toString());
                        tokens.put(position - 1, c.toString());
                        buffer.clear();
                    } else {
                        buffer.append(c);
                    }
                    break;
                }
                case TEXT: {
                    if (c == null) {
                        throw new RuntimeException("missing " + quote + " : " + buffer.toString());
                    }
                    if (c == '\\') {
                        buffer.append(c);
                        buffer.append(input.read());
                    } else if (c == quote) {
                        buffer.append(c);
                        mode = Mode.OPERAND;
                    } else {
                        buffer.append(c);
                    }
                    break;
                }
                case FULL_COMMENT: {
                    VarStringBuffer buf = new VarStringBuffer();
                    while (true) {
                        Character t = input.read();
                        if (t == null) {
                            throw new RuntimeException("missing " + closeComment);
                        } else if (isDelimiter(t)) {
                            if (isCloseComment(buf.toString())) break;
                            buf.clear();
                        } else {
                            buf.append(t);
                        }
                    }
                    mode = Mode.OPERAND;
                    break;
                }
                case LINE_COMMENT: {
                    while (true) {
                        Character t = input.read();
                        if (t == null || t == '\n') {
                            mode = Mode.OPERAND;
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return tokens;
    }


    private class VarCharInputStream {
        private char[] c;
        private int p = 0;

        public VarCharInputStream(String s) {
            c = s.toCharArray();
        }

        public int getPosition() {
            return p;
        }

        public Character read() {
            Character x = null;
            if (p < c.length) x = c[p];
            p++;
            return x;
        }
    }

    private class VarStringBuffer {
        private ArrayList<Character> a = new ArrayList<Character>();

        public void append(Character c) {
            a.add(c);
        }

        public int length() {
            return a.size();
        }

        public void clear() {
            a.clear();
        }

        public boolean hasSome() {
            return !a.isEmpty();
        }

        public String toString() {
            int s = a.size();
            char[] c = new char[s];
            int i = 0;
            while (i < s) c[i] = a.get(i++);
            return new String(c);
        }
    }
}
