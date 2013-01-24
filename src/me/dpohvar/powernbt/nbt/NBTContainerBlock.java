package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.PowerNBT;
import net.minecraft.server.v1_4_R1.EntityPlayer;
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
    public NBTTagCompound getTag() {
        NBTTagCompound compound = getFullTag();
        if (compound==null) return null;
        if(plugin.getConfig().getBoolean("display.block_location")==false){
            compound.remove("x");
            compound.remove("y");
            compound.remove("z");
        }
        if(plugin.getConfig().getBoolean("display.block_id")==false){
            compound.remove("id");
        }
        return compound;
    }

    public NBTTagCompound getFullTag() {
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
        Object tile = callMethod(block.getWorld(), "getTileEntityAt", new Class[]{int.class, int.class, int.class}, block.getX(), block.getY(), block.getZ());
        if (tile != null) {
            if(plugin.getConfig().getBoolean("display.block_location")==false){
                NBTTagCompound b = (NBTTagCompound) base;
                NBTTagCompound compound = getFullTag();
                b.set("x",compound.get("x"));
                b.set("y",compound.get("y"));
                b.set("z",compound.get("z"));

            }
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
    public String getName() {
        return PowerNBT.plugin.translate("object_block", block.getType().name(), block.getX(), block.getY(), block.getZ(), block.getWorld().getName());
    }
}
