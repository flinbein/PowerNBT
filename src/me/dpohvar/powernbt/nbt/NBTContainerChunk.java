package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.PowerNBT;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_5_R1.block.CraftBlock;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.utils.StaticValues.*;
import static me.dpohvar.powernbt.utils.VersionFix.*;

public class NBTContainerChunk extends NBTContainer {

    Chunk chunk;

    static Method methodLoadChunk;
    static Method methodSetChunk;
    static Method methodPut;
    static Method methodCraftPlayerGetHandle;
    static Field fieldChunkProviderServer;
    static Field fieldChunkCoordIntPairQueue;
    static Field fieldChunks;
    static Constructor conChunkCoordIntPair;
    static Field fieldBukkitChunk;
    static Object loader;
    static Object chunkRegionLoader;

    static {
        try {
            Constructor conChunkRegionLoader = classChunkRegionLoader.getConstructor(File.class);
            chunkRegionLoader = conChunkRegionLoader.newInstance(new Object[]{null});
            loader = getNew(classChunkRegionLoader, new Class[]{File.class}, new Object[]{null});
            methodCraftPlayerGetHandle = classCraftPlayer.getDeclaredMethod("getHandle");
            methodLoadChunk = classChunkRegionLoader.getDeclaredMethod("a", classChunk, classWorld, classNBTTagCompound);
            methodSetChunk = classChunkRegionLoader.getDeclaredMethod("a", classWorld, classNBTTagCompound);
            fieldChunkCoordIntPairQueue = classEntityPlayer.getDeclaredField("chunkCoordIntPairQueue");
            fieldChunkProviderServer = classWorldServer.getDeclaredField("chunkProviderServer");
            fieldChunks = classChunkProviderServer.getDeclaredField("chunks");
            conChunkCoordIntPair = classChunkCoordIntPair.getConstructor(int.class, int.class);
            fieldBukkitChunk = classChunk.getDeclaredField("bukkitChunk");
            methodPut = classLongObjectHashMap.getDeclaredMethod("put",long.class, Object.class);
            methodLoadChunk.setAccessible(true);
            methodSetChunk.setAccessible(true);
            fieldChunkProviderServer.setAccessible(true);
        } catch (Exception e) {
            if(PowerNBT.plugin.isDebug()) e.printStackTrace();
        }
    }

    public NBTContainerChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getObject() {
        return chunk;
    }

    @Override
    public List<String> getTypes() {
        return new ArrayList<String>();
    }

    @Override
    public NBTTagCompound getTag() {
        NBTTagCompound compound = new NBTTagCompound();
        Object chank = callMethod(chunk,"getHandle",new Class[0]);
        Object world = callMethod(chunk.getWorld(), "getHandle", new Class[0]);
        try {
            methodLoadChunk.invoke(loader, chank, world, compound.getHandle());
        } catch (Exception e) {
            if(PowerNBT.plugin.isDebug()) e.printStackTrace();
        }
        return compound;
    }

    @Override
    public void setTag(NBTBase base) {
        if(!(base instanceof NBTTagCompound)) return;
        NBTTagCompound compound = ((NBTTagCompound) base).clone();
        int x = chunk.getX();
        int z = chunk.getZ();
        chunk.unload();
        compound.set("xPos",x);
        compound.set("zPos",z);
        Object world = callMethod(chunk.getWorld(), "getHandle", new Class[0]);

        NBTTagList tiles = compound.getList("TileEntities");
        if(tiles!=null) for(NBTBase b: tiles){
            NBTTagCompound c = (NBTTagCompound) b;
            Integer bx = c.getInt("x");
            Integer by = c.getInt("y");
            Integer bz = c.getInt("z");
            if ( bx==null || bz==null ) continue;
            int rx = chunk.getX()*16 + bx%16;
            int ry = by;
            int rz = chunk.getZ()*16 + bz%16;
            c.set("x", rx );
            c.set("z", ry );
        }
        try {
            Object chank = methodSetChunk.invoke(loader,world,compound.getHandle());
            methodSetChunk.invoke(chunkRegionLoader,world,compound.getHandle());
            Object provider = fieldChunkProviderServer.get(world);
            Object chunks = fieldChunks.get(provider);
            methodPut.invoke(chunks,((long) x << 32) + z - Integer.MIN_VALUE, chank);
            for(Player p:chunk.getWorld().getPlayers()){
                Object entityPlayer = methodCraftPlayerGetHandle.invoke(p);
                List list = (List) fieldChunkCoordIntPairQueue.get(entityPlayer);
                list.add(conChunkCoordIntPair.newInstance(x,z));
            }
            if(tiles!=null) for(NBTBase b: tiles){
                NBTTagCompound c = (NBTTagCompound) b;
                Integer bx = c.getInt("x");
                Integer by = c.getInt("y");
                Integer bz = c.getInt("z");
                if ( bx==null || bz==null ) continue;
                int rx = chunk.getX()*16 + bx%16;
                int ry = by;
                int rz = chunk.getZ()*16 + bz%16;
                c.set("x", rx );
                c.set("z", ry );
                Block block = chunk.getWorld().getBlockAt(rx,ry,rz);
                NBTContainerBlock con = new NBTContainerBlock(block);
                con.setTag(c);
            }
        } catch (Exception e) {
            if(PowerNBT.plugin.isDebug()) e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Chunk:" +chunk.getX()+ ":" +chunk.getZ()+":"+chunk.getWorld().getName();
    }

    @Override
    public void removeTag() {
        //todo: remove tag
    }
}
