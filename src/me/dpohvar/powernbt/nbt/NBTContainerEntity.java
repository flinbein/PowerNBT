package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.PowerNBT;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import javax.persistence.EntityListeners;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;
import static me.dpohvar.powernbt.utils.StaticValues.*;
import static me.dpohvar.powernbt.utils.VersionFix.callMethod;

public class NBTContainerEntity extends NBTContainer {

    Entity entity;
    static Method createEntityFromNBT;
    static Method setPassengerOf;
    static {
        try{
            createEntityFromNBT = classEntityTypes.getDeclaredMethod("a",classNBTTagCompound,classWorld);
            setPassengerOf = classEntity.getDeclaredMethod("setPassengerOf",classEntity);
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
        Object liv = callMethod(entity, "getHandle", noInput);
        NBTTagCompound base = new NBTTagCompound();
        if (classEntityPlayer.isInstance(liv)){
            callMethod(liv, "e", oneNBTTagCompound, base.getHandle());
        } else {
            callMethod(liv, "c", oneNBTTagCompound, base.getHandle());
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
        Object liv = callMethod(entity, "getHandle", noInput);
        callMethod(liv, "f", oneNBTTagCompound, base.getHandle());
    }

    @Override
    public void setCustomTag(NBTBase base){
        if (!(base instanceof NBTTagCompound)) return;
        NBTTagCompound tag = ((NBTTagCompound) base).clone();
        if(plugin.getConfig().getBoolean("tags.entity_ignore_Pos")) tag.remove("Pos");
        if(plugin.getConfig().getBoolean("tags.entity_ignore_Rotation")) tag.remove("Rotation");
        if(plugin.getConfig().getBoolean("tags.entity_ignore_Motion")) tag.remove("Motion");
        if(plugin.getConfig().getBoolean("tags.entity_ignore_UUID")) {
            tag.remove("UUIDMost");
            tag.remove("UUIDLeast");
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
        Object cworld = callMethod(world, "getHandle", new Class[0]);
        Object entity = null;
        try {
            entity = createEntityFromNBT.invoke(null,compound.getHandle(),cworld);
            if (ridable instanceof Entity) {
                ridable = callMethod(ridable, "getHandle", noInput);
            }
            if (ridable != null) setPassengerOf.invoke(ridable,entity);
            NBTTagCompound compoundRiding = compound.getCompound("Riding");
            if(compoundRiding!=null) {
                spawnEntity(compoundRiding,world,entity);
            }
        } catch (Exception e) {
            if(PowerNBT.plugin.isDebug()) e.printStackTrace();
        }
        return entity;

    }

}
