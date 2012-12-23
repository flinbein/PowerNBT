package me.dpohvar.powernbt;

import me.dpohvar.powernbt.command.CommandNBT;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.Tokenizer;
import me.dpohvar.powernbt.utils.Translator;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class PowerNBT extends JavaPlugin {
    public static PowerNBT plugin;
    private HashMap<CommandSender, Caller> callers = new HashMap<CommandSender, Caller>();
    private Translator translator;
    private static Tokenizer tokenizer = new Tokenizer(
            null, null, null, Arrays.asList('\"'), null, Arrays.asList(' ')
    );
    private String prefix = ChatColor.GOLD.toString() + ChatColor.BOLD + "[" + ChatColor.YELLOW + "PowerNBT" + ChatColor.GOLD + ChatColor.BOLD + "] " + ChatColor.RESET;
    private String errorPrefix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "[" + ChatColor.RED + "PowerNBT" + ChatColor.DARK_RED + ChatColor.BOLD + "] " + ChatColor.RESET;

    public File getNBTFilesFolder() {
        return new File(getDataFolder(), "nbt");
    }

    public Caller getCaller(CommandSender sender) {
        Caller c = callers.get(sender);
        if (c != null) return c;
        c = new Caller(sender);
        callers.put(sender, c);
        return c;
    }

    public File getLangFolder() {
        File lang = new File(getDataFolder(), "lang");
        lang.mkdirs();
        return lang;
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getErrorPrefix() {
        return errorPrefix;
    }

    public boolean isDebug() {
        return true;
    }

    public String translate(String key) {
        return translator.translate(key);
    }

    public String translate(String key, Object... values) {
        return translator.translate(key, values);
    }

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        String lang = this.getConfig().getString("lang");
        this.translator = new Translator(this, lang);
        getCommand("nbt").setExecutor(new CommandNBT());
    }
}

