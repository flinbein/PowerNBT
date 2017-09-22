package me.dpohvar.powernbt.utils;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Map;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;
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

    private RefClass classCraftWorld = getRefClass("{cb}.CraftWorld, {CraftWorld}");
    private RefClass classTileEntity = getRefClass("{nms}.TileEntity, {nm}.tileentity.TileEntity, {TileEntity}");
    private RefMethod getTileEntityAt = classCraftWorld.findMethodByReturnType(classTileEntity); // (int x, int y, int z)
    private RefMethod getUpdatePacket = classTileEntity.findMethodByReturnType(
            "{nms}.PacketPlayOutTileEntityData",
            "{nms}.Packet, {nm}.network.Packet {nm}.network.packet.Packet, {Packet}"
    );
    private RefMethod read = classTileEntity.findMethod(
            new MethodCondition()
                    .withTypes("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}")
                    .withSuffix("b"),
            new MethodCondition()
                    .withTypes("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}")
                    .withSuffix("save")
    );
    private RefMethod write = classTileEntity.findMethod(
            new MethodCondition() // 1.12
                    .withTypes("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}")
                    .withSuffix("load"),
            new MethodCondition() // 1.11, cauldron
                    .withTypes("{NBTTagCompound}, {nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound")
                    .withSuffix("a")
                    .withReturnType(void.class)
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
     * set NBTTagCompound to block. Watch for x, y, z
     * @param block bukkit block
     * @param compound NBTTagCompound
     */
    public void setTag(Block block, Object compound){
        compound = nbtUtils.cloneTag(compound);
        Map<String, Object> map = nbtUtils.getHandleMap(compound);
        map.put("x", nbtUtils.createTagInt(block.getX()));
        map.put("y", nbtUtils.createTagInt(block.getY()));
        map.put("z", nbtUtils.createTagInt(block.getZ()));
        setTagUnsafe(block, compound);
    }

    /**
     * set NBTTagCompound to block
     * @param block bukkit block
     * @param compound NBTTagCompound
     */
    public void setTagUnsafe(Block block, Object compound){
        Object tile = getTileEntity(block);
        if (tile != null) write.of(tile).call(compound);
    }

    /**
     * send update packet to all nearby players
     * @param block bukkit block
     */
    public void update(Block block){
        if (block == null) return;
        Object tile = getTileEntity(block);
        if (tile == null) return;
        Object packet = getUpdatePacket.of(tile).call();
        if (packet == null) return;
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
