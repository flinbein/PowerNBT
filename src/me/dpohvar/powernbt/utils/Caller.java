package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.utils.nbt.NBTContainer;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class Caller extends NBTContainer {
    private final CommandSender owner;
    private TempListener listener;
    private XNBTBase base;
    private HashMap<String, NBTContainer> variables = new HashMap<String, NBTContainer>();

    public CommandSender getOwner() {
        return owner;
    }

    public NBTContainer getVariable(String name) {
        return variables.get(name);
    }

    public void setVariable(String name, NBTContainer value) {
        variables.put(name, value);
    }

    public void removeVariable(String name) {
        variables.remove(name);
    }

    public void send(Object o) {
        owner.sendMessage(plugin.getPrefix() + o);
    }

    public void handleException(Throwable o) {
        if (o.getClass().equals(RuntimeException.class)) {
            owner.sendMessage(plugin.getErrorPrefix() + o.getMessage());
        } else {
            owner.sendMessage(plugin.getErrorPrefix() +
                    ChatColor.RED.toString() + ChatColor.BOLD +
                    o.getClass().getSimpleName() + ChatColor.GOLD + ": " + o.getMessage());
        }
        if (plugin.isDebug()) o.printStackTrace();
    }

    public Caller(CommandSender owner) {
        this.owner = owner;
    }

    public TempListener getListener() {
        return listener;
    }

    public void unregisterListener() {
        if (listener != null) listener.unregister();
        listener = null;
    }

    public void setListener(TempListener listener) {
        this.listener = listener;
    }

    public XNBTBase getBase() {
        return base;
    }

    @Override
    public Object getObject() {
        return this;
    }

    @Override
    public XNBTBase getRootBase() {
        return this.base;
    }

    @Override
    public void setRootBase(XNBTBase base) {
        this.base = base;
    }

    @Override
    public String getName() {
        return owner.getName();
    }

    @Override
    public void removeRootBase() {
        this.base = null;
    }
}
