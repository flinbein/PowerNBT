package me.dpohvar.powernbt.utils;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

/**
 * Created by DPOH-VAR on 23.01.14
 */
public final class NBTBlockUtils {

    /**
     * static access to utils
     */
    public static final NBTBlockUtils nbtBlockUtils = new NBTBlockUtils();

    private NBTBlockUtils(){}

    RefClass classCraftWorld = getRefClass("{cb}.CraftWorld, {CraftWorld}");
    RefClass classTileEntity = getRefClass("{nms}.TileEntity, {nm}.tileentity.TileEntity, {TileEntity}");
    RefMethod getTileEntityAt = classCraftWorld.findMethodByReturnType(classTileEntity); // (int x, int y, int z)
    RefMethod getUpdatePacket = classTileEntity.findMethodByReturnType("{nms}.Packet, {nm}.network.Packet, {Packet}");
    RefMethod read = classTileEntity.findMethod( new MethodCondition()
                    .withTypes("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}")
                    .withSuffix("b")
    );
    RefMethod write = classTileEntity.findMethod( new MethodCondition()
                    .withTypes("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}")
                    .withSuffix("a")
    );

    /**
     * read NBTTagCompound for block
     * @param block bukkit block
     * @param compound empty compound to read
     */
    public void readTag(Block block, Object compound){
        Object tile = getTileEntity(block);
        if (tile!=null) read.of(tile).call(compound);

    }

    /**
     * set NBTTagCompound to block
     * @param block bukkit block
     * @param compound NBTTagCompound
     */
    public void setTag(Block block, Object compound){
        Object tile = getTileEntity(block);
        if (tile != null) write.of(tile).call(compound);
    }

    /**
     * send update packet to all nearby players
     * @param block bukkit block
     */
    public void update(Block block){
        Object tile = getTileEntity(block);
        Object packet = getUpdatePacket.of(tile).call();
        int maxDist = Bukkit.getServer().getViewDistance() * 32;
        for (Player p : block.getWorld().getPlayers()) {
            if (p.getLocation().distance(block.getLocation()) < maxDist) {
                PacketUtils.packetUtils.sendPacket(p, packet);
            }
        }
    }

    /**
     * Get tile entity at block coordinates
     * @param block bukkit block
     * @return tile entity
     */
    public Object getTileEntity(Block block){
        return getTileEntityAt.of(block.getWorld()).call(block.getX(), block.getY(), block.getZ());
    }


}
