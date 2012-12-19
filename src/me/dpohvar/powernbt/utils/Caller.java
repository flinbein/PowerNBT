package me.dpohvar.powernbt.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class Caller {
    private final CommandSender owner;
    private Listener listener;

    public CommandSender getOwner() {
        return owner;
    }

    public void send(Object o) {
        owner.sendMessage(plugin.getPrefix() + o);
    }

    public void handleException(Throwable o) {
        send(ChatColor.RED.toString() + ChatColor.BOLD + o.getClass().getSimpleName() + ChatColor.RED + ": " + o.getMessage());
        if (plugin.isDebug()) o.printStackTrace();
    }

    public Caller(CommandSender owner) {
        this.owner = owner;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
