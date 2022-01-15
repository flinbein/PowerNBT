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

    static boolean checkCrossReferences(LinkedList<Object> list, Collection<?> values){
        for (Object value : values) {
            if (list.contains(value)) return true;
            if (value instanceof Collection col) {
                list.push(value);
                if (checkCrossReferences(list, col)) return true;
                list.pop();
            } else if (value instanceof Map map) {
                list.push(value);
                if (checkCrossReferences(list, map.values())) return true;
                list.pop();
            } else if (value instanceof Object[]) {
                list.push(value);
                if (checkCrossReferences(list, Arrays.asList((Object[])value))) return true;
                list.pop();
            }
        }
        return false;
    }

    static boolean checkCrossReferences(Map<?,?> map){
        LinkedList<Object> list = new LinkedList<>();
        list.push(map);
        return checkCrossReferences(list, map.values());
    }

    static boolean checkCrossReferences(Collection<?> collection){
        LinkedList<Object> list = new LinkedList<>();
        list.push(collection);
        return checkCrossReferences(list, collection);
    }

    static boolean checkCrossReferences(Object[] collection){
        LinkedList<Object> list = new LinkedList<>();
        list.push(collection);
        return checkCrossReferences(list, Arrays.asList(collection));
    }

    public static Object convertValue(Object value, byte type) {
        return switch (type) {
            case 0 /*end*/ -> null;
            case 1 /*byte*/ -> {
                if (value instanceof Byte current) yield current;
                if (value == null) yield (byte) 0;
                if (value instanceof Boolean b) yield (byte) (b ? 1 : 0);
                if (value instanceof Number n) yield n.byteValue();
                if (value instanceof CharSequence) yield Byte.valueOf(value.toString());
                if (value instanceof Character n) yield (byte) (char) n;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 2 /*short*/ -> {
                if (value instanceof Short current) yield current;
                if (value == null) yield (short) 0;
                if (value instanceof Boolean b) yield (short) (b ? 1 : 0);
                if (value instanceof Number n) yield n.shortValue();
                if (value instanceof CharSequence) yield Short.valueOf(value.toString());
                if (value instanceof Character n) yield (short) (char) n;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 3 /*int*/ -> {
                if (value instanceof Integer current) yield current;
                if (value == null) yield (int) 0;
                if (value instanceof Boolean b) yield (int) (b ? 1 : 0);
                if (value instanceof Number n) yield n.intValue();
                if (value instanceof CharSequence) yield Integer.valueOf(value.toString());
                if (value instanceof Character n) yield (int) (char) n;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 4 /*long*/ -> {
                if (value instanceof Long current) yield current;
                if (value == null) yield (long) 0;
                if (value instanceof Boolean b) yield (long) (b ? 1 : 0);
                if (value instanceof Number n) yield n.longValue();
                if (value instanceof CharSequence) yield Long.valueOf(value.toString());
                if (value instanceof Character n) yield (long) (char) n;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 5 /*float*/ -> {
                if (value instanceof Float current) yield current;
                if (value == null) yield (float) 0;
                if (value instanceof Boolean b) yield (float) (b ? 1 : 0);
                if (value instanceof Number n) yield n.floatValue();
                if (value instanceof CharSequence) yield Float.valueOf(value.toString());
                if (value instanceof Character n) yield (float) (char) n;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 6 /*double*/ -> {
                if (value instanceof Double current) yield current;
                if (value == null) yield (double) 0;
                if (value instanceof Boolean b) yield (double) (b ? 1 : 0);
                if (value instanceof Number n) yield n.doubleValue();
                if (value instanceof CharSequence) yield Double.valueOf(value.toString());
                if (value instanceof Character n) yield (double) (char) n;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 7 /*byte[]*/ -> {
                if (value instanceof byte[] current) yield current;
                if (value == null) yield new byte[0];
                Object[] arr = convertToObjectArrayOrNull(value);
                if (arr != null) {
                    Byte[] array = Arrays.stream(arr).map(val -> (Byte) convertValue(val, (byte) 1)).toArray(Byte[]::new);
                    yield ArrayUtils.toPrimitive(array);
                }
                if (value instanceof CharSequence cs) yield cs.toString().getBytes(StandardCharsets.UTF_8);
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 8 /*String*/ -> {
                if (value instanceof String s) yield s;
                if (value instanceof CharSequence) yield value.toString();
                if (value instanceof char[] c) yield String.copyValueOf(c);
                if (value instanceof byte[] c) yield new String(c, StandardCharsets.UTF_8);
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 9 /*List*/ -> {
                if (value instanceof CharSequence s) yield Arrays.stream(s.toString().split("")).toList();
                if (value instanceof Collection a) yield a;
                Object[] arr = convertToObjectArrayOrNull(value);
                if (arr != null) yield Arrays.stream(arr).toList();
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 10 /*Compound*/ -> {
                if (value instanceof Map s) yield s;
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 11 /*int[]*/ -> {
                if (value instanceof int[] current) yield current;
                if (value == null) yield new int[0];
                Object[] arr = convertToObjectArrayOrNull(value);
                if (arr != null) {
                    Integer[] array = Arrays.stream(arr).map(val -> (Integer) convertValue(val, (byte) 3)).toArray(Integer[]::new);
                    yield ArrayUtils.toPrimitive(array);
                }
                throw new RuntimeException("Wrong value of type "+type);
            }
            case 12 /*long[]*/ -> {
                if (value instanceof long[] current) yield current;
                if (value == null) yield new long[0];
                Object[] arr = convertToObjectArrayOrNull(value);
                if (arr != null) {
                    Long[] array = Arrays.stream(arr).map(val -> (Long) convertValue(val, (byte) 4)).toArray(Long[]::new);
                    yield ArrayUtils.toPrimitive(array);
                }
                throw new RuntimeException("Wrong value of type "+type);
            }
            default -> throw new RuntimeException("unknown tag type:"+type);
        };
    }

    public static Object[] convertToObjectArrayOrNull(Object someArray){
        if (someArray instanceof Object[] res) return res;
        if (someArray instanceof boolean[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof byte[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof short[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof int[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof long[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof float[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof double[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof char[] a) return ArrayUtils.toObject(a);
        if (someArray instanceof Collection a) return a.toArray();
        return null;
    }

    public static Object convertToPrimitiveArrayOrNull(Object[] objArray){
        if (objArray instanceof Boolean[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Character[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Byte[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Short[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Integer[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Long[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Float[] a) return ArrayUtils.toPrimitive(a);
        if (objArray instanceof Double[] a) return ArrayUtils.toPrimitive(a);
        return null;
    }

    public static Object convertToPrimitiveClassType(Class<?> clazz, Object value){
        if (clazz == Boolean.class || clazz == boolean.class) {
            if (value == null) return false;
            if (value instanceof Boolean b) return b;
            if (value instanceof Character b) return b != 0;
            if (value instanceof Number b) return b.doubleValue() != 0 && !Double.isNaN(b.doubleValue());
            if (value instanceof String b) return !b.isEmpty();
            if (value instanceof Object[] b) return b.length > 0;
            if (value instanceof Collection b) return b.size() > 0;
            if (value instanceof Map b) return b.size() > 0;
            Object[] objects = convertToObjectArrayOrNull(value);
            if (objects != null) return objects.length > 0;
            return true;
        } else if (clazz == Character.class || clazz == char.class) {
            if (value == null) return '\0';
            if (value instanceof Boolean b) return b ? '1' : '0';
            if (value instanceof Character b) return b;
            if (value instanceof Number b) return (char) b.intValue();
            if (value instanceof String s) return s.isEmpty() ? '\0' : s.charAt(0);
        } else if (clazz == Byte.class || clazz == byte.class) {
            if (value instanceof Boolean b) return b ? (byte) 1 : (byte) 0;
            if (value instanceof Character b) return (byte) (char) b;
            if (value instanceof Number b) return b.byteValue();
            if (value instanceof String s) return Byte.valueOf(s);
        } else if (clazz == Short.class || clazz == short.class) {
            if (value instanceof Boolean b) return b ? (short) 1 : (short) 0;
            if (value instanceof Character b) return (short) (char) b;
            if (value instanceof Number b) return b.shortValue();
            if (value instanceof String s) return Short.valueOf(s);
        } else if (clazz == Integer.class || clazz == int.class) {
            if (value instanceof Boolean b) return b ? 1 : 0;
            if (value instanceof Character b) return (int) (char) b;
            if (value instanceof Number b) return b.intValue();
            if (value instanceof String s) return Integer.valueOf(s);
        } else if (clazz == Long.class || clazz == long.class) {
            if (value instanceof Boolean b) return b ? (long) 1 : (long) 0;
            if (value instanceof Character b) return (long) (char) b;
            if (value instanceof Number b) return b.longValue();
            if (value instanceof String s) return Long.valueOf(s);
        } else if (clazz == Float.class || clazz == float.class) {
            if (value instanceof Boolean b) return b ? (float) 1 : (float) 0;
            if (value instanceof Character b) return (float) (char) b;
            if (value instanceof Number b) return b.floatValue();
            if (value instanceof String s) return Float.valueOf(s);
        } else if (clazz == Double.class || clazz == double.class) {
            if (value instanceof Boolean b) return b ? (double) 1 : (double) 0;
            if (value instanceof Character b) return (double) (char) b;
            if (value instanceof Number b) return b.doubleValue();
            if (value instanceof String s) return Double.valueOf(s);
        }
        return null;
    }

    public static Object modifyArray(Object array, Consumer<List<Object>> consumer){
        if (array == null) return null;
        Class<?> baseClass = array.getClass().getComponentType();
        if (baseClass == null) return null;
        Object[] objectArray = convertToObjectArrayOrNull(array);
        List<Object> list = new ArrayList<>(List.of(objectArray));
        consumer.accept(list);
        Object[] copyArray = (Object[]) Array.newInstance(objectArray.getClass().getComponentType(), list.size());
        if (baseClass.isPrimitive()) {
            Object[] resultArray = list.stream().map(v -> convertToPrimitiveClassType(baseClass, v)).toList().toArray(copyArray);
            return convertToPrimitiveArrayOrNull(resultArray);
        } else {
            return list.toArray(copyArray);
        }
    }

    public static Object mapArray(Object array, Function<List<?>, List<?>> function){
        if (array == null) return null;
        Class<?> baseClass = array.getClass().getComponentType();
        if (baseClass == null) return null;
        Object[] objectArray = convertToObjectArrayOrNull(array);
        List<?> list = new ArrayList<>(List.of(objectArray));
        List<?> resultList = function.apply(list);
        Object[] copyArray = (Object[]) Array.newInstance(objectArray.getClass().getComponentType(), resultList.size());
        if (baseClass.isPrimitive()) {
            Object[] resultArray = resultList.stream().map(v -> convertToPrimitiveClassType(baseClass, v)).toList().toArray(copyArray);
            return convertToPrimitiveArrayOrNull(resultArray);
        } else {
            return resultList.toArray(copyArray);
        }
    }

}
