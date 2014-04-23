package me.dpohvar.powernbt.api;

import me.dpohvar.powernbt.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.UUID;

public class NBTManager {

    public static final NBTManager nbtManager = new NBTManager();

    EntityUtils entityUtils = EntityUtils.entityUtils;

    ItemStackUtils itemStackUtils = ItemStackUtils.itemStackUtils;
    NBTBlockUtils nbtBlockUtils = NBTBlockUtils.nbtBlockUtils;
    NBTCompressedUtils nbtCompressedUtils = NBTCompressedUtils.nbtCompressedUtils;
    NBTUtils nbtUtils = NBTUtils.nbtUtils;
    ReflectionUtils.RefMethod getUUID = null;

    private NBTManager(){
        try {
            getUUID = ReflectionUtils.getRefClass(OfflinePlayer.class).findMethodByReturnType(UUID.class);
        } catch (Exception ignored) {
            // can't use UUID
        }
    }


    public NBTCompound read(Entity entity){
        NBTCompound compound = new NBTCompound();
        entityUtils.readEntity(entity, compound.getHandle());
        return compound;
    }

    public void write(Entity entity, NBTCompound compound){
        entityUtils.writeEntity(entity, compound.getHandle());
    }

    public NBTCompound readForgeData(Entity entity){
        Object tag = entityUtils.getForgeData(entity);
        if (tag==null) return new NBTCompound();
        else return NBTCompound.forNBTCopy(tag);
    }

    public void writeForgeData(Entity entity, NBTCompound compound){
        entityUtils.setForgeData(entity, compound.getHandleCopy());
    }



    public NBTCompound read(ItemStack item){
        Object tag = ItemStackUtils.itemStackUtils.getTag(item);
        return NBTCompound.forNBTCopy(tag);
    }

    public void write(ItemStack item, NBTCompound compound){
        itemStackUtils.setTag(item, compound.getHandleCopy());
    }

    public NBTCompound read(Block block){
        NBTCompound compound = new NBTCompound();
        nbtBlockUtils.readTag(block,compound.getHandle());
        return compound;
    }

    public void write(Block block, NBTCompound compound){
        nbtBlockUtils.setTag(block, compound.getHandle());
    }

    public Object read(InputStream inputStream) throws IOException {
        return read((DataInput) new DataInputStream(inputStream));
    }

    public void write(OutputStream outputStream, Object value) throws IOException {
        write((DataOutput) new DataOutputStream(outputStream), value);
    }

    public NBTCompound readCompressed(InputStream inputStream){
        Object tag = nbtCompressedUtils.readCompound(inputStream);
        return NBTCompound.forNBT(tag);
    }

    public void writeCompressed(OutputStream outputStream, NBTCompound value){
        nbtCompressedUtils.writeCompound(value.getHandle(), outputStream);
    }

    public Object read(DataInput dataInput) throws IOException {
        Object tag =  nbtUtils.readTag(dataInput);
        return nbtUtils.getValue(tag);
    }

    public void write(DataOutput dataOutput, Object value) throws IOException {
        Object tag =  nbtUtils.createTag(value);
        nbtUtils.writeTagToOutput(dataOutput, tag);
    }

    public Object read(File file) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return read(inputStream);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(File file, Object value) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            write(outputStream, value);
        } finally {
            if (outputStream != null) try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public NBTCompound readCompressed(File file) throws FileNotFoundException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return readCompressed(inputStream);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeCompressed(File file, NBTCompound value) throws FileNotFoundException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            writeCompressed(outputStream, value);
        } finally {
            if (outputStream != null) try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public NBTCompound readOfflinePlayer(OfflinePlayer player){
        File file = getPlayerFile(player);
        try{
            return readCompressed(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public NBTCompound readOfflinePlayer(String player){
        return readOfflinePlayer(Bukkit.getOfflinePlayer(player));
    }

    public boolean writeOfflinePlayer(OfflinePlayer player, NBTCompound value){
        File file = getPlayerFile(player);
        try{
            writeCompressed(file, value);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public boolean writeOfflinePlayer(String player, NBTCompound value){
        return writeOfflinePlayer(Bukkit.getPlayer(player), value);
    }

    private File getPlayerFile(OfflinePlayer player){
        File baseDir = (Bukkit.getWorlds().get(0)).getWorldFolder();
        if (getUUID != null) {
            UUID uuid = player.getUniqueId();
            File playerDir = new File(baseDir, "playerdata");
            return new File(playerDir, uuid+".dat");
        } else {
            File playerDir = new File(baseDir, "players");
            return new File(playerDir, player.getName() + ".dat");
        }
    }

}
