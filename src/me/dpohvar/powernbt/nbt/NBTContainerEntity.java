package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.utils.StaticValues;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerEntity extends NBTContainer {
    private static final Class classNBTTagCompound = StaticValues.getClass("NBTTagCompound");
    private static final Class classEntityList = StaticValues.getClass("EntityList");
    private static final Class classEntityTypes = StaticValues.getClass("EntityTypes");
    private static final Class classWorld = StaticValues.getClass("World");
    private static final Class classCraftWorld = StaticValues.getClass("CraftWorld");
    private static final Class classEntity = StaticValues.getClass("Entity");
    private static final Class classCraftEntity = StaticValues.getClass("CraftEntity");
    private static final Class classEntityPlayer = StaticValues.getClass("EntityPlayer");
    private static final Class classEntityPlayerMP = StaticValues.getClass("EntityPlayerMP");

    Entity entity;
    static Method method_createEntityFromNBT;
    static Method method_getHandleEntity;
    static Method method_getHandleWorld;
    static Method method_setPassengerOf;
    static Method method_WriteEntity;
    static ArrayList<Method> method_ReadPlayerList = new ArrayList<Method>();
    static Method method_WritePlayer;
    static Method method_ReadEntity;
    static Method method_ReadPlayer;
    static {
        try{
            method_setPassengerOf = StaticValues.getMethodByTypeTypes(classEntity,void.class,classEntity);
            method_getHandleEntity = StaticValues.getMethodByTypeTypes(classCraftEntity,classEntity);
            method_getHandleWorld = StaticValues.getMethodByTypeTypes(classCraftWorld,classWorld);

            if(StaticValues.isMCPC){
                method_createEntityFromNBT = StaticValues.getMethodByTypeTypes(
                        classEntityList,
                        classEntity,
                        classNBTTagCompound,
                        classWorld);
                method_setPassengerOf = classEntity.getDeclaredMethod("setPassengerOf",classEntity);
                for(Method m:classEntity.getDeclaredMethods()){
                    if (m.getParameterTypes().length!=1) continue;
                    if (!m.getParameterTypes()[0].equals(classNBTTagCompound)) continue;
                    if (m.getName().endsWith("c")) method_ReadEntity = m;
                    if (m.getName().endsWith("e")) method_WriteEntity = m;
                    if (m.getName().endsWith("d")) method_ReadPlayer = m;
                    method_ReadPlayerList.add(m);
                }
                method_ReadPlayerList.add(null);
                for(Method m:classEntityPlayerMP.getDeclaredMethods()){
                    if (m.getParameterTypes().length!=1) continue;
                    if (!m.getParameterTypes()[0].equals(classNBTTagCompound)) continue;
                    //if (m.getName().endsWith("b")) method_ReadPlayer = m;
                    if (m.getName().endsWith("a")) method_WritePlayer = m;
                    method_ReadPlayerList.add(m);
                }
            } else {
                for(Method m:classEntity.getDeclaredMethods()){
                    if (m.getParameterTypes().length!=1) continue;
                    if (!m.getParameterTypes()[0].equals(classNBTTagCompound)) continue;
                    if (m.getName().endsWith("c")) method_ReadEntity = m;
                    if (m.getName().endsWith("f")) method_WriteEntity = m;
                    if (m.getName().endsWith("e")) method_ReadPlayer = m;
                    method_ReadPlayerList.add(m);
                }
                method_createEntityFromNBT = classEntityTypes.getDeclaredMethod("a",classNBTTagCompound,classWorld);
            }

        } catch (Exception e) {
            if(PowerNBT.plugin.isDebug()) e.printStackTrace();
        }
    }
    public NBTContainerEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getObject() {
        return entity;
    }

    @Override
    public List<String> getTypes() {
        List<String> s = new ArrayList<String>();
        s.add("entity");
        if (entity instanceof LivingEntity) s.add("living");
        s.add(entity.getType().getName());
        return s;
    }

    @Override
    public NBTTagCompound getTag() {
        NBTTagCompound base = new NBTTagCompound();
        try{
            Object liv = method_getHandleEntity.invoke(entity);

            if (classEntityPlayer.isInstance(liv)){
                method_ReadPlayer.invoke(liv,base.getHandle());
            } else {
                method_ReadEntity.invoke(liv,base.getHandle());
                //callMethod(liv, "c", oneNBTTagCompound, base.getHandle());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return base;
    }

    @Override
    public NBTTagCompound getCustomTag() {
        NBTTagCompound tag = getTag();
        if (tag!=null) return tag;
        return tag;
    }

    @Override
    public void setTag(NBTBase base) {
        try {
            Object liv = method_getHandleEntity.invoke(entity);
            //callMethod(entity, "getHandle", noInput);
            if(StaticValues.isMCPC && classEntityPlayerMP.isInstance(liv)){
                method_WritePlayer.invoke(liv, base.getHandle());
            } else {
                method_WriteEntity.invoke(liv, base.clone().getHandle());
            }
            //callMethod(liv, "f", oneNBTTagCompound, base.getHandle());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setCustomTag(NBTBase base){
        if (!(base instanceof NBTTagCompound)) return;
        NBTTagCompound tag = (NBTTagCompound)base.clone();
        if(plugin.getConfig().getBoolean("tags.entity_ignore_Pos")) tag.remove("Pos");
        if(plugin.getConfig().getBoolean("tags.entity_ignore_Rotation")) tag.remove("Rotation");
        if(plugin.getConfig().getBoolean("tags.entity_ignore_Motion")) tag.remove("Motion");
        if(plugin.getConfig().getBoolean("tags.entity_ignore_UUID")) {
            tag.remove("UUIDMost");
            tag.remove("UUIDWorldMost");
            tag.remove("UUIDLeast");
            tag.remove("UUIDWorldLeast");
        }
        if(plugin.getConfig().getBoolean("tags.entity_set_Riding")) try {
            spawnEntity(tag,entity.getWorld(),entity);
        } catch (Exception e){
            if(plugin.isDebug()) e.printStackTrace();
        }
        setTag(tag);
    }

    @Override
    public String getName() {
        return entity.getType().getName() + " (id" + entity.getEntityId() + ")";
    }

    public static Object spawnEntity(NBTTagCompound compound,World world,Object ridable){
        //callMethod(world, "getHandle", new Class[0]);
        try {
            Object cworld = method_getHandleWorld.invoke(world);
            Object entity;
            entity = method_createEntityFromNBT.invoke(null,compound.getHandle(),cworld);
            if (ridable instanceof Entity) {
                ridable = method_getHandleEntity.invoke(ridable);
                //callMethod(ridable, "getHandle", noInput);
            }
            if (ridable != null) method_setPassengerOf.invoke(ridable,entity);
            NBTTagCompound compoundRiding = compound.getCompound("Riding");
            if(compoundRiding!=null) {
                spawnEntity(compoundRiding,world,entity);
            }
            return entity;
        } catch (Exception e) {
            if(PowerNBT.plugin.isDebug()) e.printStackTrace();
        }
        return null;

    }

}
