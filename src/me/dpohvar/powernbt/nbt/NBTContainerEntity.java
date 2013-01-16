package me.dpohvar.powernbt.nbt;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

import static me.dpohvar.powernbt.utils.StaticValues.noInput;
import static me.dpohvar.powernbt.utils.StaticValues.oneNBTTagCompound;
import static me.dpohvar.powernbt.utils.VersionFix.callMethod;

public class NBTContainerEntity extends NBTContainer {

    Entity ent;

    public NBTContainerEntity(Entity ent) {
        this.ent = ent;
    }

    public Entity getObject() {
        return ent;
    }

    @Override
    public List<String> getTypes() {
        List<String> s = new ArrayList<String>();
        s.add("entity");
        if (ent instanceof LivingEntity) s.add("living");
        s.add(ent.getType().getName());
        return s;
    }

    @Override
    public NBTTagCompound getTag() {
        Object liv = callMethod(ent, "getHandle", noInput);
        NBTTagCompound base = new NBTTagCompound();
        callMethod(liv, "b", oneNBTTagCompound, base.getHandle());
        return base;
    }

    @Override
    public void setTag(NBTBase base) {
        Object liv = callMethod(ent, "getHandle", noInput);
        callMethod(liv, "a", oneNBTTagCompound, base.getHandle());

    }

    @Override
    public String getName() {
        return ent.getType().getName() + " (id" + ent.getEntityId() + ")";
    }
}
