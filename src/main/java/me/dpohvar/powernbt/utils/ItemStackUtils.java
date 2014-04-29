package me.dpohvar.powernbt.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Level;

import static me.dpohvar.powernbt.utils.ReflectionUtils.*;

public final class ItemStackUtils {

    /**
     * static access to utils
     */
    public static final ItemStackUtils itemStackUtils = new ItemStackUtils();



    RefClass classCraftItemStack = getRefClass("{cb}.inventory.CraftItemStack, {CraftItemStack}");
    RefClass classItemStack = getRefClass("{nms}.ItemStack, {nm}.item.ItemStack, {ItemStack}");
    RefField itemHandle = classCraftItemStack.findField(classItemStack);
    RefField tag = classItemStack.findField("{nms}.NBTTagCompound, {nm}.nbt.NBTTagCompound, {NBTTagCompound}");

    RefMethod asNMSCopy;
    RefMethod asCraftMirror;
    RefConstructor conNmsItemStack;
    RefConstructor conCraftItemStack;
    RefClass classItemMeta;

    private ItemStackUtils(){
        try {
            asNMSCopy = classCraftItemStack.findMethod(new MethodCondition()
                            .withTypes(ItemStack.class)
                            .withReturnType(classItemStack)
            );
            asCraftMirror = classCraftItemStack.findMethod(new MethodCondition()
                            .withTypes(classItemStack)
                            .withReturnType(classCraftItemStack)
            );
        } catch (Exception e) {
            conNmsItemStack = classItemStack.getConstructor(int.class, int.class, int.class);
            conCraftItemStack = classCraftItemStack.getConstructor(classItemStack);
        }
        try {
            classItemMeta = getRefClass("org.bukkit.inventory.meta.ItemMeta");
        } catch (Exception e) {
            classItemMeta = null;
        }

    }

    private Object getTag(Object nmsItemStack) {
        return tag.of(nmsItemStack).get();
    }

    @SuppressWarnings("unchecked")
    private void setTag(Object nmsItemStack, Object nbtTagCompound) {
        tag.of(nmsItemStack).set(nbtTagCompound);
    }

    @SuppressWarnings("deprecation")
    public Object createNmsItemStack(ItemStack itemStack){
        if (asNMSCopy != null) {
            return asNMSCopy.call(itemStack);
        } else {
            int type = itemStack.getTypeId();
            int amount = itemStack.getAmount();
            int data = itemStack.getData().getData();
            return conNmsItemStack.create(type, amount, data);
        }
    }

    public ItemStack createCraftItemStack(Object nmsItemStack){
        if (asCraftMirror != null) {
            return (ItemStack) asCraftMirror.call(nmsItemStack);
        } else {
            return (ItemStack) conCraftItemStack.create(nmsItemStack);
        }
    }

    private Object getHandle(ItemStack cbItemStack){
        return itemHandle.of(cbItemStack).get();
    }

    public ItemStack createCraftItemStack(ItemStack item){
        return createCraftItemStack(createNmsItemStack(item));
    }

    public void setTag(ItemStack itemStack, Object nbtTagCompound){
        if (classCraftItemStack.isInstance(itemStack)) setTagCB(itemStack, nbtTagCompound);
        else if (classItemMeta != null) setTagOrigin(itemStack, nbtTagCompound);
    }

    public Object getTag(ItemStack itemStack){
        if (classCraftItemStack.isInstance(itemStack)) return getTagCB(itemStack);
        else if (classItemMeta != null) return getTagOrigin(itemStack);
        else return null;
    }

    @SuppressWarnings("unchecked")
    private void setTagCB(ItemStack itemStack, Object nbtTagCompound){
        Object nmsItemStack = getHandle(itemStack);
        setTag(nmsItemStack,nbtTagCompound);
    }

    private Object getTagCB(ItemStack itemStack){
        Object nmsItemStack = getHandle(itemStack);
        return getTag(nmsItemStack);
    }

    private void setTagOrigin(ItemStack itemStack, Object nbtTagCompound){
        if (nbtTagCompound == null) {
            itemStack.setItemMeta(null);
            return;
        }
        ItemStack copyNMSItemStack = createCraftItemStack(itemStack);
        try {
            setTagCB(copyNMSItemStack, nbtTagCompound);
            ItemMeta meta = copyNMSItemStack.getItemMeta();
            itemStack.setItemMeta(meta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getTagOrigin(ItemStack itemStack){
        ItemStack copyNMSItemStack = createCraftItemStack(itemStack);
        try {
            ItemMeta meta = itemStack.getItemMeta();
            copyNMSItemStack.setItemMeta(meta);
            return getTagCB(copyNMSItemStack);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "copy item meta", e);
        }
        return null;
    }

}











