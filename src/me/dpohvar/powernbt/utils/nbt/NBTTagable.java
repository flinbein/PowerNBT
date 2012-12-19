package me.dpohvar.powernbt.utils.nbt;

import me.dpohvar.powernbt.utils.versionfix.*;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.dpohvar.powernbt.utils.versionfix.VersionFix.*;
import static me.dpohvar.powernbt.utils.versionfix.StaticValues.*;

public class NBTTagable {
    public enum Type{
        BLOCK,LIVING,ENTITY,TAG,ITEM,FILE,GZIPFILE,VARIABLE
    }
    private Object object;
    private Type type;

    public Type getType(){
        return type;
    }

    public NBTTagable(Object object){
        this.object = object;
        if      (object instanceof Block) type=Type.BLOCK;
        else if (object instanceof LivingEntity) type=Type.LIVING;
        else if (object instanceof Entity) type=Type.ENTITY;
        else if (object instanceof ItemStack) type=Type.ITEM;
        else if (StaticValues.classNBTBase.isInstance(object)) type=Type.TAG;
        else throw new RuntimeException("Object "+ this.object +" has no tag");
    }

    public Object getObject(){
        return object;
    }

    public XNBTBase getRootBase(){
        XNBTBase base = null;
        switch (type){
            case BLOCK:
                Block block = (Block) object;
                XCraftWorld w = getShell(XCraftWorld.class,block.getWorld());
                Object tile = w.getTileEntityAt(block.getX(), block.getY(), block.getZ());
                if (tile!=null) {
                    base = getShell(XNBTBase.class,getNew(classNBTTagCompound,noInput));
                    callMethod(tile,"b",oneNBTTagCompound,base);
                }
                break;
            case LIVING:
                Object liv = callMethod(object, "getHandle", noInput);
                base = getShell(XNBTBase.class,getNew(classNBTTagCompound,noInput));
                callMethod(liv,"b",oneNBTTagCompound,base);
                break;
            case ENTITY:
                Object ent = callMethod(object, "getHandle", noInput);
                base = getShell(XNBTBase.class,getNew(classNBTTagCompound,noInput));
                callMethod(ent,"b",oneNBTTagCompound,base);
                break;
            case ITEM:
                Object item = callMethod(object, "getHandle", noInput);
                base = getShell(XNBTBase.class,callMethod(item,"getTag",noInput));
                break;
            case TAG:
                base = getShell(XNBTBase.class,object);
                break;
        }
        return base;
    }

    public void setRootBase(XNBTBase base){
        switch (type){
            case BLOCK:
                Block block = (Block) object;
                XCraftWorld w = getShell(XCraftWorld.class,block.getWorld());
                XTileEntity tile = getShell(XTileEntity.class, w.getTileEntityAt(block.getX(), block.getY(), block.getZ()));
                if (tile.getProxyObject() != null) {
                    callMethod(tile, "a", oneNBTTagCompound, base);
                    int maxDist = Bukkit.getServer().getViewDistance() * 32;
                    for (Player p : block.getWorld().getPlayers()) {
                        if (p.getLocation().distance(block.getLocation()) < maxDist) {
                            Object packet = tile.getUpdatePacket();
                            Object mPlayer = callMethod(p,"getHandle",noInput);
                            callMethod(mPlayer,"sendPacket",onePacket,packet);
                        }
                    }
                }
                break;
            case LIVING:
                Object liv = callMethod(object,"getHandle",noInput);
                callMethod(liv, "a", oneNBTTagCompound, base);
                break;
            case ENTITY:
                Object ent = callMethod(object,"getHandle",noInput);
                callMethod(ent, "e", oneNBTTagCompound, base);
            case ITEM:
                Object item = callMethod(object, "getHandle", noInput);
                callMethod(item,"setTag",oneNBTTagCompound,base);
            case TAG:
                object = base.getProxyObject();
        }
    }

    public void removeRootBase(){
        XNBTBase base = getShell(XNBTBase.class,getNew(classNBTTagCompound,noInput));
        switch (type){
            case BLOCK:
                Block block = (Block) object;
                XCraftWorld w = getShell(XCraftWorld.class,block.getWorld());
                XTileEntity tile = getShell(XTileEntity.class, w.getTileEntityAt(block.getX(), block.getY(), block.getZ()));
                if (tile.getProxyObject() != null) {
                    callMethod(tile, "a", oneNBTTagCompound, base);
                    int maxDist = Bukkit.getServer().getViewDistance() * 32;
                    for (Player p : block.getWorld().getPlayers()) {
                        if (p.getLocation().distance(block.getLocation()) < maxDist) {
                            Object packet = tile.getUpdatePacket();
                            Object mPlayer = callMethod(p,"getHandle",noInput);
                            callMethod(mPlayer,"sendPacket",onePacket,packet);
                        }
                    }
                }
                break;
            case LIVING:
                base = getShell(XNBTBase.class,getNew(classNBTTagCompound,noInput));
                Object liv = callMethod(object,"getHandle",noInput);
                callMethod(liv, "a", oneNBTTagCompound, base);
                break;
            case ENTITY:
                Object ent = callMethod(object,"getHandle",noInput);
                callMethod(ent, "e", oneNBTTagCompound, base);
                break;
            case ITEM:
                Object item = callMethod(object, "getHandle", noInput);
                callMethod(item,"setTag",oneNBTTagCompound,base);
                break;
            case TAG:
                object = null;
        }
    }
}
