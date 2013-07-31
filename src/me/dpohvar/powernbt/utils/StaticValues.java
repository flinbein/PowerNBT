package me.dpohvar.powernbt.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class StaticValues {
    public static final boolean isMCPC = Bukkit.getVersion().contains("MCPC-Plus");

    private static final HashMap<String,Class> classes = new HashMap<String, Class>();
    private static String versionPrefix = "";
    static {
        try { // get bukkit class prefix (versionPrefix).
            String className = Bukkit.getServer().getClass().getName();
            String[] packages = className.split("\\.");
            if (packages.length == 5) {
                versionPrefix = packages[3] + "."; // must me empty or "vX_X."
            }
        } catch (Throwable ignored) {
        }

        classes.put("CraftWorld", fixBukkitClass("org.bukkit.craftbukkit.CraftWorld"));
        classes.put("CraftItemStack", fixBukkitClass("org.bukkit.craftbukkit.inventory.CraftItemStack"));
        classes.put("CraftPlayer", fixBukkitClass("org.bukkit.craftbukkit.entity.CraftPlayer"));
        classes.put("CraftEntity", fixBukkitClass("org.bukkit.craftbukkit.entity.CraftEntity"));
        classes.put("LongObjectHashMap", fixBukkitClass("org.bukkit.craftbukkit.util.LongObjectHashMap"));
        classes.put("CraftScoreboard", fixBukkitClass("org.bukkit.craftbukkit.scoreboard.CraftScoreboard"));

        if ( isMCPC ) try {
            // ##### server is MCPC+
            classes.put("NBTBase",Class.forName("net.minecraft.nbt.NBTBase"));
            classes.put("NBTTagByte",Class.forName("net.minecraft.nbt.NBTTagByte"));
            classes.put("NBTTagShort",Class.forName("net.minecraft.nbt.NBTTagShort"));
            classes.put("NBTTagInt",Class.forName("net.minecraft.nbt.NBTTagInt"));
            classes.put("NBTTagLong",Class.forName("net.minecraft.nbt.NBTTagLong"));
            classes.put("NBTTagFloat",Class.forName("net.minecraft.nbt.NBTTagFloat"));
            classes.put("NBTTagDouble",Class.forName("net.minecraft.nbt.NBTTagDouble"));
            classes.put("NBTTagByteArray",Class.forName("net.minecraft.nbt.NBTTagByteArray"));
            classes.put("NBTTagString",Class.forName("net.minecraft.nbt.NBTTagString"));
            classes.put("NBTTagList",Class.forName("net.minecraft.nbt.NBTTagList"));
            classes.put("NBTTagCompound",Class.forName("net.minecraft.nbt.NBTTagCompound"));
            classes.put("NBTTagIntArray",Class.forName("net.minecraft.nbt.NBTTagIntArray"));
            classes.put("NBTCompressedStreamTools", fixBukkitClass("net.minecraft.nbt.CompressedStreamTools"));

            classes.put("ItemStack", Class.forName("net.minecraft.item.ItemStack"));
            classes.put("Chunk", Class.forName("net.minecraft.world.chunk.Chunk"));
            classes.put("ChunkProviderServer", Class.forName("net.minecraft.world.gen.ChunkProviderServer"));
            classes.put("World", Class.forName("net.minecraft.world.World"));
            classes.put("WorldServer", Class.forName("net.minecraft.world.WorldServer"));
            classes.put("AnvilChunkLoader", Class.forName("net.minecraft.world.chunk.storage.AnvilChunkLoader"));
            classes.put("ChunkLoader", Class.forName("net.minecraft.world.chunk.storage.ChunkLoader"));
            classes.put("EntityList", Class.forName("net.minecraft.entity.EntityList"));
            classes.put("Entity", Class.forName("net.minecraft.entity.Entity"));
            classes.put("EntityPlayer", Class.forName("net.minecraft.entity.player.EntityPlayer"));
            classes.put("EntityPlayerMP", Class.forName("net.minecraft.entity.player.EntityPlayerMP"));
            classes.put("ChunkCoordIntPair", Class.forName("net.minecraft.world.ChunkCoordIntPair"));
            classes.put("TileEntity", Class.forName("net.minecraft.tileentity.TileEntity"));
            classes.put("Packet", Class.forName("net.minecraft.network.packet.Packet"));
            classes.put("NetServerHandler", Class.forName("net.minecraft.network.NetServerHandler"));
            classes.put("Scoreboard", Class.forName("net.minecraft.scoreboard.Scoreboard"));
            classes.put("ServerScoreboard", Class.forName("net.minecraft.scoreboard.ServerScoreboard"));
            classes.put("ScoreboardSaveData", Class.forName("net.minecraft.scoreboard.ScoreboardSaveData"));
            classes.put("WorldServer", Class.forName("net.minecraft.world.WorldServer"));
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } else try {
            // ##### server is BUKKIT
            classes.put("NBTBase", fixBukkitClass("net.minecraft.server.NBTBase"));
            classes.put("NBTTagByte", fixBukkitClass("net.minecraft.server.NBTTagByte"));
            classes.put("NBTTagShort", fixBukkitClass("net.minecraft.server.NBTTagShort"));
            classes.put("NBTTagInt", fixBukkitClass("net.minecraft.server.NBTTagInt"));
            classes.put("NBTTagLong", fixBukkitClass("net.minecraft.server.NBTTagLong"));
            classes.put("NBTTagFloat", fixBukkitClass("net.minecraft.server.NBTTagFloat"));
            classes.put("NBTTagDouble", fixBukkitClass("net.minecraft.server.NBTTagDouble"));
            classes.put("NBTTagByteArray", fixBukkitClass("net.minecraft.server.NBTTagByteArray"));
            classes.put("NBTTagString", fixBukkitClass("net.minecraft.server.NBTTagString"));
            classes.put("NBTTagList", fixBukkitClass("net.minecraft.server.NBTTagList"));
            classes.put("NBTTagCompound", fixBukkitClass("net.minecraft.server.NBTTagCompound"));
            classes.put("NBTTagIntArray", fixBukkitClass("net.minecraft.server.NBTTagIntArray"));
            classes.put("NBTCompressedStreamTools", fixBukkitClass("net.minecraft.server.NBTCompressedStreamTools"));

            classes.put("ItemStack", fixBukkitClass("net.minecraft.server.ItemStack"));
            classes.put("Chunk", fixBukkitClass("net.minecraft.server.Chunk"));
            classes.put("ChunkProviderServer", fixBukkitClass("net.minecraft.server.ChunkProviderServer"));
            classes.put("World", fixBukkitClass("net.minecraft.server.World"));
            classes.put("WorldServer", fixBukkitClass("net.minecraft.server.WorldServer"));
            classes.put("ChunkRegionLoader", fixBukkitClass("net.minecraft.server.ChunkRegionLoader"));
            classes.put("EntityTypes", fixBukkitClass("net.minecraft.server.EntityTypes"));
            classes.put("Entity", fixBukkitClass("net.minecraft.server.Entity"));
            classes.put("EntityPlayer", fixBukkitClass("net.minecraft.server.EntityPlayer"));
            classes.put("ChunkCoordIntPair", fixBukkitClass("net.minecraft.server.ChunkCoordIntPair"));
            classes.put("TileEntity", fixBukkitClass("net.minecraft.server.TileEntity"));
            classes.put("PlayerConnection", fixBukkitClass("net.minecraft.server.PlayerConnection"));
            classes.put("Packet", fixBukkitClass("net.minecraft.server.Packet"));
            classes.put("WorldServer", fixBukkitClass("net.minecraft.server.WorldServer"));
            classes.put("MinecraftServer", fixBukkitClass("net.minecraft.server.MinecraftServer"));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Class getClass(String s){
        return classes.get(s);
    }

    public static final Class[] noInput = new Class[0];






    private static Class fixBukkitClass(String className) {
        className = className.replace("org.bukkit.craftbukkit.", "org.bukkit.craftbukkit." + versionPrefix);
        className = className.replace("net.minecraft.server.", "net.minecraft.server." + versionPrefix);
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Method getMethodByNameType(Class clazz,String name,Class... paramTypes) {
        try{
            return clazz.getMethod(name,paramTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Method getMethodByNameType(String className,String name,Class... paramTypes) {
        return getMethodByNameType(classes.get(className),name,paramTypes);
    }

    public static Method getMethodByTypeTypes(Class clazz,Class type,Class... paramTypes) {
        w1: for(Method m:clazz.getDeclaredMethods()){
            if(!m.getReturnType().equals(type)) continue;
            if(m.getParameterTypes().length!=paramTypes.length) continue;
            for(int i=0;i<paramTypes.length;i++) {
                if (!m.getParameterTypes()[i].equals(paramTypes[i])) {
                    continue w1;
                }
            }
            return m;
        }
        w1: for(Method m:clazz.getMethods()){
            if(!m.getReturnType().equals(type)) continue;
            if(m.getParameterTypes().length!=paramTypes.length) continue;
            for(int i=0;i<paramTypes.length;i++) {
                if (!m.getParameterTypes()[i].equals(paramTypes[i])) {
                    continue w1;
                }
            }
            return m;
        }
        return null;
    }

    public static Method getMethodByTypeTypes(String className,Class type,Class... paramTypes) {
        return getMethodByTypeTypes(classes.get(className), type, paramTypes);
    }

    public static Method getMethodByName(Class clazz,String name) {
        for(Method m:clazz.getDeclaredMethods()){
            if(m.getName().equals(name)) return m;
        }
        for(Method m:clazz.getMethods()){
            if(m.getName().equals(name)) return m;
        }
        return null;
    }

    public static Method getMethodByName(String className,String name) {
        return getMethodByName(classes.get(className), name);
    }

    public static Field getFieldByName(Class clazz,String name) {
        try {
            return clazz.getField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static Field getFieldByName(String className,String name){
        return getFieldByName(classes.get(className),name);
    }

    public static Field getFieldByType(Class clazz,Class type) {
        for(Field f:clazz.getDeclaredFields()){
            if (f.getType().equals(type)) return f;
        }
        for(Field f:clazz.getFields()){
            if (f.getType().equals(type)) return f;
        }
        return null;
    }


    public static Constructor getConstructorByTypes(Class clazz,Class... paramTypes) {
        w1: for(Constructor c:clazz.getDeclaredConstructors()){
            if(c.getParameterTypes().length!=paramTypes.length) continue;
            for(int i=0;i<paramTypes.length;i++) {
                if (!c.getParameterTypes()[i].equals(paramTypes[i])) {
                    continue w1;
                }
            }
            return c;
        }
        w1: for(Constructor c:clazz.getConstructors()){
            if(c.getParameterTypes().length!=paramTypes.length) continue;
            for(int i=0;i<paramTypes.length;i++) {
                if (!c.getParameterTypes()[i].equals(paramTypes[i])) {
                    continue w1;
                }
            }
            return c;
        }
        return null;
    }

}
