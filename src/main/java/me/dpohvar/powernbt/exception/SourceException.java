package me.dpohvar.powernbt.exception;

import org.bukkit.ChatColor;

/**
 * Created with IntelliJ IDEA.
 * User: DPOH-VAR
 * Date: 30.06.13
 * Time: 11:20
 */
public abstract class SourceException extends RuntimeException {
    protected String source;
    protected int row, col;

    public SourceException(String source, int row, int col, Throwable reason) {
        super(reason);
        this.source = source;
        this.row = row;
        this.col = col;
    }

    public SourceException(String source, int row, int col, String reason) {
        super(reason);
        this.source = source;
        this.row = row;
        this.col = col;
    }

    public String getErrorString() {
        if (source == null) return ChatColor.translateAlternateColorCodes('&', "&c[no source]&r");
        String[] lines = source.split("\n");
        if (lines.length == row) return ChatColor.translateAlternateColorCodes('&', "&e[end of source]&r");
        else if (lines.length < row) return ChatColor.translateAlternateColorCodes('&', "&c[unknown source]&r");
        String line = lines[row];
        if (col == line.length()) return ChatColor.translateAlternateColorCodes('&', "&e[end of line]&r ") + line;
        if (col > line.length()) return ChatColor.translateAlternateColorCodes('&', "&c[unknown position]&r ") + line;
        StringBuilder builder = new StringBuilder();
        builder.append(line.substring(0, col));
        builder.append(ChatColor.RED);
        builder.append(line.charAt(col));
        builder.append(ChatColor.RESET);
        builder.append(line.substring(col + 1));
        return builder.toString();
    }


}
