package me.dpohvar.powernbt;

import me.dpohvar.powernbt.command.CommandNBT;
import me.dpohvar.powernbt.completer.CompleterNBT;
import me.dpohvar.powernbt.completer.TypeCompleter;
import me.dpohvar.powernbt.nbt.NBTTagCompound;
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

    public static final int versionAPI = 0x000401;

    // public HashMap<Integer,NBTTagCompound> entityExtra = new HashMap<Integer, NBTTagCompound>();
    // public HashMap<Block,NBTTagCompound> tileExtra = new HashMap<Block, NBTTagCompound>();

    public static PowerNBT plugin;
    private HashMap<String, Caller> callers = new HashMap<String, Caller>();
    private Translator translator;
    private static Tokenizer tokenizer = new Tokenizer(
            null, null, null, Arrays.asList('\"'), null, Arrays.asList(' ')
    );
    private String prefix = ChatColor.GOLD.toString() + ChatColor.BOLD + "[" + ChatColor.YELLOW + "PowerNBT" + ChatColor.GOLD + ChatColor.BOLD + "] " + ChatColor.RESET;
    private String errorPrefix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "[" + ChatColor.RED + "PowerNBT" + ChatColor.DARK_RED + ChatColor.BOLD + "] " + ChatColor.RESET;
    private TypeCompleter typeCompleter;

    public File getNBTFilesFolder() {
        return new File(getDataFolder(), "nbt");
    }

    public Caller getCaller(CommandSender sender) {
        Caller c = callers.get(sender.getName());
        if (c != null) {
            c.setOwner(sender);
            return c;
        }
        c = new Caller(sender);
        callers.put(sender.getName(), c);
        return c;
    }

    public File getLangFolder() {
        File lang = new File(getDataFolder(), "lang");
        lang.mkdirs();
        return lang;
    }

    public File getTemplateFolder() {
        File lang = new File(getDataFolder(), "templates");
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
        return getConfig().getBoolean("debug");
    }

    public void setDebug(boolean val) {
        getConfig().set("debug", val);
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
        this.typeCompleter = new TypeCompleter(getTemplateFolder());
        getCommand("nbt").setExecutor(new CommandNBT());
        getCommand("nbt").setTabCompleter(new CompleterNBT());
    }

    public TypeCompleter getTypeCompleter() {
        return typeCompleter;
    }
}

