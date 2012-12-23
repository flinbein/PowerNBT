package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.PowerNBT;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class TempListener implements Listener {

    public final TempListener register() {
        Bukkit.getPluginManager().registerEvents(this, PowerNBT.plugin);
        return this;
    }

    public final TempListener unregister() {
        HandlerList.unregisterAll(this);
        return this;
    }
}
