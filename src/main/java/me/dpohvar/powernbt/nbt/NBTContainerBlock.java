package me.dpohvar.powernbt.nbt;

import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;

import java.util.Arrays;
import java.util.List;

import static me.dpohvar.powernbt.PowerNBT.plugin;

public class NBTContainerBlock extends NBTContainer<Block> {

    Block block;

    public NBTContainerBlock(Block block) {
        this.block = block;
    }

    public Block getObject() {
        return block;
    }

    @Override
    public List<String> getTypes() {
        return Arrays.asList("block", "block_" + block.getType().name());
    }

    @Override
    public NBTCompound readCustomTag() {
        NBTCompound tag = readTag();
        if (tag!=null) {
            List<String> ignores = plugin.getConfig().getStringList("ignore_get.block");
            for (String s:ignores) tag.remove(s);
        }
        return tag;
    }

    public NBTCompound readTag() {
        return NBTManager.getInstance().read(block);
    }

    @Override
    public void writeTag(Object value) {
        BlockState state = block.getState();
        if (state instanceof TileState tile && value instanceof NBTCompound compound) {
            NBTManager.getInstance().write(tile, compound);
            state.update();
        }
    }

    @Override
    public void writeCustomTag(Object value) {
        if (!(value instanceof NBTCompound compound)) return;
        compound = compound.clone();
        List<String> ignores = plugin.getConfig().getStringList("ignore_set.block");
        for (String s:ignores) compound.remove(s);
        NBTCompound original = readTag();
        if(compound.get("x")==null) compound.put("x",original.get("x"));
        if(compound.get("y")==null) compound.put("y",original.get("y"));
        if(compound.get("z")==null) compound.put("z",original.get("z"));
        writeTag(compound);
    }

    @Override
    protected Class<Block> getContainerClass() {
        return Block.class;
    }

    @Override
    public String toString(){
        return "block:" + block.getBlockData();
    }

}
