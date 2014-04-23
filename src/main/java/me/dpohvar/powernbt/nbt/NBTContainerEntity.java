package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.NBTUtils;
import me.dpohvar.powernbt.utils.ReflectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.utils.EntityUtils.entityUtils;

public class NBTContainerEntity extends NBTContainer<Entity> {

    Entity entity;

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
        //noinspection deprecation
        s.add(entity.getType().getName());
        return s;
    }

    @Override
    public NBTTagCompound readTag() {
        NBTTagCompound tag = new NBTTagCompound();
        entityUtils.readEntity(entity, tag.getHandle());
        if (ReflectionUtils.isForge()) {
            Object t = entityUtils.getForgeData(entity);
            if (t != null) {
                NBTTagCompound forgeData = new NBTTagCompound(false,t);
                tag.put("ForgeData", forgeData);
            } else {
                tag.put("ForgeData", new NBTTagCompound());
            }
        }
        return tag;
    }

    @Override
    public void writeTag(NBTBase base) {
        entityUtils.writeEntity(entity, base.getHandle());
        if (ReflectionUtils.isForge()){
            Object forgeData = ((NBTTagCompound)base).get("ForgeData");
            if (forgeData instanceof NBTTagCompound) {
                Object data = base.getHandle();
                entityUtils.writeEntity(entity, NBTUtils.nbtUtils.cloneTag(data));
            } else {
                entityUtils.setForgeData(entity, NBTUtils.nbtUtils.createTagCompound());
            }
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
