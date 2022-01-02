package me.dpohvar.powernbt.api;

import net.minecraft.world.entity.Entity;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import java.io.*;
import java.util.List;
import java.util.Map;

interface NBTBridge {

    public Map<String,?> getNbtMap(Object nbtTagCompound);

    public List<?> getNbtList(Object nbtTagList);

    public NBTCompound getBlockNBT(BlockState state);

    public NBTCompound getEntityNBT(Entity entity);

    public NBTCompound getItemStackNBT(ItemStack itemStack);

    public NBTCompound getChunkNBT(Chunk chunk);

    public void setBlockNBT(BlockState state, NBTCompound compound);

    public void setEntityNBT(Entity entity, NBTCompound compound);

    public void setItemStackNBT(ItemStack itemStack, NBTCompound compound);

    public void setChunkNBT(Chunk chunk, NBTCompound compound);

    public void readNBT(InputStream inputStream) throws IOException;

    public void writeNBT(OutputStream outputStream, Object value) throws IOException;

    public void readCompressedNBT(InputStream inputStream) throws IOException;

    public void writeCompressedNBT(OutputStream outputStream, Object value) throws IOException;

    public Object readNBT(DataInput dataInput, Object value) throws IOException;

    public Object writeNBT(DataOutput dataInput, Object value) throws IOException;


}
