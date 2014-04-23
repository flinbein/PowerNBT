package me.dpohvar.powernbt.utils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

public class EntityUtils {

    public static EntityUtils entityUtils = new EntityUtils();

    RefClass classEntity = getRefClass("{nms}.Entity, {nm}.entity.Entity, {Entity}");
    RefClass classCraftEntity = getRefClass("{cb}.entity.CraftEntity, {CraftEntity}");
    RefClass classEntityPlayer = getRefClass("{nms}.EntityPlayer, {nm}.entity.player.EntityPlayer, {EntityPlayer}");
    RefClass classNBTTagCompound = getRefClass("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}");
    RefMethod getHandleEntity = classCraftEntity.findMethodByReturnType(classEntity);
    RefMethod readEntity;
    RefMethod writeEntity;
    RefMethod readPlayer;
    RefMethod writePlayer;
    RefField forgeData;

    private EntityUtils(){
        if (isForge()) {
            try {
                forgeData = classEntity.findField(classNBTTagCompound);
            } catch (Exception ignored){}
            try { // forge 1.6+
                writeEntity = writePlayer = classEntity.findMethod(
                        new MethodCondition().withTypes(classNBTTagCompound).withSuffix("e")
                );
                readEntity = classEntity.findMethod(
                        new MethodCondition().withTypes(classNBTTagCompound).withSuffix("c").withIndex(0)
                );
                readPlayer = classEntity.findMethod(
                        new MethodCondition().withTypes(classNBTTagCompound).withSuffix("d")
                );
            } catch (Exception ignored) { // old forge
                //todo: check for old forge
                ignored.printStackTrace();
            }
        } else {
            try { // bukkit 1.6+
                writeEntity = writePlayer = classEntity.findMethod(
                        new MethodCondition().withTypes(classNBTTagCompound).withName("f")
                );
                readEntity = classEntity.findMethod(
                        new MethodCondition().withTypes(classNBTTagCompound).withName("c")
                );
                readPlayer = classEntity.findMethod(
                        new MethodCondition().withTypes(classNBTTagCompound).withName("e")
                );
            } catch (Exception ignored) { // old bukkit
                writeEntity = writePlayer = classEntity.findMethod(
                        new MethodCondition().withTypes(classNBTTagCompound).withName("e")
                );
                readEntity = classEntity.findMethod(
                        new MethodCondition().withTypes(classNBTTagCompound).withName("c")
                );
                readPlayer = classEntity.findMethod(
                        new MethodCondition().withTypes(classNBTTagCompound).withName("d")
                );
            }
        }
    }

    public Object getHandleEntity(Entity entity){
        return getHandleEntity.of(entity).call();
    }

    public void readEntity(Entity entity, Object nbtTagCompound){
        Object nmsEntity = getHandleEntity(entity);
        if (classEntityPlayer.isInstance(nmsEntity)) {
            readPlayer.of(nmsEntity).call(nbtTagCompound);
        } else {
            readEntity.of(nmsEntity).call(nbtTagCompound);
        }
    }

    public void writeEntity(Entity entity, Object nbtTagCompound){
        Object liv = getHandleEntity(entity);
        if(entity.getType() == EntityType.PLAYER){
            writePlayer.of(liv).call(nbtTagCompound);
        }else{
            writeEntity.of(liv).call(nbtTagCompound);
        }
    }

    public Object getForgeData(Entity entity){
        if (forgeData == null) return null;
        Object nmsEntity = getHandleEntity(entity);
        return forgeData.of(nmsEntity).get();
    }

    public void setForgeData(Entity entity, Object nbtTagCompound){
        if (forgeData == null) return;
        Object nmsEntity = getHandleEntity(entity);
        if (nbtTagCompound!=null) NBTUtils.nbtUtils.cloneTag(nbtTagCompound);
        forgeData.of(nmsEntity).set(nbtTagCompound);
    }
}
