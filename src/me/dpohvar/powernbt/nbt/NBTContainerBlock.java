package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.PowerNBT;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static me.dpohvar.powernbt.utils.StaticValues.*;
import static me.dpohvar.powernbt.utils.VersionFix.callMethod;
import static me.dpohvar.powernbt.utils.VersionFix.getField;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerBlock extends NBTContainer {

    Block block;

    public NBTContainerBlock(Block block) {
        this.block = block;
    }

    public Block getObject() {
        return block;
    }

    @Override
    public List<String> getTypes() {
        return Arrays.asList("block", "block_" + block.getType().name());
    }

    @Override
    public NBTTagCompound getCustomTag() {
        NBTTagCompound compound = getTag();
        if (compound==null) return null;
        if( plugin.getConfig().getBoolean("tags.block_ignore_location")){
            compound.remove("x");
            compound.remove("y");
            compound.remove("z");
        }
        return compound;
    }

    public NBTTagCompound getTag() {
        Object tile = callMethod(block.getWorld(), "getTileEntityAt", new Class[]{int.class, int.class, int.class}, block.getX(), block.getY(), block.getZ());
        NBTTagCompound base = null;
        if (tile != null) {
            base = new NBTTagCompound();
            callMethod(tile, "b", oneNBTTagCompound, base.getHandle());
        }
        return base;
    }

    @Override
    public void setTag(NBTBase base) {
        if (!(base instanceof NBTTagCompound)) return;
        NBTTagCompound b = (NBTTagCompound) base;
        NBTTagCompound original = getTag();
        if (!b.has("x")) b.set("x",original.get("x"));
        if (!b.has("y")) b.set("y",original.get("y"));
        if (!b.has("z")) b.set("z",original.get("z"));
        Object tile = callMethod(block.getWorld(), "getTileEntityAt", new Class[]{int.class, int.class, int.class}, block.getX(), block.getY(), block.getZ());
        if (tile != null) {
            callMethod(tile, "a", oneNBTTagCompound, base.getHandle());
            int maxDist = Bukkit.getServer().getViewDistance() * 32;
            for (Player p : block.getWorld().getPlayers()) {
                if (p.getLocation().distance(block.getLocation()) < maxDist) {
                    Object packet = callMethod(tile, "getUpdatePacket", new Class[0]);
                    Object mPlayer = callMethod(p, "getHandle", noInput);
                    Object connection = getField(mPlayer,classEntityPlayer,"playerConnection");
                    callMethod(connection, "sendPacket", onePacket, packet);
                }
            }
        }
    }

    @Override
    public void setCustomTag(NBTBase base) {
        if (!(base instanceof NBTTagCompound)) return;
        NBTTagCompound b = (NBTTagCompound) base;
        if( plugin.getConfig().getBoolean("tags.block_ignore_location")){
            NBTTagCompound original = getTag();
            b.set("x",original.get("x"));
            b.set("y",original.get("y"));
            b.set("z",original.get("z"));
        }
        setTag(base);
    }

    @Override
    public String getName() {
        return PowerNBT.plugin.translate("object_block", block.getType().name(), block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
    }
}
