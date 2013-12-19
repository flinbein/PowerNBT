package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerBlock extends NBTContainer<Block> {

    private static final Class class_EntityPlayer = Reflections.getClass("{nms}.EntityPlayer","net.minecraft.entity.player.EntityPlayer");
    private static final Class class_EntityPlayerMP = Reflections.getClass(null,"net.minecraft.entity.player.EntityPlayerMP");
    private static final Class class_CraftWorld = Reflections.getClass("{cb}.CraftWorld");
    private static final Class class_TileEntity = Reflections.getClass("{nms}.TileEntity","net.minecraft.tileentity.TileEntity");
    private static final Class class_Packet = Reflections.getClass("{nms}.Packet","net.minecraft.network.packet.Packet");
    private static final Class class_sCraftPlayer = Reflections.getClass("{cb}.entity.CraftPlayer");
    private static final Class class_NetServerHandler  = Reflections.getClass(null,"net.minecraft.network.NetServerHandler");
    private static final Class class_PlayerConnection  = Reflections.getClass("{nms}.PlayerConnection",null);
    private static final Method method_getUpdatePacket = Reflections.getMethodByTypes(class_TileEntity, class_Packet);
    private static final Method method_getTileEntityAt = Reflections.getMethodByTypes(class_CraftWorld, class_TileEntity,int.class,int.class,int.class);
    private static final Method method_getHandle = Reflections.getMethodByTypes(class_sCraftPlayer,class_EntityPlayer);
    private static Field field_playerNetServerHandler;
    private static Field field_playerConnection;
    private static Method method_sendPacketToPlayer;
    private static Method method_sendPacket;
    private static Method method_Read;
    private static Method method_Write;
    static{
        if( Reflections.isForge() ){ // forge
            field_playerNetServerHandler = Reflections.getField(class_EntityPlayerMP, class_NetServerHandler);
            method_sendPacketToPlayer = Reflections.getMethodByTypes(class_NetServerHandler,void.class, class_Packet);
            for(Method m: class_TileEntity.getMethods()){
                if (m.getParameterTypes().length!=1) continue;
                if (!m.getParameterTypes()[0].equals(class_NBTTagCompound)) continue;
                if (m.getName().endsWith("b")) method_Read = m;
                if (m.getName().endsWith("a")) method_Write = m;
            }
        } else {
            try{ // bukkit
                method_Read = class_TileEntity.getMethod("b", class_NBTTagCompound);
                method_Write = class_TileEntity.getMethod("a", class_NBTTagCompound);
                method_sendPacket = class_PlayerConnection.getMethod("sendPacket",class_Packet);
                field_playerConnection = class_EntityPlayer.getField("playerConnection");
            } catch (Exception e){
                throw new RuntimeException("reflection error",e);
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
    public NBTTagCompound readCustomTag() {
        NBTTagCompound tag = readTag();
        if (tag!=null) {
            List<String> ignores = plugin.getConfig().getStringList("ignore_get.block");
            if(ignores!=null) for(String s:ignores) tag.remove(s);
        }
        return tag;
    }

    public NBTTagCompound readTag() {
        NBTTagCompound base = new NBTTagCompound();
        Object tile = getTile();
        Reflections.invoke(method_Read,tile, base.getHandle());
        return base;
    }

    @Override
    public void writeTag(NBTBase base) {
        if (!(base instanceof NBTTagCompound)) return;
        Object tile = getTile();
        if (tile == null) return;
        Reflections.invoke(method_Write,tile,base.getHandle());
        int maxDist = Bukkit.getServer().getViewDistance() * 32;
        for (Player p : block.getWorld().getPlayers()) {
            if (p.getLocation().distance(block.getLocation()) < maxDist) {
                Object packet = Reflections.invoke(method_getUpdatePacket,tile);
                Object mPlayer = Reflections.invoke(method_getHandle,p);
                if(Reflections.isForge()){
                    Object netServerHandler = Reflections.getFieldValue(field_playerNetServerHandler,mPlayer);
                    Reflections.invoke(method_sendPacketToPlayer,netServerHandler,packet);
                } else {
                    Object connection = Reflections.getFieldValue(field_playerConnection,mPlayer);
                    Reflections.invoke(method_sendPacket,connection,packet);
                }
            }
        }
    }

    @Override
    public void writeCustomTag(NBTBase base) {
        if (!(base instanceof NBTTagCompound)) return;
        NBTTagCompound tag = (NBTTagCompound) base.clone();
        List<String> ignores = plugin.getConfig().getStringList("ignore_set.block");
        if(ignores!=null) for(String s:ignores) tag.remove(s);
        NBTTagCompound original = readTag();
        if(tag.getInt("x")==null)tag.put("x",original.get("x"));
        if(tag.getInt("y")==null)tag.put("y",original.get("y"));
        if(tag.getInt("z")==null)tag.put("z",original.get("z"));
        writeTag(tag);
    }

    @Override
    protected Class<Block> getContainerClass() {
        return Block.class;
    }

    private Object getTile(){
        return Reflections.invoke(method_getTileEntityAt,block.getWorld(),block.getX(), block.getY(), block.getZ());
    }

    @Override
    public String toString(){
        return "block:" + block.getType().toString();
    }

}
