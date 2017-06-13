package me.dpohvar.powernbt.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static me.dpohvar.powernbt.PowerNBT.*;
import static me.dpohvar.powernbt.utils.NBTUtils.*;
import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

public class EntityUtils {

    public static EntityUtils entityUtils = new EntityUtils();

    private RefClass cEntity = getRefClass("{nms}.Entity, {nm}.entity.Entity, {Entity}");
    private RefClass cCraftEntity = getRefClass("{cb}.entity.CraftEntity, {CraftEntity}");
    private RefClass cEntityPlayer = getRefClass("{nms}.EntityPlayer, {nm}.entity.player.EntityPlayer, {EntityPlayer}");
    private RefMethod mGetHandleEntity = cCraftEntity.findMethodByReturnType(cEntity);
    private RefMethod mReadEntityToNBT;
    private RefMethod mWriteNBTToEntity;
    private RefMethod mReadPlayerToNBT;
    private RefMethod mWriteNBTToPlayer;
    private RefField fForgeData;

    private RefMethod mCreateEntity;
    private RefMethod mGetWorldHandle;
    private RefMethod mGetBukkitEntity;
    private RefMethod mAddEntityToWorld;

    private EntityUtils(){
        RefClass cCraftWorld = getRefClass("{cb}.CraftWorld, {CraftWorld}");
        RefClass cWorldServer = getRefClass("{nms}.WorldServer, {nm}.world.WorldServer, {WorldServer}");
        RefClass cWorld = getRefClass("{nms}.World, {nm}.world.World, {World}");
        RefClass cNBTTagCompound = getRefClass("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}");

        try {
            RefClass cEntityTypes = getRefClass("{nms}.EntityTypes, {nm}.entity.EntityTypes, {nm}.entity.EntityList, {EntityTypes}");
            mCreateEntity = cEntityTypes.findMethodByParams(cNBTTagCompound, cWorld);
            mGetWorldHandle = cCraftWorld.findMethodByReturnType(cWorldServer);
            mGetBukkitEntity = cEntity.findMethodByReturnType(cCraftEntity);
            mAddEntityToWorld = cWorld.findMethod(
                    new MethodCondition()
                            .withReturnType(boolean.class)
                            .withName("addEntity")
                            .withTypes(cEntity, CreatureSpawnEvent.SpawnReason.class)
            );
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
                mWriteNBTToEntity = mWriteNBTToPlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withSuffix("e")
                );
                mReadEntityToNBT = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withSuffix("c").withIndex(0)
                );
                mReadPlayerToNBT = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withSuffix("d")
                );
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "Unknown version of forge", e);
                throw new RuntimeException(e);
            }
        } else {
            try { // bukkit 1.6+
                mWriteNBTToEntity = mWriteNBTToPlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("f")
                );
                mReadEntityToNBT = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("save"), // spigot 1.12
                        new MethodCondition().withTypes(cNBTTagCompound).withName("c")
                );
                mReadPlayerToNBT = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("save"), // spigot 1.12
                        new MethodCondition().withTypes(cNBTTagCompound).withName("e")
                );
            } catch (Exception ignored) { // old bukkit
                mWriteNBTToEntity = mWriteNBTToPlayer = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("e")
                );
                mReadEntityToNBT = cEntity.findMethod(
                        new MethodCondition().withTypes(cNBTTagCompound).withName("c")
                );
                mReadPlayerToNBT = cEntity.findMethod(
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
            mReadPlayerToNBT.of(nmsEntity).call(nbtTagCompound);
        } else {
            mReadEntityToNBT.of(nmsEntity).call(nbtTagCompound);
        }
    }

    public void writeEntity(Entity entity, Object nbtTagCompound){
        Object liv = getHandleEntity(entity);
        if(entity.getType() == EntityType.PLAYER){
            mWriteNBTToPlayer.of(liv).call(nbtTagCompound);
        }else{
            mWriteNBTToEntity.of(liv).call(nbtTagCompound);
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
        mAddEntityToWorld.of(nmsWorld).call(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
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
