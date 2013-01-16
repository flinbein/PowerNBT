package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.nbt.NBTBase;
import me.dpohvar.powernbt.nbt.NBTContainer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class Caller extends NBTContainer {
    private CommandSender owner;
    private TempListener listener;
    private NBTBase base;
    private HashMap<String, NBTContainer> variables = new HashMap<String, NBTContainer>();

    public CommandSender getOwner() {
        return owner;
    }

    public void setOwner(CommandSender owner) {
        this.owner = owner;
    }

    public HashMap<String, NBTContainer> getVariables() {
        return variables;
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


    @Override
    public List<String> getTypes() {
        return Arrays.asList("entity", "living", "entity_Player");
    }

    @Override
    public NBTBase getTag() {
        return this.base;
    }

    @Override
    public void setTag(NBTBase base) {
        this.base = base;
    }

    @Override
    public String getName() {
        return owner.getName();
    }

    @Override
    public void removeTag() {
        this.base = null;
    }
}
