package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class NBTContainerItem extends NBTContainer<ItemStack> {

    private final ItemStack item;

    public NBTContainerItem(ItemStack item) {
        this(item, null);
    }

    public NBTContainerItem(ItemStack item, String selectorTag) {
        super(selectorTag);
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
    public void writeTag(Object value) {
        NBTCompound compound = null;
        if (value instanceof NBTCompound c) compound = c;
        else if (value instanceof Map map) compound = new NBTCompound(map);
        if (compound == null) return;
        NBTManager.getInstance().write(item, compound.clone());
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
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) return itemMeta.getDisplayName();
        return item.getType().name();
    }
}
