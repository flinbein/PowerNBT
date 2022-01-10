package me.dpohvar.powernbt.utils;

import java.util.*;

public class Tokenizer {
    private enum Mode {
        OPERAND, LINE_COMMENT, FULL_COMMENT, TEXT,
    }

    private final String lineComment;
    private final String openComment;
    private final String closeComment;
    private HashSet<Character> quotes = new HashSet<>();
    private HashSet<Character> singleChars = new HashSet<>();
    private HashSet<Character> delimiters = new HashSet<>();
    private HashMap<Character, Parentheses> parenthesesOpen = new HashMap<>();
    private HashMap<Character, Parentheses> parenthesesClose = new HashMap<>();

    public Tokenizer(
            String lineComment,
            String openComment,
            String closeComment,
            Collection<Character> quotes,
            Collection<Character> singleChars,
            Collection<Character> delimiters,
            String parenthesesPairs
    ) {
        this.lineComment = lineComment;
        this.openComment = openComment;
        this.closeComment = closeComment;
        if (quotes != null) this.quotes = new HashSet<>(quotes);
        if (singleChars != null) this.singleChars = new HashSet<>(singleChars);
        if (delimiters != null) this.delimiters = new HashSet<>(delimiters);
        if (parenthesesPairs != null) {
            for (int i = 1; i < parenthesesPairs.length(); i=i+2) {
                char openP = parenthesesPairs.charAt(i - 1);
                char closeP = parenthesesPairs.charAt(i);
                Parentheses par = new Parentheses(openP, closeP);
                this.parenthesesOpen.put(openP, par);
                this.parenthesesClose.put(closeP, par);
            }
        }
    }

    private record Parentheses (char openP, char closeP){};

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

    private boolean isOpenPar(char s) {
        return parenthesesOpen.containsKey(s);
    }

    private boolean isClosePar(char s) {
        return parenthesesClose.containsKey(s);
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
        TreeMap<Integer, String> tokens = new TreeMap<>();
        LinkedList<Parentheses> openedPar = new LinkedList<>();
        Mode mode = Mode.OPERAND;
        VarStringBuffer buffer = new VarStringBuffer();
        char quote = 0;
        tokenizer:
        while (true) {
            int position = input.getPosition();
            Character c = input.read();
            switch (mode) {
                case OPERAND -> {
                    if (c == null) {
                        Parentheses lastParentheses = openedPar.peekLast();
                        if (lastParentheses != null) {
                            throw new RuntimeException("Unbalanced " + lastParentheses.openP + lastParentheses.closeP);
                        }
                        if (buffer.hasSome()) tokens.put(position - buffer.length(), buffer.toString());
                        break tokenizer;
                    } else if (isOpenComment(buffer.toString(), c)) {
                        buffer.clear();
                        mode = Mode.FULL_COMMENT;
                    } else if (isLineComment(buffer.toString(), c)) {
                        buffer.clear();
                        mode = Mode.LINE_COMMENT;
                    } else if (isDelimiter(c)) {
                        if (openedPar.isEmpty()) {
                            if (buffer.hasSome()) tokens.put(position - buffer.length(), buffer.toString());
                            buffer.clear();
                        } else {
                            buffer.append(c);
                        }
                    } else if (isOpenPar(c)) {
                        buffer.append(c);
                        openedPar.add(parenthesesOpen.get(c));
                    } else if (isClosePar(c)) {
                        Parentheses curParentheses = parenthesesClose.get(c);
                        Parentheses lastParentheses = openedPar.removeLast();
                        if (curParentheses != lastParentheses) {
                            throw new RuntimeException("Unexpected " + c + "at " + position + ": opened " + lastParentheses.closeP);
                        }
                        buffer.append(c);
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
                }
                case TEXT -> {
                    if (c == null) {
                        throw new RuntimeException("missing " + quote + " : " + buffer);
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
                }
                case FULL_COMMENT -> {
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
                }
                case LINE_COMMENT -> {
                    while (true) {
                        Character t = input.read();
                        if (t == null || t == '\n') {
                            mode = Mode.OPERAND;
                            break;
                        }
                    }
                }
            }
        }
        return tokens;
    }


    private static class VarCharInputStream {
        private final char[] c;
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

    private static class VarStringBuffer {
        private final ArrayList<Character> a = new ArrayList<Character>();

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
