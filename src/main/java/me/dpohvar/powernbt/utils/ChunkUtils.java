package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.PowerNBT;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;

import static me.dpohvar.powernbt.utils.NBTUtils.nbtUtils;
import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

public class ChunkUtils {

    public static ChunkUtils chunkUtils = new ChunkUtils();

    RefClass classCraftChunk = getRefClass("{cb}.CraftChunk, {CraftChunk}");
    RefClass classChunk = getRefClass("{nms}.Chunk, {Chunk}");
    RefClass classCraftWorld = getRefClass("{cb}.CraftWorld, {CraftWorld}");
    RefClass classWorld = getRefClass("{nms}.World, {World}");
    RefClass classWorldServer = getRefClass("{nms}.WorldServer, {WorldServer}");
    RefClass classNBTTagCompound = getRefClass("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}");
    RefClass classLongObjectHashMap = getRefClass("{cb}.util.LongObjectHashMap, {LongObjectHashMap}");
    RefClass iChunkProvider;
    RefClass cChunkProviderServer;
    RefClass iChunkLoader;
    RefClass cChunkRegionLoader;
    RefField fChunkProvider;
    RefField fChunkLoader;
    RefField fChunks;
    RefField fLongObjectHashMap;
    RefMethod mGetChunkHandle;
    RefMethod mGetWorldHandle;
    RefMethod mSaveChunk;
    RefMethod mLoadChunk;
    RefMethod mLoadEntities;
    RefMethod mPutToMap;
    RefMethod mAddEntities;
    RefMethod mLoadNearby;

    Map<Object,Object> chunkLoaderMap = new WeakHashMap<Object,Object>();


    private ChunkUtils(){
        try {
            mGetChunkHandle = classCraftChunk.findMethodByReturnType(classChunk);
            mGetWorldHandle = classCraftWorld.findMethodByReturnType(classWorldServer);
            iChunkProvider = getRefClass("{nms}.IChunkProvider, {IChunkProvider}");
            cChunkProviderServer = getRefClass("{nms}.ChunkProviderServer, {ChunkProviderServer}");
            iChunkLoader = getRefClass("{nms}.IChunkLoader, {IChunkLoader}");
            fChunkProvider = classWorldServer.findField(cChunkProviderServer);
            fChunkLoader = cChunkProviderServer.findField(iChunkLoader);
            cChunkRegionLoader = getRefClass("{nms}.ChunkRegionLoader, {ChunkRegionLoader}");
            mSaveChunk = cChunkRegionLoader.findMethodByParams(classChunk, classWorld, classNBTTagCompound);
            fLongObjectHashMap = cChunkProviderServer.findField(classLongObjectHashMap);
            fChunks = cChunkProviderServer.findField(classLongObjectHashMap);
            mLoadChunk = cChunkRegionLoader.findMethodByParams(classWorld, classNBTTagCompound);
            mLoadEntities = cChunkRegionLoader.findMethodByParams(classChunk, classNBTTagCompound, classWorld);
            mPutToMap = classLongObjectHashMap.findMethodByParams(long.class, Object.class);
            mAddEntities = classChunk.findMethodByName("addEntities, {Chunk:AddEntities}");
            mLoadNearby = classChunk.findMethodByParams(iChunkProvider, iChunkProvider,int.class,int.class);
        } catch (Exception e){
            if (PowerNBT.plugin.isDebug()){
                PowerNBT.plugin.getLogger().log(Level.WARNING, "Can't load ChunkUtils!", e);
            } else {
                PowerNBT.plugin.getLogger().log(Level.WARNING, "Can't load ChunkUtils!");
            }
        }
    }

    private Object getChunkLoader(Object nmsWorld){
        Object chunkLoader = chunkLoaderMap.get(nmsWorld);
        if (chunkLoader != null) return chunkLoader;
        Object chunkProvider = fChunkProvider.of(nmsWorld).get();
        chunkLoader = fChunkLoader.of(chunkProvider).get();
        chunkLoaderMap.put(nmsWorld, chunkLoader);
        return chunkLoader;
    }

    public static long toLong(int msw, int lsw) {
        return ((long)msw << 32) + (long)lsw - -2147483648L;
    }

    public void readChunk(Chunk chunk, Object nbtTagCompound){
        Object nmsWorld = mGetWorldHandle.of(chunk.getWorld()).call();
        Object chunkLoader = getChunkLoader(nmsWorld);
        Object nmsChunk = mGetChunkHandle.of(chunk).call();
        mSaveChunk.of(chunkLoader).call(nmsChunk, nmsWorld, nbtTagCompound);
    }

    public void writeChunk(Chunk chunk, Object nbtTagCompound){
        Object nmsWorld = mGetWorldHandle.of(chunk.getWorld()).call();
        Object chunkProvider = fChunkProvider.of(nmsWorld).get();
        Object chunkLoader = fChunkLoader.of(chunkProvider).get();
        int x = chunk.getX();
        int z = chunk.getZ();
        // remove entities
        for (Entity entity : chunk.getEntities()) {
            if ( !(entity instanceof Player) ) entity.remove();
        }
        // unload chunk
        chunk.unload();
        // read nbt tag
        nbtTagCompound = nbtUtils.cloneTag(nbtTagCompound);
        Map<String, Object> handleMap = nbtUtils.getHandleMap(nbtTagCompound);
        // fix x,z coordinates
        handleMap.put("xPos", nbtUtils.createTagInt(x) );
        handleMap.put("zPos", nbtUtils.createTagInt(z) );
        // fix entities coordinates
        fixEntitiesData( handleMap.get("Entities"), x, z );
        // fix tile entities coordinates
        fixTileEntitiesData( handleMap.get("TileEntities"), x, z );
        // create new chunk
        Object newChunk = mLoadChunk.of(chunkLoader).call(nmsWorld, nbtTagCompound);
        // load entities
        mLoadEntities.of(chunkLoader).call(newChunk, nbtTagCompound, nmsWorld);
        // add entities
        mAddEntities.of(newChunk).call();
        // load nearby chunks
        mLoadNearby.of(newChunk).call(chunkProvider, chunkProvider, x, z);
        // save chunk to provider map
        Object chunkMap = fChunks.of(chunkProvider).get();
        long hash = toLong(x, z);
        mPutToMap.of(chunkMap).call(hash, newChunk);
        // update chunk
        ChunkReloadTask task = new ChunkReloadTask(chunk);
        task.run();
        Bukkit.getScheduler().runTaskLater(PowerNBT.plugin, task, 2);
        // refresh blocks
        chunk.getWorld().refreshChunk(x, z);
    }

    private void fixEntitiesData(Object nbtList, int x, int z){
        if (nbtList==null) return;
        List<Object> list = nbtUtils.getHandleList(nbtList);
        for(Object nbtEntity: list){
            while (nbtEntity != null) {
                Map<String, Object> entityMap = nbtUtils.getHandleMap(nbtEntity);
                List<Object> posList = nbtUtils.getHandleList(entityMap.get("Pos"));
                double posX = (Double) nbtUtils.getValue( posList.get(0) );
                double posZ = (Double) nbtUtils.getValue( posList.get(2) );
                posList.set(0, nbtUtils.createTagDouble((x << 4) + (posX % 16)));
                posList.set(2, nbtUtils.createTagDouble((z << 4) + (posZ % 16)));
                nbtEntity = entityMap.get("Riding");
            }

        }
    }

    private void fixTileEntitiesData(Object nbtList, int x, int z){
        if (nbtList==null) return;
        List<Object> list = nbtUtils.getHandleList(nbtList);
        for(Object nbtEntity: list){
            Map<String, Object> entityMap = nbtUtils.getHandleMap(nbtEntity);
            int posX = (Integer) nbtUtils.getValue( entityMap.get("x") );
            int posZ = (Integer) nbtUtils.getValue( entityMap.get("z") );
            entityMap.put("x", nbtUtils.createTagInt((x << 4) | (posX & 0xf)));
            entityMap.put("z", nbtUtils.createTagInt((z << 4) | (posZ & 0xf)));
        }
    }

    class ChunkReloadTask implements Runnable{

        private Chunk chunk;

        public ChunkReloadTask(Chunk chunk){
            this.chunk = chunk;
        }

        @Override
        public void run() {
            chunk.unload();
            chunk.load();
        }
    }
}
