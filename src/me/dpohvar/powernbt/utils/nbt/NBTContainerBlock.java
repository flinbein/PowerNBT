package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.utils.versionfix.XCraftWorld;
import me.dpohvar.powernbt.utils.versionfix.XNBTBase;
import me.dpohvar.powernbt.utils.versionfix.XTileEntity;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static me.dpohvar.powernbt.utils.versionfix.StaticValues.*;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.*;

public class NBTContainerBlock extends NBTContainer {

    Block block;

    public NBTContainerBlock(Block block) {
        this.block = block;
    }

    @Override
    public Block getObject() {
        return block;
    }

    @Override
    public List<String> getTypes() {
        return Arrays.asList("block", "block_" + block.getType().name());
    }

    @Override
    public XNBTBase getRootBase() {
        XCraftWorld w = getShell(XCraftWorld.class, block.getWorld());
        Object tile = w.getTileEntityAt(block.getX(), block.getY(), block.getZ());
        XNBTBase base = null;
        if (tile != null) {
            base = getShell(XNBTBase.class, getNew(classNBTTagCompound, noInput));
            callMethod(tile, "b", oneNBTTagCompound, base);
        }
        return base;
    }

    @Override
    public void setRootBase(XNBTBase base) {
        XCraftWorld w = getShell(XCraftWorld.class, block.getWorld());
        XTileEntity tile = getShell(XTileEntity.class, w.getTileEntityAt(block.getX(), block.getY(), block.getZ()));
        if (tile.getProxyObject() != null) {
            callMethod(tile, "a", oneNBTTagCompound, base);
            int maxDist = Bukkit.getServer().getViewDistance() * 32;
            for (Player p : block.getWorld().getPlayers()) {
                if (p.getLocation().distance(block.getLocation()) < maxDist) {
                    Object packet = tile.getUpdatePacket();
                    Object mPlayer = callMethod(p, "getHandle", noInput);
                    callMethod(mPlayer, "sendPacket", onePacket, packet);
                }
            }
        }
    }

    @Override
    public String getName() {
        return PowerNBT.plugin.translate("object_block", block.getType().name(), block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
    }
}
