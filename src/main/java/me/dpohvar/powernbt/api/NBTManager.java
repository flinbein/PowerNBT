package me.dpohvar.powernbt.api;

import me.dpohvar.powernbt.utils.NBTParser;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * PowerNBT API.<br>
 * This class has methods to read and write NBT tags.<br>
 * {@link me.dpohvar.powernbt.api.NBTCompound} is used to work with NBTTagCompound,<br>
 * {@link me.dpohvar.powernbt.api.NBTList} is used to work with NBTTagList,<br>
 * {@link java.lang.String} is used to work with NBTTagString,<br>
 * all other NBT tags represents by primitive values.<br>
 * @since 0.7.1
 */

@SuppressWarnings("UnusedDeclaration")
public class NBTManager {

    private final NBTBridge nbtBridge = NBTBridge.getInstance();
    public static final NBTManager nbtManager = new NBTManager();

    /**
     * Get single instance of {@link me.dpohvar.powernbt.api.NBTManager}.
     *
     * @return NBTManager
     */
    public static NBTManager getInstance() {
        return nbtManager;
    }

    private NBTManager(){}

    /**
     * Read NBT tag of bukkit entity.
     *
     * @param entity Entity to read
     * @return Nbt tag of bukkit entity
     */
    public NBTCompound read(Entity entity){
        return new NBTCompound(nbtBridge.getEntityNBTTag(entity));
    }

    /**
     * Store nbt data to entity.
     *
     * @param entity Entity to modify
     * @param compound Nbt data to be stored
     */
    public void write(Entity entity, NBTCompound compound){
        nbtBridge.setEntityNBTTag(entity, compound.getHandle());
    }

    /**
     * Read extra nbt data of entity.<br>
     * Works with forge only.
     *
     * @param entity Entity to read
     * @return Extra nbt data. null if no forge
     */
    public NBTCompound readForgeData(Entity entity){
        return null;
    }

    /**
     * Store extra nbt data to entity.
     *
     * Works with forge only
     * @param entity entity
     * @param compound extra nbt data
     */
    public void writeForgeData(Entity entity, NBTCompound compound){}

    /**
     * Read nbt tag of {@link org.bukkit.inventory.ItemStack}.
     *
     * @param item Bukkit {@link org.bukkit.inventory.ItemStack}
     * @return Nbt data. null if item has no nbt and no meta
     */
    public NBTCompound read(ItemStack item){
        return new NBTCompound(nbtBridge.getItemStackNBTTag(item));
    }
    /**
     * Read nbt tag of {@link org.bukkit.Chunk}.
     *
     * @param chunk Bukkit chunk
     * @return Nbt data of chunk
     * @since 0.8.1
     */
    public NBTCompound read(Chunk chunk){
        throw new RuntimeException("chunk methods not implemented");
    }

    /**
     * Store nbt tag to selected chunk.
     *
     * @param chunk Chunk to be changed
     * @param compound Nbt data
     * @since 0.8.1
     */
    public void write(Chunk chunk, NBTCompound compound){
        throw new RuntimeException("chunk methods not implemented");
    }

    /**
     * Save nbt tag to item stack.<br>
     * You can save any data to CraftItemStack.<br>
     * You can save to ItemStack only data allowed by {@link org.bukkit.inventory.meta.ItemMeta}.<br>
     *
     * @param item bukkit item stack
     * @param compound tag
     */
    public void write(ItemStack item, NBTCompound compound){
        nbtBridge.setItemStackNBTTag(item, compound.getHandle());
    }

    /**
     * Read nbt data of tile entity at block.
     *
     * @param block Block with tile entity
     * @return Nbt data of tile entity or empty compound if no data
     */
    public NBTCompound read(Block block){
        BlockState state = block.getState();
        if (state instanceof TileState tileState) return read(tileState);
        return null;
    }

    /**
     * Read nbt data of tile entity at block.
     *
     * @param tileState Block with tile entity
     * @return Nbt data of tile entity or empty compound if no data
     */
    public NBTCompound read(TileState tileState){
        return new NBTCompound(nbtBridge.getBlockNBTTag(tileState));
    }

    /**
     * Save nbt data to tile entity at block.
     *
     * @param block Block with tile entity
     * @param compound Tag to be saved
     */
    public void write(Block block, NBTCompound compound){
        BlockState state = block.getState();
        if (state instanceof TileState tileState) {
            write(tileState, compound);
            tileState.update();
        }
    }

    /**
     * Save nbt data to tile entity at block.
     *
     * @param tileState Block with tile entity
     * @param compound Tag to be saved
     */
    public void write(TileState tileState, NBTCompound compound){
        nbtBridge.setBlockNBTTag(tileState, compound.getHandle());
    }

    /**
     * Read raw NBT data from input stream and convert to java object.
     *
     * @param inputStream InputStream to read
     * @return Read object
     * @throws IOException it happens sometimes
     */
    public Object read(InputStream inputStream) throws IOException {
        return read((DataInput) new DataInputStream(inputStream));
    }

    public Object read(InputStream inputStream, byte type) throws IOException {
        return read((DataInput) new DataInputStream(inputStream), type);
    }

    /**
     * Convert java object to nbt and write to outputStream.<br>
     * Allowed all primitive types, collections and maps.
     *
     * @param outputStream outputStream to write
     * @param value value to be written
     * @throws IOException it happens sometimes
     */
    public void write(OutputStream outputStream, Object value) throws IOException {
        write((DataOutput) new DataOutputStream(outputStream), value);
    }

    /**
     * Read compressed nbt compound.
     *
     * @param inputStream InputStream to read
     * @return Nbt rag
     */
    public Object readCompressed(InputStream inputStream) throws IOException {
        var dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputStream)));
        return getValueOfTag(nbtBridge.readNBTData(dis, dis.readByte()));
    }

    public Object readCompressed(InputStream inputStream, byte type) throws IOException {
        var dis = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputStream)));
        return getValueOfTag(nbtBridge.readNBTData(dis, type));
    }

    /**
     * Compress nbt compound and write to outputStream.
     *
     * @param outputStream outputStream to write
     * @param value value
     * @deprecated use {@link NBTManager#writeCompressed(OutputStream, Object)}
     */
    public void writeCompressed(OutputStream outputStream, NBTCompound value) throws IOException {
        writeCompressed(outputStream, (Object) value);
    }

    public void writeCompressed(OutputStream outputStream, Object value) throws IOException {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)));
        nbtBridge.writeNBTData(dos, getTagOfValue(value));
    }

    /**
     * Read nbt data from dataInput and convert to java object.
     *
     * @param dataInput dataInput to read
     * @return nbt data converted to java object
     * @throws IOException it happens
     */
    public Object read(DataInput dataInput) throws IOException {
        return read(dataInput, dataInput.readByte());
    }

    public Object read(DataInput dataInput, byte type) throws IOException {
        Object tag = nbtBridge.readNBTData(dataInput, type);
        return getValueOfTag(tag);
    }

    /**
     * Convert value to nbt and write to dataOutput.
     *
     * @param dataOutput dataOutput to save
     * @param value value to be written
     * @throws IOException it happens sometimes
     */
    public void write(DataOutput dataOutput, Object value) throws IOException {
        Object tag;
        if (value instanceof Map map) tag = new NBTCompound(map).getHandle();
        else if (value instanceof Collection col) tag = new NBTList(col).getHandle();
        else if (value instanceof Object[] col) tag = new NBTList(col).getHandle();
        else tag = nbtBridge.getTagValueByPrimitive(value);
        nbtBridge.writeNBTData(dataOutput, tag);
    }

    /**
     * Read raw nbt data from file and convert to java object.
     *
     * @param file file to read
     * @return nbt data converted to java types
     * @throws IOException it happens
     */
    public Object read(File file) throws IOException {
        try (var inputStream = new FileInputStream(file)) {
            return this.read(inputStream);
        }
    }

    /**
     * Write to file value converted to nbt tag.
     *
     * @param file file to write
     * @param value value to be written
     * @throws IOException it happens
     */
    public void write(File file, Object value) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        if (file.isDirectory()) throw new RuntimeException(new FileNotFoundException(file.getPath()));
        try (var outputStream = new FileOutputStream(file)){
            write(outputStream, value);
        }
    }

    /**
     * Read items as nbt values from inventory
     *
     * @param inventory an inventory
     * @return NBTList with items
     */
    @Deprecated
    public NBTList read(Inventory inventory){
        ItemStack[] contents = inventory.getContents();
        List<NBTCompound> compounds = new ArrayList<>(36);
        for (int i = 0; i<contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack == null) continue;
            Object tag = nbtBridge.getItemStackNBTTag(stack);
            NBTCompound compound = NBTCompound.forNBT(tag);
            compound.put("Slot", (byte) i);
            compounds.add(compound);
        }
        if (inventory instanceof PlayerInventory) try {
            ItemStack[] armor = ((PlayerInventory) inventory).getArmorContents();
            for (int i = 0; i<armor.length; i++) {
                ItemStack stack = armor[i];
                if (stack == null) continue;
                Object tag = nbtBridge.getItemStackNBTTag(stack);
                NBTCompound compound = NBTCompound.forNBT(tag);
                compound.put("Slot", (byte) 100 + i);
                compounds.add(compound);
            }
            ItemStack[] extra = ((PlayerInventory) inventory).getExtraContents();
            for (int i = 0; i<extra.length; i++) {
                ItemStack stack = extra[i];
                if (stack == null) continue;
                Object tag = nbtBridge.getItemStackNBTTag(stack);
                NBTCompound compound = NBTCompound.forNBT(tag);
                compound.put("Slot", (byte) 150 + i);
                compounds.add(compound);
            }
        } catch (Exception ignored) {}
        return new NBTList(compounds);
    }

    /**
     * Store items from nbt data to inventory
     *
     * @param inventory an inventory to change
     * @param value nbt array with items
     */
    @Deprecated
    public void write(Inventory inventory, NBTList value){
        ItemStack[] contents = new ItemStack[inventory.getContents().length];
        ItemStack[] armor = null;
        ItemStack[] extra = null;
        if (inventory instanceof PlayerInventory) try {
            armor = new ItemStack[((PlayerInventory) inventory).getArmorContents().length];
            extra = new ItemStack[((PlayerInventory) inventory).getExtraContents().length];
        } catch (Exception ignored) {}
        byte valueType = value.getType();
        if (valueType != 0 && valueType != 10) return;
        for (Object tag : value) {
            NBTCompound compound = (NBTCompound) tag;
            int slot = compound.getByte("Slot") & 255;
            ItemStack itemstack = createCraftItemStack(compound);
            if (slot < contents.length) {
                contents[slot] = itemstack;
            } else if (armor != null && slot >= 100 && slot < armor.length + 100) {
                armor[slot - 100] = itemstack;
            } else if (extra != null && slot >= 150 && slot < extra.length + 150) {
                extra[slot - 150] = itemstack;
            }
        }
        inventory.clear();
        inventory.setContents(contents);
        if (inventory instanceof PlayerInventory) try {
            ((PlayerInventory) inventory).setArmorContents(armor);
            ((PlayerInventory) inventory).setExtraContents(extra);
        } catch (Exception ignored) {}
    }

    /**
     * Read compressed nbt data from file and convert to java object.
     *
     * @param file file to read
     * @return nbt data converted to java types
     */
    public Object readCompressed(File file) {
        try (var inputStream = new FileInputStream(file)){
            return readCompressed(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert value to nbt and write to file with compression.
     *
     * @param file file to write
     * @param value value to be written
     */
    public void writeCompressed(File file, Object value) {
        if (!file.exists()) try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (var outputStream = new FileOutputStream(file)){
            writeCompressed(outputStream, value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read offline player's .dat file.
     *
     * @param player player to read
     * @return nbt data read from a file
     */
    public NBTCompound readOfflinePlayer(OfflinePlayer player){
        return (NBTCompound) readCompressed(getPlayerFile(player));
    }

    /**
     * Read offline player's .dat file.
     *
     * @param player player name
     * @return nbt data read from a file
     */
    @SuppressWarnings("deprecation")
    public NBTCompound readOfflinePlayer(String player){
        return readOfflinePlayer(Bukkit.getOfflinePlayer(player));
    }

    /**
     * Write nbt data to player's .dat file.
     *
     * @param player offline player
     * @param value value to be written
     * @return true on success, false otherwise
     */
    public boolean writeOfflinePlayer(OfflinePlayer player, NBTCompound value){
        try{
            writeCompressed(getPlayerFile(player), value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Write nbt data to player's .dat file.
     *
     * @param player player name
     * @param value value to be written
     * @return true on success, false otherwise
     */
    public boolean writeOfflinePlayer(String player, NBTCompound value){
        return writeOfflinePlayer(Bukkit.getPlayer(player), value);
    }

    /**
     * Get {@link org.bukkit.OfflinePlayer} file with stored nbt data.
     *
     * @param player offline player
     * @return player's file
     */
    public File getPlayerFile(OfflinePlayer player){
        File baseDir = (Bukkit.getWorlds().get(0)).getWorldFolder();
        UUID uuid = player.getUniqueId();
        File playerDir = new File(baseDir, "playerdata");
        return new File(playerDir, uuid+".dat");
    }

    /**
     * Parse mojangson string.<br>
     * This method can return:<br>
     * byte, short, int, long, float, double, byte[], String, int[],<br>
     * {@link me.dpohvar.powernbt.api.NBTList} or {@link me.dpohvar.powernbt.api.NBTCompound}<br>
     * Examples:<br> <pre>
     *     manager.parseMojangson("12s\\"); // 12 short
     *     manager.parseMojangson("{foo:bar}"); // NBTCompound
     * </pre>
     *
     * @param value String in Mojangson format
     * @return Parse result
     * @since 0.8.2
     */
    public Object parseMojangson(String value){
        if (value == null) return null;
        Object tag = NBTParser.parser("", value).parse();
        return getValueOfTag(tag);
    }

    public Object getValueOfTag(Object tag){
        if (tag == null) return null;
        byte type = nbtBridge.getTagType(tag);
        return switch (type) {
            case 0 -> null;
            case 9 -> new NBTList(tag);
            case 10 -> new NBTCompound(tag);
            default -> nbtBridge.getPrimitiveValue(tag);
        };
    }

    public Object getTagOfValue(Object value){
        if (value instanceof Map a) return new NBTCompound(a).getHandle();
        if (value instanceof Collection a) return new NBTList(a).getHandle();
        if (value instanceof Object[] a) return new NBTList(a).getHandle();
        return nbtBridge.getTagValueByPrimitive(value);
    }

    public byte getTagType(Object tag){
        if (tag == null) return 0;
        return nbtBridge.getTagType(tag);
    }

    public byte getValueType(Object value){
        if (value == null) return 0;
        if (value instanceof Byte) return 1;
        if (value instanceof Short) return 2;
        if (value instanceof Integer) return 3;
        if (value instanceof Long) return 4;
        if (value instanceof Float) return 5;
        if (value instanceof Double) return 6;
        if (value instanceof byte[]) return 7;
        if (value instanceof String) return 8;
        if (value instanceof NBTList) return 9;
        if (value instanceof NBTCompound) return 10;
        if (value instanceof int[]) return 11;
        if (value instanceof long[]) return 12;
        throw new RuntimeException("unknown tag type");
    }

    /**
     * Spawn entity in world by nbt compound.<br>
     * Entity location must be stored in "Pos" tag.
     *
     * @param compound Entity data
     * @param world World where to spawn entity
     * @return Spawned entity
     * @since 0.8.2
     */
    public Entity spawnEntity(NBTCompound compound, World world){
        if (compound == null) return null;
        compound = compound.clone();
        compound.remove("UUID");
        var entity = nbtBridge.spawnEntity(compound.getHandle(), world);
        var passengers = compound.getList("Passengers");

        if (passengers != null) {
            var pos = compound.getList("Pos");
            for (Object passengerData : passengers) {
                if (passengerData instanceof NBTCompound passengerCompound) {
                    passengerCompound.put("Pos", pos);
                    var passenger = spawnEntity(passengerCompound, world);
                    if (passenger != null) entity.addPassenger(passenger);
                }
            }
        }
        return entity;
    }

    /**
     * Convert bukkit ItemStack to CraftItemStack.
     *
     * @param itemStack Bukkit ItemStack
     * @return CraftItemStack with nms item
     */
    public ItemStack asCraftItemStack(ItemStack itemStack){
        return nbtBridge.asCraftCopyItemStack(itemStack);
    }

    /**
     * Create CraftItemStack from nbt value
     *
     * @param compound nbt data of item: Slot, Count, Damage, id, tag
     * @return new CraftItemStack
     */
    public ItemStack createCraftItemStack(NBTCompound compound){
        if (compound == null) return new ItemStack(Material.AIR);
        var itemStack = asCraftItemStack(new ItemStack(Material.APPLE));
        nbtBridge.setItemStackNBTTag(itemStack, compound.getHandle());
        return itemStack;
    }

}
