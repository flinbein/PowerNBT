package me.dpohvar.powernbt.extension;

import groovy.lang.Closure;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.extension.nbt.NBTCompoundProperties;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class NBTExtension {

    // getter

    public static NBTCompound getNbt(Block self){
        return NBTManager.getInstance().read(self);
    }

    public static NBTCompound getNbt(Entity self){
        return NBTManager.getInstance().read(self);
    }

    public static NBTCompound getNbt(TileState self){
        return NBTManager.getInstance().read(self);
    }

    public static NBTCompound getNbto(OfflinePlayer self){
        return NBTManager.getInstance().readOfflinePlayer(self);
    }

    public static NBTCompound getNbt(Chunk self){
        return NBTManager.getInstance().read(self);
    }

    public static NBTCompound getNbt(ItemStack self){
        return NBTManager.getInstance().read(self);
    }

    public static Object getNbt(File self) throws IOException {
        return NBTManager.getInstance().read(self);
    }

    public static Object getNbtc(File self) throws IOException {
        return NBTManager.getInstance().readCompressed(self);
    }

    // setter

    public static void setNbt(Block self, NBTCompound data){
        NBTManager.getInstance().write(self, data);
    }

    public static void setNbt(Entity self, NBTCompound data){
        NBTManager.getInstance().write(self, data);
    }

    public static void setNbt(TileState self, NBTCompound data){
        NBTManager.getInstance().write(self, data);
    }

    public static void setNbto(OfflinePlayer self, NBTCompound data){
        NBTManager.getInstance().readOfflinePlayer(self);
    }

    public static void setNbt(Chunk self, NBTCompound data){
        NBTManager.getInstance().write(self, data);
    }

    public static void setNbt(ItemStack self, NBTCompound data){
        NBTManager.getInstance().write(self, data);
    }

    public static void setNbt(File self, Object data) throws IOException {
        NBTManager.getInstance().write(self, data);
    }

    public static void setNbtc(File self, Object data) throws IOException {
        NBTManager.getInstance().readCompressed(self);
    }

    // accessor

    public static <T extends Block> Object nbt(T self, Closure<NBTCompound> modifier){
        NBTCompound tag = getNbt(self);
        NBTCompound ext = tag != null ? tag.clone() : new NBTCompound();
        modifier.setDelegate(new NBTCompoundProperties(ext));
        modifier.setResolveStrategy(Closure.DELEGATE_FIRST);
        Object result = modifier.call(self);
        if (!ext.equals(tag)) setNbt(self, ext);
        return result;
    }

    public static <T extends TileState> Object nbt(T self, Closure<NBTCompound> modifier){
        NBTCompound tag = getNbt(self);
        NBTCompound ext = tag != null ? tag.clone() : new NBTCompound();
        modifier.setDelegate(new NBTCompoundProperties(ext));
        modifier.setResolveStrategy(Closure.DELEGATE_FIRST);
        Object result = modifier.call(self);
        if (!ext.equals(tag)) setNbt(self, ext);
        return result;
    }

    public static <T extends Entity> Object nbt(T self, Closure<NBTCompound> modifier){
        NBTCompound tag = getNbt(self);
        NBTCompound ext = tag != null ? tag.clone() : new NBTCompound();
        modifier.setDelegate(new NBTCompoundProperties(ext));
        modifier.setResolveStrategy(Closure.DELEGATE_FIRST);
        Object result = modifier.call(self);
        if (!ext.equals(tag)) setNbt(self, ext);
        return result;
    }

    public static <T extends Chunk> Object nbt(T self, Closure<NBTCompound> modifier){
        NBTCompound tag = getNbt(self);
        NBTCompound ext = tag != null ? tag.clone() : new NBTCompound();
        modifier.setDelegate(new NBTCompoundProperties(ext));
        modifier.setResolveStrategy(Closure.DELEGATE_FIRST);
        Object result = modifier.call(self);
        if (!ext.equals(tag)) setNbt(self, ext);
        return result;
    }

    public static <T extends ItemStack> Object nbt(T self, Closure<NBTCompound> modifier){
        NBTCompound tag = getNbt(self);
        NBTCompound ext = tag != null ? tag.clone() : new NBTCompound();
        modifier.setDelegate(new NBTCompoundProperties(ext));
        modifier.setResolveStrategy(Closure.DELEGATE_FIRST);
        Object result = modifier.call(self);
        if (!ext.equals(tag)) setNbt(self, ext);
        return result;
    }

    public static <T extends File> Object nbt(T self, Closure<Object> modifier) throws IOException {
        NBTCompound tag = (NBTCompound) getNbt(self);
        NBTCompound ext = tag != null ? tag.clone() : new NBTCompound();
        modifier.setDelegate(new NBTCompoundProperties(ext));
        modifier.setResolveStrategy(Closure.DELEGATE_FIRST);
        Object result = modifier.call(self);
        if (!ext.equals(tag)) setNbt(self, ext);
        return result;
    }

    public static <T extends OfflinePlayer> Object nbto(T self, Closure<NBTCompound> modifier) throws IOException {
        NBTCompound tag = getNbto(self);
        NBTCompound ext = tag != null ? tag.clone() : new NBTCompound();
        modifier.setDelegate(new NBTCompoundProperties(ext));
        modifier.setResolveStrategy(Closure.DELEGATE_FIRST);
        Object result = modifier.call(self);
        if (!ext.equals(tag)) setNbto(self, ext);
        return result;
    }

    public static <T extends File> Object nbtc(T self, Closure<Object> modifier) throws IOException {
        NBTCompound tag = (NBTCompound) getNbtc(self);
        NBTCompound ext = tag != null ? tag.clone() : new NBTCompound();
        modifier.setDelegate(new NBTCompoundProperties(ext));
        modifier.setResolveStrategy(Closure.DELEGATE_FIRST);
        Object result = modifier.call(self);
        if (!ext.equals(tag)) setNbtc(self, ext);
        return result;
    }

    public static NBTCompound toNBT(Map self) throws IOException {
        if (self instanceof NBTCompound cmp) return cmp;
        return new NBTCompound(self);
    }

    public static NBTList toNBT(Collection self) throws IOException {
        if (self instanceof NBTList cmp) return cmp;
        return new NBTList(self);
    }

}
