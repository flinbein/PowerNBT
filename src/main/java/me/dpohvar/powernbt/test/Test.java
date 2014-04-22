package me.dpohvar.powernbt.test;

import me.dpohvar.powernbt.nbt.NBTBase;
import me.dpohvar.powernbt.nbt.NBTContainerItem;
import me.dpohvar.powernbt.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Test {
    public void run() throws Exception {
        testJsonAndNBT();
        testItemStackContainer();
    }

    private void testItemStackContainer() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.put("a", 5);

        ItemStack itemStack = createCraftItemStack();
        NBTContainerItem containerItem = new NBTContainerItem(itemStack);
        containerItem.setTag(compound);

        itemStack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
        NBTBase nbt = containerItem.getTag();
        if (!(nbt instanceof NBTTagCompound) || !((NBTTagCompound)nbt).containsKey("ench"))
            throw new AssertionError("Test failed! No ench compound!");
    }

    private void testJsonAndNBT() {
        String[] jsons = {
                "{Unbreakable:1,display:{Name:\"Unbreaking Pickaxe of Redundancy\"},ench:[{id:34,lvl:3}]}",
                "{AttributeModifiers:[{AttributeName:\"generic.attackDamage\",Name:\"generic.attackDamage\",Amount:50,Operation:0,UUIDLeast:894654,UUIDMost:2872}]}",
                "{display:{Name:\"This can Rename an Item\",Lore:[This is a line of 'Lore']},ench:[{id:16,lvl:2}]}",
                "{Fuse:20,Motion:[1.0,0.0,0.0]}",
                "{LifeTime:20,FireworksItem:{id:401,Count:1,tag:{Fireworks:{Explosions:[{Flicker:1,Trail:1,Type:4,Colors:[255,16711680,16776960],FadeColors:[255,16711680,16776960]},{Flicker:0,Trail:0,Type:1,Colors:[16711680,16776960],FadeColors:[255,16711680]}]}}}}",
                "{Riding:{id:Pig,Riding:{id:Chicken}}}"
        };
        for (String json: jsons) {
            NBTBase nbt = JsonToNBT.parse(json);
            nbt.clone();
        }
    }

    private ItemStack createCraftItemStack() {
        ItemStack stack = new ItemStack(Material.BUCKET);
        return itemStackToCraftItemStack(stack);
    }

    private ItemStack itemStackToCraftItemStack(ItemStack stack) {
        Inventory inventory = Bukkit.getServer().createInventory(null, 9);
        inventory.addItem(stack);
        return inventory.getItem(0);
    }
}
