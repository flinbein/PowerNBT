package me.dpohvar.powernbt.api;

import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

abstract class NBTBridge {

    private static NBTBridge instance;

    public static NBTBridge getInstance(){
        if (instance == null) instance = new NBTBridgeSpigot();
        return instance;
    }

    abstract Map<String, Object> getNbtInnerMap(Object nbtTagCompound);

    abstract List<Object> getNbtInnerList(Object nbtTagList);

    abstract Object getBlockNBTTag(BlockState state);

    abstract Object getEntityNBTTag(Entity entity);

    abstract Object getItemStackNBTTag(ItemStack itemStack);

    abstract void setBlockNBTTag(BlockState state, Object tag);

    abstract void setEntityNBTTag(Entity entity, Object tag);

    abstract void setItemStackNBTTag(ItemStack itemStack, Object tag);

    abstract ItemStack asCraftCopyItemStack(ItemStack itemStack);

    abstract Object readNBTData(DataInput dataInput, byte type) throws IOException;

    abstract void writeNBTData(DataOutput dataInput, Object tag) throws IOException;

    abstract Entity spawnEntity(Object tag, World world);

    abstract byte getTagType(Object tag);

    abstract Object getPrimitiveValue(Object tag);

    abstract Object getTagValueByPrimitive(Object javaPrimitive);

    abstract Object cloneTag(Object tag);

    abstract Object createNBTTagCompound();

    abstract Object createNBTTagList();

    abstract byte getNBTTagListType(Object tagList);

    abstract void setNBTTagListType(Object tagList, byte type);

}
