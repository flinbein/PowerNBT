package me.dpohvar.powernbt.command;

import me.dpohvar.powernbt.utils.*;
import me.dpohvar.powernbt.utils.nbt.NBTEdit;
import me.dpohvar.powernbt.utils.nbt.NBTQuery;
import me.dpohvar.powernbt.utils.nbt.NBTTagable;
import me.dpohvar.powernbt.utils.nbt.NBTType;
import net.minecraft.server.v1_4_5.NBTTagInt;
import net.minecraft.server.v1_4_5.NBTTagList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

import me.dpohvar.powernbt.utils.versionfix.*;

import static me.dpohvar.powernbt.PowerNBT.plugin;
import static me.dpohvar.powernbt.utils.versionfix.VersionFix.*;
import static me.dpohvar.powernbt.utils.versionfix.StaticValues.*;


public class CommandNBT extends Command {
    private static HashSet<String> specialTokens = new HashSet<String>(
            Arrays.asList("=","+=","rem","remove","copy","select","sel","to","for","*")
    );

    @Override public boolean command(final Caller caller,Queue<String> words) throws Throwable{
        if(words.size()==0) return false;
        NBTTagable tagable = getTagable(caller, words);
        caller.send(getNBTView(tagable.getRootBase()));
        return true;
    }

    public static NBTTagable getTagable(final Caller caller,Queue<String> words){
        String word = words.poll();
        if (word==null) return null;
        if (word.equals("me")){
            if (!(caller.getOwner() instanceof Player)) throw new RuntimeException(plugin.translate("error_noplayer"));
            return new NBTTagable(caller.getOwner());
        } else if (word.equals("item")){
            if (!(caller.getOwner() instanceof Player)) throw new RuntimeException(plugin.translate("error_noplayer"));
            return new NBTTagable(((Player)caller.getOwner()).getItemInHand());
        } else if (word.startsWith("id")){
            int id = Integer.parseInt(word.substring(2).replaceAll("\\(.*\\)", ""));
            for(World w:Bukkit.getWorlds())for(Entity e:w.getEntities())if(e.getEntityId()==id){
                return new NBTTagable(e);
            }
            throw new RuntimeException(plugin.translate("error_noentity",id));
        } else if (word.startsWith("block")){
            if (!(caller.getOwner() instanceof Player)) throw new RuntimeException(plugin.translate("error_noplayer"));
            return new NBTTagable(((Player) caller.getOwner()).getTargetBlock(null,20));
        }
        return null;
    }




    public static String getNBTView(XNBTBase base) throws IllegalAccessException {
        if (base==null) return "null";
        NBTType type = NBTType.fromBase(base);
        String s = type.color + type.name + ' ' + ChatColor.BOLD + base.getName()+ ChatColor.RESET
                + ": "+getNBTValue(base)+ChatColor.RESET+'\n' ;
        switch (type){
            case COMPOUND:
                Map<String,Object> map = (Map<String,Object>) base.getProxyField("map");
                for(Map.Entry<String,Object> e:map.entrySet()){
                    XNBTBase b = getShell(XNBTBase.class,e.getValue());
                    NBTType t = NBTType.fromBase(b);
                    if(t.equals(NBTType.LIST)){
                        NBTType subType = NBTType.fromByte((Byte) b.getProxyField("type"));
                        s += "" + subType.color + ChatColor.BOLD + b.getName() + "[]: " + ChatColor.RESET + getNBTValue(b)+'\n';
                    } else {
                        s += "" + t.color + ChatColor.BOLD + b.getName() + ": " + ChatColor.RESET + getNBTValue(b)+'\n';
                    }
                }
                break;
            case LIST:
                List<Object> list = (List<Object>) base.getProxyField("list");
                NBTType subType = NBTType.fromByte((Byte) base.getProxyField("type"));
                for(int i=0;i<list.size();i++){
                    XNBTBase b = getShell(XNBTBase.class,list.get(i));
                    s+=subType.color.toString() + ChatColor.BOLD + "["+i+"]: "+ChatColor.RESET+getNBTValue(b)+'\n';
                }
                break;
            case BYTEARRAY:
                byte[] bytes = (byte[]) base.getProxyField("data");
                for(byte b:bytes) s+=b+",";
                if(bytes.length!=0)s=s.substring(0,s.length()-1);
                break;
            case INTARRAY:
                int[] ints = (int[]) base.getProxyField("data");
                for(int b:ints) s+=b+",";
                if(ints.length!=0)s=s.substring(0,s.length()-1);
                break;
            default:
                break;
        }
        if(s.endsWith("\n"))s=s.substring(0,s.length()-1);
        return s;
    }

    public static String getNBTValue(XNBTBase base){
        NBTType type = NBTType.fromBase(base);
        switch (type){
            case LIST:
                return plugin.translate("data_elements",((List)base.getProxyField("list")).size());
            case COMPOUND:
                return plugin.translate("data_elements",((Map)base.getProxyField("map")).size());
            case BYTEARRAY:
                return plugin.translate("data_bytes",((byte[])base.getProxyField("data")).length);
            case INTARRAY:
                return plugin.translate("data_ints",((int[])base.getProxyField("data")).length);
            default:
                return base.getProxyField("data").toString();
        }
    }
}


















