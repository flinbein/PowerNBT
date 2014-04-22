package me.dpohvar.powernbt.utils;

import org.bukkit.entity.Entity;

import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

public class EntityUtils {

    public static EntityUtils entityUtils = new EntityUtils();

    private RefClass classEntity = getRefClass("{Entity}, {nms}.Entity, {nm}.entity.Entity");
    private RefClass classCraftEntity = getRefClass("{CraftEntity}, {cb}.entity.CraftEntity");
    RefClass classEntityPlayer = getRefClass("{EntityPlayer}, {nms}.EntityPlayer, {nm}.entity.player.EntityPlayer");
    RefClass classEntityPlayerMP = getRefClass("{EntityPlayerMP}, {nm}.entity.player.EntityPlayerMP, null");
    RefMethod getHandleEntity = classCraftEntity.findMethodByReturnType(classEntity);
    RefMethod readEntity;
    RefMethod writeEntity;
    RefMethod readPlayer;
    RefMethod writePlayer;

    private EntityUtils(){
        RefClass classNBTTagCompound = getRefClass("{NBTTagCompound}, {nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound");
        if (isForge()) {
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
        if(isForge() && classEntityPlayerMP.isInstance(liv)){
            writePlayer.of(liv).call(nbtTagCompound);
        }else{
            writeEntity.of(liv).call(nbtTagCompound);
        }
    }
}
