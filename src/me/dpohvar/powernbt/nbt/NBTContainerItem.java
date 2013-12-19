package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.utils.Reflections;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NBTContainerItem extends NBTContainer<ItemStack> {

    ItemStack item;

    private static final Class class_CraftItemStack = Reflections.getClass("{cb}.inventory.CraftItemStack");
    private static final Class class_ItemStack = Reflections.getClass("{nms}.ItemStack","net.minecraft.item.ItemStack");
    private static final Class class_NBTTagCompound = Reflections.getClass("{nms}.NBTTagCompound","net.minecraft.nbt.NBTTagCompound");
    static Field field_Tag = Reflections.getField(class_ItemStack,class_NBTTagCompound);
    static Field field_Handle = null;
    static Method method_getHandle = null;
    static {
        try{
            method_getHandle = Reflections.getMethodByTypes(class_CraftItemStack, class_ItemStack);
        } catch (Exception e){
        }
        try{
            field_Handle = Reflections.getField(class_CraftItemStack, class_ItemStack);
        } catch (Exception e){
        }
        if (method_getHandle==null && field_Handle==null){
            throw new RuntimeException("nbt item error: no way to get NBT tag");
        }
    }

    public NBTContainerItem(ItemStack item) {
        this.item = item;
    }

    public ItemStack getObject() {
        return item;
    }

    @Override
    public List<String> getTypes() {
        int id = item.getTypeId();
        List<String> s = new ArrayList<String>();
        s.add("item");
        if (id == 387 || id == 386) s.add("item_book");
        else if (id >= 298 && id <= 301) s.add("item_leather");
        else if (id == 397) s.add("item_skull");
        else if (id == 403) s.add("item_enchbook");
        else if (id == 373) s.add("item_potion");
        else if (id == 401) s.add("item_rocket");
        else if (id == 402) s.add("item_firework");
        if ((id >= 267 && id <= 279)
                || (id >= 283 && id <= 286)
                || (id >= 290 && id <= 294)
                || (id >= 298 && id <= 317)
                || id == 261
                || id == 346
                || id == 359
                || id == 256
                || id == 257
                ) s.add("item_repair");
        return s;
    }

    @Override
    public NBTTagCompound readTag() {
        Object is = getItemStackHandle();
        if(is==null) return null;
        Object tag = Reflections.getFieldValue(field_Tag,is);
        if (tag==null) return null;
        return (NBTTagCompound) NBTBase.wrap(tag).clone();
    }

    @Override
    public void writeTag(NBTBase base) {
        try {
            Object handle = getItemStackHandle();
            if(handle!=null) field_Tag.set(getItemStackHandle(), base.getHandle());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eraseTag() {
        Object is = getItemStackHandle();
        if(is!=null) Reflections.setFieldValue(field_Tag,is,null);
    }

    @Override
    protected Class<ItemStack> getContainerClass() {
        return ItemStack.class;
    }

    private Object getItemStackHandle() {
        //todo: create handle if not exist
        try {
            return Reflections.invoke(method_getHandle,item);
        } catch (Exception ignored) {
            return Reflections.getFieldValue(field_Handle,item);
        }
    }

    @Override
    public String toString(){
        return item.toString();
    }
}
