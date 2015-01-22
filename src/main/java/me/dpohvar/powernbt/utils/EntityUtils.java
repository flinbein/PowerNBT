package me.dpohvar.powernbt.utils;

import me.dpohvar.powernbt.nbt.NBTTagByte;
import me.dpohvar.powernbt.nbt.NBTType;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static me.dpohvar.powernbt.PowerNBT.*;
import static me.dpohvar.powernbt.utils.NBTUtils.*;
import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

public class EntityUtils {

    public static EntityUtils entityUtils = new EntityUtils();

    RefClass cEntity = getRefClass("{nms}.Entity, {nm}.entity.Entity, {Entity}");
    RefClass cCraftWorld = getRefClass("{cb}.CraftWorld, {CraftWorld}");
    RefClass cWorldServer = getRefClass("{nms}.WorldServer, {WorldServer}");
    RefClass cCraftEntity = getRefClass("{cb}.entity.CraftEntity, {CraftEntity}");
    RefClass cWorld = getRefClass("{nms}.World, {World}");
    RefClass cEntityPlayer = getRefClass("{nms}.EntityPlayer, {nm}.entity.player.EntityPlayer, {EntityPlayer}");
    RefClass cNBTTagCompound = getRefClass("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}");
    RefClass cCraftChunk = getRefClass("{cb}.CraftChunk, {CraftChunk}");
    RefClass cChunk = getRefClass("{nms}.Chunk, {Chunk}");
    RefMethod mGetHandleEntity = cCraftEntity.findMethodByReturnType(cEntity);
    RefMethod mReadEntity;
    RefMethod mWriteEntity;
    RefMethod mReadPlayer;
    RefMethod mWritePlayer;
    RefField fForgeData;

    RefClass cEntityTypes;
    RefClass cEntityInsentient;
    RefMethod mCreateEntity;
    RefMethod mGetWorldHandle;
    RefMethod mGetBukkitEntity;
    RefMethod mGetChunkHandle;
    RefMethod mAddEntityToChunk;
    RefMethod mAddEntityToWorld;

    private EntityUtils(){

        try {
            cEntityTypes = getRefClass("{nms}.EntityTypes, {nm}.entity.EntityTypes, {EntityTypes}");
            cEntityInsentient = getRefClass("{nms}.EntityInsentient, {EntityInsentient}");
            mCreateEntity = cEntityTypes.findMethodByParams(cNBTTagCompound, cWorld);
            mGetWorldHandle = cCraftWorld.findMethodByReturnType(cWorldServer);
            mGetBukkitEntity = cEntity.findMethodByReturnType(cCraftEntity);
            mGetChunkHandle = cCraftChunk.findMethodByReturnType(cChunk);
            mAddEntityToChunk = cChunk.findMethod(
                    new MethodCondition()
                            .withReturnType(void.class)
                            .withTypes(cEntity)
                            .withPrefix("a")
            );
            mAddEntityToWorld = cWorld.findMethodByName("addEntity");
        } catch (Exception e){
            if (plugin.isDebug()) {
                plugin.getLogger().log(Level.WARNING, "entity utils error", e);
            }
        }

        if (isForge()) {
            try {
                fForgeData = cEntity.findField(cNBTTagCompound);
            } catch (Exception ignored){}
            try { // forge 1.6+
                mWriteEntity = mWritePlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withSuffix("e")
                );
                mReadEntity = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withSuffix("c").withIndex(0)
                );
                mReadPlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withSuffix("d")
                );
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "Unknown version of forge", e);
                throw new RuntimeException(e);
            }
        } else {
            try { // bukkit 1.6+
                mWriteEntity = mWritePlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("f")
                );
                mReadEntity = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("c")
                );
                mReadPlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("e")
                );
            } catch (Exception ignored) { // old bukkit
                mWriteEntity = mWritePlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("e")
                );
                mReadEntity = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("c")
                );
                mReadPlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("d")
                );
            }
        }
    }

    public Object getHandleEntity(Entity entity){
        return mGetHandleEntity.of(entity).call();
    }

    public void readEntity(Entity entity, Object nbtTagCompound){
        Object nmsEntity = getHandleEntity(entity);
        if (cEntityPlayer.isInstance(nmsEntity)) {
            mReadPlayer.of(nmsEntity).call(nbtTagCompound);
        } else {
            mReadEntity.of(nmsEntity).call(nbtTagCompound);
        }
    }

    public void writeEntity(Entity entity, Object nbtTagCompound){
        Object liv = getHandleEntity(entity);
        if(entity.getType() == EntityType.PLAYER){
            mWritePlayer.of(liv).call(nbtTagCompound);
        }else{
            mWriteEntity.of(liv).call(nbtTagCompound);
        }
    }

    public Object getForgeData(Entity entity){
        if (fForgeData == null) return null;
        Object nmsEntity = getHandleEntity(entity);
        return fForgeData.of(nmsEntity).get();
    }

    public void setForgeData(Entity entity, Object nbtTagCompound){
        if (fForgeData == null) return;
        Object nmsEntity = getHandleEntity(entity);
        if (nbtTagCompound!=null) nbtUtils.cloneTag(nbtTagCompound);
        fForgeData.of(nmsEntity).set(nbtTagCompound);
    }

    public Entity spawnEntity(Object nbtTagCompound, World world){
        nbtTagCompound = nbtUtils.cloneTag(nbtTagCompound);
        Object nmsWorld = mGetWorldHandle.of(world).call();
        Object nmsEntity = mCreateEntity.call(nbtTagCompound,nmsWorld);
        if (nmsEntity == null) return null;
        mAddEntityToWorld.of(nmsWorld).call(nmsEntity);
        Entity entity = (Entity) mGetBukkitEntity.of(nmsEntity).call();

        Location loc = entity.getLocation();
        Object tagPos = nbtUtils.createTagList();
        nbtUtils.setNBTTagListType(tagPos, (byte)6);
        List<Object> handleList = nbtUtils.getHandleList(tagPos);
        handleList.add(nbtUtils.createTagDouble(loc.getX()));
        handleList.add(nbtUtils.createTagDouble(loc.getY()));
        handleList.add(nbtUtils.createTagDouble(loc.getZ()));

        Entity currentEntity = entity;
        while(true) {
            Map<String,Object> handleMap = nbtUtils.getHandleMap(nbtTagCompound);
            nbtTagCompound = handleMap.get("Riding");
            if (nbtTagCompound == null) break;
            Map<String,Object> ridingMap = nbtUtils.getHandleMap(nbtTagCompound);
            ridingMap.put("Pos", nbtUtils.cloneTag(tagPos));
            Object nmsRiding = mCreateEntity.call(nbtTagCompound,nmsWorld);
            if (nmsRiding == null) break;
            mAddEntityToWorld.of(nmsWorld).call(nmsRiding);
            Entity riding = (Entity) mGetBukkitEntity.of(nmsRiding).call();
            riding.setPassenger(currentEntity);
            currentEntity = riding;
        }
        return entity;
    }
}
