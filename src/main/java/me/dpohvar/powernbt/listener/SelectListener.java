package me.dpohvar.powernbt.listener;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.command.action.Action;
import me.dpohvar.powernbt.command.action.Argument;
import me.dpohvar.powernbt.nbt.*;
import me.dpohvar.powernbt.utils.Caller;
import me.dpohvar.powernbt.utils.NBTQuery;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

import static me.dpohvar.powernbt.PowerNBT.plugin;

/**
 * Created by DPOH-VAR
 * 20.12.13 2:05.
 */
public class SelectListener implements Listener {



    @EventHandler
    public void block(PlayerInteractEvent event) {
        if (!event.getAction().equals(org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK)) return;
        Player player = event.getPlayer();
        Caller caller = PowerNBT.plugin.getCaller(player);
        try {
            Argument argument = caller.getArgument();
            Action action = caller.getAction();
            if (argument == null || action == null) return;
            if (!player.isSneaking()) caller.hold(null,null);
            Block b = event.getClickedBlock();
            argument.select(new NBTContainerBlock(b));
            action.execute();
            event.setCancelled(true);
        } catch (Throwable t) {
            caller.handleException(t);
        }
    }

    @EventHandler
    public void entity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Caller caller = PowerNBT.plugin.getCaller(player);
        try {
            Argument argument = caller.getArgument();
            Action action = caller.getAction();
            if (argument == null || action == null) return;
            if (!player.isSneaking()) caller.hold(null,null);
            Entity e = event.getRightClicked();
            argument.select(new NBTContainerEntity(e));
            action.execute();
            event.setCancelled(true);
        } catch (Throwable t) {
            caller.handleException(t);
        }
    }

    @EventHandler
    public void inventory(InventoryClickEvent event) {
        GameMode gm = event.getWhoClicked().getGameMode();
        if (event.isRightClick() && gm == GameMode.CREATIVE) return;
        ItemStack cursor = event.getCursor();
        if (cursor!=null && !cursor.getType().equals(Material.AIR)) return;
        ItemStack item = event.getCurrentItem();
        if (item==null || item.getType().equals(Material.AIR)) return;
        HumanEntity human = event.getWhoClicked();
        if (!(human instanceof Player)) return;
        Player player = (Player) human;
        Caller caller = PowerNBT.plugin.getCaller(player);
        try {
            Argument argument = caller.getArgument();
            Action action = caller.getAction();
            if (argument == null || action == null) return;
            if (!event.isShiftClick()) caller.hold(null,null);
            argument.select(new NBTContainerItem(item));
            action.execute();
            event.setCancelled(true);
        } catch (Throwable t) {
            caller.handleException(t);
        }
    }

}
