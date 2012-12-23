package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.PowerNBT;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class SimpleListener implements Listener {

    public final SimpleListener register() {
        Bukkit.getPluginManager().registerEvents(this, PowerNBT.plugin);
        return this;
    }

    public final SimpleListener unregister() {
        HandlerList.unregisterAll(this);
        return this;
    }
}
