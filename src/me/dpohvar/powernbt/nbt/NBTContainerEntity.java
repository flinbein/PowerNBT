package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.PowerNBT;
import me.dpohvar.powernbt.utils.Reflections;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerEntity extends NBTContainer<Entity> {
    private static final Class classNBTTagCompound = Reflections.getClass("{nms}.NBTTagCompound","net.minecraft.nbt,NBTTagCompound");
    private static final Class classEntityList = Reflections.getClass(null,"net.minecraft.entity.EntityList");
    private static final Class classEntityTypes = Reflections.getClass("{nms}.EntityTypes",null);
    private static final Class classWorld = Reflections.getClass("{nms}.World","net.minecraft.world.World");
    private static final Class classCraftWorld = Reflections.getClass("{cb}.CraftWorld");
    private static final Class classEntity = Reflections.getClass("{nms}.Entity","net.minecraft.entity.Entity");
    private static final Class classCraftEntity = Reflections.getClass("{cb}.entity.CraftEntity");
    private static final Class class_EntityPlayer = Reflections.getClass("{nms}.EntityPlayer","net.minecraft.entity.player.EntityPlayer");
    private static final Class classEntityPlayerMP = Reflections.getClass(null,"net.minecraft.entity.player.EntityPlayerMP");

    Entity entity;
    static Method method_createEntityFromNBT;
    static Method method_getHandleEntity = Reflections.getMethodByTypes(classCraftEntity, classEntity);
    static Method method_WriteEntity;
    static Method method_WritePlayer;
    static Method method_ReadEntity;
    static Method method_ReadPlayer;
    static {
        try{
            if(Reflections.isForge()){
                method_createEntityFromNBT = Reflections.getMethodByTypes(
                        classEntityList,
                        classEntity,
                        classNBTTagCompound,
                        classWorld);
                for(Method m:classEntity.getDeclaredMethods()){
                    if (m.getParameterTypes().length!=1) continue;
                    if (!m.getParameterTypes()[0].equals(classNBTTagCompound)) continue;
                    if (m.getName().endsWith("c")) method_ReadEntity = m;
                    if (m.getName().endsWith("e")) method_WriteEntity = m;
                    if (m.getName().endsWith("d")) method_ReadPlayer = m;
                }
                for(Method m:classEntityPlayerMP.getDeclaredMethods()){
                    if (m.getParameterTypes().length!=1) continue;
                    if (!m.getParameterTypes()[0].equals(classNBTTagCompound)) continue;
                    //if (m.getName().endsWith("b")) method_ReadPlayer = m;
                    if (m.getName().endsWith("a")) method_WritePlayer = m;
                }
            } else {
                for(Method m:classEntity.getDeclaredMethods()){
                    if (m.getParameterTypes().length!=1) continue;
                    if (!m.getParameterTypes()[0].equals(classNBTTagCompound)) continue;
                    if (m.getName().endsWith("c")) method_ReadEntity = m;
                    if (m.getName().endsWith("f")) method_WriteEntity = m;
                    if (m.getName().endsWith("e")) method_ReadPlayer = m;
                }
                method_createEntityFromNBT = classEntityTypes.getDeclaredMethod("a",classNBTTagCompound,classWorld);
            }

        } catch (Exception e) {
            throw new RuntimeException("reflection init error",e);
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
    public NBTTagCompound readTag() {
        NBTTagCompound t = new NBTTagCompound();
        Object l = Reflections.invoke(method_getHandleEntity,entity);
        Reflections.invoke((class_EntityPlayer.isInstance(l)?method_ReadPlayer:method_ReadEntity), l, t.getHandle());
        return t;
    }

    @Override
    public void writeTag(NBTBase base) {
        Object liv = Reflections.invoke(method_getHandleEntity,entity);
        if(Reflections.isForge() && classEntityPlayerMP.isInstance(liv)){
            Reflections.invoke(method_WritePlayer,liv, base.getHandle());
        }else{
            Reflections.invoke(method_WriteEntity,liv, base.getHandle());
        }
    }

    @Override
    protected Class<Entity> getContainerClass() {
        return Entity.class;
    }

    @Override
    public String toString(){
        if (entity instanceof Player) return ((Player) entity).getDisplayName();
        else return entity.getType().toString();
    }

}
