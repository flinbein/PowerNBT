package me.dpohvar.powernbt.api;

import me.dpohvar.powernbt.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.UUID;
import java.util.logging.Level;

import static me.dpohvar.powernbt.utils.MojangsonUtils.*;

/**
 * NBTManager has methods to read and write NBT tags
 */

@SuppressWarnings("UnusedDeclaration")
public class NBTManager {

    public static final NBTManager nbtManager = new NBTManager();

    /**
     * get single instance of NBTManager
     * @return NBTManager
     */
    public static NBTManager getInstance() {
        return nbtManager;
    }

    EntityUtils entityUtils = EntityUtils.entityUtils;
    ItemStackUtils itemStackUtils = ItemStackUtils.itemStackUtils;
    NBTBlockUtils nbtBlockUtils = NBTBlockUtils.nbtBlockUtils;
    NBTCompressedUtils nbtCompressedUtils = NBTCompressedUtils.nbtCompressedUtils;
    NBTUtils nbtUtils = NBTUtils.nbtUtils;
    ChunkUtils chunkUtils = ChunkUtils.chunkUtils;
    ReflectionUtils.RefMethod getUUID;

    private NBTManager(){
        try {
            getUUID = ReflectionUtils.getRefClass(OfflinePlayer.class).findMethodByReturnType(UUID.class);
        } catch (Exception ignored) {
            // can't use UUID, do nothing
        }
    }

    /**
     * read NBT tag of bukkit entity
     * @param entity entity
     * @return nbt data of entity
     */
    public NBTCompound read(Entity entity){
        NBTCompound compound = new NBTCompound();
        entityUtils.readEntity(entity, compound.getHandle());
        return compound;
    }

    /**
     * save nbt data to entity
     * @param entity entity
     * @param compound nbt data
     */
    public void write(Entity entity, NBTCompound compound){
        entityUtils.writeEntity(entity, compound.getHandle());
    }

    /**
     * read extra nbt data of entity.
     * Works with forge only
     * @param entity entity
     * @return extra nbt data. null if not forge
     */
    public NBTCompound readForgeData(Entity entity){
        Object tag = entityUtils.getForgeData(entity);
        if (tag==null) return new NBTCompound();
        else return NBTCompound.forNBTCopy(tag);
    }

    /**
     * save extra nbt data to entity.
     * Works with forge only
     * @param entity entity
     * @param compound extra nbt data
     */
    public void writeForgeData(Entity entity, NBTCompound compound){
        entityUtils.setForgeData(entity, compound.getHandleCopy());
    }

    /**
     * read nbt tag of ItemStack
     * @param item bukkit item stack
     * @return nbt data. null if item has no nbt and no meta
     */
    public NBTCompound read(ItemStack item){
        Object tag = ItemStackUtils.itemStackUtils.getTag(item);
        return NBTCompound.forNBTCopy(tag);
    }
    /**
     * read nbt tag of Chunk
     * @param chunk bukkit chunk
     * @return nbt data of chunk
     */
    public NBTCompound read(Chunk chunk){
        NBTCompound compound = new NBTCompound();
        chunkUtils.readChunk(chunk, compound.getHandle());
        return compound;
    }

    /**
     * change selected chunk with nbt data
     * @param chunk chunk to be changed
     * @param compound nbt data
     */
    public void write(Chunk chunk, NBTCompound compound){
        chunkUtils.writeChunk(chunk, compound.getHandle());
    }

    /**
     * save nbt tag to item stack.
     * On CraftItemStack you can save any data.
     * On ItemStack you can save only data allowed by ItemMeta.
     * @param item bukkit item stack
     * @param compound tag
     */
    public void write(ItemStack item, NBTCompound compound){
        itemStackUtils.setTag(item, compound.getHandleCopy());
    }

    /**
     * read nbt data of tile entity at block
     * @param block block with tile entity
     * @return nbt data of tile entity or empty compound if no tile
     */
    public NBTCompound read(Block block){
        NBTCompound compound = new NBTCompound();
        nbtBlockUtils.readTag(block,compound.getHandle());
        return compound;
    }

    /**
     * save nbt data to tile entity at block
     * @param block block with tile entity
     * @param compound tag to be saved
     */
    public void write(Block block, NBTCompound compound){
        nbtBlockUtils.setTag(block, compound.getHandle());
        nbtBlockUtils.update(block);
    }

    /**
     * read raw NBT data from input stream and convert to java object
     * @param inputStream inputStream to read
     * @return read object
     * @throws IOException it happens sometimes
     */
    public Object read(InputStream inputStream) throws IOException {
        return read((DataInput) new DataInputStream(inputStream));
    }

    /**
     * convert java object to nbt and write to outputStream.
     * Allowed all primitive types, collections and maps
     * @param outputStream outputStream to write
     * @param value value to be written
     * @throws IOException
     */
    public void write(OutputStream outputStream, Object value) throws IOException {
        write((DataOutput) new DataOutputStream(outputStream), value);
    }

    /**
     * read compressed nbt compound
     * @param inputStream inputStream to read
     * @return nbt rag
     */
    public NBTCompound readCompressed(InputStream inputStream){
        Object tag = nbtCompressedUtils.readCompound(inputStream);
        return NBTCompound.forNBT(tag);
    }

    /**
     * compress nbt compound and write to outputStream
     * @param outputStream outputStream to write
     * @param value value
     */
    public void writeCompressed(OutputStream outputStream, NBTCompound value){
        nbtCompressedUtils.writeCompound(value.getHandle(), outputStream);
    }

    /**
     * read nbt data from dataInput and convert to java object
     * @param dataInput dataInput to read
     * @return nbt data converted to java object
     * @throws IOException it happens
     */
    public Object read(DataInput dataInput) throws IOException {
        Object tag =  nbtUtils.readTag(dataInput);
        return nbtUtils.getValue(tag);
    }

    /**
     * convert value to nbt and write to dataOutput
     * @param dataOutput dataOutput to save
     * @param value value to be written
     * @throws IOException it happens sometimes
     */
    public void write(DataOutput dataOutput, Object value) throws IOException {
        Object tag =  nbtUtils.createTag(value);
        nbtUtils.writeTagToOutput(dataOutput, tag);
    }

    /**
     * read raw nbt data from file and convert to java object
     * @param file file to read
     * @return nbt data converted to java types
     * @throws IOException
     */
    public Object read(File file) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return read(inputStream);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.ALL, "can not close NBT file " + file, e);
            }
        }
    }

    /**
     * convert value to nbt and write to file
     * @param file file to write
     * @param value value to be written
     * @throws IOException it happens
     */
    public void write(File file, Object value) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            write(outputStream, value);
        } finally {
            if (outputStream != null) try {
                outputStream.close();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.ALL, "can not close NBT file "+file, e);
            }
        }
    }

    /**
     * read compressed nbt data from file and convert to java object
     * @param file file to read
     * @return nbt data converted to java types
     * @throws FileNotFoundException
     */
    public NBTCompound readCompressed(File file) throws FileNotFoundException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return readCompressed(inputStream);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.ALL, "can not close NBT file "+file, e);
            }
        }
    }

    /**
     * convert value to nbt and write to file with compression
     * @param file file to write
     * @param value value to be written
     * @throws FileNotFoundException check your file
     */
    public void writeCompressed(File file, NBTCompound value) throws FileNotFoundException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            writeCompressed(outputStream, value);
        } finally {
            if (outputStream != null) try {
                outputStream.close();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.ALL, "can not close NBT file " + file, e);
            }
        }
    }

    /**
     * read offline player's .dat file
     * @param player player to read
     * @return nbt data read from a file
     */
    public NBTCompound readOfflinePlayer(OfflinePlayer player){
        File file = getPlayerFile(player);
        try{
            return readCompressed(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * read offline player's .dat file
     * @param player player name
     * @return nbt data read from a file
     */
    @SuppressWarnings("deprecation")
    public NBTCompound readOfflinePlayer(String player){
        return readOfflinePlayer(Bukkit.getOfflinePlayer(player));
    }

    /**
     * write nbt data to player's .dat file
     * @param player offline player
     * @param value value to be written
     * @return true on success, false otherwise
     */
    public boolean writeOfflinePlayer(OfflinePlayer player, NBTCompound value){
        File file = getPlayerFile(player);
        try{
            writeCompressed(file, value);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    /**
     * write nbt data to player's .dat file
     * @param player player name
     * @param value value to be written
     * @return true on success, false otherwise
     */
    @SuppressWarnings("deprecation")
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

    /**
     * Parse mojangson string
     * @param value string in Mojangson format
     * @return a primitive value or {@link NBTCompound} or {@link NBTList}
     */
    public Object parseMojangson(String value){
        if (value == null) return null;
        Object tag = mojangsonUtils.parseString("",value);
        return nbtUtils.getValue(tag);
    }

    /**
     * Spawn entity in world by nbt compound
     * @param compound entity data
     * @param world world
     * @return spawned entity
     */
    public Entity spawnEntity(NBTCompound compound, World world){
        if (compound == null) return null;
        return entityUtils.spawnEntity(compound.getHandle(), world);
    }

}
