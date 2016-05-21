package me.dpohvar.powernbt;

import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.command.CommandNBT;
import me.dpohvar.powernbt.completer.CompleterNBT;
import me.dpohvar.powernbt.completer.TypeCompleter;
import me.dpohvar.powernbt.listener.SelectListener;
import me.dpohvar.powernbt.utils.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

public class PowerNBT extends JavaPlugin {

    private static final boolean SILENT = true;
    public static PowerNBT plugin;
    public static final Charset charset = Charset.forName("UTF8");
    private final HashMap<String, Caller> callers = new HashMap<String, Caller>();
    private Translator translator;
    private static final Tokenizer tokenizer = new Tokenizer(
            null, null, null, Arrays.asList('\"'), null, Arrays.asList(' ')
    );
    private final String prefix = ChatColor.GOLD.toString() + ChatColor.BOLD + "[" + ChatColor.YELLOW + "PowerNBT" + ChatColor.GOLD + ChatColor.BOLD + "] " + ChatColor.RESET;
    private final String errorPrefix = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "[" + ChatColor.RED + "PowerNBT" + ChatColor.DARK_RED + ChatColor.BOLD + "] " + ChatColor.RESET;
    private TypeCompleter typeCompleter;

    /**
     * Get the folder where are stored saved files
     *
     * This files are used for command: <code>/nbt $filename</code>
     * @return folder
     */
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

    /**
     * Get plugin debug mode
     *
     * @return true if plugin in debug mode
     */
    public boolean isDebug() {
        return getConfig().getBoolean("debug");
    }

    /**
     * Set plugin debug mode
     *
     * @param val true to enable debug mode
     */
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
        String classMap = this.getConfig().getString("classmap");
        if (classMap != null && !classMap.isEmpty()) {
            File classMapFile = new File(getDataFolder(),classMap);
            ReflectionUtils.addReplacementsYaml(classMapFile);
        }
        String lang = this.getConfig().getString("lang");
        this.translator = new Translator(this, lang);
        this.typeCompleter = new TypeCompleter(getTemplateFolder());
        getServer().getPluginManager().registerEvents(new SelectListener(), this);
        NBTViewer.applyConfig(getConfig());
        getCommand("powernbt").setExecutor(new CommandNBT());
        getCommand("powernbt.").setExecutor(new CommandNBT(SILENT));
        getCommand("powernbt").setTabCompleter(new CompleterNBT());
        getCommand("powernbt.").setTabCompleter(new CompleterNBT());

        initializeUtils();
    }

    public TypeCompleter getTypeCompleter() {
        return typeCompleter;
    }

    private void initializeUtils() {
        printDebug(EntityUtils.entityUtils);
        printDebug(ItemStackUtils.itemStackUtils);
        printDebug(NBTBlockUtils.nbtBlockUtils);
        printDebug(NBTCompressedUtils.nbtCompressedUtils);
        if (getConfig().getBoolean("utils.chunk", false)) {
            printDebug(ChunkUtils.chunkUtils);
        }
        printDebug(NBTUtils.nbtUtils);
        printDebug(PacketUtils.packetUtils);
    }

    private void printDebug(Object t){
        if (isDebug()) getLogger().info("" + t);
    }

    /**
     * Get PowerNBT API
     *
     * @return api
     */
    public static NBTManager getApi(){
        return NBTManager.getInstance();
    }
}

