package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;
import me.dpohvar.powernbt.utils.ReflectionUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public NBTCompound readTag() {
        NBTCompound tag = NBTManager.getInstance().read(entity);
        if (ReflectionUtils.isForge()) {
            NBTCompound nbtCompound = NBTManager.getInstance().readForgeData(entity);
            tag.put("ForgeData", Objects.requireNonNullElseGet(nbtCompound, NBTCompound::new));
        }
        return tag;
    }

    @Override
    public void writeTag(Object value) {
        NBTCompound compound = null;
        if (value instanceof NBTCompound c) compound = c;
        else if (value instanceof Map map) compound = new NBTCompound(map);
        if (compound == null) return;
        NBTManager.getInstance().write(entity, compound);
        if (ReflectionUtils.isForge()){
            Object forgeData = compound.get("ForgeData");
            if (forgeData instanceof NBTCompound forgeCompound)
            NBTManager.getInstance().writeForgeData(entity, forgeCompound);
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
