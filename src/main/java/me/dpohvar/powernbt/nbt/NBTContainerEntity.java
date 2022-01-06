package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.utils.ReflectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

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
        NBTTagCompound tag = new NBTTagCompound(false, NBTManager.getInstance().read(entity).getHandle());
        if (ReflectionUtils.isForge()) {
            NBTCompound nbtCompound = NBTManager.getInstance().readForgeData(entity);
            if (nbtCompound != null) {
                NBTTagCompound forgeData = new NBTTagCompound(false,nbtCompound.getHandle());
                tag.put("ForgeData", forgeData);
            } else {
                tag.put("ForgeData", new NBTTagCompound());
            }
        }
        return tag;
    }

    @Override
    public void writeTag(NBTBase base) {
        NBTManager.getInstance().write(entity, NBTCompound.forNBT(base.getHandle()));
        if (ReflectionUtils.isForge() && base instanceof NBTTagCompound nbtTagCompound){
            NBTBase forgeData = nbtTagCompound.get("ForgeData");
            NBTManager.getInstance().writeForgeData(entity, NBTCompound.forNBT(forgeData.getHandle()));
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
