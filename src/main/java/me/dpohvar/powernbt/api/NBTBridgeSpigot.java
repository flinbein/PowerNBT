package me.dpohvar.powernbt.api;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.utils.ReflectionUtils.*;
import static me.dpohvar.powernbt.utils.ReflectionUtils.getRefClass;
import net.minecraft.world.entity.Entity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Chicken;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import static java.util.Comparator.comparingInt;

public class NBTBridgeSpigot implements NBTBridge {

    private final JavaPlugin plugin;

    // NBT:
    private final RefClass<?> nmCompoundClazz = getRefClass("net.minecraft.nbt.NBTTagCompound");
    private final RefClass<?> nmListClazz = getRefClass("net.minecraft.nbt.NBTTagList");
    private final RefField<?> nmCompoundMapField = nmCompoundClazz.findField(Map.class);
    private final RefConstructor<?> nmCompoundCon = nmCompoundClazz.getConstructor();
    private final RefConstructor<?> nmCompoundConMap = nmCompoundClazz.getConstructor(Map.class);
    private final RefField<?> nmListListField = nmListClazz.findField(List.class);

    // Entity:
    private final RefClass<?> nmEntityClazz = getRefClass("net.minecraft.world.entity.Entity");
    private final RefClass<?> cPlayerClazz = getRefClass("{cb}.entity.CraftPlayer");
    private final RefClass<?> cEntityClazz = getRefClass("{cb}.entity.CraftEntity");
    private final RefMethod<?> cPlayerReadExtra = cPlayerClazz.findMethodByNameAndParams("readExtraData", nmCompoundClazz);
    private final RefMethod<?> cPlayerSetExtra = cPlayerClazz.findMethodByNameAndParams("setExtraData", nmCompoundClazz);
    private final RefMethod<?> cEntityGetHandleMethod = cEntityClazz.findMethod(new MethodCondition().withReturnType(nmEntityClazz).withTypes()); // getHandle()
    private RefMethod<?> cEntityGetNBTMethod; // NBTTagCompound getNBT(NBTTagCompound tag)
    private RefMethod<?> cEntitySetNBTMethod; // void setNBT(NBTTagCompound tag)

    public NBTBridgeSpigot(JavaPlugin plugin){
        this.plugin = plugin;
        String refVersion = plugin.getConfig().getString("reflections.bukkit_version");
        String pluginVersion = plugin.getConfig().getString("reflections.plugin_version");
        if (plugin.getServer().getBukkitVersion().equals(refVersion) && plugin.getDescription().getVersion().equals(pluginVersion)) {
            defineEntityMethods(plugin.getConfig());
            plugin.getConfig().set("reflections.bukkit_version", plugin.getServer().getBukkitVersion());
            plugin.getConfig().set("reflections.plugin_version", plugin.getDescription().getVersion());
        } else {
            readEntityMethods(plugin.getConfig());
        }

    }

    @Override
    public Map<String, ?> getNbtMap(Object nbtTagCompound) {
        //noinspection unchecked
        return (Map<String, ?>) nmCompoundMapField.of(nbtTagCompound).get();
    }

    @Override
    public List<?> getNbtList(Object nbtTagList) {
        return (List<?>) nmListListField.of(nbtTagList).get();
    }

    private void readEntityMethods(FileConfiguration config){
        cEntityGetNBTMethod = RefMethod.parse(config.getString("reflections.entity.getNBT", ""));
        cEntitySetNBTMethod = RefMethod.parse(config.getString("reflections.entity.setNBT", ""));
    }

    private void defineEntityMethods(FileConfiguration config){
        // create world
        WorldCreator wc = new WorldCreator("PowerNBT_dummy_world");
        wc.environment(World.Environment.CUSTOM);
        wc.type(WorldType.FLAT);
        World world = wc.createWorld();

        world.spawn(world.getSpawnLocation(), Chicken.class, chicken -> {
            var nmChicken = cEntityGetHandleMethod.of(chicken).call();
            RefMethod<?>[] likeGetMethods = nmEntityClazz.findMethodsByNameTypeAndParams(null, nmCompoundClazz, nmCompoundClazz);
            // find the method to read NBT: NBTTagCompound xxx(NBTTagCompound rag)
            var methodRecord = Arrays.stream(likeGetMethods)
                    .map(likeMethod -> {
                        var map = new CountHashMap();
                        var tag = nmCompoundConMap.create(map);
                        likeMethod.of(nmChicken).call(tag);
                        return new ArgType(likeMethod, map);
                    })
                    .max(comparingInt(a -> a.map.size()))
                    .orElseThrow(() -> new RuntimeException("no ENTITY NBT READ method"));
            cEntityGetNBTMethod = methodRecord.method;
            var bestMap = methodRecord.map;

            // find the method to write NBT: void xxx(NBTTagCompound rag)
            RefMethod<?>[] likeSetMethods = nmEntityClazz.findMethodsByNameTypeAndParams(null, void.class, nmCompoundClazz);
            cEntitySetNBTMethod = Arrays.stream(likeSetMethods)
                    .map(likeMethod -> {
                        var counterMap = new CountHashMap(bestMap);
                        var tag = nmCompoundConMap.create(counterMap);
                        likeMethod.of(nmChicken).call(tag);
                        return new ArgType(likeMethod, counterMap);
                    })
                    .max(comparingInt(a -> a.map.getAccessCount()))
                    .orElseThrow(() -> new RuntimeException("no ENTITY NBT WRITE method"))
                    .method;
            // remove chicken entity on end
        }).remove();

        Bukkit.getServer().unloadWorld(world, false);
    }

    private static class CountHashMap extends HashMap<String, Object> {
        private final Set<Object> accessKeys = new HashSet<>();

        CountHashMap(){ super(); }
        CountHashMap(Map<String, Object> map){ super(map); }

        @Override public Object get(Object key) {
            accessKeys.add(key);
            return super.get(key);
        }

        public int getAccessCount(){
            return accessKeys.size();
        }
    }

    private record ArgType(RefMethod<?> method, CountHashMap map){}

    @Override
    public NBTCompound getBlockNBT(BlockState state) {
        return null;
    }

    @Override
    public NBTCompound getEntityNBT(Entity entity) {
        Object nmEntity = cEntityGetHandleMethod.of(entity).call();
        Object tag = nmCompoundCon.create();
        Object result = cEntityGetNBTMethod.of(nmEntity).call(tag);
        if (cPlayerClazz.isInstance(entity)) cPlayerSetExtra.of(entity).call(result);
        return new NBTCompound(result);
    }

    @Override
    public NBTCompound getItemStackNBT(ItemStack itemStack) {
        return null;
    }

    @Override
    public NBTCompound getChunkNBT(Chunk chunk) {
        return null;
    }

    @Override
    public void setBlockNBT(BlockState state, NBTCompound compound) {

    }

    @Override
    public void setEntityNBT(Entity entity, @NotNull NBTCompound compound) {
        Object nmEntity = cEntityGetHandleMethod.of(entity).call();
        Object tag = compound.getHandle();
        cEntitySetNBTMethod.of(nmEntity).call(tag);
        if (cPlayerClazz.isInstance(entity)) cPlayerReadExtra.of(entity).call(tag);
    }

    @Override
    public void setItemStackNBT(ItemStack itemStack, NBTCompound compound) {

    }

    @Override
    public void setChunkNBT(Chunk chunk, NBTCompound compound) {

    }

    @Override
    public void readNBT(InputStream inputStream) throws IOException {

    }

    @Override
    public void writeNBT(OutputStream outputStream, Object value) throws IOException {

    }

    @Override
    public void readCompressedNBT(InputStream inputStream) throws IOException {

    }

    @Override
    public void writeCompressedNBT(OutputStream outputStream, Object value) throws IOException {

    }

    @Override
    public Object readNBT(DataInput dataInput, Object value) throws IOException {
        return null;
    }

    @Override
    public Object writeNBT(DataOutput dataInput, Object value) throws IOException {
        return null;
    }
}
