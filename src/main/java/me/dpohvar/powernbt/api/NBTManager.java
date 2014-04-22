package me.dpohvar.powernbt.api;

import me.dpohvar.powernbt.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.*;

public class NBTManager {

    public static final NBTManager nbtManager = new NBTManager();

    private NBTManager(){}

    EntityUtils entityUtils = EntityUtils.entityUtils;
    ItemStackUtils itemStackUtils = ItemStackUtils.itemStackUtils;
    NBTBlockUtils nbtBlockUtils = NBTBlockUtils.nbtBlockUtils;
    NBTCompressedUtils nbtCompressedUtils = NBTCompressedUtils.nbtCompressedUtils;
    NBTUtils nbtUtils = NBTUtils.nbtUtils;

    public NBTCompound read(Entity entity){
        NBTCompound compound = new NBTCompound();
        entityUtils.readEntity(entity, compound.getHandle());
        return compound;
    }

    public void write(Entity entity, NBTCompound compound){
        entityUtils.writeEntity(entity, compound.getHandle());
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

    public NBTCompound readCompressed(DataInput dataInput){
        Object tag = nbtCompressedUtils.readCompound(dataInput);
        return NBTCompound.forNBT(tag);
    }

    public void writeCompressed(DataOutput dataOutput, NBTCompound value){
        nbtCompressedUtils.writeCompound(value.getHandle(), dataOutput);
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

    public NBTCompound readOfflinePlayer(String name){
        File baseDir = (Bukkit.getWorlds().get(0)).getWorldFolder();
        File playerDir = new File(baseDir, "players");
        File file = new File(playerDir, name + ".dat");
        try{
            return readCompressed(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public boolean writeOfflinePlayer(String name, NBTCompound value){
        File baseDir = (Bukkit.getWorlds().get(0)).getWorldFolder();
        File playerDir = new File(baseDir, "players");
        File file = new File(playerDir, name + ".dat");
        try{
            writeCompressed(file, value);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

}
