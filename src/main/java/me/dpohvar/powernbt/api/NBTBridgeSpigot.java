package me.dpohvar.powernbt.api;

import com.google.common.base.Charsets;
import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.utils.ReflectionUtils.RefClass;
import me.dpohvar.powernbt.utils.ReflectionUtils.RefConstructor;
import me.dpohvar.powernbt.utils.ReflectionUtils.RefField;
import me.dpohvar.powernbt.utils.ReflectionUtils.RefMethod;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.dpohvar.powernbt.utils.ReflectionUtils.getRefClass;

class NBTBridgeSpigot extends NBTBridge {

    // NBT:
    private final RefConstructor<?> nbtTagEndCon = getRefClass("net.minecraft.nbt.NBTTagEnd").getConstructor();//
    private final RefConstructor<?> nbtTagByteCon = getRefClass("net.minecraft.nbt.NBTTagByte").getConstructor(byte.class);//
    private final RefConstructor<?> nbtTagShortCon = getRefClass("net.minecraft.nbt.NBTTagShort").getConstructor(short.class);//
    private final RefConstructor<?> nbtTagIntCon = getRefClass("net.minecraft.nbt.NBTTagInt").getConstructor(int.class);//
    private final RefConstructor<?> nbtTagLongCon = getRefClass("net.minecraft.nbt.NBTTagLong").getConstructor(long.class);//
    private final RefConstructor<?> nbtTagFloatCon = getRefClass("net.minecraft.nbt.NBTTagFloat").getConstructor(float.class);//
    private final RefConstructor<?> nbtTagDoubleCon = getRefClass("net.minecraft.nbt.NBTTagDouble").getConstructor(double.class);//
    private final RefClass<?> nbtTagByteArrayClazz = getRefClass("net.minecraft.nbt.NBTTagByteArray");
    private final RefConstructor<?> nbtTagByteArrayCon = nbtTagByteArrayClazz.getConstructor(byte[].class);//
    private final RefClass<?> nmNBTTagStringClazz = getRefClass("net.minecraft.nbt.NBTTagString");
    private final RefConstructor<?> nbtTagStringCon = nmNBTTagStringClazz.getConstructor(String.class);//
    private final RefClass<?> nbtTagIntArrayClazz = getRefClass("net.minecraft.nbt.NBTTagIntArray");
    private final RefConstructor<?> nbtTagIntArrayCon = nbtTagIntArrayClazz.getConstructor(int[].class);
    private final RefClass<?> nbtTagLongArrayClazz = getRefClass("net.minecraft.nbt.NBTTagLongArray");
    private final RefConstructor<?> nbtTagLongArrayCon = nbtTagLongArrayClazz.getConstructor(long[].class);
    private final RefClass<?> nbtNumberClazz = getRefClass("net.minecraft.nbt.NBTNumber");
    private final RefClass<?> nbtTagListClazz = getRefClass("net.minecraft.nbt.NBTTagList");
    private final RefConstructor<?> nbtTagListCon = nbtTagListClazz.getConstructor();
    private final RefClass<?> nbtTagCompoundClazz = getRefClass("net.minecraft.nbt.NBTTagCompound");
    private final RefConstructor<?> nbtTagCompoundCon = nbtTagCompoundClazz.getConstructor();
    private final RefField<?> nmCompoundMapField = nbtTagCompoundClazz.findField(Map.class);
    private final RefConstructor<?> nmCompoundCon = nbtTagCompoundClazz.getConstructor();
    private final RefField<Byte> nmListTypeField = nbtTagListClazz.findField(byte.class, false);
    private final RefField<List> nmListListField = nbtTagListClazz.findField(List.class);
    private final RefMethod<?> nmNBTTagGetTypeMethod = RefMethod.parse("net.minecraft.nbt.NBTTagTypes static *(int): net.minecraft.nbt.NBTTagType");
    private final RefMethod<?> nmNBTTagParseTypeMethod = RefMethod.parse("net.minecraft.nbt.NBTTagType !static *(java.io.DataInput, int, net.minecraft.nbt.NBTReadLimiter)");
    private final RefField<?> nmNBTGetReadLimiterField = RefField.parse("net.minecraft.nbt.NBTReadLimiter *:net.minecraft.nbt.NBTReadLimiter");
    private final Object readLimiter = nmNBTGetReadLimiterField.of(null).get();
    private final RefField<?> nmNBTTagStringValueField = RefField.parse("net.minecraft.nbt.NBTTagString *:java.lang.String");
    private final RefMethod<?> nmNBTNumberValueMethod = RefMethod.parse("net.minecraft.nbt.NBTNumber *():java.lang.Number");
    private final RefField<?> nmNBTTagByteArrayValueField = RefField.parse("net.minecraft.nbt.NBTTagByteArray *:byte[]");
    private final RefField<?> nmNBTTagIntArrayValueField = RefField.parse("net.minecraft.nbt.NBTTagIntArray *:int[]");
    private final RefField<?> nmNBTTagLongArrayValueField = RefField.parse("net.minecraft.nbt.NBTTagLongArray *:long[]");
    private final RefMethod<?> nbtBaseWriteDataMethod = RefMethod.parse("net.minecraft.nbt.NBTBase *(java.io.DataOutput): void");
    private final RefMethod<?> nbtBaseGetTypeMethod = RefMethod.parse("net.minecraft.nbt.NBTBase *(): byte");
    private final RefMethod<?> nbtBaseCloneMethod = RefMethod.parse("net.minecraft.nbt.NBTBase *(): net.minecraft.nbt.NBTBase");
    private final RefMethod<?> toolsReadDataInput = RefMethod.parse("net.minecraft.nbt.NBTCompressedStreamTools *(java.io.DataInput): net.minecraft.nbt.NBTTagCompound");

    // Entity:
    private final RefMethod<?> cEntityGetHandleMethod; // getHandle()
    private final RefMethod<?> cEntityGetNBTMethod; // NBTTagCompound getNBT(NBTTagCompound tag)
    private final RefMethod<?> cEntityGetNBTSimpleMethod; // NBTTagCompound getNBTSimple(NBTTagCompound tag)
    private final RefMethod<?> cEntitySetNBTMethod; // void setNBT(NBTTagCompound tag)
    private final RefMethod<?> cEntityTypesCreateMethod; // void setNBT(NBTTagCompound tag)
    private final RefMethod<?> nEntityGetBukkitMethod; // void setNBT(NBTTagCompound tag)

    // Block
    private final RefMethod<?> cBlockGetSnapshotMethod;
    private final RefMethod<?> nBlockGetNBTMethod;
    private final RefMethod<?> nBlockSetNBTMethod;

    // ItemStack
    private final RefField<?> cItemHandleField;
    private final RefMethod<?> cItemCraftCopyMethod;
    private final RefMethod<?> nItemGetNBTMethod;
    private final RefMethod<?> nItemSetNBTMethod;

    // World
    private final RefMethod<?> cWorldGetHandleMethod;
    private final RefMethod<?> cWorldAddEntityMethod;

    public NBTBridgeSpigot(){
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/PowerNBT/config.yml"));
        final InputStream defConfigStream = PowerNBT.class.getClassLoader().getResourceAsStream("config.yml");
        if (defConfigStream != null) {
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8));
            config.setDefaults(defaults);
        }
        var bukkitVersionKey = Bukkit.getBukkitVersion().replace('.','_');

        ConfigurationSection section = config.getConfigurationSection("hooks." + bukkitVersionKey);
        ConfigurationSection defSection = config.getConfigurationSection("hooks.default");

        cEntityGetNBTMethod = RefMethod.parse(getConfString("entity.getNBT", section, defSection));
        cEntityGetNBTSimpleMethod = RefMethod.parse(getConfString("entity.getNBTSimple", section, defSection));
        cEntitySetNBTMethod = RefMethod.parse(getConfString("entity.setNBT", section, defSection));
        cEntityGetHandleMethod = RefMethod.parse(getConfString("entity.getHandle", section, defSection));
        cEntityTypesCreateMethod = RefMethod.parse(getConfString("entity.createType", section, defSection));
        nEntityGetBukkitMethod = RefMethod.parse(getConfString("entity.getBukkitEntity", section, defSection));

        cBlockGetSnapshotMethod = RefMethod.parse(getConfString("block.getSnapshot", section, defSection));
        nBlockGetNBTMethod = RefMethod.parse(getConfString("block.getNBT", section, defSection));
        nBlockSetNBTMethod = RefMethod.parse(getConfString("block.setNBT", section, defSection));

        cItemHandleField = RefField.parse(getConfString("item.handle", section, defSection));
        cItemCraftCopyMethod = RefMethod.parse(getConfString("item.craftCopy", section, defSection));
        nItemGetNBTMethod = RefMethod.parse(getConfString("item.getNBT", section, defSection));
        nItemSetNBTMethod = RefMethod.parse(getConfString("item.setNBT", section, defSection));

        cWorldGetHandleMethod = RefMethod.parse(getConfString("world.getHandle", section, defSection));
        cWorldAddEntityMethod = RefMethod.parse(getConfString("world.addEntity", section, defSection));

    }

    private static String getConfString(String path, ConfigurationSection section, ConfigurationSection defSection){
        if (section != null) {
            String value = section.getString(path);
            if (value != null) return value;
        }
        return defSection.getString(path);
    }

    @Override
    public Map<String, Object> getNbtInnerMap(Object nbtTagCompound) {
        //noinspection unchecked
        return (Map<String, Object>) nmCompoundMapField.of(nbtTagCompound).get();
    }

    @Override
    public List<Object> getNbtInnerList(Object nbtTagList) {
        return (List<Object>) nmListListField.of(nbtTagList).get();
    }

    @Override
    public Object getBlockNBTTag(BlockState state) {
        if (!cBlockGetSnapshotMethod.getRefClass().isInstance(state)) return null;
        Object snapshot = cBlockGetSnapshotMethod.of(state).call();
        return nBlockGetNBTMethod.of(snapshot).call();
    }

    @Override
    public Object getEntityNBTTag(Entity entity) {
        Object nmEntity = cEntityGetHandleMethod.of(entity).call();
        Object tag = nmCompoundCon.create();
        boolean readCompleted = (boolean) cEntityGetNBTMethod.of(nmEntity).call(tag);
        if (!readCompleted) tag = cEntityGetNBTSimpleMethod.of(nmEntity).call(tag);
        return tag;
    }

    @Override
    public Object getItemStackNBTTag(ItemStack itemStack) {
        Object cItemStack = cItemHandleField.getRefClass().isInstance(itemStack) ? itemStack : cItemCraftCopyMethod.call(itemStack);
        Object nItemStack = cItemHandleField.of(cItemStack).get();
        Object tag = nmCompoundCon.create();
        return nItemGetNBTMethod.of(nItemStack).call(tag);
    }

    @Override
    public void setBlockNBTTag(BlockState state, Object tag) {
        if (!cBlockGetSnapshotMethod.getRefClass().isInstance(state)) return;
        Object snapshot = cBlockGetSnapshotMethod.of(state).call();
        nBlockSetNBTMethod.of(snapshot).call(tag);
    }

    @Override
    public void setEntityNBTTag(Entity entity, @NotNull Object tag) {
        Object nmEntity = cEntityGetHandleMethod.of(entity).call();
        cEntitySetNBTMethod.of(nmEntity).call(tag);
    }

    @Override
    public void setItemStackNBTTag(ItemStack itemStack, Object tag) {
        if (cItemHandleField.getRealField().getDeclaringClass().isInstance(itemStack)) {
            this.setItemStackNBT0(itemStack, tag);
        } else {
            var copy = asCraftCopyItemStack(itemStack);
            setItemStackNBT0(copy, tag);
            itemStack.setItemMeta( copy.getItemMeta() );
        }
    }

    private void setItemStackNBT0(Object cItemStack, Object tag) {
        Object nItemStack = cItemHandleField.of(cItemStack).get();
        nItemSetNBTMethod.of(nItemStack).call(tag);
    }

    @Override
    ItemStack asCraftCopyItemStack(ItemStack itemStack) {
        return (ItemStack) cItemCraftCopyMethod.call(itemStack);
    }

    @Override
    public @Nullable Object readNBTData(@NotNull DataInput dataInput) throws IOException {
        byte type = dataInput.readByte();
        if (type == 0) return null;
        dataInput.skipBytes(dataInput.readUnsignedShort());
        var nbtTagType = nmNBTTagGetTypeMethod.call(type);
        return nmNBTTagParseTypeMethod.of(nbtTagType).call(dataInput, (int)type, readLimiter);
    }

    @Override
    public Object getTagValueByPrimitive(Object javaValue){
        if (javaValue == null) return nbtTagEndCon.create();
        if (javaValue instanceof Byte) return nbtTagByteCon.create(javaValue);
        if (javaValue instanceof Short) return nbtTagShortCon.create(javaValue);
        if (javaValue instanceof Integer) return nbtTagIntCon.create(javaValue);
        if (javaValue instanceof Long) return nbtTagLongCon.create(javaValue);
        if (javaValue instanceof Float) return nbtTagFloatCon.create(javaValue);
        if (javaValue instanceof Double) return nbtTagDoubleCon.create(javaValue);
        if (javaValue instanceof String) return nbtTagStringCon.create(javaValue);
        if (javaValue instanceof byte[]) return nbtTagByteArrayCon.create(javaValue);
        if (javaValue instanceof int[]) return nbtTagIntArrayCon.create(javaValue);
        if (javaValue instanceof long[]) return nbtTagLongArrayCon.create(javaValue);
        throw new RuntimeException("can not convert value to nbt tag");
    }

    public byte getTagType(Object tag){
        return (byte) nbtBaseGetTypeMethod.of(tag).call();
    }

    public Object cloneTag(Object tag){
        return nbtBaseCloneMethod.of(tag).call();
    }

    public void writeNBTData(DataOutput dataInput, Object tag) throws IOException {
        byte type = getTagType(tag);
        dataInput.writeByte(type);
        if (type == 0) return;
        dataInput.writeUTF("");
        nbtBaseWriteDataMethod.of(tag).call(dataInput);
    }

    @Override
    public Entity spawnEntity(Object tag, World world) {
        var entityTag = cloneTag(tag);
        Object nWorldServer = cWorldGetHandleMethod.of(world).call();
        Optional<?> optNmsEntity = (Optional<?>) cEntityTypesCreateMethod.call(entityTag,nWorldServer);
        Object nmsEntity = optNmsEntity.orElse(null);
        if (nmsEntity == null) return null;
        cWorldAddEntityMethod.of(nWorldServer).call(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (Entity) nEntityGetBukkitMethod.of(nmsEntity).call();
    }

    @Override
    public Object getPrimitiveValue(Object tag){
        if (nmNBTTagStringClazz.isInstance(tag)) return nmNBTTagStringValueField.of(tag).get();
        if (nbtNumberClazz.isInstance(tag)) return nmNBTNumberValueMethod.of(tag).call();
        if (nbtTagByteArrayClazz.isInstance(tag)) return nmNBTTagByteArrayValueField.of(tag).get();
        if (nbtTagIntArrayClazz.isInstance(tag)) return nmNBTTagIntArrayValueField.of(tag).get();
        if (nbtTagLongArrayClazz.isInstance(tag)) return nmNBTTagLongArrayValueField.of(tag).get();
        throw new RuntimeException("unknown tag");
    }

    @Override
    Object createNBTTagCompound() {
        return nbtTagCompoundCon.create();
    }

    @Override
    Object createNBTTagList() {
        return nbtTagListCon.create();
    }

    @Override
    byte getNBTTagListType(Object tagList) {
        return nmListTypeField.of(tagList).get();
    }

    @Override
    void setNBTTagListType(Object tagList, byte type) {
        nmListTypeField.of(tagList).set(type);
    }
}
