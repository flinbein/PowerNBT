package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;
import org.bukkit.inventory.ItemStack;

public class NBTContainerItem extends NBTContainer<ItemStack> {

    ItemStack item;

    public NBTContainerItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getObject() {
        return item;
    }

    @Override
    public NBTCompound readTag() {
        return NBTManager.getInstance().read(item);
    }

    @Override
    public void writeTag(Object base) {
        if (base instanceof NBTCompound compound) {
            NBTManager.getInstance().write(item, compound.clone());
        }

    }

    @Override
    public void eraseTag() {
        writeTag(null);
    }

    @Override
    protected Class<ItemStack> getContainerClass() {
        return ItemStack.class;
    }

    @Override
    public String toString(){
        return item.toString();
    }
}
