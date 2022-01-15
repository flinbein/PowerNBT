package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.command.action.Action;
import me.dpohvar.powernbt.command.action.Argument;
import me.dpohvar.powernbt.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.query.NBTQuery;
import me.dpohvar.powernbt.utils.viewer.InteractiveViewer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class Caller extends NBTContainer<Caller> {
    private CommandSender owner;
    private boolean silent;
    private Object base;
    private Argument argument;
    private Action action;
    private final HashMap<String, NBTContainer<?>> variables = new HashMap<>();

    public Argument getArgument() {
        return argument;
    }

    public Action getAction() {
        return action;
    }

    public void hold(Argument argument, Action action) {
        this.argument = argument;
        this.action = action;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public CommandSender getOwner() {
        return owner;
    }

    public void setOwner(CommandSender owner) {
        this.owner = owner;
    }

    public HashMap<String, NBTContainer<?>> getVariables() {
        return variables;
    }

    public NBTContainer<?> getVariable(String name) {
        return variables.get(name);
    }

    public void setVariable(String name, NBTContainer<?> value) {
        variables.put(name, value);
    }

    public void removeVariable(String name) {
        variables.remove(name);
    }

    private boolean isInteractiveMode(Player player){
        return plugin.getConfig().getBoolean("editor.enabled");
    }

    public void send(Object o) {
        if (silent) return;
        String message = plugin.getPrefix() + o;
        if (message.length()>32743) message = message.substring(0,32743);
        owner.sendMessage(message);
    }

    public void sendValue(String prefix, Object value, boolean hex, boolean bin) {
        if (owner instanceof Player player && isInteractiveMode(player)) {
            InteractiveViewer viewer = plugin.getViewer();
            TextComponent component = new TextComponent("");
            component.addExtra(plugin.getPrefix());
            component.addExtra(prefix);
            if (prefix != null && !ChatColor.stripColor(prefix).isEmpty()) component.addExtra(" ");
            component.addExtra(viewer.getShortValue(value, hex, bin));
            player.spigot().sendMessage(component);
        } else {
            String message = plugin.getPrefix() + prefix + " " + NBTStaticViewer.getShortValueWithPrefix(value, hex, bin);
            if (message.length()>32743) message = message.substring(0,32743);
            owner.sendMessage(message);
            owner.sendMessage();
        }
    }

    public void sendValueView(String prefix, NBTContainer<?> container, NBTQuery query, int start, int end, boolean hex, boolean bin) throws Exception {
        if (owner instanceof Player player && isInteractiveMode(player)) {
            InteractiveViewer viewer = plugin.getViewer();
            TextComponent component = new TextComponent("");
            component.addExtra(plugin.getPrefix());
            component.addExtra(prefix);
            if (prefix != null && !ChatColor.stripColor(prefix).isEmpty()) component.addExtra(" ");
            component.addExtra(viewer.getFullValue(container, query, start, end, hex, bin));
            player.spigot().sendMessage(component);
        } else {
            Object value = container.getCustomTag(query);
            String message = plugin.getPrefix() + prefix + " " + NBTStaticViewer.getFullValue(value, start, end, hex, bin);
            if (message.length()>32743) message = message.substring(0,32743);
            owner.sendMessage(message);
            owner.sendMessage();
        }
    }

    public void handleException(Throwable o) {
        String message;
        if (o.getClass().equals(RuntimeException.class)) {
             message = plugin.getErrorPrefix() + o.getMessage();

        } else {
            message = plugin.getErrorPrefix() +
                    ChatColor.RED + ChatColor.BOLD +
                    o.getClass().getSimpleName() + ": " + ChatColor.RESET+ o.getMessage();
        }
        if (message.length()>32743) message = message.substring(0,32743);
        owner.sendMessage(message);
        if (plugin.isDebug()) {
            Bukkit.getLogger().log(Level.ALL, message, o);
        }
    }

    public Caller(CommandSender owner) {
        super("buffer");
        this.owner = owner;
    }

    @Override
    public List<String> getTypes() {
        return Arrays.asList("entity", "living", "entity_Player");
    }

    @Override
    public Caller getObject() {
        return this;
    }

    @Override
    public Object readTag() {
        return this.base;
    }

    @Override
    public void writeTag(Object base) {
        this.base = base;
    }

    @Override
    public Class<Caller> getContainerClass() {
        return Caller.class;
    }

    @Override
    public void eraseTag() {
        this.base = null;
    }

    @Override
    public String getSelector() {
        return "buffer";
    }
}
