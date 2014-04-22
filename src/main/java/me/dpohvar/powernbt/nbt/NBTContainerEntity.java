package me.dpohvar.powernbt.nbt;

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
        NBTTagCompound t = new NBTTagCompound();
        entityUtils.readEntity(entity, t.getHandle());
        return t;
    }

    @Override
    public void writeTag(NBTBase base) {
        entityUtils.writeEntity(entity, base.getHandle());
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
