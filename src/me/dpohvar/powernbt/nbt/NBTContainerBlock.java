package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.utils.StaticValues;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerBlock extends NBTContainer {

    private static final Class class_EntityPlayer = StaticValues.getClass("EntityPlayer");
    private static final Class class_EntityPlayerMP = StaticValues.getClass("EntityPlayerMP");
    private static final Class class_CraftWorld = StaticValues.getClass("CraftWorld");
    private static final Class class_TileEntity = StaticValues.getClass("TileEntity");
    private static final Class class_Packet = StaticValues.getClass("Packet");
    private static final Class class_sCraftPlayer = StaticValues.getClass("CraftPlayer");
    private static final Class class_NetServerHandler  = StaticValues.getClass("NetServerHandler");
    private static final Class class_PlayerConnection  = StaticValues.getClass("PlayerConnection");
    private static Field field_playerNetServerHandler;
    private static Field field_playerConnection;
    private static Method method_sendPacketToPlayer;
    private static Method method_sendPacket;
    private static Method method_getTileEntityAt;
    private static Method method_Read;
    private static Method method_Write;
    private static Method method_getUpdatePacket;
    private static Method method_getHandle;
    static{

        try { //both
            method_getUpdatePacket = StaticValues.getMethodByTypeTypes(class_TileEntity, class_Packet);
            method_getTileEntityAt = StaticValues.getMethodByTypeTypes(class_CraftWorld, class_TileEntity,int.class,int.class,int.class);
            method_getHandle = class_sCraftPlayer.getMethod("getHandle");
        } catch (NoSuchMethodException e){
            e.printStackTrace();
        }
        if( StaticValues.isMCPC ){
            try{ //mcpc
                field_playerNetServerHandler = StaticValues.getFieldByType(class_EntityPlayerMP, class_NetServerHandler);
                method_sendPacketToPlayer = StaticValues.getMethodByTypeTypes(class_NetServerHandler,void.class, class_Packet);
                for(Method m: class_TileEntity.getMethods()){
                    if (m.getParameterTypes().length!=1) continue;
                    if (!m.getParameterTypes()[0].equals(class_NBTTagCompound)) continue;
                    if (m.getName().endsWith("b")) method_Read = m;
                    if (m.getName().endsWith("a")) method_Write = m;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            try{ // bukkit
                method_Read = class_TileEntity.getMethod("b", class_NBTTagCompound);
                method_Write = class_TileEntity.getMethod("a", class_NBTTagCompound);
                method_sendPacket = class_PlayerConnection.getMethod("sendPacket",class_Packet);
                field_playerConnection = class_EntityPlayer.getField("playerConnection");
            } catch (Exception ignored){
            }
        }
    }


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
        Object tile;
        NBTTagCompound base;
        base = new NBTTagCompound();
        try{
            tile = method_getTileEntityAt.invoke(block.getWorld(),block.getX(), block.getY(), block.getZ());
            method_Read.invoke(tile, base.getHandle());
        } catch (Exception ignored){
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
        Object tile = null;
        try{
            tile = method_getTileEntityAt.invoke(block.getWorld(),block.getX(), block.getY(), block.getZ());
        } catch (Exception e){
            e.printStackTrace();
        }
        if (tile != null) {
            try{
                method_Write.invoke(tile,base.getHandle());
            } catch (Exception e){
                e.printStackTrace();
            }
            int maxDist = Bukkit.getServer().getViewDistance() * 32;
            for (Player p : block.getWorld().getPlayers()) {
                if (p.getLocation().distance(block.getLocation()) < maxDist) {
                    try{
                        Object packet = method_getUpdatePacket.invoke(tile);
                        Object mPlayer = method_getHandle.invoke(p);
                        if(StaticValues.isMCPC){
                            Object netServerHandler = field_playerNetServerHandler.get(mPlayer);
                            method_sendPacketToPlayer.invoke(netServerHandler,packet);
                        } else {
                            Object connection = field_playerConnection.get(mPlayer);
                            method_sendPacket.invoke(connection,packet);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
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
